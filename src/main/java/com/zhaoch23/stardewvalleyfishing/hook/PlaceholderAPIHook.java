package com.zhaoch23.stardewvalleyfishing.hook;

import com.zhaoch23.stardewvalleyfishing.StardewValleyFishing;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "svfishing";
    }

    @Override
    public @NotNull String getAuthor() {
        return "zhaoch23";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equalsIgnoreCase("is_raining")){
            return player.getPlayer().getWorld().hasStorm() ? "1" : "0";
        }

        if (params.equalsIgnoreCase("test_fishing_power")) {
            return "50";
        }

        StardewValleyFishing.logger().warning("Unknown placeholder: " + params);

        return ""; // Placeholder is unknown by the expansion
    }
}
