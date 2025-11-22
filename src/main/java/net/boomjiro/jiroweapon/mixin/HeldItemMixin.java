package net.boomjiro.jiroweapon.mixin;

import net.boomjiro.jiroweapon.ability.DisguiseManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemMixin {

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void hideHeldItem(
            LivingEntity entity,
            ItemStack stack,
            ModelTransformationMode mode,
            boolean leftHanded,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            CallbackInfo ci
    ) {
        if (entity == null) return;

        // ONLY hide for disguised players
        if (DisguiseManager.isClientDisguised(entity.getUuid())) {
            if (!stack.isEmpty()) {
                ItemStack fake = ItemStack.EMPTY;
                HeldItemRenderer self = (HeldItemRenderer)(Object)this;
                // Render an empty item instead of canceling
                self.renderItem(entity, fake, mode, leftHanded, matrices, vertexConsumers, light);
                ci.cancel();
            }
        }
    }
}
