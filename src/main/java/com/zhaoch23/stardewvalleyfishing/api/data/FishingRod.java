package com.zhaoch23.stardewvalleyfishing.api.data;

public class FishingRod {
    double max_vel = 10.0;
    double acc = 0.5;
    double gravity = 0.5;
    double scale = 1.5;
    int initial_progress = 20;

    public double getMaxVelocity() {
        return max_vel;
    }

    /**
     * Set the maximum velocity of the fishing rod
     * The maximum velocity should be positive
     *
     * @param maxVelocity maximum velocity
     */
    public void setMaxVelocity(double maxVelocity) {
        assert maxVelocity >= 0;
        this.max_vel = maxVelocity;
    }

    public double getPullingAcceleration() {
        return acc;
    }

    /**
     * Set the acceleration of the fishing rod when pulling
     * The acceleration should be positive
     *
     * @param acc acceleration
     */
    public void setPullingAcceleration(double acc) {
        assert acc >= 0;
        this.acc = acc;
    }

    public double getGravity() {
        return gravity;
    }

    /**
     * Set the gravity of the fishing rod when not pulling
     * The gravity should be positive
     *
     * @param gravity gravity
     */
    public void setGravity(double gravity) {
        assert gravity >= 0;
        this.gravity = gravity;
    }

    public double getScale() {
        return scale;
    }

    /**
     * Set the scale of the fishing rod
     * The scale should be positive
     *
     * @param scale scale
     */
    public void setScale(double scale) {
        assert scale >= 0;
        this.scale = scale;
    }

    public int getInitialProgress() {
        return initial_progress;
    }

    /**
     * Set the initial progress of the fishing rod
     * The initial progress should be positive
     *
     * @param initialProgress initial progress
     */
    public void setInitialProgress(int initialProgress) {
        assert initialProgress >= 0;
        this.initial_progress = initialProgress;
    }

    public String toString() {
        return "FishingRodDTO{" +
                "max_vel=" + max_vel +
                ", acc=" + acc +
                ", gravity=" + gravity +
                ", scale=" + scale +
                ", initial_progress=" + initial_progress +
                '}';
    }
}
