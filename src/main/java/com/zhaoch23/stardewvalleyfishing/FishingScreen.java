package com.zhaoch23.stardewvalleyfishing;

import com.germ.germplugin.api.dynamic.gui.GermGuiScreen;
import com.germ.germplugin.api.dynamic.gui.GuiManager;
import com.zhaoch23.stardewvalleyfishing.api.FishDTO;
import com.zhaoch23.stardewvalleyfishing.api.FishingRodDTO;
import net.minecraft.server.v1_12_R1.EntityFishingHook;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class FishingScreen extends GermGuiScreen {

    public static ConfigurationSection germConfiguration;

    public final List<ItemStack> fishes;

    public final EntityFishingHook hook;

    private final Map<String, BiConsumer<Map<String, Object>, Map<String, Object>>> callbackMap = new HashMap<>();

    public FishingScreen(String title,
                         ConfigurationSection configurationSection,
                         List<ItemStack> fishes,
                         EntityFishingHook hook) {
        super(title, configurationSection);
        this.fishes = fishes;
        this.hook = hook;

        callbackMap.put("caughtFish", this::caughtFish);
        callbackMap.put("attemptFailed", this::attemptFailed);
    }

    public static FishingScreen createFishingScreen(Player player, List<ItemStack> fishes, EntityFishingHook hook) {
        // Show the fishing screen
        String title = getTitle(player);
        if (GuiManager.isOpenedGui(player, title)) {
            GuiManager.getOpenedGui(player, title).close();
        }

        return new FishingScreen(title, germConfiguration, fishes, hook);
    }

    public static void setGermConfiguration(ConfigurationSection configurationSection) {
        germConfiguration = configurationSection;
    }

    public static String getTitle(Player player) {
        return "Fishing-Screen-" + player.getUniqueId();
    }

    public void loadData(FishDTO fishDTO, FishingRodDTO fishingRodDTO) {
        Map<String, Object> data = new HashMap<>();

        data.put("fishDTO", fishDTO);
        data.put("fishingRodDTO", fishingRodDTO);

        getOptions().setData(data);
    }

    public void onReceivePost(String path, Map<String, Object> contentMap, Map<String, Object> responseMap) {
        if (callbackMap.containsKey(path)) {
            callbackMap.get(path).accept(contentMap, responseMap);
        }
        super.onReceivePost(path, contentMap, responseMap);
    }

    public void caughtFish(Map<String, Object> contentMap, Map<String, Object> responseMap) {
        responseMap.put("message", "You caught a fish!");


        Bukkit.getScheduler().runTask(StardewValleyFishing.instance, () -> {
            Field pendingTime = null;

            try {
                pendingTime = EntityFishingHook.class.getDeclaredField("g");

            } catch (NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }

            assert pendingTime != null;
            pendingTime.setAccessible(true);

            try {
                pendingTime.setInt(hook, 100);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }

            hook.j();

            for (ItemStack fish : fishes) {
                getPlayer().getInventory().addItem(fish);
            }

            close();
        });
    }

    public void attemptFailed(Map<String, Object> contentMap, Map<String, Object> responseMap) {
        responseMap.put("message", "You failed to catch a fish!");
    }

}
