package com.tacz.guns.client.model.functional;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.model.IFunctionalRenderer;
import com.tacz.guns.client.resource.GunDisplayInstance;
import com.tacz.guns.client.resource.index.ClientAttachmentIndex;
import com.tacz.guns.client.resource.pojo.display.LaserConfig;
import com.tacz.guns.config.client.RenderConfig;
import com.tacz.guns.util.LaserColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class BeamRenderer implements IFunctionalRenderer {
    private final Supplier<ItemStack> itemProvider;
    private static final LaserConfig DEFAULT_LASER_CONFIG = new LaserConfig();

    public BeamRenderer(Supplier<ItemStack> itemProvider) {
        this.itemProvider = itemProvider;
    }

    @Override
    public void render(PoseStack poseStack, VertexConsumer vertexBuffer, ItemDisplayContext transformType, int light, int overlay) {
        if (itemProvider.get() == null || !transformType.firstPerson() && !(transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)) {
            return;
        }
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer builder = bufferSource.getBuffer(LaserBeamRenderState.getLaserBeam());
        poseStack.pushPose();
        {
            LaserConfig laserConfig = getLaserConfig();

            int color = LaserColorUtil.getLaserColor(itemProvider.get(), laserConfig);
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;

            stringVertex(transformType.firstPerson() ? -getLaserConfig().getLength() : -laserConfig.getLengthThird(),
                    transformType.firstPerson() ? laserConfig.getWidth() : laserConfig.getWidthThird(),
                    builder, poseStack.last(), r, g, b, RenderConfig.ENABLE_LASER_FADE_OUT.get());
        }
        poseStack.popPose();
    }

    private LaserConfig getLaserConfig() {
        if (itemProvider.get() == null) {
            return DEFAULT_LASER_CONFIG;
        }

        if (itemProvider.get().getItem() instanceof IAttachment iAttachment) {
            return TimelessAPI.getClientAttachmentIndex(iAttachment.getAttachmentId(itemProvider.get()))
                    .map(ClientAttachmentIndex::getLaserConfig)
                    .orElse(DEFAULT_LASER_CONFIG);
        }

        if (itemProvider.get().getItem() instanceof IGun) {
            return TimelessAPI.getGunDisplay(itemProvider.get())
                    .map(GunDisplayInstance::getLaserConfig)
                    .orElse(DEFAULT_LASER_CONFIG);
        }

        return DEFAULT_LASER_CONFIG;
    }

    private static void stringVertex(float z, float width, VertexConsumer pConsumer, PoseStack.Pose pPose, int r, int g, int b, boolean fadeOut) {
        float halfWidth = width / 2;
        int endAlpha = fadeOut ? 0 : 255;
    	pConsumer.vertex(pPose.pose(), -halfWidth, -halfWidth, 0).color(r, g, b, 255).normal(pPose.normal(),-1, 0, 0).endVertex();
        pConsumer.vertex(pPose.pose(), -halfWidth, halfWidth, 0).color(r, g, b, 255).normal(pPose.normal(), -1, 0, 0).endVertex();
        pConsumer.vertex(pPose.pose(), -halfWidth, halfWidth, z).color(r, g, b, endAlpha).normal(pPose.normal(), -1, 0, 0).endVertex();
        pConsumer.vertex(pPose.pose(), -halfWidth, -halfWidth, z).color(r, g, b, endAlpha).normal(pPose.normal(), -1, 0, 0).endVertex();

        pConsumer.vertex(pPose.pose(), -halfWidth, halfWidth, 0).color(r, g, b, 255).normal(pPose.normal(), 0, 1, 0).endVertex();
        pConsumer.vertex(pPose.pose(), halfWidth, halfWidth, 0).color(r, g, b, 255).normal(pPose.normal(), 0, 1, 0).endVertex();
        pConsumer.vertex(pPose.pose(), halfWidth, halfWidth, z).color(r, g, b, endAlpha).normal(pPose.normal(), 0, 1, 0).endVertex();
        pConsumer.vertex(pPose.pose(), -halfWidth, halfWidth, z).color(r, g, b, endAlpha).normal(pPose.normal(), 0, 1, 0).endVertex();

        pConsumer.vertex(pPose.pose(), halfWidth, halfWidth, 0).color(r, g, b, 255).normal(pPose.normal(), 1, 0, 0).endVertex();
        pConsumer.vertex(pPose.pose(), halfWidth, -halfWidth, 0).color(r, g, b, 255).normal(pPose.normal(), 1, 0, 0).endVertex();
        pConsumer.vertex(pPose.pose(), halfWidth, -halfWidth, z).color(r, g, b, endAlpha).normal(pPose.normal(), 1, 0, 0).endVertex();
        pConsumer.vertex(pPose.pose(), halfWidth, halfWidth, z).color(r, g, b, endAlpha).normal(pPose.normal(), 1, 0, 0).endVertex();

        pConsumer.vertex(pPose.pose(), halfWidth, -halfWidth, 0).color(r, g, b, 255).normal(pPose.normal(), 0, -1, 0).endVertex();
        pConsumer.vertex(pPose.pose(), -halfWidth, -halfWidth, 0).color(r, g, b, 255).normal(pPose.normal(), 0, -1, 0).endVertex();
        pConsumer.vertex(pPose.pose(), -halfWidth, -halfWidth, z).color(r, g, b, endAlpha).normal(pPose.normal(), 0, -1, 0).endVertex();
        pConsumer.vertex(pPose.pose(), halfWidth, -halfWidth, z).color(r, g, b, endAlpha).normal(pPose.normal(), 0, -1, 0).endVertex();
    }

    private static class LaserBeamRenderState extends RenderStateShard {
    	
        public LaserBeamRenderState(String pName, Runnable pSetupState, Runnable pClearState) {
			super(pName, pSetupState, pClearState);
		}

        protected static final RenderStateShard.TransparencyStateShard LIGHTNING_ADDITIVE_TRANSPARENCY = new RenderStateShard.TransparencyStateShard(
                "lightning_transparency", () -> {
                    RenderSystem.enableBlend();
                    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
                            GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                }, () -> {
                    RenderSystem.disableBlend();
                    RenderSystem.defaultBlendFunc();
                });

        protected static final RenderType LASER_BEAM = RenderType.create("laser_beam", DefaultVertexFormat.POSITION_COLOR_NORMAL,
                VertexFormat.Mode.QUADS, 256, false, true,
                RenderType.CompositeState.builder()
                        .setShaderState(POSITION_COLOR_SHADER)
                        .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                        .setTransparencyState(LIGHTNING_ADDITIVE_TRANSPARENCY)
                        .setOutputState(ITEM_ENTITY_TARGET)
                        .setWriteMaskState(COLOR_DEPTH_WRITE)
                        .setCullState(NO_CULL)
                        .createCompositeState(false));
    	
        public static RenderType getLaserBeam() {
            return LASER_BEAM;
        }
	}
}
