package com.tacz.guns.resource.pojo.data.gun;

import com.google.gson.annotations.SerializedName;

public class HeatData {
    @SerializedName("use_heat")
    private boolean useHeat;

    @SerializedName("infinite_ammo")
    private boolean infiniteAmmo;

    @SerializedName("magazine_lock_type")
    private MagazineLockType magazineLockType;

    @SerializedName("upper_limit")
    private int upperLimit;

    @SerializedName("heat_rate")
    private int heatRate;

    @SerializedName("cooling_rate")
    private int coolingRate;

    @SerializedName("cooling_delay")
    private float coolingDelay;

    @SerializedName("over_heat_time")
    private float overHeatTime;

    public HeatData(boolean useHeat, boolean infiniteAmmo, MagazineLockType magazineLockType, int upperLimit, int heatRate, int coolingRate, float coolingDelay, float overHeatTime) {
        this.useHeat = useHeat;
        this.infiniteAmmo = infiniteAmmo;
        this.magazineLockType = magazineLockType;
        this.upperLimit = upperLimit;
        this.heatRate = heatRate;
        this.coolingRate = coolingRate;
        this.coolingDelay = coolingDelay;
        this.overHeatTime = overHeatTime;
    }

    public boolean isUseHeat() {
        return useHeat;
    }

    public boolean isInfiniteAmmo() {
        return infiniteAmmo;
    }

    public MagazineLockType getMagazineLockType() {
        return magazineLockType;
    }

    public int getUpperLimit() {
        return upperLimit;
    }

    public int getHeatRate() {
        return heatRate;
    }

    public int getCoolingRate() {
        return coolingRate;
    }

    public float getCoolingDelay() {
        return coolingDelay;
    }

    public float getOverHeatTime() {
        return overHeatTime;
    }
}
