package com.zhaoch23.stardewvalleyfishing.api;

import com.zhaoch23.stardewvalleyfishing.StardewValleyFishing;
import com.zhaoch23.stardewvalleyfishing.api.data.FishAI;
import com.zhaoch23.stardewvalleyfishing.api.loot.FishingLoot;
import com.zhaoch23.stardewvalleyfishing.api.loot.FishingLootTable;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

@Getter
public class SVFishHook implements Cloneable {

    private String ticketsExpression;

    private FishAI fishAI;

    private FishingLootTable fishingLoots;

    private int dropCount;

    public SVFishHook() {
        this.fishAI = new FishAI();
        this.fishingLoots = new FishingLootTable();
        this.ticketsExpression = "1";
        this.dropCount = 1;
    }

    public SVFishHook(
            FishAI fishAI,
            FishingLootTable fishingLootTable,
            String tickets,
            int dropCount
    ) {
        assert fishAI != null && fishingLootTable != null && tickets != null && dropCount >= 0;
        this.fishAI = fishAI;
        this.fishingLoots = fishingLootTable;
        this.ticketsExpression = tickets;
        this.dropCount = dropCount;
    }

    public SVFishHook(ConfigurationSection section) {
        if (section.contains("tickets")) {
            setTicketsExpression(section.getString("tickets"));
        } else {
            StardewValleyFishing.logger().warning("tickets not found in " + section.getName() + ", defaulting to 1");
            setTicketsExpression("1");
        }
        if (section.contains("fish_ai")) {
            setFishAI(new FishAI(section.getConfigurationSection("fish_ai")));
        } else {
            StardewValleyFishing.logger().warning("fish_ai not found in " + section.getName() + ", defaulting to default FishAI");
            setFishAI(new FishAI());
        }
        if (section.contains("loots")) {
            setFishingLoots(new FishingLootTable(section.getConfigurationSection("loots")));
        } else {
            StardewValleyFishing.logger().warning("loots not found in " + section.getName() + ", defaulting to empty FishingLootTable");
            setFishingLoots(new FishingLootTable());
        }
        if (section.contains("drop_count")) {
            setDropCount(section.getInt("drop_count"));
        } else {
            StardewValleyFishing.logger().warning("drop_count not found in " + section.getName() + ", defaulting to 1");
            setDropCount(1);
        }
    }

    @Override
    public SVFishHook clone() {
        return new SVFishHook(fishAI.clone(), fishingLoots.clone(), ticketsExpression, dropCount);
    }

    /**
     * Set the tickets expression
     * Will be evaluated as a PAPI expression if it is not a number
     *
     * @param tickets the tickets expression
     */
    public void setTicketsExpression(String tickets) {
        assert tickets != null;
        this.ticketsExpression = tickets;
    }

    /**
     * Set the fish AI
     *
     * @param fishAI the fish AI
     */
    public void setFishAI(FishAI fishAI) {
        Objects.requireNonNull(fishAI, "fishAI cannot be null");
        this.fishAI = fishAI;
    }

    /**
     * Set the fishing loots table
     *
     * @param fishingLoots the fishing loots
     */
    public void setFishingLoots(FishingLootTable fishingLoots) {
        Objects.requireNonNull(fishingLoots, "loots cannot be null");
        this.fishingLoots = fishingLoots;
    }

    /**
     * Set the drop count
     *
     * @param dropCount the drop count
     */
    public void setDropCount(int dropCount) {
        assert dropCount >= 0;
        this.dropCount = dropCount;
    }

    /**
     * Spawn loots for the player
     *
     * @param hook   the entity hook
     * @param player the player
     */
    public void spawnLoots(Entity hook, Player player) {
        for (int i = 0; i < dropCount; i++) {
            fishingLoots.randomSelect(dropCount).forEach(loot -> loot.spawn(hook, player));
        }
    }

    /**
     * Select loots
     *
     * @return the selected loots
     */
    public List<FishingLoot> selectLoots() {
        return fishingLoots.randomSelect(dropCount);
    }

    /**
     * Get the tickets for the fish hook
     *
     * @param player the player to evaluate PAPI expression
     * @return the tickets
     */
    public int getTickets(Player player) {
        int value = 0;
        try {
            value = Integer.parseInt(ticketsExpression);
        } catch (NumberFormatException e) { // Is an expression
            if (StardewValleyFishing.settings().enablePlaceholderExpression) {
                try {
                    String expString = PlaceholderAPI.setPlaceholders(player, ticketsExpression);
                    Expression expression = new ExpressionBuilder(expString).build();
                    value = (int) expression.evaluate();
                } catch (Exception e1) {
                    StardewValleyFishing.logger().warning("Failed to evaluate expression " + ticketsExpression + ", defaulting to 0");
                }
            } else {
                StardewValleyFishing.logger().warning("Expression " + ticketsExpression + " is not supported, defaulting to 0");
            }
        }
        if (value < 0) {
            StardewValleyFishing.logger().warning("tickets expression " + ticketsExpression +
                    " evaluated to negative value, defaulting to 0");
            value = 0;
        }
        return value;
    }

    @Override
    public String toString() {
        return "SVFishHook{" +
                "ticketsExpression='" + ticketsExpression + '\'' +
                ", fishAI=" + fishAI +
                ", fishingLoots=" + fishingLoots +
                ", dropCount=" + dropCount +
                '}';
    }
}
