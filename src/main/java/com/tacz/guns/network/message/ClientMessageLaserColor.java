package com.tacz.guns.network.message;

import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ClientMessageLaserColor {
    private final Map<AttachmentType, Integer> colorMap = new HashMap<>();
    private boolean applyGunColor = false;
    private int gunColor = 0;

    private int gunSlotIndex = -1;

    private ClientMessageLaserColor() {
    }

    public ClientMessageLaserColor(@NotNull ItemStack gun, int gunSlotIndex) {
        if (gun.getItem() instanceof IGun iGun) {
            for (AttachmentType type : AttachmentType.values()) {
                ItemStack attachment = iGun.getAttachment(gun, type);
                if (attachment.getItem() instanceof IAttachment iAttachment) {
                    if (iAttachment.hasCustomLaserColor(attachment)) {
                        colorMap.put(type, iAttachment.getLaserColor(attachment));
                    }
                }
            }
            if (iGun.hasCustomLaserColor(gun)) {
                this.gunColor = iGun.getLaserColor(gun);
                this.applyGunColor = true;
            }
            this.gunSlotIndex = gunSlotIndex;
        }
    }

    public static void encode(ClientMessageLaserColor message, FriendlyByteBuf buf) {
        buf.writeMap(message.colorMap, FriendlyByteBuf::writeEnum, FriendlyByteBuf::writeInt);
        buf.writeBoolean(message.applyGunColor);
        buf.writeInt(message.gunColor);
        buf.writeInt(message.gunSlotIndex);
    }

    public static ClientMessageLaserColor decode(FriendlyByteBuf buf) {
        ClientMessageLaserColor message = new ClientMessageLaserColor();
        message.colorMap.putAll(buf.readMap(buf1 -> buf.readEnum(AttachmentType.class), FriendlyByteBuf::readInt));
        message.applyGunColor = buf.readBoolean();
        message.gunColor = buf.readInt();
        message.gunSlotIndex = buf.readInt();
        return message;
    }

    public static void handle(ClientMessageLaserColor message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isServer()) {
            context.enqueueWork(() -> {
                ServerPlayer player = context.getSender();
                if (player == null || message.gunSlotIndex == -1) {
                    return;
                }
                Inventory inventory = player.getInventory();
                ItemStack gunItem = inventory.getItem(message.gunSlotIndex);
                IGun iGun = IGun.getIGunOrNull(gunItem);
                if (iGun != null) {
                    for (var entry : message.colorMap.entrySet()) {
                        AttachmentType type = entry.getKey();
                        int color = entry.getValue();
                        ItemStack attachment = iGun.getAttachment(gunItem, type);
                        if (attachment.getItem() instanceof IAttachment iAttachment) {
                            iAttachment.setLaserColor(attachment, color);
                        }
                    }
                    if (message.applyGunColor) {
                        iGun.setLaserColor(gunItem, message.gunColor);
                    }
                }
            });
        }
        context.setPacketHandled(true);
    }

}
