package com.zhaoch23.stardewvalleyfishing;

import com.germ.germplugin.api.condition.ItemStackCondition;
import io.lumine.xikage.mythicmobs.utils.config.ConfigurationSection;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class FishingRodTacklesManager implements Listener {

    // Condition to check if the item is valid fishing rod
    ItemStackCondition openCondition = new ItemStackCondition();
    ItemStackCondition baitCondition = new ItemStackCondition();
    ItemStackCondition tackleCondition = new ItemStackCondition();

    public void loadConfig(ConfigurationSection openSection,
                            ConfigurationSection baitSection,
                            ConfigurationSection tackleSection) {

    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();

        ItemStack mainHandItem = player.getInventory().getItemInMainHand();

        if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) &&
            mainHandItem != null && mainHandItem.getType() == Material.FISHING_ROD &&
            openCondition.check(player, player.getInventory().getItemInMainHand())) {
                // do something

            }
        }
    }

}
