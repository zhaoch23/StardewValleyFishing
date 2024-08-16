package com.zhaoch23.stardewvalleyfishing.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class StardewValleyPlayerStartFishingEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private int biteTimeMax = 300;
    private int biteTimeMin = 100;

    private boolean cancelled = false;

    public StardewValleyPlayerStartFishingEvent(Player who) {
        super(who);
    }

    public int getBiteTimeMax() {
        return biteTimeMax;
    }

    public void setBiteTimeMax(int biteTimeMax) {
        assert biteTimeMax >= 0;
        this.biteTimeMax = biteTimeMax;
    }

    public int getBiteTimeMin() {
        return biteTimeMin;
    }

    public void setBiteTimeMin(int biteTimeMin) {
        assert biteTimeMin >= 0;
        this.biteTimeMin = biteTimeMin;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
