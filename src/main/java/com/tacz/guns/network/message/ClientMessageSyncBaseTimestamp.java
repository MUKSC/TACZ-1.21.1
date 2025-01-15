package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;

public class ClientMessageSyncBaseTimestamp implements CustomPacketPayload {
    public static final ClientMessageSyncBaseTimestamp INSTANCE = new ClientMessageSyncBaseTimestamp();
    public static final CustomPacketPayload.Type<ClientMessageSyncBaseTimestamp> TYPE = new CustomPacketPayload.Type<>(
        ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "client_sync_base_timestamp")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientMessageSyncBaseTimestamp> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private ClientMessageSyncBaseTimestamp() { }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static final Marker MARKER = MarkerManager.getMarker("SYNC_BASE_TIMESTAMP");

    public static void handle(ClientMessageSyncBaseTimestamp message, IPayloadContext context) {
        long timestamp = System.currentTimeMillis();
        context.enqueueWork(() -> {
            ServerPlayer entity = (ServerPlayer) context.player();
            ShooterDataHolder dataHolder = IGunOperator.fromLivingEntity(entity).getDataHolder();
            dataHolder.baseTimestamp = timestamp;
            GunMod.LOGGER.debug(MARKER, "Update server base timestamp: {}", dataHolder.baseTimestamp);
        });
    }
}
