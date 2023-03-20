package com.github.gpaddons.gpsiege;

import com.github.gpaddons.gpsiege.command.SiegeCommand;
import com.github.gpaddons.gpsiege.config.SiegeConfig;
import com.github.gpaddons.gpsiege.listener.SiegeFeatureListener;
import com.github.gpaddons.gpsiege.siege.SiegeManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;

public class GPSiege extends JavaPlugin {

  private SiegeConfig config;
  private SiegeManager manager;

  @Override
  public void onEnable() {
    this.manager = new SiegeManager(this);
    reloadFeatures();

    Objects.requireNonNull(getCommand("siege"), "Command not declared in plugin.yml")
        .setExecutor(new SiegeCommand(manager));
  }

  private void reloadFeatures() {
    HandlerList.unregisterAll(this);
    ConfigurationSection settings = getConfig().getConfigurationSection("siege");
    if (settings == null) {
      settings = getConfig().createSection("siege");
    }
    config = new SiegeConfig(settings);
    new SiegeFeatureListener(this, config, manager).registerFeatures();
  }

  @Override
  public void reloadConfig() {
    super.reloadConfig();
    reloadFeatures();
  }

}
