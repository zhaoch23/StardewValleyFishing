package com.zhaoch23.stardewvalleyfishing;

import com.germ.germplugin.api.dynamic.gui.GermGuiScreen;
import com.germ.germplugin.api.dynamic.gui.GuiManager;
import com.germ.germplugin.api.dynamic.gui.IGuiScreenHandler;
import com.zhaoch23.stardewvalleyfishing.api.FishDTO;
import com.zhaoch23.stardewvalleyfishing.api.FishingRodDTO;
import com.zhaoch23.stardewvalleyfishing.api.event.StardewValleyPlayerFishingEvent;
import net.minecraft.server.v1_12_R1.EntityFishingHook;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class FishingScreen extends GermGuiScreen {

    public static ConfigurationSection germConfiguration;

    public final List<ItemStack> fishes;

    public final EntityFishingHook hook;

    private final Map<String, BiConsumer<Map<String, Object>, Map<String, Object>>> callbackMap = new HashMap<>();

    Long startTime = System.currentTimeMillis();

    public FishingScreen(String title,
                         ConfigurationSection configurationSection,
                         List<ItemStack> fishes,
                         EntityFishingHook hook) {
        super(title, configurationSection);
        this.fishes = fishes;
        this.hook = hook;

        callbackMap.put("caughtFish", this::caughtFish);
        callbackMap.put("attemptFailed", this::attemptFailed);

        this.setClosedHandler(new IGuiScreenHandler() {
            @Override
            public void handle(Player player, GermGuiScreen germGuiScreen) {
                if (hook.isAlive()) {
                    hook.j(); // Pull the hook back
                }
            }
        });
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

        Map<String, Object> dataMap = getOptions().getDataMap();

        if (dataMap.containsKey("fishing_bar_height")) {
            data.put("fishing_bar_height", dataMap.get("fishing_bar_height"));
        } else {
            StardewValleyFishing.logger().warning("Missing fishing_bar_height in germ configuration, " +
                    "using default value 20");
            data.put("fishing_bar_height", 20);
        }

        if (dataMap.containsKey("progress_bar_offset")) {
            data.put("progress_bar_offset", dataMap.get("progress_bar_offset"));
        } else {
            StardewValleyFishing.logger().warning("Missing progress_bar_offset in germ configuration, " +
                    "using default value 0");
            data.put("progress_bar_offset", 0);
        }

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
        boolean perfect = contentMap.get("perfect") != null && (boolean) contentMap.get("perfect");

        StardewValleyPlayerFishingEvent customEvent =
                new StardewValleyPlayerFishingEvent(getPlayer(), StardewValleyPlayerFishingEvent.State.CAUGHT_FISH);
        customEvent.setCaughtItems(fishes);
        customEvent.setPerfect(perfect);

        Bukkit.getPluginManager().callEvent(customEvent);

        if (customEvent.isCancelled()) {
            close();
            return;
        }

        if (StardewValleyFishing.settings().verbose) {
            StardewValleyFishing.logger().info("Player " +
                    getPlayer().getName() + " caught " + customEvent.getCaughtItems().size() +
                    " items with perfect " + perfect + " status after " + (System.currentTimeMillis() - startTime) + "ms");
        }

        fishes.clear();
        fishes.addAll(customEvent.getCaughtItems());


        Bukkit.getScheduler().runTask(StardewValleyFishing.instance, () -> {
            FishingManager.setFishingTime(hook, 999); // Set and value and pull the rod

            hook.j();

            for (ItemStack fish : fishes) {
                getPlayer().getInventory().addItem(fish);
            }

            close();
        });
    }

    public void attemptFailed(Map<String, Object> contentMap, Map<String, Object> responseMap) {
        if (StardewValleyFishing.settings().verbose) {
            StardewValleyFishing.logger().info("Player " +
                    getPlayer().getName() + " failed to catch fish after " +
                    (System.currentTimeMillis() - startTime) + "ms");
        }

        StardewValleyPlayerFishingEvent customEvent =
                new StardewValleyPlayerFishingEvent(getPlayer(), StardewValleyPlayerFishingEvent.State.FAILED_ATTEMPT);
        customEvent.setCaughtItems(fishes);

        Bukkit.getPluginManager().callEvent(customEvent);

        close();
    }

}
