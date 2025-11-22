package net.boomjiro.jiroweapon.mixin;

import net.boomjiro.jiroweapon.ability.DisguiseManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class PlayerRenderMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void hideArmor(AbstractClientPlayerEntity player,
                           float yaw, float tickDelta,
                           MatrixStack matrices,
                           VertexConsumerProvider vertexConsumers,
                           int light, CallbackInfo ci) {

        if (DisguiseManager.isClientDisguised(player.getUuid())) {
            PlayerEntityRenderer renderer = (PlayerEntityRenderer)(Object)this;
            PlayerEntityModel<AbstractClientPlayerEntity> model = renderer.getModel();

            model.head.visible = false;
            model.hat.visible = false;
            model.body.visible = false;
            model.jacket.visible = false;
            model.leftArm.visible = false;
            model.rightArm.visible = false;
            model.leftPants.visible = false;
            model.rightPants.visible = false;
            model.leftLeg.visible = false;
            model.rightLeg.visible = false;
        }
    }
}
