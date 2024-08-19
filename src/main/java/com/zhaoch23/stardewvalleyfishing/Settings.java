package com.zhaoch23.stardewvalleyfishing;

import com.zhaoch23.stardewvalleyfishing.api.ScreenDos;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;

public class Settings {

    public ScreenDos screenDos;
    public final WaitingTime waitingTime = new WaitingTime();
    public String fishingPower = "lore";
    public boolean useDefaultFishSetup = true;
    public boolean verbose = false;

    public void loadConfig(ConfigurationSection configurationSection) {
        fishingPower = configurationSection.getString("fishing-power", "lore");
        useDefaultFishSetup = configurationSection.getBoolean("use-default-fish-setup", true);
        verbose = configurationSection.getBoolean("verbose", true);

        waitingTime.loadConfig(configurationSection.getConfigurationSection("waiting-time"));

        ConfigurationSection screenDosSection = configurationSection.getConfigurationSection("screen-dos");
        screenDos = new ScreenDos(
                screenDosSection.contains("screen-open") ?
                        screenDosSection.getStringList("screen-open") : new ArrayList<>(),
                screenDosSection.contains("screen-close") ?
                        screenDosSection.getStringList("screen-close") : new ArrayList<>(),
                screenDosSection.contains("releasing-rod") ?
                        screenDosSection.getStringList("releasing-rod") : new ArrayList<>(),
                screenDosSection.contains("hooking-fish") ?
                        screenDosSection.getStringList("hooking-fish") : new ArrayList<>(),
                screenDosSection.contains("fish-caught") ?
                        screenDosSection.getStringList("fish-caught") : new ArrayList<>(),
                screenDosSection.contains("perfect-fish-caught") ?
                        screenDosSection.getStringList("perfect-fish-caught") : new ArrayList<>(),
                screenDosSection.contains("fish-escaped") ?
                        screenDosSection.getStringList("fish-escaped") : new ArrayList<>()
        );
    }

    public static class WaitingTime {
        public int min = 100;
        public int max = 400;
        public double fishingPowerDiscount = 0.0;

        public void loadConfig(ConfigurationSection configurationSection) {
            min = configurationSection.getInt("min", 100);
            max = configurationSection.getInt("max", 400);
            fishingPowerDiscount = configurationSection.getDouble("fishing-power-discount", 0.0);
            if (min > max) {
                StardewValleyFishing.logger().severe("Invalid waiting time configuration: min > max");
                min = max;
            }
            if (fishingPowerDiscount < 0 || fishingPowerDiscount > 1) {
                StardewValleyFishing.logger().severe("Invalid fishing power discount: 0 <= discount <= 1");
                fishingPowerDiscount = 0;
            }
        }
    }

}
