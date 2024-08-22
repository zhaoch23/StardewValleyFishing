package com.zhaoch23.stardewvalleyfishing.api.event;

import com.zhaoch23.stardewvalleyfishing.api.SVFishHook;
import com.zhaoch23.stardewvalleyfishing.api.loot.FishingLoot;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.List;
import java.util.Objects;

/**
 * Called when a fish is caught.
 * <p>
 * Setup the FishingLoot for the fish hook and the player who caught the fish.
 */
public class SVFishCaughtEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final SVFishHook fishHook;
    @Getter
    private final boolean perfect;
    private boolean cancelled = false;
    @Getter
    private List<FishingLoot> loots;

    public SVFishCaughtEvent(Player who, SVFishHook fishHook, List<FishingLoot> loots, boolean perfect) {
        super(who);
        this.fishHook = fishHook;
        this.loots = loots;
        this.perfect = perfect;
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
        cancelled = b;
    }

    /**
     * Sets the list of FishingLoot for the event.
     *
     * @param loots the list of FishingLoot to set, must not be null
     * @throws NullPointerException if the loots parameter is null
     */
    public void setLoots(List<FishingLoot> loots) {
        Objects.requireNonNull(loots);
        this.loots = loots;
    }

}
