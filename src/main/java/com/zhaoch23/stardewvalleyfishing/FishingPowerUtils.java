package com.zhaoch23.stardewvalleyfishing;

import com.zhaoch23.stardewvalleyfishing.api.data.FishingRod;
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


    /**
     * Get the fishing power of the player.
     *
     * <p>
     * If the fishing power is set to lore, then the fishing power
     * will be extracted from the lore of the fishing rod. Otherwise,
     * the fishing power will be parsed from the configuration.
     *
     * @param player the player
     * @return the fishing power
     */
    public static int getFishingPower(Player player) {
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
            String stringPower = PlaceholderAPI.setPlaceholders(player,
                    StardewValleyFishing.settings().fishingPower);
            try {
                fishingPower = Integer.parseInt(stringPower);
            } catch (NumberFormatException e) {
                StardewValleyFishing.logger().severe("Failed to parse fishing power " +
                        StardewValleyFishing.settings().fishingPower);
            }
        }
        return fishingPower;
    }

    /**
     * Set up a default fishing rod DTO.
     *
     * @param fishingPower the fishing power
     * @return the fishing rod DTO
     */
    public static FishingRod setupFishingDTO(int fishingPower) {
        FishingRod finshingRodDTO = new FishingRod();
        finshingRodDTO.setPullingAcceleration(Math.min(1.0, 0.5 + fishingPower / 100.0));
        finshingRodDTO.setGravity(0.5);
        finshingRodDTO.setScale(2.0 + fishingPower / 50.0);
        return finshingRodDTO;
    }


}
