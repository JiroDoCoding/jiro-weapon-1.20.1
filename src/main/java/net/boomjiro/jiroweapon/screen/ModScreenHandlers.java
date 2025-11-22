package net.boomjiro.jiroweapon.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreenHandlers {

    public static ScreenHandlerType<AbilityScreenHandler> ABILITY_SCREEN;

    public static void register() {
        ABILITY_SCREEN = ScreenHandlerRegistry.registerSimple(
                new net.minecraft.util.Identifier("jiroweapon", "ability_screen"),
                AbilityScreenHandler::new
        );
    }
}
