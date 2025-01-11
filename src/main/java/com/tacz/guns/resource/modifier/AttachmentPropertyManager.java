package com.tacz.guns.resource.modifier;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tacz.guns.GunMod;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.event.common.AttachmentPropertyEvent;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.modifier.IAttachmentModifier;
import com.tacz.guns.event.ChangeGunPropertyEvent;
import com.tacz.guns.resource.pojo.data.attachment.Modifier;
import com.tacz.guns.resource.modifier.custom.*;
import com.tacz.guns.resource.pojo.data.gun.MagazineLockType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.StringUtils;
import org.luaj.vm2.script.LuaScriptEngineFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.*;

public class AttachmentPropertyManager {
    private static final ScriptEngine LUAJ_ENGINE = new LuaScriptEngineFactory().getScriptEngine();
    private static final Map<String, IAttachmentModifier<?, ?>> MODIFIERS = Maps.newLinkedHashMap();

    public static void registerModifier() {
        MODIFIERS.put(AdsModifier.ID, new AdsModifier());
        MODIFIERS.put(AimInaccuracyModifier.ID, new AimInaccuracyModifier());
        MODIFIERS.put(AmmoSpeedModifier.ID, new AmmoSpeedModifier());
        MODIFIERS.put(ArmorIgnoreModifier.ID, new ArmorIgnoreModifier());
        MODIFIERS.put(DamageModifier.ID, new DamageModifier());
        MODIFIERS.put(EffectiveRangeModifier.ID, new EffectiveRangeModifier());
        MODIFIERS.put(ExplosionModifier.ID, new ExplosionModifier());
        MODIFIERS.put(HeatModifier.ID, new HeatModifier());
        MODIFIERS.put(HeadShotModifier.ID, new HeadShotModifier());
        MODIFIERS.put(IgniteModifier.ID, new IgniteModifier());
        MODIFIERS.put(InaccuracyModifier.ID, new InaccuracyModifier());
        MODIFIERS.put(KnockbackModifier.ID, new KnockbackModifier());
        MODIFIERS.put(PierceModifier.ID, new PierceModifier());
        MODIFIERS.put(RecoilModifier.ID, new RecoilModifier());
        MODIFIERS.put(RpmModifier.ID, new RpmModifier());
        MODIFIERS.put(SilenceModifier.ID, new SilenceModifier());
        MODIFIERS.put(WeightModifier.ID, new WeightModifier());
        MODIFIERS.put(ExtraMovementModifier.ID, new ExtraMovementModifier());
    }

    public static Map<String, IAttachmentModifier<?, ?>> getModifiers() {
        return MODIFIERS;
    }

    public static void postChangeEvent(LivingEntity shooter, ItemStack gunItem) {
        if (!(gunItem.getItem() instanceof IGun iGun)) {
            return;
        }
        ResourceLocation gunId = iGun.getGunId(gunItem);
        TimelessAPI.getCommonGunIndex(gunId).ifPresent(index -> {
            AttachmentCacheProperty cacheProperty = new AttachmentCacheProperty();
            // 发布事件
            AttachmentPropertyEvent event = new AttachmentPropertyEvent(gunItem, cacheProperty);
            ChangeGunPropertyEvent.internalOnAttachmentPropertyEvent(event);
            event.postEventToKubeJS(event);
            MinecraftForge.EVENT_BUS.post(event);
            // 更新实体的缓存对象
            IGunOperator.fromLivingEntity(shooter).updateCacheProperty(cacheProperty);
            // 做一次无限弹药检查（用于在进入和退出无限弹药模式时刷新枪械的子弹数据）
            if (!iGun.isInfiniteAmmo(gunItem, shooter)) {
                if (shooter instanceof Player player) {
                    // 做一次弹匣检查（用于处理装载扩容弹匣后装满子弹，然后装上无限弹药配件后先卸载扩容弹匣，再卸载无限弹药配件导致的子弹数目异常）
                    iGun.magazineCheck(player, gunItem);
                }
            }
        });
    }

    public static double eval(Modifier modifier, double defaultValue) {
        return eval(Collections.singletonList(modifier), defaultValue);
    }

    public static double eval(List<Modifier> modifiers, double defaultValue) {
        double addend = defaultValue;
        double percent = 1;
        double multiplier = 1;
        for (Modifier modifier : modifiers) {
            addend += modifier.getAddend();
            percent += modifier.getPercent();
            multiplier *= Math.max(modifier.getMultiplier(), 0f);
        }
        percent = Math.max(percent, 0f);
        double value = addend * percent * multiplier;
        for (Modifier modifier : modifiers) {
            String function = modifier.getFunction();
            if (StringUtils.isEmpty(function)) {
                continue;
            }
            value = functionEval(value, defaultValue, function);
        }
        return value;
    }

    public static boolean eval(List<Boolean> modified, boolean matchType) {
        if (matchType) {
            // 如果设定值为 true，那么只要有一个 false 就返回 false
            return modified.stream().allMatch(s -> s);
        } else {
            // 如果设定值为 false，那么只要有一个 true 就返回 true
            return modified.stream().anyMatch(s -> s);
        }
    }

    public static MagazineLockType eval(List<MagazineLockType> modified, MagazineLockType defaultValue) {
        // 如果默认值为非 disabled，那么只要有一个 disabled 就返回 disabled（关闭弹匣锁）
        if (defaultValue != MagazineLockType.DISABLED) {
            return modified.stream().anyMatch(s -> s == MagazineLockType.DISABLED) ? MagazineLockType.DISABLED : defaultValue;
        } else {
            // 如果默认值为 disabled，那么按照一个定好的优先级进行判断使用哪种弹匣锁（all > over_heat > assist）
            Set<MagazineLockType> magazineLockTypes = Sets.newHashSet();
            magazineLockTypes.addAll(modified);
            if (magazineLockTypes.contains(MagazineLockType.ALL)) {
                return MagazineLockType.ALL;
            }
            if (magazineLockTypes.contains(MagazineLockType.OVER_HEAT)) {
                return MagazineLockType.OVER_HEAT;
            }
            if (magazineLockTypes.contains(MagazineLockType.ASSIST)) {
                return MagazineLockType.ASSIST;
            }
            return MagazineLockType.DISABLED;
        }
    }

    public static double functionEval(double value, double defaultValue, String script) {
        script = script.toLowerCase(Locale.ENGLISH);
        LUAJ_ENGINE.put("x", value);
        LUAJ_ENGINE.put("r", defaultValue);
        try {
            LUAJ_ENGINE.eval(script);
        } catch (ScriptException e) {
            GunMod.LOGGER.catching(e);
        }
        if (LUAJ_ENGINE.get("y") instanceof Number number) {
            return number.doubleValue();
        }
        return value;
    }
}
