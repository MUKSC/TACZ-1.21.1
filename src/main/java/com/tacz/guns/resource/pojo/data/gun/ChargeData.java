package com.tacz.guns.resource.pojo.data.gun;

import com.google.gson.annotations.SerializedName;

public class ChargeData {
    @SerializedName("type")
    private ChargeType chargeType = ChargeType.AUTO;

    @SerializedName("increase_per_tick")
    private float increasePerTick = 0.2f;

    @SerializedName("decrease_per_tick")
    private float decreasePerTick = 0.5f;

    @SerializedName("decrease_on_fire")
    private float decreaseOnFire = 0.0f;

    @SerializedName("max_charge")
    private float maxCharge = 1.0f;

    @SerializedName("fire_threshold")
    private float fireThreshold = 0.6f;

    @SerializedName("charge_during_cooldown")
    private boolean chargeDuringCooldown = true;

    public ChargeType getChargeType() {
        return chargeType;
    }

    public float getIncreasePerTick() {
        return increasePerTick;
    }

    public float getDecreasePerTick() {
        return decreasePerTick;
    }

    public float getDecreaseOnFire() {
        return decreaseOnFire;
    }

    public float getMaxCharge() {
        return maxCharge;
    }

    public float getFireThreshold() {
        return fireThreshold;
    }

    public boolean isChargeDuringCooldown() {
        return chargeDuringCooldown;
    }
}
