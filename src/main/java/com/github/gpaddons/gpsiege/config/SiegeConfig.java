package com.github.gpaddons.gpsiege.config;

import com.github.jikoo.planarwrappers.config.impl.BooleanSetting;
import com.github.jikoo.planarwrappers.config.impl.MaterialSetSetting;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class SiegeConfig {

  private BooleanSetting siegeEnabled;
  private BooleanSetting siegeBlocksIsWhitelist;
  private MaterialSetSetting siegeBlocks;
  private boolean isIncludeAllDefenderClaims;
  private int includeNearbyClaimsDefenderRadius;
  private int includeNearbyClaimsAttackerRadius;

  public SiegeConfig(@NotNull ConfigurationSection settings) {
    reload(settings);
  }

  public void reload(@NotNull ConfigurationSection settings) {
    siegeEnabled = new BooleanSetting(settings, "enabled", false);

    siegeBlocksIsWhitelist = new BooleanSetting(settings, "post-siege_blocks_is_whitelist", true);
    Set<Material> defaultSiegeBlocks = new HashSet<>();
    defaultSiegeBlocks.add(Material.DIRT);
    defaultSiegeBlocks.add(Material.GRASS_BLOCK);
    defaultSiegeBlocks.add(Material.COBBLESTONE);
    defaultSiegeBlocks.add(Material.COBBLED_DEEPSLATE);
    defaultSiegeBlocks.add(Material.GRAVEL);
    defaultSiegeBlocks.add(Material.SAND);
    defaultSiegeBlocks.add(Material.SNOW_BLOCK);
    defaultSiegeBlocks.addAll(Tag.PLANKS.getValues());
    defaultSiegeBlocks.addAll(Tag.LOGS.getValues());
    defaultSiegeBlocks.addAll(Tag.WOOL.getValues());
    defaultSiegeBlocks.addAll(Tag.WOOL_CARPETS.getValues());
    defaultSiegeBlocks.add(Material.GLASS);
    for (Material material : Material.values()) {
      if (material.name().endsWith("STAINED_GLASS") || material.name().endsWith("GLASS_PANE")) {
        defaultSiegeBlocks.add(material);
      }
    }
    siegeBlocks = new MaterialSetSetting(settings, "post-siege_blocks", defaultSiegeBlocks);

    isIncludeAllDefenderClaims = settings.getBoolean("features.include_all_defender_claims");
    includeNearbyClaimsDefenderRadius = settings.getInt("features.include_nearby_claims.defender_radius", -1);
    includeNearbyClaimsAttackerRadius = settings.getInt("features.include_nearby_claims.attacker_radius", -1);
  }

  public boolean siegeEnabled(@NotNull World world) {
    return siegeEnabled.get(world.getName());
  }

  public boolean siegeBlocksIsWhitelist(@NotNull World world) {
    return siegeBlocksIsWhitelist.get(world.getName());
  }

  public Set<Material> siegeBlocks(@NotNull World world) {
    return siegeBlocks.get(world.getName());
  }

  public boolean isIncludeAllDefenderClaims() {
    return isIncludeAllDefenderClaims;
  }

  public int getIncludeNearbyClaimsDefenderRadius() {
    return includeNearbyClaimsDefenderRadius;
  }

  public int getIncludeNearbyClaimsAttackerRadius() {
    return includeNearbyClaimsAttackerRadius;
  }

  // Return to battle boss bar stuff
  public String getBarTitle() {
    // TODO
    return "Return to battle!";
  }

  public int getReturnToBattleTicks() {
    return 20 * 30; // TODO 30 seconds
  }

  // TODO startTitle, startSubtitle, startActionBar, etc.
  // These may need to be in a lang file instead, translation system, yada yada

}
