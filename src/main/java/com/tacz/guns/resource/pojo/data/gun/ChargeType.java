package com.tacz.guns.resource.pojo.data.gun;

import com.google.gson.annotations.SerializedName;

public enum ChargeType {
    @SerializedName("auto")
    AUTO,
    @SerializedName("hold")
    HOLD,
    @SerializedName("delay")
    DELAY
}
