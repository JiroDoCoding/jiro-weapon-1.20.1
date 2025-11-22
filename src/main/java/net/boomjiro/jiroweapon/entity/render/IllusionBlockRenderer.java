package net.boomjiro.jiroweapon.entity.render;

import net.boomjiro.jiroweapon.entity.IllusionBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.LightType;

@Environment(EnvType.CLIENT)
public class IllusionBlockRenderer extends EntityRenderer<IllusionBlockEntity> {

    private final BlockRenderManager brm;

    public IllusionBlockRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.brm = ctx.getBlockRenderManager();

        this.shadowRadius = 0f;
        this.shadowOpacity = 0f;
    }

    @Override
    public void render(IllusionBlockEntity entity, float yaw, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vcp, int light) {

        BlockState state = entity.getBlockStateTracked();
        if (state == null || state.isAir()) return;

        float scale = entity.getScale();

        int sky = 10;
        int block = 7;
        int customLight = (sky << 20) | (block << 4);

        matrices.push();

        matrices.translate(0, 1.0, 0);
        matrices.translate(-0.5, 0, -0.5);
        matrices.scale(scale, scale, scale);

        brm.renderBlockAsEntity(state, matrices, vcp, customLight, OverlayTexture.DEFAULT_UV);

        matrices.pop();
    }

    @Override
    public Identifier getTexture(IllusionBlockEntity entity) {
        return null;
    }
}