package com.tacz.guns.api;

import net.minecraft.world.phys.Vec3;

import java.lang.annotation.*;

/**
 * 文档性质的注解。被注解的枪械属性生效时的值可以被逻辑脚本修改。
 * 
 * @see com.tacz.guns.entity.EntityKineticBullet
 * @see com.tacz.guns.entity.EntityKineticBullet#getDamage(Vec3)
 * @see com.tacz.guns.item.ModernKineticGunScriptAPI#shootOnce(boolean)
 * @author ChloePrime
 * @since 1.1.7
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(Holder.class)
public @interface ValueModifiableAtRuntime {
    /**
     * 运行时值的类型
     */
    Class<?> value();
}


/**
 * @author ChloePrime
 * @since 1.1.7
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
@interface Holder {
    ValueModifiableAtRuntime[] value();
}
