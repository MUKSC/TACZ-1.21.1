package com.tacz.guns.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tacz.guns.GunMod;
import com.tacz.guns.api.client.animation.AnimationController;
import com.tacz.guns.api.client.animation.Animations;
import com.tacz.guns.api.client.animation.statemachine.LuaAnimationStateMachine;
import com.tacz.guns.api.client.animation.statemachine.LuaStateMachineFactory;
import com.tacz.guns.client.animation.statemachine.ThrowableAnimationStateContext;
import com.tacz.guns.client.model.BedrockAnimatedModel;
import com.tacz.guns.client.model.bedrock.BedrockModel;
import com.tacz.guns.client.model.bedrock.BedrockPart;
import com.tacz.guns.client.model.functional.LeftHandRender;
import com.tacz.guns.client.model.functional.RightHandRender;
import com.tacz.guns.client.resource.ClientAssetsManager;
import com.tacz.guns.client.resource.pojo.model.BedrockModelPOJO;
import com.tacz.guns.client.resource.pojo.model.BedrockVersion;
import com.tacz.guns.util.math.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.tacz.guns.client.model.GunModelConstant.LEFTHAND_POS_NODE;
import static com.tacz.guns.client.model.GunModelConstant.RIGHTHAND_POS_NODE;


public class AnimateGeoItemRendererWrapper extends BlockEntityWithoutLevelRenderer {
    private LuaAnimationStateMachine<ThrowableAnimationStateContext> stateMachine;
    private AnimationController controller;
    private BedrockAnimatedModel model;

    public AnimateGeoItemRendererWrapper() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    public void startThrowing() {
        stateMachine.trigger("start_use");
    }

    public void throwItem() {
        if (stateMachine.getContext().getUsingTick() >= 10) {
            stateMachine.trigger("throw");
        }
    }

    /**
     * 获取摄像机定位组的反相矩阵
     */
    @Nonnull
    private static Matrix4f getPositioningNodeInverse(List<BedrockPart> nodePath) {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.identity();
        if (nodePath != null) {
            for (int i = nodePath.size() - 1; i >= 0; i--) {
                BedrockPart part = nodePath.get(i);
                // 计算反向的旋转
                matrix4f.rotate(Axis.XN.rotation(part.xRot));
                matrix4f.rotate(Axis.YN.rotation(part.yRot));
                matrix4f.rotate(Axis.ZN.rotation(part.zRot));
                // 计算反向的位移
                if (part.getParent() != null) {
                    matrix4f.translate(-part.x / 16.0F, -part.y / 16.0F, -part.z / 16.0F);
                } else {
                    matrix4f.translate(-part.x / 16.0F, (1.5F - part.y / 16.0F), -part.z / 16.0F);
                }
            }
        }
        return matrix4f;
    }

    private static void applyFirstPersonPositioningTransform(PoseStack poseStack, BedrockAnimatedModel model, ItemStack stack) {
        Matrix4f transformMatrix = new Matrix4f();
        transformMatrix.identity();
        // 应用瞄准定位
        List<BedrockPart> idleNodePath = model.getIdleSightPath();

        Matrix4f idleViewMatrix = getPositioningNodeInverse(idleNodePath);

        // 应用瞄准变换
        MathUtil.applyMatrixLerp(transformMatrix, idleViewMatrix, transformMatrix, 1);

        // 应用变换到 PoseStack
        poseStack.translate(0, 1.5f, 0);
        poseStack.mulPoseMatrix(transformMatrix);
        poseStack.translate(0, -1.5f, 0);
    }

    public void renderFirstPerson(ItemStack stack, ItemDisplayContext ctx, PoseStack poseStack, MultiBufferSource bufferSource,
                                  int light, float partialTick) {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        if (model != null) {
            poseStack.pushPose();
            float xRotOffset = Mth.lerp(partialTick, player.xBobO, player.xBob);
            float yRotOffset = Mth.lerp(partialTick, player.yBobO, player.yBob);
            float xRot = player.getViewXRot(partialTick) - xRotOffset;
            float yRot = player.getViewYRot(partialTick) - yRotOffset;
            poseStack.mulPose(Axis.XP.rotationDegrees(xRot * -0.1F));
            poseStack.mulPose(Axis.YP.rotationDegrees(yRot * -0.1F));

            // 从渲染原点 (0, 24, 0) 移动到模型原点 (0, 0, 0)
            poseStack.translate(0, 1.5f, 0);
            // 基岩版模型是上下颠倒的，需要翻转过来。
            poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
            applyFirstPersonPositioningTransform(poseStack, model, stack);

            if (!stateMachine.isInitialized()) {
                ThrowableAnimationStateContext context = new ThrowableAnimationStateContext();
                context.setUsingTick(player.getTicksUsingItem());
                context.setPartialTicks(partialTick);

                stateMachine.setContext(context);
                stateMachine.initialize();
            }

            stateMachine.processContextIfExist(context -> {
                context.setUsingTick(player.getTicksUsingItem());
                context.setUsing(player.isUsingItem());
                context.setPartialTicks(partialTick);
            });

            stateMachine.update();

            model.render(poseStack, ctx, RenderType.entityCutout(
                    new ResourceLocation(GunMod.MOD_ID, "textures/throwable/m67_uv.png")
            ), light, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }

    @ParametersAreNonnullByDefault
    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack poseStack, MultiBufferSource bufferSource,
                             int light, int overlay) {
        if (ctx.firstPerson() || true) return;
        if (model != null) {
            poseStack.pushPose();
            // 从渲染原点 (0, 24, 0) 移动到模型原点 (0, 0, 0)
            poseStack.translate(0.5, 1.5f, 0.5);
            // 基岩版模型是上下颠倒的，需要翻转过来。
            poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
            model.render(poseStack, ctx, RenderType.entityCutout(
                    new ResourceLocation(GunMod.MOD_ID, "textures/throwable/m67_uv.png")
            ), light, overlay);
            poseStack.popPose();
        }
    }

    public void init() {
        BedrockModelPOJO modelPOJO = ClientAssetsManager.INSTANCE
                .getBedrockModelPOJO(new ResourceLocation(GunMod.MOD_ID, "throwable/m67_grenade_geo"));
        model = new BedrockAnimatedModel(modelPOJO, BedrockVersion.NEW);
        // 左手手臂
        model.setFunctionalRenderer(LEFTHAND_POS_NODE, bedrockPart -> new LeftHandRender(model));
        // 右手手臂
        model.setFunctionalRenderer(RIGHTHAND_POS_NODE, bedrockPart -> new RightHandRender(model));

        var animation = ClientAssetsManager.INSTANCE.getBedrockAnimations(new ResourceLocation(GunMod.MOD_ID, "m67_grenade"));
        controller = Animations.createControllerFromBedrock(animation, model);

        var script = ClientAssetsManager.INSTANCE.getScript(new ResourceLocation(GunMod.MOD_ID, "m67_state_machine"));

        stateMachine = new LuaStateMachineFactory<ThrowableAnimationStateContext>()
                .setController(controller)
                .setLuaScripts(script)
                .build();
    }

    public BedrockModel getModel() {
        return model;
    }

    public ResourceLocation getTextureLocation() {
        return new ResourceLocation(GunMod.MOD_ID, "textures/throwable/m67_uv.png");
    }
}
