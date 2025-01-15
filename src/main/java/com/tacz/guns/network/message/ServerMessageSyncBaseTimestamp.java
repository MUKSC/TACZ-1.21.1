package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.client.gameplay.LocalPlayerDataHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ServerMessageSyncBaseTimestamp implements CustomPacketPayload {
    public static final ServerMessageSyncBaseTimestamp INSTANCE = new ServerMessageSyncBaseTimestamp();
    public static final CustomPacketPayload.Type<ServerMessageSyncBaseTimestamp> TYPE = new CustomPacketPayload.Type<>(
        ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "server_sync_base_timestamp")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerMessageSyncBaseTimestamp> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private ServerMessageSyncBaseTimestamp() { }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static final Marker MARKER = MarkerManager.getMarker("SYNC_BASE_TIMESTAMP");

    public static void handle(ServerMessageSyncBaseTimestamp message, IPayloadContext context) {
        long timestamp = System.currentTimeMillis();
        context.enqueueWork(() -> updateBaseTimestamp(timestamp));
        context.reply(ClientMessageSyncBaseTimestamp.INSTANCE);
    }

    @OnlyIn(Dist.CLIENT)
    private static void updateBaseTimestamp(long timestamp) {
        LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);
        LocalPlayerDataHolder dataHolder = IClientPlayerGunOperator.fromLocalPlayer(player).getDataHolder();
        dataHolder.clientBaseTimestamp = timestamp;
        GunMod.LOGGER.debug(MARKER, "Update client base timestamp: {}", dataHolder.clientBaseTimestamp);
    }
}
