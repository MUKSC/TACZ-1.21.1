package com.tacz.guns.client.event;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.client.other.KeepingItemRenderer;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.renderer.item.AnimateGeoItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = GunMod.MOD_ID)
public class FirstPersonRenderEvent {
    // 上一个渲染中的物品
    private static ItemStack prevStack = ItemStack.EMPTY;

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        if (event.getHand() == InteractionHand.OFF_HAND) {
            ItemStack stack = KeepingItemRenderer.getRenderer().getCurrentItem();
            if (stack.getItem() instanceof IGun) {
                event.setCanceled(true);
            }
            return;
        }
        // 事件事件给的是被延长渲染修改过后的物品，不是玩家实际手持的
        ItemStack stack = KeepingItemRenderer.getRenderer().getCurrentItem();

        // 获取 TransformType
        ItemDisplayContext transformType;
        if (event.getHand() == InteractionHand.MAIN_HAND) {
            transformType = ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
        } else {
            transformType = ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
        }

        boolean flag = !ItemStack.matches(prevStack, stack) && !isSame(prevStack, stack);

        // 渲染相关内容整理到物品的IClientItemExtensions了，这个接口有待进一步抽象
        if (IClientItemExtensions.of(stack.getItem()).getCustomRenderer() instanceof AnimateGeoItemRenderer<?, ?> renderer) {
            // 如果物品不一样了，先尝试初始化状态机
            if (flag || renderer.needReInit(stack)) {
                renderer.tryInit(stack, player, event.getPartialTick());
            }

            renderer.renderFirstPerson(player, stack, transformType, event.getPoseStack(), event.getMultiBufferSource(),
                    event.getPackedLight(), event.getPartialTick());
            event.setCanceled(true);
        }

        if (flag) {
            prevStack = stack.copy();
        }
    }

    private static boolean isSame(ItemStack i, ItemStack j) {
        IGun iGun1 = IGun.getIGunOrNull(i);
        IGun iGun2 = IGun.getIGunOrNull(j);
        if (iGun1 != null && iGun2 != null) {
            return iGun1.getGunId(i).equals(iGun2.getGunId(j));
        }
        if (i.isEmpty() || j.isEmpty()) {
            return i.isEmpty() && j.isEmpty();
        }
        return ItemStack.matches(i, j);
    }
}
