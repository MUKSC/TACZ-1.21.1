package com.tacz.guns.entity.shooter;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.pojo.data.gun.GunHeatData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class LivingEntityHeat {

    private final LivingEntity shooter;
    private final ShooterDataHolder data;

    public LivingEntityHeat(LivingEntity shooter, ShooterDataHolder dataHolder) {
        this.shooter = shooter;
        this.data = dataHolder;
    }

    public void tickHeat() {
        var gunStack = shooter.getMainHandItem();
        var iGun = IGun.getIGunOrNull(gunStack);
        if(iGun == null) return;
        TimelessAPI.getCommonGunIndex(iGun.getGunId(gunStack))
                .map(index -> index.getGunData().getHeatData())
                .ifPresent(heatData -> {
                    if (iGun.getHeatAmount(gunStack) <= 0) return;
                    if (iGun.isOverheatLocked(gunStack)) {
                        tickLocked(iGun, gunStack, heatData);
                    } else {
                        tickNormal(iGun, gunStack, heatData);
                    }
                });
    }

    public void tickLocked(IGun iGun, ItemStack gunStack, GunHeatData heatData) {
        if(System.currentTimeMillis() - data.heatTimestamp >= heatData.getHeatCooldown()) {
            float heatAmount = iGun.getHeatAmount(gunStack)
                    - ((float)(System.currentTimeMillis() - data.heatTimestamp) / 10000f)
                    * heatData.getDecreaseMultiplier();

            iGun.setHeatAmount(gunStack, heatAmount);
            if (heatAmount <= 0) {
                iGun.setOverheatLocked(gunStack, false);
            }
        }
    }

    public void tickNormal(IGun iGun, ItemStack gunStack, GunHeatData heatData) {
        if(System.currentTimeMillis() - data.heatTimestamp >= heatData.getDelay()) {
            float heatAmount = iGun.getHeatAmount(gunStack)
                    - ((float)(System.currentTimeMillis() - data.heatTimestamp) / 10000f)
                    * heatData.getDecreaseMultiplier();

            iGun.setHeatAmount(gunStack, heatAmount);
        }
    }
}
