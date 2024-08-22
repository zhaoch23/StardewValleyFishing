package com.zhaoch23.stardewvalleyfishing.api.loot;

import com.zhaoch23.stardewvalleyfishing.StardewValleyFishing;
import io.lumine.xikage.mythicmobs.MythicMobs;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FishingLootTable implements Cloneable {

    private final List<FishingLoot> fishingLoots = new ArrayList<>();

    public FishingLootTable() {
    }

    /**
     * Load a FishingLootTable from a configuration section
     *
     * @param section the configuration section
     */
    public FishingLootTable(ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            try {
                // Itemstack
                if (key.startsWith("itemstack")) {
                    ItemStack itemStack = section.getItemStack(key);
                    if (itemStack == null) {
                        StardewValleyFishing.logger().warning("Failed to load itemstack " + key + " in " + section.getName());
                        continue;
                    }
                    fishingLoots.add(new FishingLootItemStack(itemStack));
                    // Mythic item
                } else if (key.startsWith("mythicitem")) {
                    String mmitem = section.getString(key);
                    ItemStack itemStack = MythicMobs.inst().getItemManager().getItemStack(mmitem);
                    if (itemStack == null) {
                        StardewValleyFishing.logger().warning("Failed to load mythicitem " + key + " in " + section.getName());
                        continue;
                    }
                    fishingLoots.add(new FishingLootItemStack(itemStack));
                // Mythic mob
                } else if (key.startsWith("mythicmob")) {
                    String mobName = section.getString(key);
                    fishingLoots.add(new FishingLootMythicMob(mobName));
                } else {
                    StardewValleyFishing.logger().warning("Unknown loot type " + key + " in " + section.getName());
                }
            } catch (Exception e) {
                StardewValleyFishing.logger().severe("Failed to load loot " + key + " in " +
                        section.getName() + "\n" + e.getMessage());
            }

        }
    }

    public static void throwToPlayer(Entity entity, Player player) {
        double dx = player.getLocation().getX() - entity.getLocation().getX();
        double dy = player.getLocation().getY() - entity.getLocation().getY();
        double dz = player.getLocation().getZ() - entity.getLocation().getZ();

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        // create a vector from the entity to the player
        // Copied from NMS EntityFishingHook
        org.bukkit.util.Vector vector = new org.bukkit.util.Vector(
                dx * 0.1,
                dy * 0.1 + Math.sqrt(distance) * 0.08,
                dz * 0.1
        );

        entity.setVelocity(vector);
    }

    @Override
    public FishingLootTable clone() {
        FishingLootTable clone = new FishingLootTable();
        for (FishingLoot loot : fishingLoots) {
            clone.fishingLoots.add(loot.clone());
        }
        return clone;
    }

    public List<FishingLoot> randomSelect(int count) {
        assert count > 0;
        if (fishingLoots.size() == 0) {
            return new ArrayList<>();
        }

        List<FishingLoot> selected = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int index = StardewValleyFishing.random().nextInt(fishingLoots.size());
            selected.add(fishingLoots.get(index));
        }

        return selected;
    }

    @Override
    public String toString() {
        return "FishingLootTable{" +
                "fishingLoots=" + fishingLoots +
                '}';
    }
}
