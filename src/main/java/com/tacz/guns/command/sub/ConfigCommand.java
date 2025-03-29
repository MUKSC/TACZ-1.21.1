package com.tacz.guns.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.tacz.guns.config.sync.SyncConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.server.command.EnumArgument;

public class ConfigCommand {
    private static final String CONFIG_NAME = "config";
    private static final String KEY = "key";
    private static final String ENABLE = "state";

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        var config = Commands.literal(CONFIG_NAME);
        var configKey = Commands.argument(KEY, EnumArgument.enumArgument(ConfigKey.class));
        var state = Commands.argument(ENABLE, BoolArgumentType.bool());
        return config.then(configKey.then(state.executes(ConfigCommand::setConfig)));
    }

    private static int setConfig(CommandContext<CommandSourceStack> context) {
        ConfigKey key = context.getArgument(KEY, ConfigKey.class);
        boolean state = BoolArgumentType.getBool(context, ENABLE);

        if (ConfigKey.defaultTableLimit.equals(key)) {
            SyncConfig.ENABLE_TABLE_FILTER.set(state);
            context.getSource().sendSystemMessage(Component.translatable("commands.tacz.config.default_table_limit." + (state ? "enabled" : "disabled")));
        }

        return Command.SINGLE_SUCCESS;
    }

    public enum ConfigKey {
        defaultTableLimit,
    }
}
