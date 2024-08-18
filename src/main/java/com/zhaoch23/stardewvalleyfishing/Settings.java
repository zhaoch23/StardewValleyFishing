package com.zhaoch23.stardewvalleyfishing;

import org.bukkit.configuration.ConfigurationSection;

public class Settings {

    public final WaitingTime waitingTime = new WaitingTime();
    public String fishingPower = "lore";
    public boolean useDefaultFishSetup = true;
    public boolean verbose = false;

    public void loadConfig(ConfigurationSection configurationSection) {
        fishingPower = configurationSection.getString("fishing-power", "lore");
        useDefaultFishSetup = configurationSection.getBoolean("use-default-fish-setup", true);
        verbose = configurationSection.getBoolean("verbose", true);

        waitingTime.loadConfig(configurationSection.getConfigurationSection("waiting-time"));
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
