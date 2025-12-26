package com.tacz.guns.compat.ar;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.VertexConsumerExtension;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class AcceleratedBeamRenderer implements IAcceleratedRenderer<BeamRenderContext> {

	public static final AcceleratedBeamRenderer INSTANCE = new AcceleratedBeamRenderer();

	@Override
	public void render(VertexConsumer vertexConsumer, BeamRenderContext context, Matrix4f transform, Matrix3f normal, int light, int overlay, int color) {
		var halfWidth = context.width() / 2;
		var z = context.z();
		var endColor =  (context.fadeOut() ? 0 : 255) << 24 | color & 0xFF_FF_FF;
		var extension = VertexConsumerExtension.getAccelerated(vertexConsumer);

		extension.beginTransform(transform, normal);

		vertexConsumer.vertex(-halfWidth, -halfWidth, 0).color(color).uv(0, 0).uv2(light).overlayCoords(overlay).normal(-1, 0, 0).endVertex();
		vertexConsumer.vertex(-halfWidth, halfWidth, 0).color(color).uv(0, 1).uv2(light).overlayCoords(overlay).normal(-1, 0, 0).endVertex();
		vertexConsumer.vertex(-halfWidth, halfWidth, z).color(endColor).uv(1, 1).uv2(light).overlayCoords(overlay).normal(-1, 0, 0).endVertex();
		vertexConsumer.vertex(-halfWidth, -halfWidth, z).color(endColor).uv(1, 0).uv2(light).overlayCoords(overlay).normal(-1, 0, 0).endVertex();

		extension.beginTransform(transform, normal);

		vertexConsumer.vertex(-halfWidth, halfWidth, 0).color(color).uv(0, 0).uv2(light).overlayCoords(overlay).normal(0, 1, 0).endVertex();
		vertexConsumer.vertex(halfWidth, halfWidth, 0).color(color).uv(0, 1).uv2(light).overlayCoords(overlay).normal(0, 1, 0).endVertex();
		vertexConsumer.vertex(halfWidth, halfWidth, z).color(endColor).uv(1, 1).uv2(light).overlayCoords(overlay).normal(0, 1, 0).endVertex();
		vertexConsumer.vertex(-halfWidth, halfWidth, z).color(endColor).uv(1, 0).uv2(light).overlayCoords(overlay).normal(0, 1, 0).endVertex();

		extension.beginTransform(transform, normal);

		vertexConsumer.vertex(halfWidth, halfWidth, 0).color(color).uv(0, 0).uv2(light).overlayCoords(overlay).normal(1, 0, 0).endVertex();
		vertexConsumer.vertex(halfWidth, -halfWidth, 0).color(color).uv(0, 1).uv2(light).overlayCoords(overlay).normal(1, 0, 0).endVertex();
		vertexConsumer.vertex(halfWidth, -halfWidth, z).color(endColor).uv(1, 1).uv2(light).overlayCoords(overlay).normal(1, 0, 0).endVertex();
		vertexConsumer.vertex(halfWidth, halfWidth, z).color(endColor).uv(1, 0).uv2(light).overlayCoords(overlay).normal(1, 0, 0).endVertex();

		extension.beginTransform(transform, normal);

		vertexConsumer.vertex(halfWidth, -halfWidth, 0).color(color).uv(0, 1).uv2(light).overlayCoords(overlay).normal(0, -1, 0).endVertex();
		vertexConsumer.vertex(-halfWidth, -halfWidth, 0).color(color).uv(0, 1).uv2(light).overlayCoords(overlay).normal(0, -1, 0).endVertex();
		vertexConsumer.vertex(-halfWidth, -halfWidth, z).color(endColor).uv(1, 1).uv2(light).overlayCoords(overlay).normal(0, -1, 0).endVertex();
		vertexConsumer.vertex(halfWidth, -halfWidth, z).color(endColor).uv(1, 0).uv2(light).overlayCoords(overlay).normal(0, -1, 0).endVertex();

		extension.endTransform();
	}

}
