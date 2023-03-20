package com.github.gpaddons.gpsiege.siege;

import com.github.gpaddons.gpsiege.event.SiegeEndEvent;
import com.github.gpaddons.gpsiege.event.SiegeIncludeClaimsEvent;
import com.github.gpaddons.gpsiege.event.SiegeJoinEvent;
import com.github.gpaddons.gpsiege.event.SiegeStartEvent;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.net.http.WebSocket.Listener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SiegeManager implements Listener {

  private final Long2ObjectMap<Siege> claimToSiege = new Long2ObjectOpenHashMap<>();
  private final Map<UUID, Siege> uuidToSiege = new Object2ObjectOpenHashMap<>();
  private final Collection<Siege> sieges = new HashSet<>();
  private final @NotNull Plugin plugin;

  public SiegeManager(@NotNull Plugin plugin) {
    this.plugin = plugin;
  }

  public @Nullable Siege startSiege(
      @NotNull Player target,
      @NotNull Player initiator,
      @NotNull Claim claim) {
    Siege existing = uuidToSiege.get(initiator.getUniqueId());
    if (existing != null) {
      // If initiator is involved in a siege, they cannot start a new one. Return the active siege.
      return existing;
    }

    // First get the existing siege on the target.
    existing = uuidToSiege.get(target.getUniqueId());

    if (existing == null) {
      // If that doesn't exist, check for an existing siege on the claim.
      existing = claimToSiege.get((long) claim.getID());
    }

    if (existing != null) {
      // If either exist, the user is a new recruit to an existing siege.
      if (!existing.isOngoing()) {
        // Siege has already completed. User cannot join.
        return existing;
      }

      if (existing.isAttacking(target.getUniqueId())) {
        // Targeted user is an attacker, initiator is a new defender.
        joinSiege(existing, initiator, true);
        return existing;
      } else if (existing.isDefending(target.getUniqueId())) {
        // Targeted user is a defender, initiator is a new attacker.
        joinSiege(existing, initiator, false);
        return existing;
      } else {
        // User specified is not involved in the siege.
        // TODO ensure join by name prioritizes join on user
        // Invalid user specified. Cannot create siege and cannot determine side.
        return null;
      }
    }

    Siege siege = new Siege(target, initiator, claim);
    SiegeStartEvent event = new SiegeStartEvent(siege, claim);
    plugin.getServer().getPluginManager().callEvent(event);

    return siege;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  private void onSiegeStart(@NotNull SiegeStartEvent event) {
    Siege siege = event.getSiege();
    if (event.isCancelled()) {
      siege.setComplete();
      return;
    }

    // Check if a player is already involved in a different siege.
    if (Stream.concat(siege.getAttackers().stream(), siege.getDefenders().stream())
        .anyMatch(uuid -> uuidToSiege.containsKey(uuid) && uuidToSiege.get(uuid) != siege)) {
      event.setCancelled(true);
      siege.setComplete();
      return;
    }

    Stream.concat(siege.getAttackers().stream(), siege.getDefenders().stream())
        .forEach(uuid -> uuidToSiege.put(uuid, siege));

    siege.getAffectedClaims().forEach(claim -> claimToSiege.putIfAbsent(claim.getID(), siege));
    sieges.add(siege);
    new CheckStatusTask(this, siege).runTaskTimer(plugin, 20L, 20L * 30L);
  }

  private void joinSiege(@NotNull Siege siege, @NotNull Player player, boolean isDefender) {
    if (isNotAllowedInSiege(siege, player.getUniqueId())) {
      return;
    }

    SiegeJoinEvent event = new SiegeJoinEvent(siege, player, isDefender);
    // Pass to completion via event handler.
    plugin.getServer().getPluginManager().callEvent(event);
  }

  private boolean isNotAllowedInSiege(@NotNull Siege siege, @NotNull UUID uuid) {
    return siege.isAttacking(uuid) || siege.isDefending(uuid) || siege.isDisqualified(uuid);
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  private void completeSiegeJoin(@NotNull SiegeJoinEvent event) {
    Siege siege = event.getSiege();
    UUID uuid = event.getPlayer().getUniqueId();

    if (!siege.isOngoing()
        || isNotAllowedInSiege(event.getSiege(), uuid)
        || this.uuidToSiege.containsKey(uuid)) {
      event.setCancelled(true);
      return;
    }

    Consumer<UUID> addToTeam = event.isDefender() ? siege::addDefender : siege::addAttacker;
    addToTeam.accept(uuid);
    if (this.sieges.contains(siege)) {
      // Siege is already ongoing, add new member.
      uuidToSiege.put(uuid, siege);
    }
  }

  public boolean extendSiegeToClaim(@NotNull Siege siege, @NotNull Claim claim) {
    return extendSiegeToClaims(siege, Set.of(claim));
  }

  public boolean extendSiegeToClaims(
      @NotNull Siege siege,
      @NotNull Collection<@NotNull Claim> claims) {
    // TODO sanitize further so no attackers attack own claims? Does it matter?

    claims.removeIf(claim ->
        !claim.hasExplicitPermission(siege.getTarget().getUniqueId(), ClaimPermission.Build)
            || claim.hasExplicitPermission(siege.getInitiator().getUniqueId(),
            ClaimPermission.Build));
    SiegeIncludeClaimsEvent event = new SiegeIncludeClaimsEvent(siege, claims);
    Bukkit.getPluginManager().callEvent(event);
    if (event.isCancelled()) {
      return false;
    }

    claims = event.getClaims();

    claims.removeIf(claim -> claimToSiege.putIfAbsent(claim.getID(), siege) != null);
    claims.forEach(siege::addClaim);

    return true;
  }

  public void endSiege(@NotNull Siege siege, boolean defenderWins) {
    siege.setComplete();
    siege.getAffectedClaims().stream().mapToLong(Claim::getID).forEach(claimToSiege::remove);
    Stream.concat(siege.getAttackers().stream(), siege.getDefenders().stream())
        .forEach(uuidToSiege::remove);
    sieges.remove(siege);

    SiegeEndEvent event = new SiegeEndEvent(siege, defenderWins);
    Bukkit.getPluginManager().callEvent(event);
  }

  public @Nullable Siege getSiege(@NotNull Claim claim) {
    return claimToSiege.get((long) claim.getID());
  }

  public @Nullable Siege getSiege(@NotNull UUID uuid) {
    return uuidToSiege.get(uuid);
  }

}
