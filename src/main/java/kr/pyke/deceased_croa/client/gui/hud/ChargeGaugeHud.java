package kr.pyke.deceased_croa.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import kr.pyke.deceased_croa.registry.item.chargeable.Chargeable;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

public class ChargeGaugeHud implements HudRenderCallback {
    private static final int COLOR_BG = 0xFF424242;
    private static final int COLOR_GAUGE = 0xFFD1D1D1;
    private static final int SEGMENTS = 60;

    @Override
    public void onHudRender(GuiGraphics guiGraphics, float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.options.hideGui) { return; }

        ItemStack useStack = player.getUseItem();
        if (!(useStack.getItem() instanceof Chargeable)) { return; }

        float remainingTicks = (float) player.getUseItemRemainingTicks();
        float maxDuration = (float) useStack.getUseDuration();
        float progress = Mth.clamp((maxDuration - remainingTicks) / maxDuration, 0.f, 1.f);

        int cx = guiGraphics.guiWidth() / 2;
        int cy = guiGraphics.guiHeight() / 2;

        submitCircle(guiGraphics, cx, cy, 20.f, 2.5f, 1.f, COLOR_BG);
        submitCircle(guiGraphics, cx, cy, 20.f, 2.5f, progress, COLOR_GAUGE);

        float second = remainingTicks / 20.f;
        String timeLeft = (second < 1.f) ? (second <= 0.f) ? "0" : String.format("%.1f", remainingTicks / 20.f) : String.format("%.0f", remainingTicks / 20.f);
        guiGraphics.drawCenteredString(mc.font, timeLeft, cx, cy - 4, 0xFFFFFFFF);
    }

    private static void submitCircle(GuiGraphics guiGraphics, float cx, float cy, float r, float t, float p, int col) {
        float innerRadius = r - t;
        float outerRadius = r;
        int filledSegments = Mth.ceil(SEGMENTS * Mth.clamp(p, 0.f, 1.f));
        if (filledSegments <= 0) { return; }

        float a = (col >> 24 & 0xFF) / 255.f;
        float red = (col >> 16 & 0xFF) / 255.f;
        float green = (col >> 8 & 0xFF) / 255.f;
        float blue = (col & 0xFF) / 255.f;

        Matrix4f matrix = guiGraphics.pose().last().pose();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        for (int i = 0; i <= filledSegments; ++i) {
            float angle = (float) (-Math.PI / 2.f + (Math.PI * 2.f) * i / SEGMENTS);
            float cos = Mth.cos(angle);
            float sin = Mth.sin(angle);

            buffer.vertex(matrix, cx + cos * outerRadius, cy + sin * outerRadius, 0.f).color(red, green, blue, a).endVertex();
            buffer.vertex(matrix, cx + cos * innerRadius, cy + sin * innerRadius, 0.f).color(red, green, blue, a).endVertex();
        }

        tesselator.end();

        RenderSystem.disableBlend();
    }
}