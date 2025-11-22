package net.boomjiro.jiroweapon.mixin;

import net.boomjiro.jiroweapon.network.ModPackets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class AttackInputMixin {

    private boolean oldAttackState = false;

    @Inject(method = "handleInputEvents", at = @At("HEAD"))
    private void jiroweapon_onLeftClick(CallbackInfo ci) {

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;

        ClientPlayerEntity player = client.player;
        if (player == null) return;

        KeyBinding attack = client.options.attackKey;

        boolean isPressed = attack.isPressed();

        if (isPressed && !oldAttackState) {

            if (player.isSneaking()
                    && player.getMainHandStack() != null
                    && player.getMainHandStack().getItem().getTranslationKey().contains("reaper_bell")) {

                ModPackets.sendOpenAbilityGui();
            }
        }

        oldAttackState = isPressed;
    }
}