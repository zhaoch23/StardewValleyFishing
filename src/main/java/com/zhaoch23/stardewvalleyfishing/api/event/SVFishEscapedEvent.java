package com.zhaoch23.stardewvalleyfishing.api.event;

import com.zhaoch23.stardewvalleyfishing.api.SVFishHook;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Called when a fish escaped.
 */
@Getter
public class SVFishEscapedEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final SVFishHook fishHook;

    public SVFishEscapedEvent(Player who, SVFishHook fishHook) {
        super(who);
        this.fishHook = fishHook;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }


}
