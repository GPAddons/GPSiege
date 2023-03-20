package com.github.gpaddons.gpsiege.listener;

import com.github.gpaddons.gpsiege.config.SiegeConfig;
import com.github.gpaddons.gpsiege.event.SiegeIncludeClaimsEvent;
import com.github.gpaddons.gpsiege.event.SiegeJoinEvent;
import com.github.gpaddons.gpsiege.event.SiegeStartEvent;
import com.github.gpaddons.gpsiege.siege.SiegeManager;
import com.github.jikoo.planarwrappers.event.Event;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class SiegeFeatureListener {

  private final @NotNull Plugin plugin;
  private final @NotNull SiegeConfig config;
  private final @NotNull SiegeManager manager;

  public SiegeFeatureListener(@NotNull Plugin plugin, @NotNull SiegeConfig config, @NotNull SiegeManager manager) {
    this.plugin = plugin;
    this.config = config;
    this.manager = manager;
  }

  public void registerFeatures() {
    if (config.isIncludeAllDefenderClaims()) {
      Event.register(SiegeStartEvent.class, this::includeAllDefenderClaims, plugin, EventPriority.LOW);
      Event.register(SiegeJoinEvent.class, this::includeAllDefenderClaims, plugin, EventPriority.LOW);
    }
    if (config.getIncludeNearbyClaimsDefenderRadius() > 0) {
      Event.register(SiegeStartEvent.class, this::includeNearbyClaims, plugin, EventPriority.LOW);
    }
    if (config.getIncludeNearbyClaimsAttackerRadius() > 0) {
      Event.register(SiegeIncludeClaimsEvent.class, this::onlyIncludeIfAttackerNearby, plugin, EventPriority.LOWEST);
    }
  }

  private void includeAllDefenderClaims(@NotNull SiegeStartEvent event) {
    manager.extendSiegeToClaims(
        event.getSiege(),
        GriefPrevention.instance.dataStore
            .getPlayerData(event.getSiege().getTarget().getUniqueId())
            .getClaims());
  }

  private void includeAllDefenderClaims(@NotNull SiegeJoinEvent event) {
    if (event.isDefender()) {
      manager.extendSiegeToClaims(
          event.getSiege(),
          GriefPrevention.instance.dataStore
              .getPlayerData(event.getPlayer().getUniqueId())
              .getClaims());
    }
  }

  private void includeNearbyClaims(@NotNull SiegeStartEvent event) {
    Location location = event.getSiege().getTarget().getLocation();
    manager.extendSiegeToClaims(
        event.getSiege(),
        GriefPrevention.instance.dataStore.getClaims().stream()
            .filter(
                claim ->
                    Objects.equals(location.getWorld(), claim.getLesserBoundaryCorner().getWorld())
                        && claim.isNear(location, config.getIncludeNearbyClaimsDefenderRadius()))
            .collect(Collectors.toSet()));
  }

  private void onlyIncludeIfAttackerNearby(@NotNull SiegeIncludeClaimsEvent event) {
    Collection<Location> attackers = event.getSiege().getOnlineAttackers().stream()
        .map(Player::getLocation)
        .toList();
    event.getClaims().removeIf(claim -> {
      for (Location location : attackers) {
        if (claim.isNear(location, config.getIncludeNearbyClaimsAttackerRadius())) {
          return false;
        }
      }
      return true;
    });
  }

  // TODO cooldowns: SiegeStartEvent, SiegeIncludeClaimsEvent, etc.

}
