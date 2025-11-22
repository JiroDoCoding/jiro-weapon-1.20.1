package net.boomjiro.jiroweapon.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;

public class AbilityScreenHandler extends ScreenHandler {

    public AbilityScreenHandler(int syncId, PlayerInventory inv) {
        super(ModScreenHandlers.ABILITY_SCREEN, syncId);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public net.minecraft.item.ItemStack quickMove(PlayerEntity player, int slot) {
        return net.minecraft.item.ItemStack.EMPTY;
    }
}
