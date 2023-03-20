package com.github.gpaddons.gpsiege.siege;

import com.github.gpaddons.gpsiege.config.SiegeConfig;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

// TODO move BossBarTimer to PlanarWrappers
// Also add in better key management/cleanup
class ReturnToBattleTask implements Runnable {

  private final @NotNull SiegeManager manager;
  private final @NotNull Siege siege;
  private final @NotNull Player target;
  private final int ticksToReturn;
  private final @NotNull BossBar bossBar;

  private int ticksRemaining;

  ReturnToBattleTask(
      @NotNull SiegeManager manager,
      @NotNull Siege siege,
      @NotNull Player target,
      @NotNull SiegeConfig config) {
    this.manager = manager;
    this.siege = siege;
    this.target = target;
    this.ticksToReturn = config.getReturnToBattleTicks();
    this.bossBar = Bukkit.createBossBar(config.getBarTitle(), BarColor.RED, BarStyle.SEGMENTED_20);
    this.ticksRemaining = ticksToReturn;
  }

  @Override
  public void run() {
    // cancel if predicate indicates in battle
    // if < 1 ticks left, complete DQ
    // tick down
  }

}
