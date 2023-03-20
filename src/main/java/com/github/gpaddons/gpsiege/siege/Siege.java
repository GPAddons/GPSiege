package com.github.gpaddons.gpsiege.siege;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

public class Siege {

  private final @NotNull Collection<@NotNull Claim> claims = new HashSet<>();
  private final @NotNull Collection<@NotNull UUID> attackers = new HashSet<>();
  private final @NotNull Collection<@NotNull UUID> defenders = new HashSet<>();
  private final @NotNull Collection<@NotNull UUID> disqualified = new HashSet<>();
  private boolean ongoing = true;
  private boolean isLooting = false;
  private final @NotNull Player target;
  private final @NotNull Player initiator;

  public Siege(@NotNull Player target, @NotNull Player initiator, @NotNull Claim claim) {
    this.target = target;
    this.defenders.add(this.target.getUniqueId());
    this.initiator = initiator;
    this.attackers.add(this.initiator.getUniqueId());
    this.claims.add(claim);
  }

  public @NotNull Player getTarget() {
    return this.target;
  }

  public @NotNull Player getInitiator() {
    return this.initiator;
  }

  void addAttacker(@NotNull UUID uuid) {
    this.attackers.add(uuid);
  }

  public boolean isAttacking(@NotNull UUID uuid) {
    return this.attackers.contains(uuid);
  }

  public @NotNull @UnmodifiableView Collection<UUID> getAttackers() {
    return Collections.unmodifiableCollection(this.attackers);
  }

  public @NotNull @Unmodifiable Collection<Player> getOnlineAttackers() {
    return getOnlinePlayers(this.attackers);
  }

  private @NotNull Collection<Player> getOnlinePlayers(@NotNull Collection<UUID> uuids) {
    return uuids.stream()
        .map(Bukkit::getPlayer)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  void addDefender(@NotNull UUID uuid) {
    this.defenders.add(uuid);
  }

  public boolean isDefending(@NotNull UUID uuid) {
    return this.defenders.contains(uuid);
  }

  public @NotNull @UnmodifiableView Collection<UUID> getDefenders() {
    return Collections.unmodifiableCollection(this.defenders);
  }

  public @NotNull @Unmodifiable Collection<Player> getOnlineDefenders() {
    return getOnlinePlayers(this.defenders);
  }

  void setDisqualified(@NotNull UUID uuid, boolean canLoot) {
    this.disqualified.add(uuid);
    if (!canLoot) {
      this.attackers.remove(uuid);
    }
  }

  public boolean isDisqualified(@NotNull UUID uuid) {
    return this.disqualified.contains(uuid);
  }

  public boolean isOngoing() {
    return this.ongoing;
  }

  void setComplete() {
    this.ongoing = false;
  }

  public boolean isLooting() {
    return this.isLooting;
  }

  public boolean canLoot(@NotNull UUID uuid) {
    return isLooting() && this.attackers.contains(uuid);
  }

  void setLooting(boolean looting) {
    this.isLooting = looting;
  }

  public @UnmodifiableView Collection<Claim> getAffectedClaims() {
    return Collections.unmodifiableCollection(claims);
  }

  void addClaim(@NotNull Claim claim) {
    this.claims.add(claim);
  }

}
