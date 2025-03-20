package com.tacz.guns.entity.shooter;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import net.minecraft.world.entity.LivingEntity;

public class LivingEntityHeat {

    private final LivingEntity shooter;
    private final ShooterDataHolder data;

    public LivingEntityHeat(LivingEntity shooter, ShooterDataHolder dataHolder) {
        this.shooter = shooter;
        this.data = dataHolder;
    }

    public void tickHeatTimestamp() {
        var gunStack = shooter.getMainHandItem();
        var iGun = IGun.getIGunOrNull(gunStack);
        if(iGun == null) return;
        var index = TimelessAPI.getCommonGunIndex(iGun.getGunId(gunStack));
        if(index.isEmpty()) return;
        if(index.get().getGunData().getHeatData() == null) return;
        if(iGun.getHeatAmount(gunStack) <= 0) return;
        if(System.currentTimeMillis() - data.heatTimestamp >= index.get().getGunData().getHeatData().getHeatCooldown()) {
            float heatAmount = iGun.getHeatAmount(gunStack)
                    - ((float)(System.currentTimeMillis() - data.heatTimestamp) / 10000f)
                    * index.get().getGunData().getHeatData().getDecreaseMultiplier();

            iGun.setHeatAmount(gunStack, heatAmount);
        }
    }
}
