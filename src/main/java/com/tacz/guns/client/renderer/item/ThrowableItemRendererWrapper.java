package com.tacz.guns.client.renderer.item;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.client.animation.AnimationController;
import com.tacz.guns.api.client.animation.Animations;
import com.tacz.guns.api.client.animation.statemachine.LuaStateMachineFactory;
import com.tacz.guns.client.animation.statemachine.ThrowableAnimationStateContext;
import com.tacz.guns.client.model.BedrockAnimatedModel;
import com.tacz.guns.client.model.functional.LeftHandRender;
import com.tacz.guns.client.model.functional.RightHandRender;
import com.tacz.guns.client.resource.ClientAssetsManager;
import com.tacz.guns.client.resource.pojo.model.BedrockModelPOJO;
import com.tacz.guns.client.resource.pojo.model.BedrockVersion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static com.tacz.guns.client.model.GunModelConstant.LEFTHAND_POS_NODE;
import static com.tacz.guns.client.model.GunModelConstant.RIGHTHAND_POS_NODE;


public class ThrowableItemRendererWrapper extends AnimateGeoItemRenderer<BedrockAnimatedModel, ThrowableAnimationStateContext> {
    public ThrowableItemRendererWrapper() {
        super();
    }

    @Override
    public ThrowableAnimationStateContext initContext(ItemStack stack, Player player, float partialTick) {
        ThrowableAnimationStateContext context = new ThrowableAnimationStateContext();
        this.updateContext(context, stack, player, partialTick);
        return context;
    }

    @Override
    public void updateContext(ThrowableAnimationStateContext context, ItemStack stack, Player player, float partialTick) {
        context.setUsing(player.isUsingItem());
        context.setUsingTick(player.getTicksUsingItem());
        context.setPartialTicks(partialTick);
        context.setCurrentItem(stack);
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
        AnimationController controller = Animations.createControllerFromBedrock(animation, model);

        var script = ClientAssetsManager.INSTANCE.getScript(new ResourceLocation(GunMod.MOD_ID, "m67_state_machine"));

        stateMachine = new LuaStateMachineFactory<ThrowableAnimationStateContext>()
                .setController(controller)
                .setLuaScripts(script)
                .build();
    }

    @Override
    public long getPutAwayTime(ItemStack stack) {
        return 380;
    }

    @Override
    public ResourceLocation getTextureLocation(ItemStack stack) {
        return new ResourceLocation(GunMod.MOD_ID, "textures/throwable/m67_uv.png");
    }
}
