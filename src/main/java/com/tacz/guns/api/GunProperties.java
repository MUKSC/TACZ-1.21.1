package com.tacz.guns.api;

import com.google.common.reflect.TypeToken;
import com.tacz.guns.api.modifier.ParameterizedCachePair;
import com.tacz.guns.resource.modifier.custom.InaccuracyModifier;
import com.tacz.guns.resource.pojo.data.gun.*;
import it.unimi.dsi.fastutil.Pair;

import java.util.LinkedList;
import java.util.Map;

/**
 * 用于AttachmentCacheProperty的，类型安全的key
 */
public class GunProperties {
    public static final GunProperty<Float>                                      ADS_TIME            = GunProperty.of("ads", Float.class);
    /**@deprecated
     * 此类是一个意外和设计失误，其功能和{@link InaccuracyModifier}完全重复<br/>
     * 已不再使用，内部的所有方法实际不会执行，请使用 {@link InaccuracyModifier} <br/>
     *
     * 同时，此Modifier的id也已经被重定向到 {@link InaccuracyModifier} <br/>
     * */
    @Deprecated
    public static final GunProperty<Map<InaccuracyType, Float>>                 AIM_INACCURACY      = GunProperty.of("inaccuracy", new TypeToken<>() {});
    public static final GunProperty<Float>                                      AMMO_SPEED          = GunProperty.of("ammo_speed", Float.class);
    public static final GunProperty<Float>                                      ARMOR_IGNORE        = GunProperty.of("armor_ignore", Float.class);
    public static final GunProperty<LinkedList<ExtraDamage.DistanceDamagePair>> DAMAGE              = GunProperty.of("damage", new TypeToken<>() {});
    public static final GunProperty<Float>                                      EFFECTIVE_RANGE     = GunProperty.of("effective_range", Float.class);
    public static final GunProperty<ExplosionData>                              EXPLOSION           = GunProperty.of("explosion", ExplosionData.class);
    public static final GunProperty<MoveSpeed>                                  MOVE_SPEED          = GunProperty.of("movement_speed", MoveSpeed.class);
    public static final GunProperty<Float>                                      HEADSHOT_MULTIPLIER = GunProperty.of("head_shot", Float.class);
    public static final GunProperty<Ignite>                                     IGNITE              = GunProperty.of("ignite", Ignite.class);
    public static final GunProperty<Map<InaccuracyType, Float>>                 INACCURACY          = GunProperty.of("inaccuracy", new TypeToken<>() {});
    public static final GunProperty<Float>                                      KNOCKBACK           = GunProperty.of("knockback", Float.class);
    public static final GunProperty<Integer>                                    PIERCE              = GunProperty.of("pierce", Integer.class);
    public static final GunProperty<ParameterizedCachePair<Float, Float>>       RECOIL              = GunProperty.of("recoil", new TypeToken<>() {});
    public static final GunProperty<Integer>                                    ROUNDS_PER_MINUTE   = GunProperty.of("rpm", Integer.class);
    public static final GunProperty<Pair<Integer, Boolean>>                     SILENCE             = GunProperty.of("silence", new TypeToken<>() {});
    public static final GunProperty<Float>                                      WEIGHT              = GunProperty.of("weight_modifier", Float.class);

    private GunProperties() {
    }
}
