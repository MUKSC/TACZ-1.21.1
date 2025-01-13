package com.tacz.guns.network.message.handshake;

import com.tacz.guns.GunMod;
import com.tacz.guns.network.IMessage;
import com.tacz.guns.network.LoginIndexHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class Acknowledge extends LoginIndexHolder implements IMessage<Acknowledge> {
    public static final Marker ACKNOWLEDGE = MarkerManager.getMarker("HANDSHAKE_ACKNOWLEDGE");

    @Override
    public void encode(Acknowledge message, FriendlyByteBuf buffer) {
    }

    @Override
    public Acknowledge decode(FriendlyByteBuf buf) {
        return new Acknowledge();
    }

    @Override
    public void handle(Acknowledge message, CustomPayloadEvent.Context context) {
        GunMod.LOGGER.debug(ACKNOWLEDGE, "Received acknowledgement from client");
        context.setPacketHandled(true);
    }
}
