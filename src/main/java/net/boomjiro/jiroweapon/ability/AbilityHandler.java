package net.boomjiro.jiroweapon.ability;

import net.boomjiro.jiroweapon.item.ReaperBellItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class AbilityHandler {

    private static final int HYPNOSIS_DURATION = 20 * 20;     // 20s
    private static final int SHAPESHIFT_DURATION = 20 * 25;   // 25s
    private static final int ILLUSION_DURATION = 20 * 25;     // 25s
    private static final int ABILITY_COOLDOWN = 20 * 10;      // 10s

    public static void handleAbilitySelection(ServerPlayerEntity player, int id) {

        DisguiseManager.clear(player);

        switch (id) {
            case 1 -> castHypnosis(player);
            case 2 -> castShapeShifting(player);
            case 3 -> castIllusion(player);
        }
    }

    private static void castHypnosis(ServerPlayerEntity player) {
        player.removeStatusEffect(StatusEffects.INVISIBILITY);

        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.INVISIBILITY,
                HYPNOSIS_DURATION,
                0,
                false,
                false,
                true
        ));

        // ⭐ Delay disguise by 1 tick so the bell interaction finishes safely
        player.getServer().execute(() -> {
            DisguiseManager.startHypnosisDisguise(player, HYPNOSIS_DURATION);
        });

        player.getWorld().playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE,
                SoundCategory.PLAYERS,
                0.8f,
                1.2f
        );

        sendActionBar(player, "§5[Hypnosis] §7You vanish from sight.");
        applyBellCooldown(player);
    }

    private static void castShapeShifting(ServerPlayerEntity player) {

        var allPlayers = player.getServer().getPlayerManager().getPlayerList();
        String targetName = null;

        // Find someone who isn't you
        if (allPlayers.size() > 1) {
            for (var other : allPlayers) {
                if (!other.getUuid().equals(player.getUuid())) {
                    targetName = other.getGameProfile().getName();
                    break;
                }
            }
        }


        player.removeStatusEffect(StatusEffects.INVISIBILITY);
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.INVISIBILITY,
                SHAPESHIFT_DURATION,
                0,
                false,
                false,
                true
        ));

        DisguiseManager.startShapeShiftDisguise(player, targetName, SHAPESHIFT_DURATION);

        player.getWorld().playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE,
                SoundCategory.PLAYERS,
                1.0f,
                0.6f + player.getWorld().random.nextFloat() * 0.2f
        );

        if (targetName != null) {
            sendActionBar(player, "§2[Shape Shifting] §7You take on the form of §a" + targetName + "§7.");
        } else {
            sendActionBar(player, "§2[Shape Shifting] §7You take on a §aZombie §7form.");
        }

        applyBellCooldown(player);
    }

    private static void castIllusion(ServerPlayerEntity player) {

        BlockPos below = player.getBlockPos().down();
        BlockState state = player.getWorld().getBlockState(below);

        ItemStack blockStack = ItemStack.EMPTY;
        if (state.getBlock().asItem() instanceof BlockItem) {
            blockStack = new ItemStack(state.getBlock().asItem());
        }

        if (blockStack.isEmpty()) {
            blockStack = new ItemStack(Items.STONE);
        }

        player.removeStatusEffect(StatusEffects.INVISIBILITY);
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.INVISIBILITY,
                ILLUSION_DURATION,
                0,
                false,
                false,
                true
        ));

        DisguiseManager.startBlockDisguise(player, blockStack, ILLUSION_DURATION);

        player.getWorld().playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE,
                SoundCategory.PLAYERS,
                0.7f,
                1.4f
        );

        sendActionBar(player, "§3[Illusion] §7You meld into §b" + blockStack.getName().getString() + "§7.");
        applyBellCooldown(player);
    }

    private static void applyBellCooldown(ServerPlayerEntity player) {
        ItemStack main = player.getMainHandStack();
        if (main.getItem() instanceof ReaperBellItem) {
            player.getItemCooldownManager().set(main.getItem(), ABILITY_COOLDOWN);
            return;
        }

        ItemStack off = player.getOffHandStack();
        if (off.getItem() instanceof ReaperBellItem) {
            player.getItemCooldownManager().set(off.getItem(), ABILITY_COOLDOWN);
        }
    }

    private static void sendActionBar(ServerPlayerEntity player, String msg) {
        player.sendMessage(Text.literal(msg), true);
    }
}
