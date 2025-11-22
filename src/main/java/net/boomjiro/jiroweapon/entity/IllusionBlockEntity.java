package net.boomjiro.jiroweapon.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.world.World;
import net.minecraft.util.Identifier;

public class IllusionBlockEntity extends Entity {


    private static final TrackedData<BlockState> BLOCK_STATE =
            DataTracker.registerData(IllusionBlockEntity.class, TrackedDataHandlerRegistry.BLOCK_STATE);

    private static final TrackedData<Float> SCALE =
            DataTracker.registerData(IllusionBlockEntity.class, TrackedDataHandlerRegistry.FLOAT);


    public IllusionBlockEntity(EntityType<? extends IllusionBlockEntity> type, World world) {
        super(type, world);
        this.noClip = true;
    }

    @Override
    protected void initDataTracker() {
        dataTracker.startTracking(BLOCK_STATE, Blocks.AIR.getDefaultState());
        dataTracker.startTracking(SCALE, 1.0f);
    }

    public void setBlockState(BlockState state) {
        dataTracker.set(BLOCK_STATE, state);
    }

    public BlockState getBlockStateTracked() {
        return dataTracker.get(BLOCK_STATE);
    }

    public void setScale(float scale) {
        dataTracker.set(SCALE, scale);
    }

    public float getScale() {
        return dataTracker.get(SCALE);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        Block block = getBlockStateTracked().getBlock();
        Identifier id = Registries.BLOCK.getId(block);
        nbt.putString("BlockId", id.toString());
        nbt.putFloat("Scale", getScale());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        Block block = Registries.BLOCK.get(new Identifier(nbt.getString("BlockId")));
        setBlockState(block.getDefaultState());
        setScale(nbt.getFloat("Scale"));
    }

    @Override
    public void tick() {
        setVelocity(0, 0, 0);
        setPos(getX(), getY(), getZ());
    }

    public boolean collides() { return false; }

    @Override
    public boolean isInvisible() { return true; }

    @Override
    public boolean shouldSave() { return false; }
}