package com.zhaoch23.stardewvalleyfishing.api.loot;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface FishingLoot extends Cloneable {

    public void spawn(Entity hook, Player player);

    FishingLootType getType();

    public FishingLoot clone();
}
