package com.tacz.guns.resource.pojo.data.gun;

import com.google.gson.annotations.SerializedName;

public class HeatData {
    @SerializedName("use_heat")
    private boolean useHeat = false;

    @SerializedName("infinite_ammo")
    private boolean infiniteAmmo = false;

    @SerializedName("magazine_lock_type")
    private MagazineLockType magazineLockType = MagazineLockType.DISABLED;

    @SerializedName("upper_limit")
    private int upperLimit = 100;

    @SerializedName("heat_rate")
    private int heatRate = 2;

    @SerializedName("cooling_rate")
    private int coolingRate = 1;

    @SerializedName("cooling_delay")
    private float coolingDelay = 0.05f;

    @SerializedName("over_heat_time")
    private float overHeatTime = 2f;

    public boolean isUseHeat() {
        return useHeat;
    }

    public boolean isInfiniteAmmo() {
        return useHeat && infiniteAmmo;
    }

    public MagazineLockType getMagazineLockType() {
        return useHeat ? magazineLockType : MagazineLockType.DISABLED;
    }

    public int getUpperLimit() {
        return useHeat ? upperLimit : 0;
    }

    public int getHeatRate() {
        return useHeat ? heatRate : 0;
    }

    public int getCoolingRate() {
        return useHeat ? coolingRate : 0;
    }

    public float getCoolingDelay() {
        return useHeat ? coolingDelay : 0f;
    }

    public float getOverHeatTime() {
        return useHeat ? overHeatTime : 0f;
    }
}
