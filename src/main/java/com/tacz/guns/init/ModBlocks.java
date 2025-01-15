package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import com.tacz.guns.block.*;
import com.tacz.guns.block.entity.GunSmithTableBlockEntity;
import com.tacz.guns.block.entity.StatueBlockEntity;
import com.tacz.guns.block.entity.TargetBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GunMod.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, GunMod.MOD_ID);

    // 旧方块就让他独占一个了
    public static DeferredBlock<Block> GUN_SMITH_TABLE = BLOCKS.register("gun_smith_table", GunSmithTableBlockB::new);
    public static DeferredBlock<Block> WORKBENCH_111 = BLOCKS.register("workbench_a", GunSmithTableBlockA::new);
    public static DeferredBlock<Block> WORKBENCH_211 = BLOCKS.register("workbench_b", GunSmithTableBlockB::new);
    public static DeferredBlock<Block> WORKBENCH_121 = BLOCKS.register("workbench_c", GunSmithTableBlockC::new);

    public static DeferredBlock<Block> TARGET = BLOCKS.register("target", TargetBlock::new);
    public static DeferredBlock<Block> STATUE = BLOCKS.register("statue", StatueBlock::new);

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<GunSmithTableBlockEntity>> GUN_SMITH_TABLE_BE = TILE_ENTITIES.register("gun_smith_table", () -> GunSmithTableBlockEntity.TYPE);
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<TargetBlockEntity>> TARGET_BE = TILE_ENTITIES.register("target", () -> TargetBlockEntity.TYPE);
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<StatueBlockEntity>> STATUE_BE = TILE_ENTITIES.register("statue", () -> StatueBlockEntity.TYPE);
    public static final TagKey<Block> BULLET_IGNORE_BLOCKS = BlockTags.create(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "bullet_ignore"));
}
