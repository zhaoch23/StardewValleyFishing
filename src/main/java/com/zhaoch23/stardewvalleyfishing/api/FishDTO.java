package com.zhaoch23.stardewvalleyfishing.api;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FishDTO {
    final List<Integer> behavior_pattern = new ArrayList<>();
    int period = 100;
    int peak_time = 40;
    double damping_ratio = 1.0;
    double distance_min = 0.0;
    double distance_max = 40;

    public FishDTO() {
    }

    public FishDTO(ConfigurationSection section) {
        if (section.contains("period")) {
            setBehaviorPeriod(section.getInt("period"));
        }
        if (section.contains("peak_time")) {
            setPeakTime(section.getInt("peak_time"));
        }
        if (section.contains("damping_ratio")) {
            setDampingRatio(section.getDouble("damping_ratio"));
        }
        if (section.contains("distance_min")) {
            setDistanceMin(section.getDouble("distance_min"));
        }
        if (section.contains("distance_max")) {
            setDistanceMax(section.getDouble("distance_max"));
        }
        if (section.contains("behavior_pattern")) {
            setBehaviorPattern(section.getIntegerList("behavior_pattern"));
        }
    }

    public int getBehaviorPeriod() {
        return period;
    }

    /**
     * How long the fish will stay in the same behavior
     * Which is the expected value of a geometric distribution in ticks
     *
     * @param period period in ticks
     */
    public void setBehaviorPeriod(int period) {
        this.period = period;
    }

    public int getPeakTime() {
        return peak_time;
    }

    /**
     * The time when the fish reaches the peak of the distance
     * Overshoot may happen if the damping ratio is less than 1
     *
     * @param peak_time peak time in ticks
     */
    public void setPeakTime(int peak_time) {
        this.peak_time = peak_time;
    }

    public double getDampingRatio() {
        return damping_ratio;
    }

    /**
     * The damping ratio (zeta) of the fish behavior
     * This value should be between 0 and 1
     * 1 for a critically damped system
     * 0 for an undamped system
     *
     * @param damping_ratio damping ratio
     */
    public void setDampingRatio(double damping_ratio) {
        assert damping_ratio >= 0 && damping_ratio <= 1;
        this.damping_ratio = damping_ratio;
    }

    public double getDistanceMin() {
        return distance_min;
    }

    /**
     * The displacement of the fish in each period will be selected using a uniform distribution
     * The minimum displacement the fish can do in each behavior period (lower bound)
     *
     * @param distance_min minimum distance between 0 and 100
     */
    public void setDistanceMin(double distance_min) {
        assert distance_min >= 0 && distance_min <= 100;
        this.distance_min = distance_min;
    }

    public double getDistanceMax() {
        return distance_max;
    }

    /**
     * The displacement of the fish in each period will be selected using a uniform distribution
     * The maximum displacement the fish can do in each behavior period (upper bound)
     *
     * @param distance_max maximum distance between 0 and 100
     */
    public void setDistanceMax(double distance_max) {
        assert distance_max >= 0 && distance_max <= 100;
        this.distance_max = distance_max;
    }

    public List<Integer> getBehaviorPattern() {
        return new ArrayList<>(behavior_pattern);
    }

    /**
     * Add a behavior pattern to the fish
     * The behavior pattern defines the direction of the displacement of the fish
     * The behavior pattern is a list of integers, each integer represents a direction
     * <p>
     * 1 - down;
     * -1 - up;
     * 0 - random;
     * </p>
     * The direction of the fish moving in each period will follow the behavior pattern and repeat
     * <p>
     * If you don't want the fish to move too frequently, then increase the period
     *
     * @param behavior behavior in the pattern
     */
    public void setBehaviorPattern(List<Integer> behavior) {
        Objects.requireNonNull(behavior, "Behavior pattern cannot be null");
        this.behavior_pattern.clear();
        this.behavior_pattern.addAll(behavior);
    }
}
