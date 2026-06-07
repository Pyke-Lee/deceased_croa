package kr.pyke.deceased_croa.client.gui.hud;

import kr.pyke.deceased_croa.client.cache.ClientCache;
import kr.pyke.deceased_croa.client.key.ModKeyBinding;
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

public class RankingOverlay implements HudRenderCallback {
    @Override
    public void onHudRender(GuiGraphics guiGraphics, float tickDelta) {
        if (!ModKeyBinding.rankingKey.isDown()) { return; }

        Minecraft client = Minecraft.getInstance();
        Font font = client.font;
        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();

        Map<UUID, Integer> ranking = ClientCache.getRanking();

        int boxWidth = 200;
        int rowHeight = 16;
        int maxPlayersToShow = 10;
        int rowGap = 2;

        int actualDisplayCount = Math.min(ranking.size(), maxPlayersToShow);
        int displayRows = Math.max(1, actualDisplayCount);

        int headerHeight = 22;
        int footerHeight = 18;
        int listHeight = (displayRows * rowHeight) + ((displayRows - 1) * rowGap);
        int totalHeight = headerHeight + listHeight + footerHeight;

        int x = screenWidth / 2 - boxWidth / 2;
        int y = screenHeight / 2 - totalHeight / 2;

        guiGraphics.fill(x, y, x + boxWidth, y + totalHeight, 0xCC000000);

        int currentY = y;
        Component title = Component.literal("§6몬스터 처치 랭킹");

        guiGraphics.drawString(font, title, screenWidth / 2 - font.width(title) / 2, currentY + 6, 0xFFFFFF, false);

        currentY += headerHeight;

        if (ranking.isEmpty()) {
            Component emptyText = Component.literal("§7랭킹 정보 없음§r");
            guiGraphics.drawString(font, emptyText, screenWidth / 2 - font.width(emptyText) / 2, currentY + 3, 0xFFFFFF, false);
        }
        else {
            int i = 0;

            for (Map.Entry<UUID, Integer> entry : ranking.entrySet()) {
                if (i >= maxPlayersToShow) { break; }

                guiGraphics.fill(x + 8, currentY, x + boxWidth - 8, currentY + rowHeight, 0x33FFFFFF);

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
                guiGraphics.drawString(font, rankText, x + 10, currentY + 4, 0xFFFFFF, false);

                int headBoxX = x + 35;
                int headBoxY = currentY + 2;
                int headBoxSize = 12;

                PlayerFaceRenderer.draw(guiGraphics, skin, headBoxX, headBoxY, headBoxSize);

                int nameX = headBoxX + headBoxSize + 4;
                guiGraphics.drawString(font, "§f" + name + "§r", nameX, currentY + 4, 0xFFFFFF, false);

                String killText = "§e" + kills + "§r 마리";
                int killTextWidth = font.width(killText);
                int killX = x + boxWidth - killTextWidth - 10;
                guiGraphics.drawString(font, killText, killX, currentY + 4, 0xFFFFFF, false);

                currentY += rowHeight + rowGap;
                i++;
            }
        }

        Component footer = Component.literal("§8제작: Erudites (파이크)§r");
        int footerWidth = font.width(footer);
        guiGraphics.drawString(font, footer, screenWidth / 2 - footerWidth / 2, y + totalHeight - 14, 0xFFFFFF, false);
    }
}