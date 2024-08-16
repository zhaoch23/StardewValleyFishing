package com.zhaoch23.stardewvalleyfishing;

import com.germ.germplugin.api.dynamic.gui.GermGuiScreen;
import com.germ.germplugin.api.dynamic.gui.IGuiScreenHandler;
import com.zhaoch23.stardewvalleyfishing.api.FishDTO;
import com.zhaoch23.stardewvalleyfishing.api.FishingRodDTO;
import com.zhaoch23.stardewvalleyfishing.api.event.StardewValleyPlayerFishingEvent;
import com.zhaoch23.stardewvalleyfishing.api.event.StardewValleyPlayerStartFishingEvent;
import com.zhaoch23.stardewvalleyfishing.common.DefaultPlayerFishingPower;
import com.zhaoch23.stardewvalleyfishing.common.StardewValleyFish;
import net.minecraft.server.v1_12_R1.EntityFishingHook;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftFish;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FishingManager implements Listener {


    public void setFishingTime(EntityFishingHook hook, int time) {
        // Set fishing time

        Field waitTime = null;

        try {
            waitTime = EntityFishingHook.class.getDeclaredField("h");

        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }

        assert waitTime != null;
        waitTime.setAccessible(true);

        try {
            waitTime.setInt(hook, 100);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void stateFishing(PlayerFishEvent event) {
        StardewValleyPlayerStartFishingEvent customEvent = new StardewValleyPlayerStartFishingEvent(event.getPlayer());
        StardewValleyFishing.instance.getServer().getPluginManager().callEvent(customEvent);

        if (customEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }


        setFishingTime(((CraftFish) event.getHook()).getHandle(),
                (int) (customEvent.getBiteTimeMin() +
                        Math.random() * (customEvent.getBiteTimeMax() - customEvent.getBiteTimeMin()))
        );
    }

    public void stateOnHook(PlayerFishEvent event) {

        StardewValleyPlayerFishingEvent customEvent = new StardewValleyPlayerFishingEvent(event.getPlayer(), StardewValleyPlayerFishingEvent.State.BITE);

        // Setup fishes
        if (StardewValleyFishing.instance.settings.useDefaultFishSetup) {
            StardewValleyFish fish = StardewValleyFishing.instance.defaultBiomeFishSetup.randomSelect(event.getPlayer(), 1).get(0);
            customEvent.setCaughtItems(Collections.singletonList(fish.itemStack));
            customEvent.setFishDTO(fish.fishDTO);
        }

        // Setup fishing rod
        if (StardewValleyFishing.instance.settings.useDefaultFishingPower) {
            PlayerInventory playerInventory = event.getPlayer().getInventory();
            ItemStack item = playerInventory.getItemInMainHand();
            if (item.getType() != Material.FISHING_ROD) {
                item = playerInventory.getItemInOffHand();
            }
            FishingRodDTO fishingRodDTO = DefaultPlayerFishingPower.getDefaultFishingRodDTO(item);
            customEvent.setFishingRodDTO(fishingRodDTO);
        }

        StardewValleyFishing.instance.getServer().getPluginManager().callEvent(customEvent);
        event.setCancelled(true); // Pause the event
        event.getCaught().remove();

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

    public void stateCaught(PlayerFishEvent event) {
        // Your code here

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFish(PlayerFishEvent event) {
        // Your code here
        switch (event.getState()) {
            case FISHING:
                stateFishing(event);
                break;
            case CAUGHT_FISH:
                stateCaught(event);
                break;
            default:
                break;
        }
    }

    public enum State {
        WAITING,
        ON_HOOK
    }

}
