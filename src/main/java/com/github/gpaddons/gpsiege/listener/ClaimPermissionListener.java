package com.github.gpaddons.gpsiege.listener;

import com.github.gpaddons.gpsiege.config.SiegeConfig;
import com.github.gpaddons.gpsiege.siege.Siege;
import com.github.gpaddons.gpsiege.siege.SiegeManager;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.events.ClaimPermissionCheckEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class ClaimPermissionListener {

  private final @NotNull SiegeManager manager;
  private final @NotNull SiegeConfig config;

  public ClaimPermissionListener(@NotNull SiegeManager manager, @NotNull SiegeConfig config) {
    this.manager = manager;
    this.config = config;
  }

  @EventHandler(ignoreCancelled = true)
  private void onClaimPermissionCheck(@NotNull ClaimPermissionCheckEvent event) {
    Player player = event.getCheckedPlayer();
    if (player == null) {
      // Ignore offline players.
      return;
    }

    Siege siege = manager.getSiege(event.getClaim());
    if (siege == null) {
      siege = manager.getSiege(event.getCheckedUUID());

      if (siege == null) {
        // No siege affecting user.
        return;
      }
    }

    // soft blocks
    if (event.getTriggeringEvent() instanceof BlockBreakEvent breakEvent) {
      if (siege.isOngoing()) {
        // TODO ongoing soft
      } else {
        // TODO post soft
      }
    }

    if (siege.isLooting()) {
      if (!siege.canLoot(event.getCheckedUUID())) {
        // TODO lang
        event.setDenialReason(() -> "cannot modify claim during looting time");
        return;
      }
      // Container specifically allowed
      if (event.getRequiredPermission() == ClaimPermission.Inventory) {
        event.setDenialReason(null);
      }
      return;
    }


  }

  // TODO prevent resize etc. during siege
  //  event funnel may take care of this

}
