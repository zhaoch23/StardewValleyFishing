package com.zhaoch23.stardewvalleyfishing.api.loot;

import com.zhaoch23.stardewvalleyfishing.StardewValleyFishing;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@Getter
public class FishingLootMythicMob implements FishingLoot {

    private final String mobName;

    public FishingLootMythicMob(String mobName) {
        this.mobName = mobName;
    }

    @Override
    public void spawn(Entity hook, Player player) {
        // Spawn the mob
        ActiveMob mob = MythicMobs.inst().getMobManager().spawnMob(mobName, hook.getLocation());
        if (mob == null) {
            StardewValleyFishing.logger().warning("Failed to spawn mob " + mobName);
            return;
        }
        Entity bukkitEntity = BukkitAdapter.adapt(mob.getEntity());
        if (bukkitEntity == null) {
            StardewValleyFishing.logger().warning("Failed to spawn mob " + mobName);
            return;
        }
        // throw to the player
        FishingLootTable.throwToPlayer(bukkitEntity, player);
    }

    @Override
    public FishingLootType getType() {
        return FishingLootType.ENTITY;
    }

    @Override
    public FishingLoot clone() {
        return new FishingLootMythicMob(mobName);
    }
}
