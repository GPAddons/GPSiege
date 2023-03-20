package com.github.gpaddons.gpsiege.siege;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

class CheckStatusTask extends BukkitRunnable {

  private final @NotNull SiegeManager manager;
  private final @NotNull Siege siege;

  CheckStatusTask(@NotNull SiegeManager manager, @NotNull Siege siege) {
    this.manager = manager;
    this.siege = siege;
  }

  @Override
  public void run() {
    if (!siege.isOngoing()) {
      cancel();
      return;
    }

    DataStore dataStore = GriefPrevention.instance.dataStore;
    Player target = siege.getTarget();

    // Find the target's location.
    Claim targetClaim = dataStore.getClaimAt(target.getLocation(), false, null);

    Player initiator = siege.getInitiator();
    if (targetClaim != null
        && targetClaim.hasExplicitPermission(target, ClaimPermission.Inventory)
        && !targetClaim.hasExplicitPermission(initiator, ClaimPermission.Inventory)) {
      // If the target is in a claim and has trust and the defender does not, it is besieged.
      // Note that this uses explicit permission so that areas like admin claims are not rolled in.
      manager.extendSiegeToClaim(siege, targetClaim);
      // TODO if extension fails, trigger return to battle
    }

    // TODO this is old GP code - attackers and defenders should be individually considered
    //  attacker and defender side should both be checked completely
    //    -> return to battle task -> failure = DQ
    //    when one side has no eligible players, other side wins

    // Players are determined to be present if within 25 blocks of a sieged claim.
    boolean attackerRemains = this.playerRemains(initiator);
    boolean defenderRemains = this.playerRemains(target);

    if (attackerRemains && defenderRemains) {
      // If both are present, siege is still ongoing.
      return;
    }

    if (attackerRemains) {
      // The initiator wins if the target runs away.
      manager.endSiege(siege, false);
    } else if (defenderRemains) {
      // The target wins if the initiator leaves.
      manager.endSiege(siege, true);
    } else if (
        !initiator.getWorld().equals(target.getWorld())
            || initiator.getLocation().distanceSquared(target.getLocation()) > 2500) {
      // If they both left and are not within 50 blocks of each other, the siege ends.
      // The initiator is assumed to be the winner as they have run off the target.
      manager.endSiege(siege, false);
    }
  }

  private boolean playerRemains(@NotNull Player player) {
    var playerLocation = player.getLocation();
    return siege.getAffectedClaims().stream().anyMatch(claim -> claim.isNear(playerLocation, 25));
  }

}
