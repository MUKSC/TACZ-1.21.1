package com.tacz.guns.event;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.event.common.GunFinishReloadEvent;
import com.tacz.guns.api.event.common.GunFireEvent;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.pojo.data.gun.MagazineLockType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = GunMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TaczEventListener {
    private static long latestFireTimestamp = System.currentTimeMillis();

    @SubscribeEvent
    public static void onFireEvent(GunFireEvent event) {
        ItemStack gun = event.getGunItemStack();
        // 如果是客户端则不处理
        if (event.getLogicalSide() == LogicalSide.CLIENT) {
            return;
        }
        if (!(gun.getItem() instanceof IGun iGun)) {
            return;
        }
        // 不使用过热机制则不处理
        if (!iGun.isUseHeat(gun)) {
            return;
        }
        latestFireTimestamp = System.currentTimeMillis();
        iGun.setHeatCount(gun, iGun.getHeatCount(gun) + iGun.getHeatRate(gun));
        // 如果触发过热则清空无限弹药所有位置的子弹
        if (iGun.isOverHeat(gun) && iGun.isInfiniteAmmo(gun)) {
            iGun.setBulletInBarrel(gun, false);
            iGun.setCurrentAmmoCount(gun, 0);
        }
    }

    @SubscribeEvent
    public static void onReloadFinishEvent(GunFinishReloadEvent event) {
        ItemStack gun = event.getGunItemStack();
        // 如果是客户端则不处理
        if (event.getLogicalSide() == LogicalSide.CLIENT) {
            return;
        }
        if (!(gun.getItem() instanceof IGun iGun)) {
            return;
        }
        // 不使用过热机制则不处理
        if (!iGun.isUseHeat(gun)) {
            return;
        }
        MagazineLockType magazineLockType = iGun.getMagazineLockType(gun);
        // 如果弹匣锁不启用则不处理
        if (magazineLockType == MagazineLockType.DISABLED) {
            return;
        }
        // 完全恢复冷却
        iGun.setHeatCount(gun, 0);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            // 如果是客户端则不处理
            if (event.side == LogicalSide.CLIENT) {
                return;
            }
            Player entity = event.player;
            if (entity == null) {
                return;
            }
            ItemStack gun = entity.getMainHandItem();
            if (!(gun.getItem() instanceof IGun iGun)) {
                return;
            }
            // 不使用过热机制则不处理
            if (!iGun.isUseHeat(gun)) {
                return;
            }
            // 如果没有过热则设置无限弹药为有子弹状态
            if (!iGun.isOverHeat(gun) && iGun.isInfiniteAmmo(gun)) {
                iGun.setCurrentAmmoCount(gun, 1);
            }
            // 无需冷却情况不处理
            if (iGun.getHeatCount(gun) <= 0) {
                return;
            }
            MagazineLockType magazineLockType = iGun.getMagazineLockType(gun);
            // 如果具备完全弹匣锁，禁用自动冷却
            if (magazineLockType == MagazineLockType.ALL) {
                return;
            }
            // 如果是过热，且具备过热弹匣锁，禁用自动冷却
            if (iGun.isOverHeat(gun) && magazineLockType == MagazineLockType.OVER_HEAT) {
                return;
            }
            // 自动冷却
            int fireGap = 60000 / iGun.getRPM(gun);
            // 默认在停止射击 (且超出连射间隔) 50ms (1 tick) 后开始冷却
            int defaultCoolingDelay = fireGap + 50;
            if (iGun.isOverHeat(gun)) {
                // 如果过热，需要等待时间达到过热惩罚时间
                if (System.currentTimeMillis() - latestFireTimestamp > defaultCoolingDelay + iGun.getOverHeatTime(gun)) {
                    iGun.setHeatCount(gun, iGun.getHeatCount(gun) - iGun.getCoolingRate(gun));
                }
            } else {
                // 如果未过热，需要等待时间达到冷却延迟
                if (System.currentTimeMillis() - latestFireTimestamp > defaultCoolingDelay) {
                    iGun.setHeatCount(gun, iGun.getHeatCount(gun) - iGun.getCoolingRate(gun));
                }
            }
        }
    }
}