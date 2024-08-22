package com.zhaoch23.stardewvalleyfishing;

import com.zhaoch23.stardewvalleyfishing.api.SVFishHook;
import com.zhaoch23.stardewvalleyfishing.api.event.SVFishBiteEvent;
import com.zhaoch23.stardewvalleyfishing.api.event.SVStartFishingEvent;
import net.minecraft.server.v1_12_R1.EntityFishingHook;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftFish;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.lang.reflect.Field;

public class FishingStateManager implements Listener {


    public static void setFishingTime(EntityFishingHook hook, int time) {
        // Set fishing time
        Field waitTime = null;
        Field waitTime2 = null;

        try {
            waitTime = EntityFishingHook.class.getDeclaredField("h");
            waitTime2 = EntityFishingHook.class.getDeclaredField("g");

        } catch (NoSuchFieldException | SecurityException e) {
            StardewValleyFishing.logger().severe("Field not found in EntityFishingHook. " +
                    "Are you using a Forge server?\n" + e);
            return;
        }

        waitTime.setAccessible(true);
        waitTime2.setAccessible(true);

        try {
            waitTime.setInt(hook, time);
            waitTime2.setInt(hook, 0);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            StardewValleyFishing.logger().severe("Failed to set fishing time in EntityFishingHook. " +
                    "Are you using a Forge server?\n" + e);
        }

    }

    public void stateFishing(PlayerFishEvent event) {
        // Set waiting time
        int min = StardewValleyFishing.settings().waitingTime.min;
        int max = StardewValleyFishing.settings().waitingTime.max;
        if (StardewValleyFishing.settings().waitingTime.fishingPowerDiscount > 0) {
            int fishingPower = FishingPowerUtils.getFishingPower(event.getPlayer());
            min = (int) (StardewValleyFishing.settings().waitingTime.min * (1 - fishingPower * StardewValleyFishing.settings().waitingTime.fishingPowerDiscount));
            max = (int) (StardewValleyFishing.settings().waitingTime.max * (1 - fishingPower * StardewValleyFishing.settings().waitingTime.fishingPowerDiscount));
            if (max < 0) {
                max = 0;
            }
            if (min > max) {
                min = max;
            }
        }

        SVStartFishingEvent customEvent = new SVStartFishingEvent(event.getPlayer());
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

    /**
     * Player hooks a fish
     *
     * @param event PlayerFishEvent
     */
    public void stateOnHook(PlayerFishEvent event) {
        Player player = event.getPlayer();

        SVFishHook fishHook;
        // Setup fishes
        if (StardewValleyFishing.settings().useBiomeSchemeSetup) {
            // Get the biome of the hook entity
            Biome biome = event.getHook().getLocation().getBlock().getBiome();
            fishHook = StardewValleyFishing.getFishingSchemeManager().ticketPoll(
                    player,
                    biome.toString());

            // If no fish found, use default
            if (fishHook == null) {
                StardewValleyFishing.logger().warning("No fish found in biome " + biome);
                fishHook = new SVFishHook();
            } else {
                if (StardewValleyFishing.settings().verbose)
                    StardewValleyFishing.logger().info("Player " +
                            player.getName() + " hooks a fish in biome " + biome);
                fishHook = fishHook.clone(); // Clone to prevent modification
            }
        } else {
            fishHook = new SVFishHook();
        }

        // Setup fishing rod
        int fishingPower = FishingPowerUtils.getFishingPower(event.getPlayer());

        SVFishBiteEvent customEvent = new SVFishBiteEvent(
                player,
                StardewValleyFishing.settings().screenDos,
                fishHook,
                FishingPowerUtils.setupFishingDTO(fishingPower)
        );

        event.setCancelled(true); // Pause the event
        event.getCaught().remove();

        StardewValleyFishing.instance.getServer().getPluginManager().callEvent(customEvent);

        if (customEvent.isCancelled()) {
            return;
        }
        if (StardewValleyFishing.settings().verbose) {
            StardewValleyFishing.logger().info("Player " +
                    player.getName() + " fishes with attributes: " + customEvent.getFishingRod());
            StardewValleyFishing.logger().info("Player " +
                    player.getName() + " fishes with fish: " + customEvent.getFishHook());
        }

        // Set fishing time to a large number
        setFishingTime(((CraftFish) event.getHook()).getHandle(), 99999999);

        FishingScreen screen = FishingScreen.createFishingScreen(
                event.getPlayer(),
                fishHook,
                ((CraftFish) event.getHook()).getHandle()
        );

        // Load data
        screen.loadData(customEvent.getFishAI(), customEvent.getFishingRod(), customEvent.getScreenDos());
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
