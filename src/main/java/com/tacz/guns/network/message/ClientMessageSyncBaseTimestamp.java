package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class ClientMessageSyncBaseTimestamp {
    private static final Marker MARKER = MarkerManager.getMarker("SYNC_BASE_TIMESTAMP");

    public ClientMessageSyncBaseTimestamp() { }

    public static void encode(ClientMessageSyncBaseTimestamp message, FriendlyByteBuf buf) { }

    public static ClientMessageSyncBaseTimestamp decode(FriendlyByteBuf buf) {
        return new ClientMessageSyncBaseTimestamp();
    }

    public static void handle(ClientMessageSyncBaseTimestamp message, CustomPayloadEvent.Context context) {
        if (context.isServerSide()) {
            long timestamp = System.currentTimeMillis();
            context.enqueueWork(() -> {
                ServerPlayer entity = context.getSender();
                if (entity == null) {
                    return;
                }
                ShooterDataHolder dataHolder = IGunOperator.fromLivingEntity(entity).getDataHolder();
                dataHolder.baseTimestamp = timestamp;
                GunMod.LOGGER.debug(MARKER, "Update server base timestamp: {}", dataHolder.baseTimestamp);
            });
        }
        context.setPacketHandled(true);
    }
}
