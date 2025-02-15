package com.tacz.guns.resource.modifier.custom;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import com.tacz.guns.api.GunProperties;
import com.tacz.guns.api.modifier.CacheValue;
import com.tacz.guns.api.modifier.IAttachmentModifier;
import com.tacz.guns.api.modifier.JsonProperty;
import com.tacz.guns.resource.CommonAssetsManager;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.resource.pojo.data.attachment.Modifier;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.resource.pojo.data.gun.HeatData;
import com.tacz.guns.resource.pojo.data.gun.MagazineLockType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public class HeatModifier implements IAttachmentModifier<HeatModifier.HeatModifierValue, HeatData> {
    public static final String ID = GunProperties.HEAT.name();

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public JsonProperty<HeatModifierValue> readJson(String json) {
        HeatModifier.Data data = CommonAssetsManager.GSON.fromJson(json, HeatModifier.Data.class);
        return new HeatModifier.HeatJsonProperty(data.getHeat());
    }

    @Override
    public CacheValue<HeatData> initCache(ItemStack gunItem, GunData gunData) {
        HeatData heatData = gunData.getHeatData();
        if (heatData == null) {
            heatData = new HeatData(false, false, MagazineLockType.DISABLED, 200, 2, 2, 0.2f, 2f);
        }
        return new CacheValue<>(heatData);
    }

    @Override
    public void eval(List<HeatModifierValue> modifiedValues, CacheValue<HeatData> cache) {
        HeatData cacheValue = cache.getValue();

        List<Boolean> useHeatValues = Lists.newArrayList();
        useHeatValues.add(cacheValue.isUseHeat());
        List<Boolean> infiniteAmmoValues = Lists.newArrayList();
        infiniteAmmoValues.add(cacheValue.isInfiniteAmmo());
        List<MagazineLockType> magazineLockTypeValues = Lists.newArrayList();
        magazineLockTypeValues.add(cacheValue.getMagazineLockType());
        List<Modifier> upperLimitValues = Lists.newArrayList();
        List<Modifier> heatRateValues = Lists.newArrayList();
        List<Modifier> coolingRateValues = Lists.newArrayList();
        List<Modifier> coolingDelayValues = Lists.newArrayList();
        List<Modifier> overHeatTimeValues = Lists.newArrayList();

        modifiedValues.forEach(v -> {
            useHeatValues.add(v.useHeat);
            infiniteAmmoValues.add(v.infiniteAmmo);
            magazineLockTypeValues.add(v.magazineLockType);
            upperLimitValues.add(v.upperLimit);
            heatRateValues.add(v.heatRate);
            coolingRateValues.add(v.coolingRate);
            coolingDelayValues.add(v.coolingDelay);
            overHeatTimeValues.add(v.overHeatTime);
        });

        boolean useHeat = cacheValue.isUseHeat() || AttachmentPropertyManager.eval(useHeatValues, false);
        // 如果没有过热，那就没必要计算后面数值了
        if (!useHeat) {
           return;
        }
        boolean infiniteAmmo = AttachmentPropertyManager.eval(infiniteAmmoValues, false);
        MagazineLockType magazineLockType = AttachmentPropertyManager.eval(magazineLockTypeValues, cacheValue.getMagazineLockType());
        int upperLimit = (int) AttachmentPropertyManager.eval(upperLimitValues, cacheValue.getUpperLimit());
        int heatRate = (int) AttachmentPropertyManager.eval(heatRateValues, cacheValue.getHeatRate());
        int coolingRate = (int) AttachmentPropertyManager.eval(coolingRateValues, cacheValue.getCoolingRate());
        float coolingDelay = (float) AttachmentPropertyManager.eval(coolingDelayValues, cacheValue.getCoolingDelay());
        float overHeatTime = (float) AttachmentPropertyManager.eval(overHeatTimeValues, cacheValue.getOverHeatTime());
        HeatData heatData = new HeatData(true, infiniteAmmo, magazineLockType, upperLimit, heatRate, coolingRate, coolingDelay, overHeatTime);
        cache.setValue(heatData);
    }

    public static class HeatJsonProperty extends JsonProperty<HeatModifier.HeatModifierValue> {
        public HeatJsonProperty(HeatModifier.HeatModifierValue value) {
            super(value);
        }

        @Override
        public void initComponents() {
            HeatModifier.HeatModifierValue modifierValue = getValue();
            if (modifierValue == null) {
                return;
            }
            int upperLimit = (int) AttachmentPropertyManager.eval(modifierValue.upperLimit, 200);
            int heatRate = (int) AttachmentPropertyManager.eval(modifierValue.heatRate, 2);
            int coolingRate = (int) AttachmentPropertyManager.eval(modifierValue.coolingRate, 2);
            float coolingDelay = (float) AttachmentPropertyManager.eval(modifierValue.coolingDelay, 0.2f);
            float overHeatTime = (float) AttachmentPropertyManager.eval(modifierValue.overHeatTime, 2f);
            if (modifierValue.useHeat) {
                components.add(Component.translatable("tooltip.tacz.attachment.heat").withStyle(ChatFormatting.GOLD));
            }
            if (modifierValue.infiniteAmmo) {
                components.add(Component.translatable("tooltip.tacz.attachment.heat.infinite_ammo").withStyle(ChatFormatting.GOLD));
            }
            if (modifierValue.magazineLockType == MagazineLockType.DISABLED) {
                components.add(Component.translatable("tooltip.tacz.attachment.heat.magazine_lock.disabled").withStyle(ChatFormatting.GOLD));
            }
            if (modifierValue.magazineLockType == MagazineLockType.OVER_HEAT) {
                components.add(Component.translatable("tooltip.tacz.attachment.heat.magazine_lock.over_heat").withStyle(ChatFormatting.GOLD));
            }
            if (modifierValue.magazineLockType == MagazineLockType.ALL) {
                components.add(Component.translatable("tooltip.tacz.attachment.heat.magazine_lock.all").withStyle(ChatFormatting.GOLD));
            }
            if (modifierValue.magazineLockType == MagazineLockType.ASSIST) {
                components.add(Component.translatable("tooltip.tacz.attachment.heat.magazine_lock.assist").withStyle(ChatFormatting.GOLD));
            }
            if (upperLimit > 200) {
                components.add(Component.translatable("tooltip.tacz.attachment.heat.upper_limit.increase").withStyle(ChatFormatting.GREEN));
            }
            if (upperLimit < 200) {
                components.add(Component.translatable("tooltip.tacz.attachment.heat.upper_limit.decrease").withStyle(ChatFormatting.RED));
            }
            if (heatRate > 2) {
                components.add(Component.translatable("tooltip.tacz.attachment.heat.heat_rate.increase").withStyle(ChatFormatting.RED));
            }
            if (heatRate < 2) {
                components.add(Component.translatable("tooltip.tacz.attachment.heat.heat_rate.decrease").withStyle(ChatFormatting.GREEN));
            }
            if (coolingRate > 2) {
                components.add(Component.translatable("tooltip.tacz.attachment.heat.cooling_rate.increase").withStyle(ChatFormatting.GREEN));
            }
            if (coolingRate < 2) {
                components.add(Component.translatable("tooltip.tacz.attachment.heat.cooling_rate.decrease").withStyle(ChatFormatting.RED));
            }
            if (coolingDelay > 0.2f) {
                components.add(Component.translatable("tooltip.tacz.attachment.heat.cooling_delay.increase").withStyle(ChatFormatting.RED));
            }
            if (coolingDelay < 0.2f) {
                components.add(Component.translatable("tooltip.tacz.attachment.heat.cooling_delay.decrease").withStyle(ChatFormatting.GREEN));
            }
            if (overHeatTime > 2f) {
                components.add(Component.translatable("tooltip.tacz.attachment.heat.over_heat_time.increase").withStyle(ChatFormatting.RED));
            }
            if (overHeatTime < 2f) {
                components.add(Component.translatable("tooltip.tacz.attachment.heat.over_heat_time.decrease").withStyle(ChatFormatting.GREEN));
            }
        }
    }

    private static class Data {
        @Nullable
        @SerializedName("heat")
        private HeatModifier.HeatModifierValue heat = null;

        @Nullable
        public HeatModifier.HeatModifierValue getHeat() {
            return heat;
        }
    }

    public static class HeatModifierValue {
        /**
         * 需要显式开启过热！
         */
        @SerializedName("use_heat")
        private boolean useHeat = false;

        @SerializedName("infinite_ammo")
        private boolean infiniteAmmo = false;

        @SerializedName("magazine_lock_type")
        private MagazineLockType magazineLockType = MagazineLockType.DISABLED;

        @SerializedName("upper_limit")
        private Modifier upperLimit = new Modifier();

        @SerializedName("heat_rate")
        private Modifier heatRate = new Modifier();

        @SerializedName("cooling_rate")
        private Modifier coolingRate = new Modifier();

        @SerializedName("cooling_delay")
        private Modifier coolingDelay = new Modifier();

        @SerializedName("over_heat_time")
        private Modifier overHeatTime = new Modifier();
    }
}
