package com.tacz.guns.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.tacz.guns.client.resource.ClientAssetsManager;
import com.tacz.guns.resource.CommonAssetsManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;

public class ReloadCommand {
    private static final String RELOAD_NAME = "reload";

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        LiteralArgumentBuilder<CommandSourceStack> reload = Commands.literal(RELOAD_NAME);
        reload.executes(ReloadCommand::reloadAllPack);
        return reload;
    }

    private static int reloadAllPack(CommandContext<CommandSourceStack> context) {
        StopWatch watch = StopWatch.createStarted();
        {
            if (FMLEnvironment.dist == Dist.CLIENT) ReloadCommand.reloadClient();
            if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) CommonAssetsManager.reloadAllPack();
        }
        watch.stop();
        double time = watch.getTime(TimeUnit.MICROSECONDS) / 1000.0;
//        if (context.getSource().getEntity() instanceof ServerPlayer serverPlayer) {
//
//            if (OtherConfig.DEFAULT_PACK_DEBUG.get()) {
//                serverPlayer.sendSystemMessage(Component.translatable("commands.tacz.reload.overwrite_off"));
//                serverPlayer.sendSystemMessage(Component.translatable("commands.tacz.reload.overwrite_command.off"));
//            } else {
//                serverPlayer.sendSystemMessage(Component.translatable("commands.tacz.reload.overwrite_on"));
//                serverPlayer.sendSystemMessage(Component.translatable("commands.tacz.reload.overwrite_command.on"));
//                serverPlayer.sendSystemMessage(Component.translatable("commands.tacz.reload.backup"));
//            }
//        }
//        GunMod.LOGGER.info("Model loading time: {} ms", time);
        context.getSource().sendSystemMessage(Component.translatable("commands.tacz.reload.success", time));
        return Command.SINGLE_SUCCESS;
    }

    public static void reloadClient() {
        ClientAssetsManager.reloadAllPack();
    }
}
