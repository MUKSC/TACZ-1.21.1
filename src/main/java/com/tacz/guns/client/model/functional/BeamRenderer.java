package com.tacz.guns.client.model.functional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.model.IFunctionalRenderer;
import com.tacz.guns.client.resource.GunDisplayInstance;
import com.tacz.guns.client.resource.index.ClientAttachmentIndex;
import com.tacz.guns.client.resource.pojo.display.LaserConfig;
import com.tacz.guns.util.LaserColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class BeamRenderer implements IFunctionalRenderer {
    private final Supplier<ItemStack> itemProvider;

    public BeamRenderer(Supplier<ItemStack> itemProvider) {
        this.itemProvider = itemProvider;
    }

    @Override
    public void render(PoseStack poseStack, VertexConsumer vertexBuffer, ItemDisplayContext transformType, int light, int overlay) {
        if (itemProvider.get() == null || !transformType.firstPerson() && !(transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)) {
            return;
        }
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer builder = bufferSource.getBuffer(RenderType.lines());
        poseStack.pushPose();
        {
            int color = LaserColorUtil.getLaserColor(itemProvider.get());
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            stringVertex(0, 0, 0, builder, poseStack.last(), r, g, b, 200);
            stringVertex(0, 0, -getLaserLength(), builder, poseStack.last(), r, g, b, 0);
        }
        poseStack.popPose();
    }

    private float getLaserLength() {
        if (itemProvider.get() == null) {
            return 25;
        }

        if (itemProvider.get().getItem() instanceof IAttachment iAttachment) {
            return TimelessAPI.getClientAttachmentIndex(iAttachment.getAttachmentId(itemProvider.get()))
                    .map(ClientAttachmentIndex::getLaserConfig)
                    .map(LaserConfig::getLength)
                    .orElse(25);
        }

        if (itemProvider.get().getItem() instanceof IGun gun) {
            return TimelessAPI.getGunDisplay(itemProvider.get())
                    .map(GunDisplayInstance::getLaserConfig)
                    .map(LaserConfig::getLength)
                    .orElse(25);
        }

        return 25;
    }

    private static void stringVertex(float x, float y, float z, VertexConsumer pConsumer, PoseStack.Pose pPose, int r, int g, int b, int alpha) {
        pConsumer.vertex(pPose.pose(), x, y, z).color(r, g, b, alpha).normal(pPose.normal(), 0.0F, 0.0F, 0.0F).endVertex();
    }

}
