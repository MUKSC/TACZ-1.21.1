package com.tacz.guns.client.sound;

import com.google.common.collect.Lists;
import com.tacz.guns.sound.SoundManager;

import java.util.List;

public final class GunSoundPreload {
    public static final List<String> DEFAULT_PRELOAD_NAMES = Lists.newArrayList(
            SoundManager.SHOOT_SOUND,
            SoundManager.SHOOT_3P_SOUND,
            SoundManager.SILENCE_SOUND,
            SoundManager.SILENCE_3P_SOUND,
            SoundManager.DRY_FIRE_SOUND,
            SoundManager.RELOAD_EMPTY_SOUND,
            SoundManager.RELOAD_TACTICAL_SOUND,
            SoundManager.INSPECT_EMPTY_SOUND,
            SoundManager.INSPECT_SOUND,
            SoundManager.DRAW_SOUND,
            SoundManager.PUT_AWAY_SOUND,
            SoundManager.BOLT_SOUND,
            SoundManager.FIRE_SELECT
    );

    private GunSoundPreload() {
    }
}
