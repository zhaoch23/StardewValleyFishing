package com.zhaoch23.stardewvalleyfishing.api.event;

import com.zhaoch23.stardewvalleyfishing.api.FishDTO;
import com.zhaoch23.stardewvalleyfishing.api.FishingRodDTO;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StardewValleyPlayerFishingEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final List<Entity> caughtEntities = new ArrayList<>(); // TODO 240816

    private final List<ItemStack> caughtItems = new ArrayList<>();

    private final State state;

    private FishingRodDTO fishingRodDTO;
    private FishDTO fishDTO;

    private boolean cancelled = false;


    public StardewValleyPlayerFishingEvent(Player who, State state) {
        super(who);
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public FishDTO getFishDTO() {
        return fishDTO;
    }

    public void setFishDTO(FishDTO fishDTO) {
        this.fishDTO = fishDTO;
    }

    public FishingRodDTO getFishingRodDTO() {
        return fishingRodDTO;
    }

    public void setFishingRodDTO(FishingRodDTO fishingRodDTO) {
        this.fishingRodDTO = fishingRodDTO;
    }

    public List<Entity> getCaughtEntities() {
        return caughtEntities;
    }

    public void setCaughtEntities(List<Entity> caughtEntities) {
        Objects.requireNonNull(caughtEntities, "caughtEntities cannot be null");
        this.caughtEntities.clear();
        this.caughtEntities.addAll(caughtEntities);
    }

    public List<ItemStack> getCaughtItems() {
        return caughtItems;
    }

    public void setCaughtItems(List<ItemStack> caughtItems) {
        Objects.requireNonNull(caughtItems, "caughtItems cannot be null");
        this.caughtItems.clear();
        this.caughtItems.addAll(caughtItems);
    }


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public enum State {
        BITE,
        CAUGHT_FISH,
        FAILED_ATTEMPT
    }
}
