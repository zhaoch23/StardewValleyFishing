package com.zhaoch23.stardewvalleyfishing.api.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class SVStartFishingEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    @Getter
    private int biteTimeMax = 300;
    @Getter
    private int biteTimeMin = 100;

    private boolean cancelled = false;

    public SVStartFishingEvent(Player who) {
        super(who);
    }

    public void setBiteTimeMax(int biteTimeMax) {
        assert biteTimeMax >= 0;
        this.biteTimeMax = biteTimeMax;
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
