package com.tacz.guns.resource.pojo.data.gun;

import com.google.gson.annotations.SerializedName;

public class GunHeatData {
    //TODO: Implement "sweet spot" mechanic for cancelling an overheat

    @SerializedName("max")
    private float heatMax = 100f;

    @SerializedName("perShot")
    private float heatPerShot = 1f;

    @SerializedName("cooldown")
    private long heatCooldown = 1000L; //ms

    @SerializedName("inaccuracy")
    private float inaccuracy = 1f;


    public float getHeatMax() {
        return heatMax;
    }

    public float getHeatPerShot() {
        return heatPerShot;
    }

    public long getHeatCooldown() {
        return heatCooldown;
    }

    public float getHeatInaccuracy() {
        return inaccuracy;
    }

}
