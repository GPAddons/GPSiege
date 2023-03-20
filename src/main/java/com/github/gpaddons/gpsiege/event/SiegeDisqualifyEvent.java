package com.github.gpaddons.gpsiege.event;

import com.github.gpaddons.gpsiege.siege.Siege;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SiegeDisqualifyEvent extends CancellableSiegeEvent {

  private final @NotNull Player candidate;
  private final boolean isAllowedToLoot;

  public SiegeDisqualifyEvent(@NotNull Siege siege, @NotNull Player candidate,
      boolean isAllowedToLoot) {
    super(siege);
    this.candidate = candidate;
    this.isAllowedToLoot = isAllowedToLoot;
  }

  public @NotNull Player getCandidate() {
    return candidate;
  }

  public boolean isAllowedToLoot() {
    return isAllowedToLoot;
  }

}
