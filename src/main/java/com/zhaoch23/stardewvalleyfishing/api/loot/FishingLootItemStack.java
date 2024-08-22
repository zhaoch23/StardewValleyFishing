package com.zhaoch23.stardewvalleyfishing.api.loot;

import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class FishingLootItemStack implements FishingLoot {

    private final ItemStack itemStack;

    public FishingLootItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public FishingLootType getType() {
        return FishingLootType.ITEM;
    }

    @Override
    public void spawn(Entity hook, Player player) {
        Item item = hook.getWorld().dropItem(hook.getLocation(), itemStack);
        FishingLootTable.throwToPlayer(item, player);
    }

    @Override
    public FishingLootItemStack clone() {
        return new FishingLootItemStack(itemStack.clone());
    }
}
