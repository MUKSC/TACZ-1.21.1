package com.tacz.guns.client.animation.statemachine;

public class ThrowableAnimationStateContext extends ItemAnimationStateContext {
    private int usingTick = 0;
    private boolean using = false;

    public int getUsingTick() {
        return usingTick;
    }

    public void setUsingTick(int throwTime) {
        this.usingTick = throwTime;
    }

    public boolean isUsing() {
        return using;
    }

    public void setUsing(boolean using) {
        this.using = using;
    }
}
