package com.tacz.guns.network;

import com.tacz.guns.GunMod;
import com.tacz.guns.network.message.*;
import com.tacz.guns.network.message.event.*;
import com.tacz.guns.network.message.handshake.Acknowledge;
import com.tacz.guns.network.message.handshake.ServerMessageSyncedEntityDataMapping;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class NetworkHandler {
    private static final String VERSION = "1.0.4";

    public static final SimpleChannel HANDSHAKE_CHANNEL = ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "handshake")).simpleChannel();
    public static final SimpleChannel CHANNEL = ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "network")).simpleChannel();

    private static final AtomicInteger ID_COUNT = new AtomicInteger(1);
    private static final AtomicInteger HANDSHAKE_ID_COUNT = new AtomicInteger(1);

    public static void init() {
        CHANNEL.messageBuilder(ClientMessagePlayerShoot.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_SERVER).encoder(ClientMessagePlayerShoot::encode).decoder(ClientMessagePlayerShoot::decode).consumerMainThread(ClientMessagePlayerShoot::handle).add();
        CHANNEL.messageBuilder(ClientMessagePlayerReloadGun.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_SERVER).encoder(ClientMessagePlayerReloadGun::encode).decoder(ClientMessagePlayerReloadGun::decode).consumerMainThread(ClientMessagePlayerReloadGun::handle).add();
        CHANNEL.messageBuilder(ClientMessagePlayerCancelReload.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_SERVER).encoder(ClientMessagePlayerCancelReload::encode).decoder(ClientMessagePlayerCancelReload::decode).consumerMainThread(ClientMessagePlayerCancelReload::handle).add();
        CHANNEL.messageBuilder(ClientMessagePlayerFireSelect.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_SERVER).encoder(ClientMessagePlayerFireSelect::encode).decoder(ClientMessagePlayerFireSelect::decode).consumerMainThread(ClientMessagePlayerFireSelect::handle).add();
        CHANNEL.messageBuilder(ClientMessagePlayerAim.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_SERVER).encoder(ClientMessagePlayerAim::encode).decoder(ClientMessagePlayerAim::decode).consumerMainThread(ClientMessagePlayerAim::handle).add();
        CHANNEL.messageBuilder(ClientMessagePlayerCrawl.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_SERVER).encoder(ClientMessagePlayerCrawl::encode).decoder(ClientMessagePlayerCrawl::decode).consumerMainThread(ClientMessagePlayerCrawl::handle).add();
        CHANNEL.messageBuilder(ClientMessagePlayerDrawGun.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_SERVER).encoder(ClientMessagePlayerDrawGun::encode).decoder(ClientMessagePlayerDrawGun::decode).consumerMainThread(ClientMessagePlayerDrawGun::handle).add();
        CHANNEL.messageBuilder(ServerMessageSound.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_CLIENT).encoder(ServerMessageSound::encode).decoder(ServerMessageSound::decode).consumerMainThread(ServerMessageSound::handle).add();
        CHANNEL.messageBuilder(ClientMessageCraft.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_SERVER).encoder(ClientMessageCraft::encode).decoder(ClientMessageCraft::decode).consumerMainThread(ClientMessageCraft::handle).add();
        CHANNEL.messageBuilder(ServerMessageCraft.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_CLIENT).encoder(ServerMessageCraft::encode).decoder(ServerMessageCraft::decode).consumerMainThread(ServerMessageCraft::handle).add();
        CHANNEL.messageBuilder(ClientMessagePlayerZoom.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_SERVER).encoder(ClientMessagePlayerZoom::encode).decoder(ClientMessagePlayerZoom::decode).consumerMainThread(ClientMessagePlayerZoom::handle).add();
        CHANNEL.messageBuilder(ClientMessageRefitGun.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_SERVER).encoder(ClientMessageRefitGun::encode).decoder(ClientMessageRefitGun::decode).consumerMainThread(ClientMessageRefitGun::handle).add();
        CHANNEL.messageBuilder(ServerMessageRefreshRefitScreen.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_CLIENT).encoder(ServerMessageRefreshRefitScreen::encode).decoder(ServerMessageRefreshRefitScreen::decode).consumerMainThread(ServerMessageRefreshRefitScreen::handle).add();
        CHANNEL.messageBuilder(ClientMessageUnloadAttachment.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_SERVER).encoder(ClientMessageUnloadAttachment::encode).decoder(ClientMessageUnloadAttachment::decode).consumerMainThread(ClientMessageUnloadAttachment::handle).add();
        CHANNEL.messageBuilder(ServerMessageSwapItem.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_CLIENT).encoder(ServerMessageSwapItem::encode).decoder(ServerMessageSwapItem::decode).consumerMainThread(ServerMessageSwapItem::handle).add();
        CHANNEL.messageBuilder(ClientMessagePlayerBoltGun.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_SERVER).encoder(ClientMessagePlayerBoltGun::encode).decoder(ClientMessagePlayerBoltGun::decode).consumerMainThread(ClientMessagePlayerBoltGun::handle).add();
        CHANNEL.messageBuilder(ServerMessageLevelUp.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_CLIENT).encoder(ServerMessageLevelUp::encode).decoder(ServerMessageLevelUp::decode).consumerMainThread(ServerMessageLevelUp::handle).add();
        CHANNEL.messageBuilder(ServerMessageGunHurt.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_CLIENT).encoder(ServerMessageGunHurt::encode).decoder(ServerMessageGunHurt::decode).consumerMainThread(ServerMessageGunHurt::handle).add();
        CHANNEL.messageBuilder(ServerMessageGunKill.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_CLIENT).encoder(ServerMessageGunKill::encode).decoder(ServerMessageGunKill::decode).consumerMainThread(ServerMessageGunKill::handle).add();
        CHANNEL.messageBuilder(ServerMessageUpdateEntityData.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_CLIENT).encoder(ServerMessageUpdateEntityData::encode).decoder(ServerMessageUpdateEntityData::decode).consumerMainThread(ServerMessageUpdateEntityData::handle).add();
        CHANNEL.messageBuilder(ServerMessageSyncGunPack.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_CLIENT).encoder(ServerMessageSyncGunPack::encode).decoder(ServerMessageSyncGunPack::decode).consumerMainThread(ServerMessageSyncGunPack::handle).add();
        CHANNEL.messageBuilder(ClientMessagePlayerMelee.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_SERVER).encoder(ClientMessagePlayerMelee::encode).decoder(ClientMessagePlayerMelee::decode).consumerMainThread(ClientMessagePlayerMelee::handle).add();

        CHANNEL.messageBuilder(ServerMessageGunDraw.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_CLIENT).encoder(ServerMessageGunDraw::encode).decoder(ServerMessageGunDraw::decode).consumerMainThread(ServerMessageGunDraw::handle).add();
        CHANNEL.messageBuilder(ServerMessageGunFire.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_CLIENT).encoder(ServerMessageGunFire::encode).decoder(ServerMessageGunFire::decode).consumerMainThread(ServerMessageGunFire::handle).add();
        CHANNEL.messageBuilder(ServerMessageGunFireSelect.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_CLIENT).encoder(ServerMessageGunFireSelect::encode).decoder(ServerMessageGunFireSelect::decode).consumerMainThread(ServerMessageGunFireSelect::handle).add();
        CHANNEL.messageBuilder(ServerMessageGunMelee.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_CLIENT).encoder(ServerMessageGunMelee::encode).decoder(ServerMessageGunMelee::decode).consumerMainThread(ServerMessageGunMelee::handle).add();
        CHANNEL.messageBuilder(ServerMessageGunReload.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_CLIENT).encoder(ServerMessageGunReload::encode).decoder(ServerMessageGunReload::decode).consumerMainThread(ServerMessageGunReload::handle).add();
        CHANNEL.messageBuilder(ServerMessageGunShoot.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_CLIENT).encoder(ServerMessageGunShoot::encode).decoder(ServerMessageGunShoot::decode).consumerMainThread(ServerMessageGunShoot::handle).add();
        CHANNEL.messageBuilder(ServerMessageSyncBaseTimestamp.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_CLIENT).encoder(ServerMessageSyncBaseTimestamp::encode).decoder(ServerMessageSyncBaseTimestamp::decode).consumerMainThread(ServerMessageSyncBaseTimestamp::handle).add();
        CHANNEL.messageBuilder(ClientMessageSyncBaseTimestamp.class, ID_COUNT.getAndIncrement(), NetworkDirection.PLAY_TO_SERVER).encoder(ClientMessageSyncBaseTimestamp::encode).decoder(ClientMessageSyncBaseTimestamp::decode).consumerMainThread(ClientMessageSyncBaseTimestamp::handle).add();

        registerAcknowledge();
        registerHandshakeMessage(ServerMessageSyncedEntityDataMapping.class, null);
    }

    public static void registerAcknowledge() {
        Acknowledge acknowledge = new Acknowledge();
        HANDSHAKE_CHANNEL.messageBuilder(Acknowledge.class, HANDSHAKE_ID_COUNT.getAndIncrement())
                .decoder(acknowledge::decode)
                .encoder(acknowledge::encode)
                .consumerNetworkThread(acknowledge::handle)
                .add();
    }

    public static <T extends LoginIndexHolder & IMessage<T>> void registerHandshakeMessage(Class<T> messageClass, @Nullable Function<Boolean, List<Pair<String, T>>> messages) {
        try {
            Constructor<T> constructor = messageClass.getDeclaredConstructor();
            T message = constructor.newInstance();
            HANDSHAKE_CHANNEL.messageBuilder(messageClass, HANDSHAKE_ID_COUNT.getAndIncrement(), NetworkDirection.LOGIN_TO_CLIENT)
                    .encoder(message::encode)
                    .decoder(message::decode)
                    .consumerNetworkThread(message::handle)
                    .add();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(String.format("The message %s is missing an empty parameter constructor", messageClass.getName()), e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(String.format("Unable to access the constructor of %s. Make sure the constructor is public.", messageClass.getName()), e);
        } catch (Exception e) {
            GunMod.LOGGER.error("Fail to register handshake message {}", messageClass.getName());
            e.printStackTrace();
        }
    }

    public static void sendToClientPlayer(Object message, Player player) {
        CHANNEL.send(message, PacketDistributor.PLAYER.with((ServerPlayer) player));
    }

    /**
     * 发送给所有监听此实体的玩家
     */
    public static void sendToTrackingEntityAndSelf(Entity centerEntity, Object message) {
        CHANNEL.send(message, PacketDistributor.TRACKING_ENTITY_AND_SELF.with(centerEntity));
    }

    public static void sendToAllPlayers(Object message) {
        CHANNEL.send(message, PacketDistributor.ALL.noArg());
    }

    public static void sendToTrackingEntity(Object message, final Entity centerEntity) {
        CHANNEL.send(message, PacketDistributor.TRACKING_ENTITY.with(centerEntity));
    }

    public static void sendToDimension(Object message, final Entity centerEntity) {
        ResourceKey<Level> dimension = centerEntity.level().dimension();
        CHANNEL.send(message, PacketDistributor.DIMENSION.with(dimension));
    }
}
