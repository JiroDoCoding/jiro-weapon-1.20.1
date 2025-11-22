package net.boomjiro.jiroweapon.network;

import net.boomjiro.jiroweapon.screen.AbilityScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public class OpenGuiPacket {

    public static void receive(ServerPlayerEntity player) {
        player.openHandledScreen(new AbilityScreenHandlerFactory());
    }
}
