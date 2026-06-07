package kr.pyke.deceased_croa.client.gui.hud;

import kr.pyke.deceased_croa.client.cache.ClientCache;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.UUID;

public class RankingSidebarOverlay implements HudRenderCallback {
    @Override
    public void onHudRender(GuiGraphics guiGraphics, float tickDelta) {
        Map<UUID, Integer> ranking = ClientCache.getRanking();
        if (ranking.isEmpty()) { return; }

        Minecraft client = Minecraft.getInstance();
        Font font = client.font;
        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();

        int maxPlayersToShow = 5;
        int boxWidth = 100;
        int rowHeight = 14;
        int rowGap = 1;
        int headerHeight = 16;
        int footerHeight = 6;

        int actualDisplayCount = Math.min(ranking.size(), maxPlayersToShow);
        int listHeight = (actualDisplayCount * rowHeight) + ((actualDisplayCount - 1) * rowGap);
        int totalHeight = headerHeight + listHeight + footerHeight;

        int x = screenWidth - boxWidth;
        int y = screenHeight / 2 - totalHeight / 2;

        guiGraphics.fill(x, y, x + boxWidth, y + totalHeight, 0x80000000);

        Component title = Component.literal("§6몬스터 처치 랭킹§r");
        guiGraphics.drawString(font, title, x + boxWidth / 2 - font.width(title) / 2, y + 4, 0xFFFFFF, false);

        int currentY = y + headerHeight;
        int i = 0;

        for (Map.Entry<UUID, Integer> entry : ranking.entrySet()) {
            if (i >= maxPlayersToShow) { break; }

            UUID uuid = entry.getKey();
            int kills = entry.getValue();
            int rank = i + 1;

            PlayerInfo playerInfo = client.getConnection().getPlayerInfo(uuid);
            String name = "알 수 없음";
            ResourceLocation skin = DefaultPlayerSkin.getDefaultSkin(uuid);

            if (playerInfo != null) {
                Component displayName = playerInfo.getTabListDisplayName();
                if (displayName != null) { name = displayName.getString(); }
                else { name = playerInfo.getProfile().getName(); }

                skin = playerInfo.getSkinLocation();
            }

            Component rankText = Component.literal(String.format("§f%2s등§r", rank));
            guiGraphics.drawString(font, rankText, x + 5, currentY + 3, 0xFFFFFF, false);

            int headBoxX = x + 22;
            int headBoxY = currentY + 2;
            int headBoxSize = 10;

            PlayerFaceRenderer.draw(guiGraphics, skin, headBoxX, headBoxY, headBoxSize);

            int nameX = headBoxX + headBoxSize + 3;
            int maxNameWidth = (x + boxWidth - 5) - nameX - font.width(String.valueOf(kills)) - 6;
            String trimmedName = font.plainSubstrByWidth(name, maxNameWidth);
            guiGraphics.drawString(font, "§f" + trimmedName + "§r", nameX, currentY + 3, 0xFFFFFF, false);

            String killText = "§e" + kills + "§r 마리";
            int killTextWidth = font.width(killText);
            int killX = x + boxWidth - killTextWidth - 5;
            guiGraphics.drawString(font, killText, killX, currentY + 3, 0xFFFFFF, false);

            currentY += rowHeight + rowGap;
            i++;
        }
    }
}