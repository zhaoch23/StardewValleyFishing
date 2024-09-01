package com.zhaoch23.stardewvalleyfishing.gui;

import com.germ.germplugin.api.dynamic.gui.GermGuiCanvas;
import com.germ.germplugin.api.dynamic.gui.GermGuiScreen;
import com.germ.germplugin.api.dynamic.gui.GermGuiSlot;
import com.germ.germplugin.api.dynamic.gui.GuiManager;
import com.zhaoch23.stardewvalleyfishing.FishingRodTacklesManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TackleScreen extends GermGuiScreen {

    public static ConfigurationSection screenConfiguration;

    public GermGuiSlot tackleSlot, baitSlot;

    public final FishingRodTacklesManager tackleManager;

    public static void setScreenConfiguration(ConfigurationSection configurationSection) {
        screenConfiguration = configurationSection;
    }

    public static TackleScreen createTackleScreen(Player player) {
        String title = getTitle(player);
        if (GuiManager.isOpenedGui(player, title)) {
            GuiManager.getOpenedGui(player, title).close();
        }

        return new TackleScreen(title, screenConfiguration);
    }

    public static String getTitle(Player player) {
        return "tackle-screen-" + player.getUniqueId();
    }

    protected TackleScreen(String guiName, ConfigurationSection srcSection,
                           FishingRodTacklesManager tackleManager) {
        super(guiName, srcSection);

        this.tackleManager = tackleManager;

        GermGuiCanvas canvas = (GermGuiCanvas) getGuiPart("canvas");
        tackleSlot = (GermGuiSlot) canvas.getGuiPart("_slot_tackle");
        baitSlot = (GermGuiSlot) canvas.getGuiPart("_slot_bait");


        setClosedHandler((player, screen) -> {
            ItemStack tackle = tackleSlot.getItemStack();
        });
    }


}
