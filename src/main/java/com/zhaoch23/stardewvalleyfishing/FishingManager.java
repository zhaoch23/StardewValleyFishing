package com.zhaoch23.stardewvalleyfishing;

import com.zhaoch23.stardewvalleyfishing.api.FishDTO;
import com.zhaoch23.stardewvalleyfishing.api.FishingRodDTO;
import com.zhaoch23.stardewvalleyfishing.api.event.StardewValleyPlayerFishingEvent;
import com.zhaoch23.stardewvalleyfishing.api.event.StardewValleyPlayerStartFishingEvent;
import com.zhaoch23.stardewvalleyfishing.common.FishingPowerUtils;
import com.zhaoch23.stardewvalleyfishing.common.StardewValleyFish;
import net.minecraft.server.v1_12_R1.EntityFishingHook;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftFish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public class FishingManager implements Listener {


    public static void setFishingTime(EntityFishingHook hook, int time) {
        // Set fishing time
        Field waitTime = null;
        Field waitTime2 = null;

        try {
            waitTime = EntityFishingHook.class.getDeclaredField("h");
            waitTime2 = EntityFishingHook.class.getDeclaredField("g");

        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
            StardewValleyFishing.logger().severe("Field not found in EntityFishingHook. " +
                    "Are you using a Forge server?");
            return;
        }

        waitTime.setAccessible(true);
        waitTime2.setAccessible(true);

        try {
            waitTime.setInt(hook, time);
            waitTime2.setInt(hook, 0);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            StardewValleyFishing.logger().severe("Failed to set fishing time in EntityFishingHook. " +
                    "Are you using a Forge server?");
        }

    }

    public void stateFishing(PlayerFishEvent event) {
        // Set waiting time
        int min = StardewValleyFishing.settings().waitingTime.min;
        int max = StardewValleyFishing.settings().waitingTime.max;
        if (StardewValleyFishing.settings().waitingTime.fishingPowerDiscount > 0) {
            int fishingPower = FishingPowerUtils.getLoreFishingPower(event.getPlayer());
            min = (int) (StardewValleyFishing.settings().waitingTime.min * (1 - fishingPower * StardewValleyFishing.settings().waitingTime.fishingPowerDiscount));
            max = (int) (StardewValleyFishing.settings().waitingTime.max * (1 - fishingPower * StardewValleyFishing.settings().waitingTime.fishingPowerDiscount));
            if (max < 0) {
                max = 0;
            }
            if (min > max) {
                min = max;
            }
        }

        StardewValleyPlayerStartFishingEvent customEvent = new StardewValleyPlayerStartFishingEvent(event.getPlayer());
        customEvent.setBiteTimeMin(min);
        customEvent.setBiteTimeMax(max);
        StardewValleyFishing.instance.getServer().getPluginManager().callEvent(customEvent);

        if (customEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }

        int waitingTime = (int) (customEvent.getBiteTimeMin() +
                Math.random() * (customEvent.getBiteTimeMax() - customEvent.getBiteTimeMin()));
        setFishingTime(((CraftFish) event.getHook()).getHandle(), waitingTime);

        if (StardewValleyFishing.settings().verbose) {
            StardewValleyFishing.logger().info("Player " +
                    event.getPlayer().getName() + " starts fishing with waiting time " + waitingTime);
        }
    }

    public void stateOnHook(PlayerFishEvent event) {

        StardewValleyPlayerFishingEvent customEvent = new StardewValleyPlayerFishingEvent(event.getPlayer(), StardewValleyPlayerFishingEvent.State.BITE);

        // Setup fishes
        if (StardewValleyFishing.instance.settings.useDefaultFishSetup) {
            List<StardewValleyFish> fishList = StardewValleyFishing.instance.defaultBiomeFishSetup.randomSelect(event.getPlayer(), 1);
            if (fishList.size() == 0) {
                StardewValleyFishing.logger().warning("No fish found for player "
                        + event.getPlayer().getName()
                        + " in biome " + event.getPlayer().getLocation().getBlock().getBiome().name());
                customEvent.setCaughtItems(Collections.singletonList(new ItemStack(Material.AIR)));
                customEvent.setFishDTO(new FishDTO());
            } else {
                StardewValleyFish fish = fishList.get(0);
                customEvent.setCaughtItems(Collections.singletonList(fish.itemStack));
                customEvent.setFishDTO(fish.fishDTO);
            }
        }

        // Setup fishing rod
        int fishingPower = FishingPowerUtils.getLoreFishingPower(event.getPlayer());
        customEvent.setFishingRodDTO(FishingPowerUtils.setupFishingDTO(fishingPower));

        StardewValleyFishing.instance.getServer().getPluginManager().callEvent(customEvent);
        event.setCancelled(true); // Pause the event
        event.getCaught().remove();

        if (StardewValleyFishing.instance.settings.verbose) {
            StardewValleyFishing.logger().info("Player " +
                    event.getPlayer().getName() + " hooks " + customEvent.getCaughtItems().size() + " items");
            StardewValleyFishing.logger().info("Player " +
                    event.getPlayer().getName() + " fishes with attributes: " + customEvent.getFishDTO().toString());
            StardewValleyFishing.logger().info("Player " +
                    event.getPlayer().getName() + " fishes with attributes: " + customEvent.getFishingRodDTO().toString());
        }

        if (customEvent.isCancelled()) {
            return;
        }

        // Set fishing time to a large number
        setFishingTime(((CraftFish) event.getHook()).getHandle(), 99999999);

        FishingScreen screen = FishingScreen.createFishingScreen(
                event.getPlayer(),
                customEvent.getCaughtItems(),
                ((CraftFish) event.getHook()).getHandle()
        );

        if (customEvent.getFishDTO() == null) {
            customEvent.setFishDTO(new FishDTO());
        }
        if (customEvent.getFishingRodDTO() == null) {
            customEvent.setFishingRodDTO(new FishingRodDTO());
        }

        screen.loadData(customEvent.getFishDTO(), customEvent.getFishingRodDTO());

        screen.openGui(event.getPlayer());

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFish(PlayerFishEvent event) {
        // Your code here
        switch (event.getState()) {
            case FISHING:
                stateFishing(event);
                break;
            case CAUGHT_FISH:
                stateOnHook(event);
                break;
            default:
                break;
        }
    }

}
