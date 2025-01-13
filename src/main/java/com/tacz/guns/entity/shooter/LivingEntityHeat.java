package com.tacz.guns.entity.shooter;

import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.pojo.data.gun.MagazineLockType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class LivingEntityHeat {
    private final LivingEntity shooter;
    private final ShooterDataHolder data;

    public LivingEntityHeat(LivingEntity shooter, ShooterDataHolder data) {
        this.shooter = shooter;
        this.data = data;
    }

    public void tickHeat() {
        if (shooter == null) {
            return;
        }
        ItemStack gun = shooter.getMainHandItem();
        if (!(gun.getItem() instanceof IGun iGun)) {
            return;
        }
        // 不使用过热机制则不处理
        if (!iGun.isUseHeat(gun, shooter)) {
            return;
        }
        // 如果没有过热则设置无限弹药模式为有子弹状态
        if (!iGun.isOverHeat(gun, shooter) && iGun.isInfiniteAmmo(gun, shooter)) {
            iGun.setBulletInBarrel(gun, true);
            iGun.setCurrentAmmoCount(gun, 1);
        }
        // 无需冷却情况不处理
        if (iGun.getHeatCount(gun, shooter) <= 0) {
            return;
        }
        MagazineLockType magazineLockType = iGun.getMagazineLockType(gun, shooter);
        // 如果具备完全弹匣锁，禁用自动冷却
        if (magazineLockType == MagazineLockType.ALL) {
            return;
        }
        // 如果是过热，且具备过热弹匣锁，禁用自动冷却
        if (iGun.isOverHeat(gun, shooter) && magazineLockType == MagazineLockType.OVER_HEAT) {
            return;
        }
        // 自动冷却
        // 连射间隔计算
        int fireGap = 60000 / iGun.getRPM(gun);
        // 默认在停止射击 (且超出连射间隔) 50ms (1 tick) 后开始冷却
        int defaultCoolingDelay = fireGap + iGun.getCoolingDelay(gun, shooter);
        if (iGun.isOverHeat(gun, shooter)) {
            // 如果过热，需要等待时间达到过热惩罚时间
            if (System.currentTimeMillis() - data.lastFireTimestamp > defaultCoolingDelay + iGun.getOverHeatTime(gun, shooter)) {
                iGun.setHeatCount(gun, iGun.getHeatCount(gun, shooter) - iGun.getCoolingRate(gun, shooter), shooter);
            }
        } else {
            // 如果未过热，需要等待时间达到冷却延迟
            if (System.currentTimeMillis() - data.lastFireTimestamp > defaultCoolingDelay) {
                iGun.setHeatCount(gun, iGun.getHeatCount(gun, shooter) - iGun.getCoolingRate(gun, shooter), shooter);
            }
        }
    }
}
