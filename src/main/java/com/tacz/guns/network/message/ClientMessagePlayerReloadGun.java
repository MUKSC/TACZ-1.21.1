package com.tacz.guns.network.message;

import com.tacz.guns.api.entity.IGunOperator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class ClientMessagePlayerReloadGun {
    public ClientMessagePlayerReloadGun() {
    }

    public static void encode(ClientMessagePlayerReloadGun message, FriendlyByteBuf buf) {
    }

    public static ClientMessagePlayerReloadGun decode(FriendlyByteBuf buf) {
        return new ClientMessagePlayerReloadGun();
    }

    public static void handle(ClientMessagePlayerReloadGun message, CustomPayloadEvent.Context context) {
        if (context.isServerSide()) {
            context.enqueueWork(() -> {
                ServerPlayer entity = context.getSender();
                if (entity == null) {
                    return;
                }
                IGunOperator.fromLivingEntity(entity).reload();
            });
        }
        context.setPacketHandled(true);
    }
}
