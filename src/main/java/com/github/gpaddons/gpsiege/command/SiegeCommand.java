package com.github.gpaddons.gpsiege.command;

import com.github.gpaddons.gpsiege.siege.SiegeManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class SiegeCommand implements TabExecutor {

  private final @NotNull SiegeManager manager;

  public SiegeCommand(@NotNull SiegeManager manager) {
    this.manager = manager;
  }

  @Override
  public boolean onCommand(
      @NotNull CommandSender sender,
      @NotNull Command command,
      @NotNull String label,
      @NotNull String[] args) {
    // TODO
    return false;
  }

  @Override
  public @Nullable List<String> onTabComplete(
      @NotNull CommandSender sender,
      @NotNull Command command,
      @NotNull String label,
      @NotNull String[] args) {
    // TODO
    return null;
  }

}
