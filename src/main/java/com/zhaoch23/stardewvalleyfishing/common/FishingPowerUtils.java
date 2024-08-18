package com.zhaoch23.stardewvalleyfishing.common;

import com.zhaoch23.stardewvalleyfishing.StardewValleyFishing;
import com.zhaoch23.stardewvalleyfishing.api.FishingRodDTO;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class FishingPowerUtils {

    public static int getFromItemStackLore(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        String num = null;
        if (itemMeta != null) {
            List<String> lore = itemMeta.getLore();
            if (lore != null) {
                for (String line : lore) {
                    if (line.contains("Fishing Power") || line.contains("渔力")) {
                        int index = 0;
                        for (int i = 0; i < line.length(); i++) {
                            if (line.charAt(index) == '&' || line.charAt(index) == '§') {
                                index += 1;
                                continue;
                            }
                            if (Character.isDigit(line.charAt(i))) {
                                index = i;
                                break;
                            }
                        }
                        line = line.substring(index);
                        String[] split = line.split("(?=\\D)");
                        if (split.length >= 1) {
                            num = split[0];
                        }
                        break;
                    }
                }
            }
        }
        if (num != null) {
            return Integer.parseInt(num);
        }
        return 0;
    }


    public static int getLoreFishingPower(Player player) {
        Objects.requireNonNull(player);
        int fishingPower = 0;
        // Setup fishing rod
        if (Objects.equals(StardewValleyFishing.settings().fishingPower, "lore")) {
            PlayerInventory playerInventory = player.getInventory();
            ItemStack item = playerInventory.getItemInMainHand();
            if (item.getType() != Material.FISHING_ROD) {
                item = playerInventory.getItemInOffHand();
            }

            fishingPower = FishingPowerUtils.getFromItemStackLore(item);
        } else {
            PlaceholderAPI.setPlaceholders(player,
                    StardewValleyFishing.settings().fishingPower);
            try {
                fishingPower = Integer.parseInt(StardewValleyFishing.settings().fishingPower);
            } catch (NumberFormatException e) {
                StardewValleyFishing.logger().severe("Failed to parse fishing power " +
                        StardewValleyFishing.settings().fishingPower);
            }
        }
        return fishingPower;
    }

    public static FishingRodDTO setupFishingDTO(int fishingPower) {
        FishingRodDTO finshingRodDTO = new FishingRodDTO();
        finshingRodDTO.setPullingAcceleration(Math.min(3.0, 0.5 + fishingPower / 100.0));
        finshingRodDTO.setGravity(0.5);
        finshingRodDTO.setScale(1.5 + fishingPower / 100.0);
        return finshingRodDTO;
    }


}
