package net.boomjiro.jiroweapon;

import net.boomjiro.jiroweapon.ability.DisguiseManager;
import net.boomjiro.jiroweapon.entity.ModEntities;
import net.boomjiro.jiroweapon.item.ModItemGroups;
import net.boomjiro.jiroweapon.item.Moditems;
import net.boomjiro.jiroweapon.network.ModPackets;
import net.boomjiro.jiroweapon.screen.AbilityScreenHandlerFactory;
import net.boomjiro.jiroweapon.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiroWeapon implements ModInitializer {
    public static final String MOD_ID = "jiroweapon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItemGroups.registerItemGroups();
        Moditems.registerModItems();
        ModEntities.registerModEntities();
        ModScreenHandlers.register();

        DisguiseManager.init();

        ServerPlayNetworking.registerGlobalReceiver(ModPackets.OPEN_ABILITY_GUI,
                (server, player, handler, buf, responseSender) ->
                        server.execute(() ->
                                player.openHandledScreen(new AbilityScreenHandlerFactory())
                        )
        );

        ServerPlayNetworking.registerGlobalReceiver(ModPackets.SELECT_ABILITY,
                (server, player, handler, buf, responseSender) -> {
                    int abilityId = buf.readVarInt();
                    server.execute(() ->
                            net.boomjiro.jiroweapon.ability.AbilityHandler.handleAbilitySelection(player, abilityId)
                    );
                });
    }
}
