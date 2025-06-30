/*
 * This file contains code originally derived from the Iris Shaders mod
 * licensed under the GNU LGPL v3 License.
 *
 * As permitted by section 3 of the GNU Lesser General Public License v3,
 * this file is now licensed under the GNU GPL v3 License.
 *
 * This file has been modified by MUKSC on 2025-06-24.
 * The modifications were made to remove dependency on the original project.
 *
 * Copyright (C) 2025  MUKSC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.tacz.guns.client.renderer.other;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tacz.guns.compat.iris.IrisCompat;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class HandRenderer {
    public static final HandRenderer INSTANCE = new HandRenderer();
    public static final float DEPTH = 0.125F;
    private boolean ACTIVE;

    public static Matrix4fc projection;

    private PoseStack setupGlState(GameRenderer gameRenderer, Camera camera, float tickDelta) {
        final PoseStack poseStack = new PoseStack();

        // We need to scale the matrix by 0.125 so the hand doesn't clip through blocks.
        Matrix4f scaleMatrix = new Matrix4f().scale(1F, 1F, DEPTH);
        scaleMatrix.mul(gameRenderer.getProjectionMatrix(gameRenderer.getFov(camera, tickDelta, false)));
        gameRenderer.resetProjectionMatrix(scaleMatrix);

        poseStack.setIdentity();

        gameRenderer.bobHurt(poseStack, tickDelta);

        if (Minecraft.getInstance().options.bobView().get()) {
            gameRenderer.bobView(poseStack, tickDelta);
        }

        return poseStack;
    }

    private boolean canRender(Camera camera, GameRenderer gameRenderer) {
        return !(!gameRenderer.renderHand
            || camera.isDetached()
            || !(camera.getEntity() instanceof Player)
            || gameRenderer.panoramicMode
            || Minecraft.getInstance().options.hideGui
            || (camera.getEntity() instanceof LivingEntity && ((LivingEntity) camera.getEntity()).isSleeping())
            || Minecraft.getInstance().gameMode.getPlayerMode() == GameType.SPECTATOR);
    }

    public void renderSolid(float tickDelta, Camera camera, GameRenderer gameRenderer, MultiBufferSource.BufferSource bufferSource) {
        if (!canRender(camera, gameRenderer) || IrisCompat.isPackInUseQuick()) return;

        ACTIVE = true;

        PoseStack poseStack = setupGlState(gameRenderer, camera, tickDelta);

        poseStack.pushPose();

        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.getModelViewStack().set(poseStack.last().pose());
        RenderSystem.applyModelViewMatrix();

        gameRenderer.itemInHandRenderer.renderHandsWithItems(tickDelta, new PoseStack(), bufferSource, Minecraft.getInstance().player, Minecraft.getInstance().getEntityRenderDispatcher().getPackedLightCoords(camera.getEntity(), tickDelta));

        Minecraft.getInstance().getProfiler().pop();

        bufferSource.endBatch();

        gameRenderer.resetProjectionMatrix(new Matrix4f(projection));

        poseStack.popPose();
        RenderSystem.getModelViewStack().popMatrix();
        RenderSystem.applyModelViewMatrix();

        ACTIVE = false;
    }

    public boolean isActive() {
        return ACTIVE;
    }
}
