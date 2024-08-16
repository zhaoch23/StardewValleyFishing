package com.zhaoch23.stardewvalleyfishing.common;

import com.zhaoch23.stardewvalleyfishing.StardewValleyFishing;
import com.zhaoch23.stardewvalleyfishing.api.FishDTO;
import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultBiomeFishSetup {

    public final Map<Biome, List<StardewValleyFish>> biomeFishMap = new HashMap<>();

    public void loadBiomeFishMap() {
        File file = new File(StardewValleyFishing.instance.getDataFolder(), "biome/");
        if (!file.exists())
            file.mkdirs();

        // Load all files
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }

        for (File f : files) {
            if (f.isFile()) {
                String name = f.getName();
                if (name.endsWith(".yml")) {
                    // Load biome fish
                    try {
                        FileConfiguration config = YamlConfiguration.loadConfiguration(f);
                        List<StardewValleyFish> fish = loadFish(config);
                        biomeFishMap.put(Biome.valueOf(name.substring(0, name.length() - 4)), fish);
                        Bukkit.getLogger().info("[StardewValleyFishing] Loaded " + fish.size() + " fish from " + name);
                    } catch (Exception e) {
                        Bukkit.getLogger().severe("[StardewValleyFishing] Failed to load biome fish file " + name + "\n" + e.getMessage());
                    }
                }
            }
        }
    }

    public List<StardewValleyFish> loadFish(FileConfiguration config) {
        List<StardewValleyFish> fishList = new ArrayList<>();
        for (String fishKey : config.getKeys(false)) {
            ConfigurationSection fishSection = config.getConfigurationSection(fishKey);
            StardewValleyFish fish = new StardewValleyFish();
            if (fishSection.contains("itemstack")) {
                fish.itemStack = fishSection.getItemStack("itemstack");

            } else if (fishSection.contains("mythicitem")) {
                String mmItems = fishSection.getString("mythicitem");
                ItemStack itemStack = MythicMobs.inst().getItemManager().getItemStack(mmItems);
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    fish.itemStack = itemStack;
                }
            } else {
                Bukkit.getLogger().warning("[StardewValleyFishing] Fish information not found for " + fishKey);
                continue;
            }

            if (fish.itemStack == null || fish.itemStack.getType() == Material.AIR) {
                Bukkit.getLogger().warning("[StardewValleyFishing] Fish is null for " + fishKey);
            }

            if (fishSection.contains("difficulty")) {
                fish.fishDTO = new FishDTO(fishSection.getConfigurationSection("difficulty"));
            } else {
                fish.fishDTO = new FishDTO();
            }

        }

        return fishList;
    }

    public List<StardewValleyFish> randomSelect(Player player, int num) {

        List<StardewValleyFish> fishList = new ArrayList<>();
        if (biomeFishMap.get(player.getLocation().getBlock().getBiome()) != null) {
            List<StardewValleyFish> fish = biomeFishMap.get(player.getLocation().getBlock().getBiome());
            for (int i = 0; i < num; i++) {
                fishList.add(fish.get((int) (Math.random() * fish.size())));
            }
        }
        return fishList;
    }

}
