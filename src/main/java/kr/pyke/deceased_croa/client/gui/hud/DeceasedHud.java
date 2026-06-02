package kr.pyke.deceased_croa.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import kr.pyke.deceased_croa.registry.component.ModComponents;
import kr.pyke.deceased_croa.registry.component.info.IDeceasedInfo;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;

public class DeceasedHud implements HudRenderCallback {
    private static final ResourceLocation ZOMBIE_TEXTURE = new ResourceLocation("minecraft", "textures/entity/zombie/zombie.png");

    @Override
    public void onHudRender(GuiGraphics guiGraphics, float tickDelta) {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;
        if (player == null) { return; }

        IDeceasedInfo info = ModComponents.DECEASED_INFO.get(player);
        int killCount = info.getMonsterKillCount();
        String text = String.valueOf(killCount);

        Font font = client.font;
        int textWidth = font.width(text);
        int boxWidth = 24 + textWidth + 8;
        int boxHeight = 16;

        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();

        HumanoidArm offhandArm = player.getMainArm().getOpposite();
        int x;

        if (offhandArm == HumanoidArm.LEFT) { x = (screenWidth / 2) + 91 + 25; }
        else { x = (screenWidth / 2) - 91 - boxWidth - 25; }

        int y = screenHeight - 20;

        guiGraphics.fill(x, y, x + boxWidth, y + boxHeight, 0x80000000);

        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        guiGraphics.blit(ZOMBIE_TEXTURE, x + 4, y + 4, 8.f, 8.f, 8, 8, 64, 64);

        int textX = x + boxWidth - 4 - textWidth;
        int textY = y + 4;

        drawGradientText(guiGraphics, font, text, textX, textY);
    }

    private void drawGradientText(GuiGraphics guiGraphics, Font font, String text, int x, int y) {
        int length = text.length();
        int currentX = x;

        for (int i = 0; i < length; i++) {
            String character = String.valueOf(text.charAt(i));

            guiGraphics.drawString(font, character, currentX, y, 0xBDC3C7, true);
            currentX += font.width(character);
        }
    }
}