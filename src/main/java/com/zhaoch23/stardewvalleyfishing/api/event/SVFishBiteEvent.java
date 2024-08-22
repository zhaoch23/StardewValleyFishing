package com.zhaoch23.stardewvalleyfishing.api.event;

import com.zhaoch23.stardewvalleyfishing.api.data.FishAI;
import com.zhaoch23.stardewvalleyfishing.api.data.FishingRod;
import com.zhaoch23.stardewvalleyfishing.api.SVFishHook;
import com.zhaoch23.stardewvalleyfishing.api.data.ScreenDos;
import com.zhaoch23.stardewvalleyfishing.api.loot.FishingLootTable;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Called when a fish bites the hook.
 * <p>
 * Setup the Fish AI and FishingLootTable for the fish hook and
 * the ScreenDos and FishingRod properties for the player.
 */
public class SVFishBiteEvent extends PlayerEvent implements Cancellable {

    public static final HandlerList handlers = new HandlerList();
    @Getter
    private final ScreenDos screenDos;
    @Getter
    private final SVFishHook fishHook;
    @Getter
    private final FishingRod fishingRod;
    private boolean cancelled = false;

    public SVFishBiteEvent(Player who, ScreenDos screenDos, SVFishHook fishHook, FishingRod fishingRod) {
        super(who);
        this.screenDos = screenDos;
        this.fishHook = fishHook;
        this.fishingRod = fishingRod;
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
     * Gets the FishAI associated with the fish hook.
     *
     * @return the FishAI of the fish hook
     */
    public FishAI getFishAI() {
        return fishHook.getFishAI();
    }

    /**
     * Sets the FishAI for the fish hook.
     *
     * @param fishAI the FishAI to set
     */
    public void setFishAI(FishAI fishAI) {
        fishHook.setFishAI(fishAI);
    }

    /**
     * Gets the FishingLootTable associated with the fish hook.
     *
     * @return the FishingLootTable of the fish hook
     */
    public FishingLootTable getFishingLootTable() {
        return fishHook.getFishingLoots();
    }

    /**
     * Sets the FishingLootTable for the fish hook.
     *
     * @param fishingLootTable the FishingLootTable to set
     */
    public void setFishingLootTable(FishingLootTable fishingLootTable) {
        fishHook.setFishingLoots(fishingLootTable);
    }
}
