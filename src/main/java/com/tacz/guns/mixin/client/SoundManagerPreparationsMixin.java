package com.tacz.guns.mixin.client;

import com.google.common.collect.Maps;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(targets = "net.minecraft.client.sounds.SoundManager$Preparations")
public class SoundManagerPreparationsMixin {
    private static final FileToIdConverter TACZ_SOUND_LISTER = new FileToIdConverter("tacz_sounds", ".ogg");

    @Shadow
    private Map<ResourceLocation, Resource> soundCache;

    @Inject(method = "listResources", at = @At("TAIL"))
    private void tacz$includeTaczSoundResources(ResourceManager resourceManager, CallbackInfo ci) {
        Map<ResourceLocation, Resource> merged = Maps.newHashMap(this.soundCache);
        merged.putAll(TACZ_SOUND_LISTER.listMatchingResources(resourceManager));
        this.soundCache = merged;
    }
}
