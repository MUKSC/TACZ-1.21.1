package com.tacz.guns.compat.controllable;

import com.mrcrayfish.controllable.Controllable;
import com.mrcrayfish.controllable.client.binding.ButtonBinding;
import com.mrcrayfish.controllable.client.binding.context.BindingContext;
import com.mrcrayfish.controllable.client.binding.context.InGameContext;
import com.mrcrayfish.controllable.client.binding.handlers.OnPressAndReleaseHandler;
import com.mrcrayfish.controllable.client.input.Buttons;
import com.mrcrayfish.controllable.client.input.Controller;
import com.mrcrayfish.framework.api.event.TickEvents;
import com.tacz.guns.GunMod;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.client.input.*;
import com.tacz.guns.client.resource.pojo.display.gun.ControllableData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.EnumMap;
import java.util.Optional;

public class ControllableInner {
    public static final BindingContext GUN_KEY_CONFLICT = new GunKeyConflict(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "gun_key"));
    public static final ButtonBinding AIM = new ButtonBinding(Buttons.LEFT_TRIGGER, "key.tacz.aim.desc", "key.category.tacz", GUN_KEY_CONFLICT, OnPressAndReleaseHandler.create(
        context -> Optional.of(() -> AimKey.onAimControllerPress(true)),
        context -> AimKey.onAimControllerPress(false))
    );
    public static final ButtonBinding SHOOT = new ButtonBinding(Buttons.RIGHT_TRIGGER, "key.tacz.shoot.desc", "key.category.tacz", GUN_KEY_CONFLICT, OnPressAndReleaseHandler.create(
        context -> Optional.of(() -> ShootKey.semiShootController(true)),
        context -> ShootKey.semiShootController(false))
    );
    public static final ButtonBinding RELOAD = new ButtonBinding(Buttons.B, "key.tacz.reload.desc", "key.category.tacz", GUN_KEY_CONFLICT, OnPressAndReleaseHandler.create(
        context -> Optional.of(() -> ReloadKey.onReloadControllerPress(true)),
        context -> ReloadKey.onReloadControllerPress(false))
    );
    public static final ButtonBinding MELEE = new ButtonBinding(Buttons.X, "key.tacz.melee.desc", "key.category.tacz", GUN_KEY_CONFLICT, OnPressAndReleaseHandler.create(
        context -> Optional.of(() -> MeleeKey.onMeleeControllerPress(true)),
        context -> MeleeKey.onMeleeControllerPress(false))
    );
    public static final ButtonBinding ZOOM = new ButtonBinding(Buttons.X, "key.tacz.zoom.desc", "key.category.tacz", GUN_KEY_CONFLICT, OnPressAndReleaseHandler.create(
        context -> Optional.of(() -> ZoomKey.onZoomControllerPress(true)),
        context -> ZoomKey.onZoomControllerPress(false))
    );
    public static final ButtonBinding CRAWL = new ButtonBinding(Buttons.LEFT_THUMB_STICK, "key.tacz.crawl.desc", "key.category.tacz", GUN_KEY_CONFLICT, OnPressAndReleaseHandler.create(
        context -> Optional.of(() -> CrawlKey.onCrawlControllerPress(true)),
        context -> CrawlKey.onCrawlControllerPress(false))
    );
    public static final ButtonBinding FIRE_SELECT = new ButtonBinding(Buttons.DPAD_LEFT, "key.tacz.fire_select.desc", "key.category.tacz", GUN_KEY_CONFLICT, OnPressAndReleaseHandler.create(
        context -> Optional.of(() -> FireSelectKey.onFireSelectControllerPress(true)),
        context -> FireSelectKey.onFireSelectControllerPress(false))
    );
    public static final ButtonBinding INTERACT = new ButtonBinding(-1, "key.tacz.interact.desc", "key.category.tacz", GUN_KEY_CONFLICT, OnPressAndReleaseHandler.create(
        context -> Optional.of(() -> InteractKey.onInteractControllerPress(true)),
        context -> InteractKey.onInteractControllerPress(false))
    );
    public static final ButtonBinding INSPECT = new ButtonBinding(-1, "key.tacz.inspect.desc", "key.category.tacz", GUN_KEY_CONFLICT, OnPressAndReleaseHandler.create(
        context -> Optional.of(() -> InspectKey.onInspectControllerPress(true)),
        context -> InspectKey.onInspectControllerPress(false))
    );

    public static void init() {
        Controllable.getBindingRegistry().register(AIM);
        Controllable.getBindingRegistry().register(SHOOT);
        Controllable.getBindingRegistry().register(RELOAD);
        Controllable.getBindingRegistry().register(MELEE);
        Controllable.getBindingRegistry().register(CRAWL);
        Controllable.getBindingRegistry().register(ZOOM);
        Controllable.getBindingRegistry().register(FIRE_SELECT);
        Controllable.getBindingRegistry().register(INTERACT);
        Controllable.getBindingRegistry().register(INSPECT);

        TickEvents.END_CLIENT.register(ControllableInner::onClientTickEnd);
    }

    public static void onClientTickEnd() {
        if (!GUN_KEY_CONFLICT.isActive()) {
            return;
        }
        Controller controller = Controllable.getController();
        if (controller == null) {
            return;
        }
        if (controller.isButtonPressed(SHOOT.getButton()) && ShootKey.autoShootController()) {
            doRumble(controller);
        }
    }

    private static void doRumble(Controller controller) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        ItemStack mainHandItem = player.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(mainHandItem);
        if (iGun == null) {
            return;
        }

        FireMode fireMode = iGun.getFireMode(mainHandItem);
        TimelessAPI.getGunDisplay(mainHandItem).ifPresent(index -> {
            EnumMap<FireMode, ControllableData> data = index.getControllableData();
            if (data.containsKey(fireMode)) {
                ControllableData controllableData = data.get(fireMode);
                controller.rumble(controllableData.getLowFrequency(), controllableData.getHighFrequency(), controllableData.getTimeInMs());
            } else {
                if (fireMode == FireMode.AUTO) {
                    controller.rumble(0.15f, 0.25f, 80);
                } else {
                    controller.rumble(0.25f, 0.5f, 100);
                }
            }
        });
    }

    public static class GunKeyConflict extends InGameContext {
        protected GunKeyConflict(ResourceLocation id) {
            super(id);
        }

        @Override
        public int priority() {
            return 1;
        }
    }
}
