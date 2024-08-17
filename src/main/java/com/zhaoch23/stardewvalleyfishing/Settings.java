package com.zhaoch23.stardewvalleyfishing;

import org.bukkit.configuration.ConfigurationSection;

public class Settings {

    public boolean useDefaultFishingPower = true;
    public boolean useDefaultFishSetup = true;
    public boolean verbose = false;


    public void loadConfig(ConfigurationSection configurationSection) {
        useDefaultFishingPower = configurationSection.getBoolean("useDefaultFishingPower", true);
        useDefaultFishSetup = configurationSection.getBoolean("useDefaultFishSetup", true);
        verbose = configurationSection.getBoolean("verbose", true);
    }
}
