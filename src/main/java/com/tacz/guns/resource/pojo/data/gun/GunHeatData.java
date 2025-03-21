package com.tacz.guns.resource.pojo.data.gun;

import com.google.gson.annotations.SerializedName;

public class GunHeatData {

    @SerializedName("max")
    private float heatMax = 100f;

    @SerializedName("per_shot")
    private float heatPerShot = 3f;

    @SerializedName("decrease_multiplier")
    private float decreaseMultiplier = 1f;

    @SerializedName("cooldown")
    private long heatCooldown = 3000L; //ms

    @SerializedName("delay")
    private long delay = 1000L; //ms

    @SerializedName("min_inaccuracy")
    private float minInaccuracy = 1f;

    @SerializedName("max_inaccuracy")
    private float maxInaccuracy = 1f;

    @SerializedName("min_rpm_mod")
    private float minRpmMod = 1f;

    @SerializedName("max_rpm_mod")
    private float maxRpmMod = 1f;

    public long getDelay() {
        return delay;
    }

    public float getHeatMax() {
        return heatMax;
    }

    public float getHeatPerShot() {
        return heatPerShot;
    }

    public long getHeatCooldown() {
        return heatCooldown;
    }

    public float getMinInaccuracy() {
        return minInaccuracy;
    }

    public float getMaxInaccuracy() {
        return maxInaccuracy;
    }

    public float getDecreaseMultiplier() {
        return decreaseMultiplier;
    }

    public float getMinRpmMod() {
        return minRpmMod;
    }

    public float getMaxRpmMod() {
        return maxRpmMod;
    }
}
