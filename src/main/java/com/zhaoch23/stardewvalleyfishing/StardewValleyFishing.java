package com.zhaoch23.stardewvalleyfishing;

import com.germ.germplugin.api.GermSrcManager;
import com.germ.germplugin.api.RootType;
import com.zhaoch23.stardewvalleyfishing.hook.PlaceholderAPIHook;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Random;

public final class StardewValleyFishing extends JavaPlugin implements CommandExecutor {

    public static StardewValleyFishing instance;
    public final Settings settings = new Settings();

    public final FishingSchemeManager fishingSchemeManager = new FishingSchemeManager();
    public final Random random = new Random();
    public FishingStateManager fishingManager = new FishingStateManager();

    public static java.util.logging.Logger logger() {
        return instance.getLogger();
    }

    public static Settings settings() {
        return instance.settings;
    }

    public static Random random() {
        return instance.random;
    }

    @Override
    public void onEnable() {
        instance = this;

        getServer().getPluginManager().registerEvents(fishingManager, this);

        // Register command
        getCommand("stardewvalleyfishing").setExecutor(this);

        loadConfiguration();

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook().register();
        } else {
            getLogger().warning("PlaceholderAPI not found, some features may not work");
        }
    }

    public static FishingSchemeManager getFishingSchemeManager() {
        return instance.fishingSchemeManager;
    }

    public void loadConfiguration() {
        // Load configuration
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }
        reloadConfig();

        settings.loadConfig(getConfig().getConfigurationSection("stardew-valley-fishing"));

        File jexl = new File(getDataFolder(), "jexl");
        jexl.mkdirs();
        GermSrcManager.getGermSrcManager().registerSrcFolder(RootType.JEXL, jexl);
        saveResource("jexl/fishing.js", true);
        saveResource("fishing.yml", false);
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "fishing.yml"));
        FishingScreen.setGermConfiguration(
                yamlConfiguration.getConfigurationSection("fishing-screen")
        );


        fishingSchemeManager.loadBiomeScheme();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            loadConfiguration();
            if (sender != null) {
                sender.sendMessage("Reloaded configuration");
            }
            return true;
        } else {
            return false;
        }
    }
}
