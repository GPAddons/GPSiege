package com.github.gpaddons.gpsiege.event;

import com.github.gpaddons.gpsiege.siege.Siege;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SiegeJoinEvent extends CancellableSiegeEvent {

  private final @NotNull Player player;
  private final boolean isDefender;

  public SiegeJoinEvent(@NotNull Siege siege, @NotNull Player player, boolean isDefender) {
    super(siege);
    this.player = player;
    this.isDefender = isDefender;
  }

  public @NotNull Player getPlayer() {
    return player;
  }

  public boolean isDefender() {
    return isDefender;
  }

  public boolean isAttacker() {
    return !isDefender;
  }

}
