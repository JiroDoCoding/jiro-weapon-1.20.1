package net.boomjiro.jiroweapon.screen;

import net.boomjiro.jiroweapon.network.ModPackets;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class AbilityScreen extends HandledScreen<AbilityScreenHandler> {

    private static final ItemStack HYPNOSIS = new ItemStack(Items.STRUCTURE_VOID);
    private static final ItemStack SHAPESHIFT = new ItemStack(Items.PLAYER_HEAD);
    private static final ItemStack ILLUSION = new ItemStack(Items.BARRIER);

    public AbilityScreen(AbilityScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, title);
    }

    @Override
    protected void init() {
        super.init();

        int cx = width / 2;
        int cy = height / 2;

        addDrawableChild(ButtonWidget.builder(Text.literal("Hypnosis"), b -> onSelect(1))
                .dimensions(cx - 40, cy - 55, 80, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Shape Shifting"), b -> onSelect(2))
                .dimensions(cx - 40, cy - 5, 80, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Illusion"), b -> onSelect(3))
                .dimensions(cx - 40, cy + 45, 80, 20).build());
    }

    private void onSelect(int id) {
        // 🔹 Send selected ability to server
        ModPackets.sendSelectAbility(id);
        // 🔹 Close GUI on client
        close();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx);
        super.render(ctx, mouseX, mouseY, delta);

        int cx = width / 2;
        int cy = height / 2;

        ctx.drawItem(HYPNOSIS, cx - 70, cy - 58);
        ctx.drawItem(SHAPESHIFT, cx - 70, cy - 8);
        ctx.drawItem(ILLUSION, cx - 70, cy + 42);

        drawMouseoverTooltip(ctx, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext ctx, float delta, int mouseX, int mouseY) {}
}