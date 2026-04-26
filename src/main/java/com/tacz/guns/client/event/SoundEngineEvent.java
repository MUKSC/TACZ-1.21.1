package com.tacz.guns.client.event;

import com.tacz.guns.GunMod;
import com.tacz.guns.client.resource.ClientAssetsManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.sound.SoundEngineLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GunMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SoundEngineEvent {
    @SubscribeEvent
    public static void onSoundEngineLoad(SoundEngineLoadEvent event) {
        ClientAssetsManager.INSTANCE.invalidateSoundBuffers();
    }
}
