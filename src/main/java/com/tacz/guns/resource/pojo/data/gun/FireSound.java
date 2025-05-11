package com.tacz.guns.resource.pojo.data.gun;

import com.google.gson.annotations.SerializedName;

public class FireSound {
    @SerializedName("fire_multiplier")
    private float fireMultiplier = 1.0f;

    @SerializedName("silence_multiplier")
    private float silenceMultiplier = 1.0f;

    public float getFireMultiplier() {
        return fireMultiplier;
    }

    public float getSilenceMultiplier() {
        return silenceMultiplier;
    }
}
