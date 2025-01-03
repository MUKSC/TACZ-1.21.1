package com.tacz.guns.resource.pojo.data.gun;

import com.google.gson.annotations.SerializedName;

/**
 * 弹匣锁类型，用于在使用过热的武器时创建依靠换弹降温的特殊需求
 */
public enum MagazineLockType {
    /**
     * 禁止使用弹匣锁
     */
    @SerializedName("disabled")
    DISABLED,
    /**
     * 在完全过热后锁定过热状态，直至一次换弹结束完全恢复过热计数器与过热锁
     */
    @SerializedName("over_heat")
    OVER_HEAT,
    /**
     * 禁用自动恢复过热计数器的功能，直至一次换弹结束完全恢复过热计数器与过热锁
     */
    @SerializedName("all")
    ALL,
    /**
     * 一次换弹结束完全恢复过热计数器与过热锁，平时不影响其他的过热进程
     */
    @SerializedName("assist")
    ASSIST
}
