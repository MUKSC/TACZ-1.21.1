package com.tacz.guns.network.message;

import com.tacz.guns.inventory.GunSmithTableMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class ClientMessageCraft {
    private final ResourceLocation recipeId;
    private final int menuId;

    public ClientMessageCraft(ResourceLocation recipeId, int menuId) {
        this.recipeId = recipeId;
        this.menuId = menuId;
    }

    public static void encode(ClientMessageCraft message, FriendlyByteBuf buf) {
        buf.writeResourceLocation(message.recipeId);
        buf.writeVarInt(message.menuId);
    }

    public static ClientMessageCraft decode(FriendlyByteBuf buf) {
        return new ClientMessageCraft(buf.readResourceLocation(), buf.readVarInt());
    }

    public static void handle(ClientMessageCraft message, CustomPayloadEvent.Context context) {
        if (context.isServerSide()) {
            context.enqueueWork(() -> {
                ServerPlayer entity = context.getSender();
                if (entity == null) {
                    return;
                }
                if (entity.containerMenu.containerId == message.menuId && entity.containerMenu instanceof GunSmithTableMenu menu) {
                    menu.doCraft(message.recipeId, entity);
                }
            });
        }
        context.setPacketHandled(true);
    }
}
