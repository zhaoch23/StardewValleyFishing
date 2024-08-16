package com.zhaoch23.stardewvalleyfishing;

import com.germ.germplugin.api.GermSrcManager;
import com.germ.germplugin.api.RootType;
import com.zhaoch23.stardewvalleyfishing.common.DefaultBiomeFishSetup;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class StardewValleyFishing extends JavaPlugin implements CommandExecutor {

    public static StardewValleyFishing instance;
    public final Settings settings = new Settings();

    public final DefaultBiomeFishSetup defaultBiomeFishSetup = new DefaultBiomeFishSetup();
    public FishingManager fishingManager = new FishingManager();

    @Override
    public void onEnable() {
        instance = this;

        getServer().getPluginManager().registerEvents(fishingManager, this);

        // Register command
        getCommand("stardewvalleyfishing").setExecutor(this);

        loadConfiguration();
    }


    public void loadConfiguration() {
        // Load configuration
        saveResource("fish.yml", false);
        saveResource("fish.js", false);

        saveConfig();

        FishingScreen.setGermConfiguration(
                YamlConfiguration.loadConfiguration(new File(getDataFolder(), "fish.yml"))
        );

        settings.loadConfig(getConfig().getConfigurationSection("stardew-valley-fishing"));

        GermSrcManager.getGermSrcManager().registerSrcFolder(RootType.JEXL, getDataFolder());

        defaultBiomeFishSetup.loadBiomeFishMap();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Objects.equals(label, "reload")) {
            loadConfiguration();
            return true;
        } else {
            return false;
        }
    }
}
