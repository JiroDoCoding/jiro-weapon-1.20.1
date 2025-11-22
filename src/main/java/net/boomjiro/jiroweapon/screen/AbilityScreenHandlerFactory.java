package net.boomjiro.jiroweapon.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

public class AbilityScreenHandlerFactory implements NamedScreenHandlerFactory {

    @Override
    public Text getDisplayName() {
        return Text.literal("Choose Ability");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return new AbilityScreenHandler(syncId, inventory);
    }
}
