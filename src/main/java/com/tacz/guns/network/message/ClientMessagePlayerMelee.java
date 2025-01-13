package com.tacz.guns.network.message;

import com.tacz.guns.api.entity.IGunOperator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class ClientMessagePlayerMelee {
    public static void encode(ClientMessagePlayerMelee message, FriendlyByteBuf buf) {
    }

    public static ClientMessagePlayerMelee decode(FriendlyByteBuf buf) {
        return new ClientMessagePlayerMelee();
    }

    public static void handle(ClientMessagePlayerMelee message, CustomPayloadEvent.Context context) {
        if (context.isServerSide()) {
            context.enqueueWork(() -> {
                ServerPlayer entity = context.getSender();
                if (entity == null) {
                    return;
                }
                IGunOperator.fromLivingEntity(entity).melee();
            });
        }
        context.setPacketHandled(true);
    }
}
