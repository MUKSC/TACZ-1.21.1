package com.tacz.guns.resource.pojo.data.gun;

import com.google.gson.annotations.SerializedName;

public class GunHeatData {

    @SerializedName("max")
    private float heatMax = 100f;

    @SerializedName("perShot")
    private float heatPerShot = 1f;

    @SerializedName("decreaseMultiplier")
    private float decreaseMultiplier = 1f;

    @SerializedName("cooldown")
    private long heatCooldown = 1000L; //ms

    @SerializedName("minInaccuracy")
    private float minInaccuracy = 1f;

    @SerializedName("maxInaccuracy")
    private float maxInaccuracy = 1f;

    @SerializedName("minRpmMod")
    private float minRpmMod = 1f;

    @SerializedName("maxRpmMod")
    private float maxRpmMod = 1f;


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
