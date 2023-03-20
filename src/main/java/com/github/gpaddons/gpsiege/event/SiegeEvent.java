package com.github.gpaddons.gpsiege.event;

import com.github.gpaddons.gpsiege.siege.Siege;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SiegeEvent extends Event {

  private final @NotNull Siege siege;

  public SiegeEvent(@NotNull Siege siege) {

    this.siege = siege;
  }

  public @NotNull Siege getSiege() {
    return siege;
  }

  // Listenable event requirements
  private static final HandlerList HANDLERS = new HandlerList();

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLERS;
  }

}
