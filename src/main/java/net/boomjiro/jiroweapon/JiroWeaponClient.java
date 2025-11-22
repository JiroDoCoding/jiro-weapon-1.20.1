package net.boomjiro.jiroweapon;

import net.boomjiro.jiroweapon.ability.DisguiseManager;
import net.boomjiro.jiroweapon.entity.ReaperSkullProjectileEntity;
import net.boomjiro.jiroweapon.entity.RitualCircleEntity;
import net.boomjiro.jiroweapon.entity.ModEntities;

import net.boomjiro.jiroweapon.entity.render.IllusionBlockRenderer;
import net.boomjiro.jiroweapon.screen.AbilityScreen;
import net.boomjiro.jiroweapon.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.MathHelper;

import org.joml.Matrix4f;

import java.util.UUID;

public class JiroWeaponClient implements ClientModInitializer {

    private static final Identifier RITUAL_TEXTURE =
            new Identifier(JiroWeapon.MOD_ID, "textures/entity/reaper_circle.png");

    private static final Identifier CROSS_TEXTURE =
            new Identifier(JiroWeapon.MOD_ID, "textures/entity/reaper_cross.png");

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.REAPER_SKULL, ReaperSkullRenderer::new);
        EntityRendererRegistry.register(ModEntities.RITUAL_CIRCLE, RitualCircleRenderer::new);
        HandledScreens.register(ModScreenHandlers.ABILITY_SCREEN, AbilityScreen::new);
        EntityRendererRegistry.register(ModEntities.ILLUSION_BLOCK, IllusionBlockRenderer::new);
    }

    public static class ReaperSkullRenderer extends EntityRenderer<ReaperSkullProjectileEntity> {

        private final ItemRenderer itemRenderer;

        public ReaperSkullRenderer(EntityRendererFactory.Context ctx) {
            super(ctx);
            this.itemRenderer = ctx.getItemRenderer();
        }

        @Override
        public Identifier getTexture(ReaperSkullProjectileEntity entity) {
            return null;
        }

        @Override
        public void render(ReaperSkullProjectileEntity entity, float yaw, float tickDelta,
                           MatrixStack matrices, VertexConsumerProvider vertices, int light) {

            matrices.push();

            double vx = entity.getVelocity().x;
            double vy = entity.getVelocity().y;
            double vz = entity.getVelocity().z;

            float projYaw;
            float projPitch;

            double h = Math.sqrt(vx * vx + vz * vz);
            if (h > 1.0e-4) {
                projYaw = (float) (-Math.toDegrees(Math.atan2(vx, vz)));
                projPitch = (float) Math.toDegrees(Math.atan2(vy, h));
            } else {
                projYaw = entity.getYaw();
                projPitch = entity.getPitch();
            }

            float bob = (float) Math.sin((entity.age + tickDelta) * 0.25f) * 0.1f;
            matrices.translate(0, bob, 0);

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(projYaw));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(projPitch));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));

            matrices.scale(1.3f, 1.3f, 1.3f);

            ItemStack skull = new ItemStack(Items.SKELETON_SKULL);

            itemRenderer.renderItem(
                    skull,
                    ModelTransformationMode.FIXED,
                    light,
                    OverlayTexture.DEFAULT_UV,
                    matrices,
                    vertices,
                    entity.getWorld(),
                    0
            );

            matrices.pop();
        }
    }

    public static class RitualCircleRenderer extends EntityRenderer<RitualCircleEntity> {

        public RitualCircleRenderer(EntityRendererFactory.Context ctx) {
            super(ctx);
        }

        @Override
        public Identifier getTexture(RitualCircleEntity entity) {
            return RITUAL_TEXTURE;
        }

        @Override
        public void render(RitualCircleEntity entity, float yaw, float tickDelta,
                           MatrixStack matrices, VertexConsumerProvider vertices, int light) {

            float age = entity.age + tickDelta;

            float lifeTime  = RitualCircleEntity.DURATION_TICKS;
            float remaining = lifeTime - age;
            float fade = MathHelper.clamp(remaining / 60.0f, 0.0f, 1.0f);

            float pulse = 1.0f + 0.05f * MathHelper.sin(age * 0.3f);

            int glowLight = 0xF000F0;

            matrices.push();

            matrices.push();
            matrices.translate(0, 0.05, 0);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f));         // lay flat
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(age * 0.7f));  // slow spin
            matrices.scale(12f * pulse, 12f * pulse, 12f * pulse);

            drawQuad(
                    matrices,
                    vertices.getBuffer(RenderLayer.getEntityTranslucentEmissive(RITUAL_TEXTURE)),
                    fade,
                    glowLight
            );
            matrices.pop();

            matrices.push();
            matrices.translate(0.0, 5.5, 0.0);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(age * 2.0f));
            matrices.scale(3.0f * pulse, 6.0f * pulse, pulse);

            drawQuad(
                    matrices,
                    vertices.getBuffer(RenderLayer.getEntityTranslucentEmissive(CROSS_TEXTURE)),
                    fade,
                    glowLight
            );
            matrices.pop();

            matrices.pop();
        }

        private void drawQuad(MatrixStack matrices, VertexConsumer vc, float fadeAlpha, int light) {
            MatrixStack.Entry entry = matrices.peek();
            Matrix4f mat = entry.getPositionMatrix();

            int a = (int) (MathHelper.clamp(fadeAlpha, 0.0f, 1.0f) * 255.0f);

            int r = 255;
            int g = 200;
            int b = 200;

            vc.vertex(mat, -0.5f, -0.5f, 0.0f)
                    .color(r, g, b, a)
                    .texture(0.0f, 0.0f)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(light)
                    .normal(0f, 1f, 0f)
                    .next();

            vc.vertex(mat, 0.5f, -0.5f, 0.0f)
                    .color(r, g, b, a)
                    .texture(1.0f, 0.0f)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(light)
                    .normal(0f, 1f, 0f)
                    .next();

            vc.vertex(mat, 0.5f, 0.5f, 0.0f)
                    .color(r, g, b, a)
                    .texture(1.0f, 1.0f)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(light)
                    .normal(0f, 1f, 0f)
                    .next();

            vc.vertex(mat, -0.5f, 0.5f, 0.0f)
                    .color(r, g, b, a)
                    .texture(0.0f, 1.0f)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(light)
                    .normal(0f, 1f, 0f)
                    .next();
        }
    }

    public static boolean isClientDisguised(UUID playerId) {
        return DisguiseManager.isClientDisguised(playerId);
    }
}
