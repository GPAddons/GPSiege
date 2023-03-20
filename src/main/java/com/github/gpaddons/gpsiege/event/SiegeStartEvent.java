package com.github.gpaddons.gpsiege.event;

import com.github.gpaddons.gpsiege.siege.Siege;
import me.ryanhamshire.GriefPrevention.Claim;
import org.jetbrains.annotations.NotNull;

public class SiegeStartEvent extends CancellableSiegeEvent {

  private final @NotNull Claim focusedClaim;

  public SiegeStartEvent(@NotNull Siege siege, @NotNull Claim focusedClaim) {
    super(siege);
    this.focusedClaim = focusedClaim;
  }

  public @NotNull Claim getFocusedClaim() {
    return focusedClaim;
  }

}
