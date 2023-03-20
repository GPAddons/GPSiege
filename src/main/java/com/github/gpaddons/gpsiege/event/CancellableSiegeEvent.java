package com.github.gpaddons.gpsiege.event;

import com.github.gpaddons.gpsiege.siege.Siege;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public class CancellableSiegeEvent extends SiegeEvent implements Cancellable {

  private boolean cancelled = false;

  public CancellableSiegeEvent(@NotNull Siege siege) {
    super(siege);
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    // TODO should this follow the MessageCancellable system GP is using?
    this.cancelled = cancelled;
  }

}
