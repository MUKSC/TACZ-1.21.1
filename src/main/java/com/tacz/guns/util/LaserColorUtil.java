package com.tacz.guns.util;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.resource.GunDisplayInstance;
import com.tacz.guns.client.resource.index.ClientAttachmentIndex;
import com.tacz.guns.client.resource.pojo.display.LaserConfig;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LaserColorUtil {
    public static int getLaserColor(ItemStack stack, @NotNull LaserConfig defaultConfig) {
        if (stack == null) {
            return defaultConfig.getDefaultColor();
        }

        if (stack.getItem() instanceof IAttachment iAttachment) {
            if (iAttachment.hasCustomLaserColor(stack)) {
                return iAttachment.getLaserColor(stack);
            } else {
                return defaultConfig.getDefaultColor();
            }
        }

        if (stack.getItem() instanceof IGun gun) {
            if (gun.hasCustomLaserColor(stack)) {
                return gun.getLaserColor(stack);
            } else {
                return defaultConfig.getDefaultColor();
            }
        }

        return defaultConfig.getDefaultColor();
    }

    public static int getLaserColor(ItemStack stack) {
        if (stack == null) {
            return 0xFF0000;
        }

        if (stack.getItem() instanceof IAttachment iAttachment) {
            if (iAttachment.hasCustomLaserColor(stack)) {
                return iAttachment.getLaserColor(stack);
            } else {
                return TimelessAPI.getClientAttachmentIndex(iAttachment.getAttachmentId(stack))
                        .map(ClientAttachmentIndex::getLaserConfig)
                        .map(LaserConfig::getDefaultColor)
                        .orElse(0xFF0000);
            }
        }

        if (stack.getItem() instanceof IGun gun) {
            if (gun.hasCustomLaserColor(stack)) {
                return gun.getLaserColor(stack);
            } else {
                return TimelessAPI.getGunDisplay(stack)
                        .map(GunDisplayInstance::getLaserConfig)
                        .map(LaserConfig::getDefaultColor)
                        .orElse(0xFF0000);
            }
        }

        return 0xFF0000;
    }
}
