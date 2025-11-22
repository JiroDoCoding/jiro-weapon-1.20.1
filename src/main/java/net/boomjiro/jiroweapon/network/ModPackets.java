package net.boomjiro.jiroweapon.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ModPackets {

    public static final Identifier OPEN_ABILITY_GUI =
            new Identifier("jiroweapon", "open_ability_gui");

    public static final Identifier SELECT_ABILITY =
            new Identifier("jiroweapon", "select_ability");

    public static void sendOpenAbilityGui() {
        ClientPlayNetworking.send(OPEN_ABILITY_GUI, net.fabricmc.fabric.api.networking.v1.PacketByteBufs.empty());
    }

    public static void sendSelectAbility(int abilityId) {
        PacketByteBuf buf = net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create();
        buf.writeVarInt(abilityId);
        ClientPlayNetworking.send(SELECT_ABILITY, buf);
    }
}
