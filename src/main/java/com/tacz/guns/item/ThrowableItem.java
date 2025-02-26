package com.tacz.guns.item;

import com.tacz.guns.client.renderer.item.AnimateGeoItemRendererWrapper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class ThrowableItem extends Item {
    public ThrowableItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private AnimateGeoItemRendererWrapper renderer = null;

            @Override
            public AnimateGeoItemRendererWrapper getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new AnimateGeoItemRendererWrapper();
                    this.renderer.init();
                }
                return renderer;
            }
        });
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        pPlayer.startUsingItem(pUsedHand);
        if (pLevel.isClientSide()) {
            ((AnimateGeoItemRendererWrapper)IClientItemExtensions.of(pPlayer.getItemInHand(pUsedHand).getItem()).getCustomRenderer()).startThrowing();
        }

        return InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand));
    }


    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {

    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int timeLeft) {
        ((AnimateGeoItemRendererWrapper)IClientItemExtensions.of(stack.getItem()).getCustomRenderer()).throwItem();
    }

    @Override
    public boolean useOnRelease(ItemStack pStack) {
        return true;
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {

    }
}
