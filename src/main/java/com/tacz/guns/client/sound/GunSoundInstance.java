package com.tacz.guns.client.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public class GunSoundInstance extends EntityBoundSoundInstance {
    private static final FileToIdConverter TACZ_SOUND_LISTER = new FileToIdConverter("tacz_sounds", ".ogg");

    @Nullable
    private final ResourceLocation registryName;
    @Nullable
    private Sound redirectedSound;

    public GunSoundInstance(SoundEvent soundEvent, SoundSource source, float volume, float pitch, Entity entity, int soundDistance, ResourceLocation registryName, boolean mono) {
        super(soundEvent, source, volume, pitch, entity, 943);
        this.attenuation = Attenuation.NONE;
        this.registryName = registryName;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            this.volume = volume * (1.0F - Math.min(1.0F, (float) Math.sqrt(player.distanceToSqr(x, y, z)) / soundDistance));
            this.volume *= this.volume;
        }
    }

    public void setStop() {
        this.stop();
    }

    @Nullable
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    @Override
    public WeighedSoundEvents resolve(SoundManager manager) {
        WeighedSoundEvents events = super.resolve(manager);
        if (events != null && this.registryName != null) {
            this.redirectedSound = new TaczSound(this.registryName, TACZ_SOUND_LISTER.idToFile(this.registryName), super.getSound());
            this.sound = this.redirectedSound;
        } else {
            this.redirectedSound = null;
        }
        return events;
    }

    @Override
    public Sound getSound() {
        return redirectedSound == null ? super.getSound() : redirectedSound;
    }

    private static class TaczSound extends Sound {
        private final ResourceLocation location;
        private final ResourceLocation path;

        private TaczSound(ResourceLocation location, ResourceLocation path, Sound template) {
            super(location.toString(), template.getVolume(), template.getPitch(), template.getWeight(), Type.FILE,
                    template.shouldStream(), false, template.getAttenuationDistance());
            this.location = location;
            this.path = path;
        }

        @Override
        public ResourceLocation getLocation() {
            return location;
        }

        @Override
        public ResourceLocation getPath() {
            return path;
        }
    }
}
