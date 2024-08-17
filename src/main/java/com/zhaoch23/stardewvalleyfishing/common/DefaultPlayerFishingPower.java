package com.zhaoch23.stardewvalleyfishing.common;

import com.zhaoch23.stardewvalleyfishing.api.FishingRodDTO;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class DefaultPlayerFishingPower {


    public static FishingRodDTO getDefaultFishingRodDTO(ItemStack itemStack) {
        Objects.requireNonNull(itemStack);
        FishingRodDTO fishingRodDTO = new FishingRodDTO();

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
            int fishingPower = Integer.parseInt(num);
            setupFishingDTO(fishingRodDTO, fishingPower);
        }
        return fishingRodDTO;
    }

    public static void setupFishingDTO(FishingRodDTO finshingRodDTO, int fishingPower) {
        finshingRodDTO.setPullingAcceleration(Math.min(3.0, 0.5 + fishingPower / 100.0));
        finshingRodDTO.setGravity(0.5);
        finshingRodDTO.setScale(1.5 + fishingPower / 100.0);
    }


}
