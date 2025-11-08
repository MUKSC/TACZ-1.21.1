package com.tacz.guns.compat.kubejs.events;

import com.tacz.guns.api.client.event.BeforeRenderHandEvent;
import com.tacz.guns.api.client.event.RenderItemInHandBobEvent;
import com.tacz.guns.api.client.event.RenderLevelBobEvent;
import com.tacz.guns.api.client.event.SwapItemWithOffHand;
import com.tacz.guns.api.event.common.*;
import com.tacz.guns.api.event.server.AmmoHitBlockEvent;
import com.tacz.guns.api.item.IGun;
import dev.latvian.mods.kubejs.event.EventExit;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GunKubeJSEvents {
    public static final EventGroup GROUP = EventGroup.of("TimelessGunEvents");

    public static abstract class GunEventJS<E extends Event> implements KubeEvent, TimelessForgeEventWrappers.ForgeEventWrapper<E> {
        protected final E event;

        public GunEventJS(E event) {
            this.event = event;
        }

        @Override
        public E getForgeEvent() {
            return event;
        }

        @Nonnull
        protected ItemStack getEventItemStack() {
            return ItemStack.EMPTY;
        };

        @Nullable
        public ResourceLocation getEventSubId() {
            ItemStack itemStack = getEventItemStack();
            return itemStack.getItem() instanceof IGun iGun ? iGun.getGunId(itemStack) : null;
        }

        @HideFromJS
        public abstract @Nonnull ScriptTypeHolder getTypeHolder();

        @Override
        public Object cancel(Context cx, @Nullable Object value) throws EventExit {
            if (event instanceof ICancellableEvent cancellable) {
                cancellable.setCanceled(true);
            }
            return KubeEvent.super.cancel(cx, value);
        }
    }

    //Forge事件套皮KubeJS事件
    //common事件
    public static class AttachmentPropertyEventJS extends GunEventJS<AttachmentPropertyEvent> implements TimelessForgeEventWrappers.AttachmentPropertyWrapper {
        public AttachmentPropertyEventJS(AttachmentPropertyEvent event) {
            super(event);
        }

        @Override
        @Nonnull
        protected ItemStack getEventItemStack() {
            return event.getGunItem();
        }

        @HideFromJS
        @Override
        public @NotNull ScriptTypeHolder getTypeHolder() {
            return (ScriptTypeHolder) event.getShooter();
        }
    }

    public static class EntityHurtByGunPreEventJS extends GunEventJS<EntityHurtByGunEvent.Pre> implements TimelessForgeEventWrappers.EntityHurtByGunPreWrapper {
        public EntityHurtByGunPreEventJS(EntityHurtByGunEvent.Pre event) {
            super(event);
        }

        @Override
        public ResourceLocation getEventSubId() {
            return event.getGunId();
        }

        @HideFromJS
        @Override
        public @NotNull ScriptTypeHolder getTypeHolder() {
            return (ScriptTypeHolder) event.getBullet();
        }
    }

    public static class EntityHurtByGunPostEventJS extends GunEventJS<EntityHurtByGunEvent.Post> implements TimelessForgeEventWrappers.EntityHurtByGunWrapper<EntityHurtByGunEvent.Post> {
        public EntityHurtByGunPostEventJS(EntityHurtByGunEvent.Post event) {
            super(event);
        }

        @Override
        public ResourceLocation getEventSubId() {
            return event.getGunId();
        }

        @HideFromJS
        @Override
        public @NotNull ScriptTypeHolder getTypeHolder() {
            return (ScriptTypeHolder) event.getBullet();
        }
    }

    public static class EntityKillByGunEventJS extends GunEventJS<EntityKillByGunEvent> implements TimelessForgeEventWrappers.EntityKillByGunWrapper {
        public EntityKillByGunEventJS(EntityKillByGunEvent event) {
            super(event);
        }

        @Override
        public ResourceLocation getEventSubId() {
            return event.getGunId();
        }

        @HideFromJS
        @Override
        public @NotNull ScriptTypeHolder getTypeHolder() {
            return (ScriptTypeHolder) event.getBullet();
        }
    }

    public static class GunDrawEventJS extends GunEventJS<GunDrawEvent> implements TimelessForgeEventWrappers.GunDrawWrapper {
        public GunDrawEventJS(GunDrawEvent event) {
            super(event);
        }

        @Override
        @Nonnull
        protected ItemStack getEventItemStack() {
            return event.getCurrentGunItem();
        }

        @HideFromJS
        @Override
        public @NotNull ScriptTypeHolder getTypeHolder() {
            return (ScriptTypeHolder) event.getEntity();
        }
    }

    public static class GunFinishReloadEventJS extends GunEventJS<GunFinishReloadEvent> implements TimelessForgeEventWrappers.GunFinishReloadWrapper {
        public GunFinishReloadEventJS(GunFinishReloadEvent event) {
            super(event);
        }

        @Nonnull
        protected ItemStack getEventItemStack() {
            return event.getGunItemStack();
        }

        @HideFromJS
        public @NotNull ScriptTypeHolder getTypeHolder() {
            return event.getLogicalSide().isClient() ? ScriptType.CLIENT : ScriptType.SERVER;
        }
    }

    public static class GunFireEventJS extends GunEventJS<GunFireEvent> implements TimelessForgeEventWrappers.GunFireWrapper {
        public GunFireEventJS(GunFireEvent event) {
            super(event);
        }

        @Override
        @Nonnull
        protected ItemStack getEventItemStack() {
            return event.getGunItemStack();
        }

        @HideFromJS
        @Override
        public @NotNull ScriptTypeHolder getTypeHolder() {
            return (ScriptTypeHolder) event.getShooter();
        }
    }

    public static class GunFireSelectEventJS extends GunEventJS<GunFireSelectEvent> implements TimelessForgeEventWrappers.GunFireSelectWrapper {
        public GunFireSelectEventJS(GunFireSelectEvent event) {
            super(event);
        }

        @Override
        @Nonnull
        protected ItemStack getEventItemStack() {
            return event.getGunItemStack();
        }

        @HideFromJS
        @Override
        public @NotNull ScriptTypeHolder getTypeHolder() {
            return (ScriptTypeHolder) event.getShooter();
        }
    }

    public static class GunMeleeEventJS extends GunEventJS<GunMeleeEvent> implements TimelessForgeEventWrappers.GunMeleeWrapper {
        public GunMeleeEventJS(GunMeleeEvent event) {
            super(event);
        }

        @Override
        @Nonnull
        protected ItemStack getEventItemStack() {
            return event.getGunItemStack();
        }

        @HideFromJS
        @Override
        public @NotNull ScriptTypeHolder getTypeHolder() {
            return (ScriptTypeHolder) event.getShooter();
        }
    }

    public static class GunReloadEventJS extends GunEventJS<GunReloadEvent> implements TimelessForgeEventWrappers.GunReloadWrapper {
        public GunReloadEventJS(GunReloadEvent event) {
            super(event);
        }

        @Override
        @Nonnull
        protected ItemStack getEventItemStack() {
            return event.getGunItemStack();
        }

        @HideFromJS
        @Override
        public @NotNull ScriptTypeHolder getTypeHolder() {
            return (ScriptTypeHolder) event.getEntity();
        }
    }

    public static class GunShootEventJS extends GunEventJS<GunShootEvent> implements TimelessForgeEventWrappers.GunShootWrapper {
        public GunShootEventJS(GunShootEvent event) {
            super(event);
        }

        @Override
        @Nonnull
        protected ItemStack getEventItemStack() {
            return event.getGunItemStack();
        }

        @HideFromJS
        @Override
        public @NotNull ScriptTypeHolder getTypeHolder() {
            return (ScriptTypeHolder) event.getShooter();
        }
    }

    //server事件
    public static class AmmoHitBlockEventJS extends GunEventJS<AmmoHitBlockEvent> implements TimelessForgeEventWrappers.AmmoHitBlockWrapper {
        public AmmoHitBlockEventJS(AmmoHitBlockEvent event) {
            super(event);
        }

        @Override
        @Nullable
        public ResourceLocation getEventSubId() {
            return event.getAmmo().getGunId();
        }

        @HideFromJS
        @Override
        public @Nonnull ScriptTypeHolder getTypeHolder() {
            return (ScriptTypeHolder) event.getAmmo();
        }
    }

    //client事件
    public static class BeforeRenderHandEventJS extends GunEventJS<BeforeRenderHandEvent> implements TimelessForgeEventWrappers.BeforeRenderHandWrapper {
        public BeforeRenderHandEventJS(BeforeRenderHandEvent event) {
            super(event);
        }

        @HideFromJS
        @Override
        public @NotNull ScriptTypeHolder getTypeHolder() {
            return (ScriptTypeHolder) Minecraft.getInstance().level;
        }
    }

    public static class RenderItemInHandBobHurtEventJS extends GunEventJS<RenderItemInHandBobEvent.BobHurt> {
        public RenderItemInHandBobHurtEventJS(RenderItemInHandBobEvent.BobHurt event) {
            super(event);
        }

        @HideFromJS
        @Override
        public @NotNull ScriptTypeHolder getTypeHolder() {
            return (ScriptTypeHolder) Minecraft.getInstance().level;
        }
    }

    public static class RenderItemInHandBobViewEventJS extends GunEventJS<RenderItemInHandBobEvent.BobView> {
        public RenderItemInHandBobViewEventJS(RenderItemInHandBobEvent.BobView event) {
            super(event);
        }

        @HideFromJS
        @Override
        public @NotNull ScriptTypeHolder getTypeHolder() {
            return (ScriptTypeHolder) Minecraft.getInstance().level;
        }
    }

    public static class RenderLevelBobHurtEventJS extends GunEventJS<RenderLevelBobEvent.BobHurt> {
        public RenderLevelBobHurtEventJS(RenderLevelBobEvent.BobHurt event) {
            super(event);
        }

        @HideFromJS
        @Override
        public @NotNull ScriptTypeHolder getTypeHolder() {
            return (ScriptTypeHolder) Minecraft.getInstance().level;
        }
    }

    public static class RenderLevelBobViewEventJS extends GunEventJS<RenderLevelBobEvent.BobView> {
        public RenderLevelBobViewEventJS(RenderLevelBobEvent.BobView event) {
            super(event);
        }

        @HideFromJS
        @Override
        public @NotNull ScriptTypeHolder getTypeHolder() {
            return (ScriptTypeHolder) Minecraft.getInstance().level;
        }
    }

    public static class SwapItemWithOffHandEventJS extends GunEventJS<SwapItemWithOffHand> {
        public SwapItemWithOffHandEventJS(SwapItemWithOffHand event) {
            super(event);
        }

        @HideFromJS
        @Override
        public @NotNull ScriptTypeHolder getTypeHolder() {
            return (ScriptTypeHolder) Minecraft.getInstance().level;
        }
    }
}
