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

public final class StardewValleyFishing extends JavaPlugin implements CommandExecutor {

    public static StardewValleyFishing instance;
    public final Settings settings = new Settings();

    public final DefaultBiomeFishSetup defaultBiomeFishSetup = new DefaultBiomeFishSetup();
    public FishingManager fishingManager = new FishingManager();

    public static java.util.logging.Logger logger() {
        return instance.getLogger();
    }

    public static Settings settings() {
        return instance.settings;
    }

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
        saveConfig();

        settings.loadConfig(getConfig().getConfigurationSection("stardew-valley-fishing"));


        File jexl = new File(getDataFolder(), "jexl");
        jexl.mkdirs();
        GermSrcManager.getGermSrcManager().registerSrcFolder(RootType.JEXL, jexl);
        saveResource("jexl/fishing.js", false);
        saveResource("fishing.yml", false);
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "fishing.yml"));
        FishingScreen.setGermConfiguration(
                yamlConfiguration.getConfigurationSection("fishing-screen")
        );


        defaultBiomeFishSetup.loadBiomeFishMap();
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
