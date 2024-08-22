package com.zhaoch23.stardewvalleyfishing;

import com.zhaoch23.stardewvalleyfishing.StardewValleyFishing;
import com.zhaoch23.stardewvalleyfishing.api.SVFishHook;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FishingSchemeManager {
    public final Map<String, List<SVFishHook>> schemeMap = new HashMap<>();

    private static int binarySearch(int[] cumulativeSums, int randomTicket) {
        int low = 0;
        int high = cumulativeSums.length - 1;

        while (low < high) {
            int mid = (low + high) / 2;

            if (randomTicket > cumulativeSums[mid]) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }

        return low;
    }

    public void addScheme(String schemeName, List<SVFishHook> scheme) {
        schemeMap.put(schemeName, scheme);
    }

    public void loadBiomeScheme() {
        File file = new File(StardewValleyFishing.instance.getDataFolder(), "biome/");
        if (!file.exists()) {
            file.mkdirs();
            StardewValleyFishing.instance.saveResource("biome/plains.yml", false);
        }

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
                        List<SVFishHook> fish = loadFish(config);
                        Biome biome = Biome.valueOf(name.substring(0, name.length() - 4).toUpperCase());
                        addScheme(biome.toString(), fish);
                        StardewValleyFishing.logger().info("Loaded " +
                                fish.size() + " fish from " + name);
                    } catch (Exception e) {
                        StardewValleyFishing.logger().severe(" Failed to load biome fish file " +
                                name + "\n" + e.getMessage());
                    }
                }
            }
        }
    }

    public List<SVFishHook> loadFish(FileConfiguration config) {
        List<SVFishHook> fishList = new ArrayList<>();

        for (String fishKey : config.getKeys(false)) {
            ConfigurationSection fishSection = config.getConfigurationSection(fishKey);
            fishList.add(new SVFishHook(fishSection));
        }

        return fishList;
    }

    public SVFishHook ticketPoll(Player player, String scheme) {
        List<SVFishHook> fishList = schemeMap.get(scheme);
        if (fishList == null || fishList.isEmpty()) {
            return null;
        }

        int size = fishList.size();
        int[] accumulatedSum = new int[size];

        int totalTickets = 0;

        for (int i = 0; i < size; i++) {
            totalTickets += fishList.get(i).getTickets(player);
            accumulatedSum[i] = totalTickets;
        }

        int randomTicket = StardewValleyFishing.random().nextInt() + 1;

        int itemIndex = binarySearch(accumulatedSum, randomTicket);

        return fishList.get(itemIndex);
    }
}
