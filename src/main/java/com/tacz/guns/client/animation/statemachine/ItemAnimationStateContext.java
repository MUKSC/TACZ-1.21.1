package com.tacz.guns.client.animation.statemachine;

import com.tacz.guns.api.client.animation.statemachine.AnimationStateContext;

public class ItemAnimationStateContext extends AnimationStateContext {
    private float putAwayTime = 0f;
    protected float partialTicks = 0f;

    /**
     * 获取收起物品动画的建议时长，它只是计算结果，具体如何生效依赖于状态机实现。
     * @return 收起物品动画的建议时长
     */
    public float getPutAwayTime() {
        return putAwayTime;
    }

    /**
     * 状态机脚本不要调用此方法。此方法用于设置物品动画的建议时长
     */
    public void setPutAwayTime(float putAwayTime) {
        this.putAwayTime = putAwayTime;
    }

    /**
     * 获取最后一次更新时的 partialTicks
     * @return 状态机最后一次更新的 partialTicks.
     */
    public float getPartialTicks() {
        return partialTicks;
    }

    /**
     * 状态机脚本请不要调用此方法。此方法用于状态机更新时设置 partialTicks。
     */
    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }
}
