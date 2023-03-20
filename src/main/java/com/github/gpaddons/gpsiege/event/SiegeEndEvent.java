package com.github.gpaddons.gpsiege.event;

import com.github.gpaddons.gpsiege.siege.Siege;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SiegeEndEvent extends SiegeEvent {

  private final boolean defenderWins;

  public SiegeEndEvent(@NotNull Siege siege, boolean defenderWins) {
    super(siege);
    this.defenderWins = defenderWins;
  }

  public boolean isDefenderWinner() {
    return defenderWins;
  }

  public @NotNull Player getWinner() {
    return defenderWins ? getSiege().getTarget() : getSiege().getInitiator();
  }

}
