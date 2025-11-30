package com.tacz.guns.api;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 文档性质的注解。被注解的枪械属性在配件缓存中的值可以被逻辑脚本修改。
 * 
 * @see com.tacz.guns.resource.modifier.AttachmentPropertyManager#postChangeEvent(LivingEntity, ItemStack)
 * @author ChloePrime
 * @since 1.1.7
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface CacheModifiableByScript {
}
