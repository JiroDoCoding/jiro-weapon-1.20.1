package net.boomjiro.jiroweapon.ability;

import net.boomjiro.jiroweapon.entity.IllusionBlockEntity;
import net.boomjiro.jiroweapon.entity.ModEntities;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class DisguiseManager {

    private static class DisguiseState {
        UUID playerId;
        Entity disguiseEntity;
        int ticksRemaining;

        ItemStack originalHead;
        ItemStack originalChest;
        ItemStack originalLegs;
        ItemStack originalFeet;
        ItemStack originalMainHand;
        ItemStack originalOffHand;

        int mainHandSlotIndex;

        Text originalCustomName;
        boolean originalNameVisible;

        double yOffset;
    }

    private static final Map<UUID, DisguiseState> ACTIVE = new HashMap<>();

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(DisguiseManager::tick);
    }

    private static void tick(MinecraftServer server) {
        if (ACTIVE.isEmpty()) return;

        Iterator<Map.Entry<UUID, DisguiseState>> it = ACTIVE.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, DisguiseState> entry = it.next();
            DisguiseState state = entry.getValue();
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());

            if (player == null) {
                if (state.disguiseEntity != null && state.disguiseEntity.isAlive())
                    state.disguiseEntity.discard();
                it.remove();
                continue;
            }

            if (state.disguiseEntity != null) {
                if (!state.disguiseEntity.isAlive()) {
                    revertDisguiseInternal(player, state);
                    it.remove();
                    continue;
                }

                if (state.disguiseEntity instanceof FallingBlockEntity falling) {
                    Vec3d pos = player.getPos();
                    falling.refreshPositionAndAngles(
                            pos.x,
                            pos.y - 1,
                            pos.z,
                            0.0f,
                            0.0f
                    );
                } else {
                    Vec3d pos = player.getPos();
                    state.disguiseEntity.refreshPositionAndAngles(
                            pos.x, pos.y + state.yOffset, pos.z,
                            player.getYaw(), player.getPitch()
                    );
                }

                if (state.disguiseEntity instanceof ZombieEntity zombie) {

                    zombie.bodyYaw = player.getYaw();
                    zombie.prevBodyYaw = zombie.bodyYaw;

                    zombie.headYaw = player.getHeadYaw();
                    zombie.prevHeadYaw = zombie.headYaw;

                    zombie.setPitch(player.getPitch());
                    zombie.prevPitch = zombie.getPitch();

                    zombie.setYaw(player.getYaw());
                }
            }

            state.ticksRemaining--;
            if (state.ticksRemaining <= 0) {
                revertDisguiseInternal(player, state);
                it.remove();
            }
        }
    }

    private static void revertDisguiseInternal(ServerPlayerEntity player, DisguiseState state) {

        if (state.disguiseEntity != null && state.disguiseEntity.isAlive())
            state.disguiseEntity.discard();

        restoreEquipment(player, state);

        if (state.originalCustomName != null)
            player.setCustomName(state.originalCustomName);

        player.setCustomNameVisible(state.originalNameVisible);

        player.removeStatusEffect(net.minecraft.entity.effect.StatusEffects.INVISIBILITY);
    }

    public static void clear(ServerPlayerEntity player) {
        DisguiseState state = ACTIVE.remove(player.getUuid());
        if (state != null)
            revertDisguiseInternal(player, state);
    }

    public static void startHypnosisDisguise(ServerPlayerEntity player, int durationTicks) {
        DisguiseState state = new DisguiseState();
        state.playerId = player.getUuid();
        state.disguiseEntity = null;
        state.ticksRemaining = durationTicks;
        state.originalCustomName = player.getCustomName();
        state.originalNameVisible = player.isCustomNameVisible();
        state.yOffset = 0;

        saveAndClearEquipment(player, state);
        ACTIVE.put(player.getUuid(), state);
    }

    public static void startShapeShiftDisguise(ServerPlayerEntity player, String targetName, int durationTicks) {

        var world = player.getWorld();
        ZombieEntity zombie = new ZombieEntity(world);
        zombie.setPersistent();
        zombie.setAiDisabled(true);
        zombie.setSilent(true);

        zombie.refreshPositionAndAngles(
                player.getX(), player.getY(), player.getZ(),
                player.getYaw(), player.getPitch()
        );

        zombie.setCustomName(null);
        zombie.setCustomNameVisible(false);

        if (targetName != null && !targetName.isEmpty()) {
            ItemStack head = new ItemStack(Items.PLAYER_HEAD);
            NbtCompound skullNbt = new NbtCompound();
            skullNbt.putString("Name", targetName);
            head.getOrCreateNbt().put("SkullOwner", skullNbt);
            zombie.equipStack(EquipmentSlot.HEAD, head);
        }

        world.spawnEntity(zombie);

        DisguiseState state = new DisguiseState();
        state.playerId = player.getUuid();
        state.disguiseEntity = zombie;
        state.ticksRemaining = durationTicks;
        state.originalCustomName = player.getCustomName();
        state.originalNameVisible = player.isCustomNameVisible();
        state.yOffset = 0;

        saveAndClearEquipment(player, state);

        ACTIVE.put(player.getUuid(), state);
        player.setCustomNameVisible(false);
    }

    public static void startBlockDisguise(ServerPlayerEntity player, ItemStack blockStack, int durationTicks) {

        BlockState state;
        if (blockStack.getItem() instanceof BlockItem bi) {
            state = bi.getBlock().getDefaultState();
        } else {
            state = Blocks.STONE.getDefaultState();
        }

        IllusionBlockEntity illusion = new IllusionBlockEntity(ModEntities.ILLUSION_BLOCK, player.getWorld());
        illusion.setBlockState(state);
        illusion.setScale(1.0f);

        illusion.setPosition(player.getX(), player.getY() + 1, player.getZ());

        player.getWorld().spawnEntity(illusion);

        DisguiseState ds = new DisguiseState();
        ds.playerId = player.getUuid();
        ds.disguiseEntity = illusion;
        ds.ticksRemaining = durationTicks;
        ds.originalCustomName = player.getCustomName();
        ds.originalNameVisible = player.isCustomNameVisible();
        ds.yOffset = -1;

        saveAndClearEquipment(player, ds);

        ACTIVE.put(player.getUuid(), ds);
        player.setCustomNameVisible(false);
    }


    private static void saveAndClearEquipment(ServerPlayerEntity player, DisguiseState state) {

        state.originalHead = player.getEquippedStack(EquipmentSlot.HEAD).copy();
        state.originalChest = player.getEquippedStack(EquipmentSlot.CHEST).copy();
        state.originalLegs = player.getEquippedStack(EquipmentSlot.LEGS).copy();
        state.originalFeet = player.getEquippedStack(EquipmentSlot.FEET).copy();

        state.mainHandSlotIndex = player.getInventory().selectedSlot;

        state.originalMainHand = player.getMainHandStack().copy();
        state.originalOffHand = player.getOffHandStack().copy();

        player.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
        player.equipStack(EquipmentSlot.CHEST, ItemStack.EMPTY);
        player.equipStack(EquipmentSlot.LEGS, ItemStack.EMPTY);
        player.equipStack(EquipmentSlot.FEET, ItemStack.EMPTY);

        player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        player.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
    }

    private static void restoreEquipment(ServerPlayerEntity player, DisguiseState state) {

        player.equipStack(EquipmentSlot.HEAD, state.originalHead);
        player.equipStack(EquipmentSlot.CHEST, state.originalChest);
        player.equipStack(EquipmentSlot.LEGS, state.originalLegs);
        player.equipStack(EquipmentSlot.FEET, state.originalFeet);

        if (!state.originalMainHand.isEmpty()) {
            ItemStack current = player.getInventory().getStack(state.mainHandSlotIndex);
            if (current.isEmpty() || ItemStack.areItemsEqual(current, state.originalMainHand)) {
                player.getInventory().setStack(state.mainHandSlotIndex, state.originalMainHand);
            } else {
                player.getInventory().insertStack(state.originalMainHand.copy());
            }
        }

        if (!state.originalOffHand.isEmpty()) {
            ItemStack current = player.getInventory().getStack(state.mainHandSlotIndex);
            if (current.isEmpty() || ItemStack.areItemsEqual(current, state.originalOffHand)) {
                player.getInventory().setStack(state.mainHandSlotIndex, state.originalOffHand);
            } else {
                player.getInventory().insertStack(state.originalOffHand.copy());
            }
        }
    }

    private static NbtCompound createMarkerNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putByte("Marker", (byte) 1);
        nbt.putBoolean("Invisible", true);
        nbt.putBoolean("NoGravity", true);
        return nbt;
    }

    public static boolean isClientDisguised(UUID id) {
        return ACTIVE.containsKey(id);
    }
}