package kr.pyke.deceased_croa.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.client.cache.ClientCache;
import kr.pyke.deceased_croa.network.pakcet.c2s.C2S_TeleportToPlayerPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TeleportScreen extends Screen {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(DeceasedCroa.MOD_ID, "textures/gui/container/teleport_select.png");
    private static final ResourceLocation SCROLLER = new ResourceLocation(DeceasedCroa.MOD_ID, "textures/gui/container/scroller.png");
    private static final ResourceLocation SCROLLER_DISABLED = new ResourceLocation(DeceasedCroa.MOD_ID, "textures/gui/container/scroller_disabled.png");

    private static final ResourceLocation BUTTON = new ResourceLocation(DeceasedCroa.MOD_ID, "textures/gui/container/button.png");
    private static final ResourceLocation BUTTON_HIGHLIGHTED = new ResourceLocation(DeceasedCroa.MOD_ID, "textures/gui/container/button_highlighted.png");

    private static final int IMAGE_WIDTH = 105;
    private static final int IMAGE_HEIGHT = 166;

    private static final int LIST_X = 5;
    private static final int LIST_Y = 18;
    private static final int LIST_WIDTH = 88;
    private static final int LIST_HEIGHT = 140;

    private static final int ROW_HEIGHT = 20;
    private static final int VISIBLE_ROWS = LIST_HEIGHT / ROW_HEIGHT;

    private static final int SCROLLER_X = 94;
    private static final int SCROLLER_Y = 18;
    private static final int SCROLLER_TRACK_HEIGHT = 140;
    private static final int SCROLLER_WIDTH = 6;
    private static final int SCROLLER_HEIGHT = 27;

    private static final int BUTTON_TEX_WIDTH = 200;
    private static final int BUTTON_TEX_HEIGHT = 20;
    private static final int BUTTON_BORDER = 3;

    private int leftPos;
    private int topPos;

    private final List<Entry> entries = new ArrayList<>();
    private int scrollOffset;

    private record Entry(UUID uuid, String name) {
    }

    public TeleportScreen() {
        super(Component.literal("Teleport"));
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - IMAGE_WIDTH) / 2;
        this.topPos = (this.height - IMAGE_HEIGHT) / 2;

        this.entries.clear();
        this.scrollOffset = 0;

        UUID self = this.minecraft.player != null ? this.minecraft.player.getUUID() : null;
        for (Map.Entry<UUID, String> entry : ClientCache.getDisplayNames().entrySet()) {
            if (self != null && entry.getKey().equals(self)) { continue; }
            if (this.minecraft.getConnection() == null) { continue; }
            if (this.minecraft.getConnection().getPlayerInfo(entry.getKey()) == null) { continue; }

            this.entries.add(new Entry(entry.getKey(), entry.getValue()));
        }
    }

    private int maxScroll() {
        return Math.max(0, this.entries.size() - VISIBLE_ROWS);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.maxScroll() <= 0) { return false; }

        this.scrollOffset = Math.max(0, Math.min(this.maxScroll(), this.scrollOffset - (int) Math.signum(delta)));
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (int i = 0; i < VISIBLE_ROWS; i++) {
                int index = this.scrollOffset + i;
                if (index >= this.entries.size()) { break; }

                int rowX = this.leftPos + LIST_X;
                int rowY = this.topPos + LIST_Y + i * ROW_HEIGHT;

                if (mouseX >= rowX && mouseX < rowX + LIST_WIDTH && mouseY >= rowY && mouseY < rowY + ROW_HEIGHT) {
                    C2S_TeleportToPlayerPacket.send(this.entries.get(index).uuid());
                    this.onClose();
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);

        guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, 256, 256);

        for (int i = 0; i < VISIBLE_ROWS; i++) {
            int index = this.scrollOffset + i;
            if (index >= this.entries.size()) { break; }

            Entry entry = this.entries.get(index);
            int rowX = this.leftPos + LIST_X;
            int rowY = this.topPos + LIST_Y + i * ROW_HEIGHT;

            boolean hovered = mouseX >= rowX && mouseX < rowX + LIST_WIDTH && mouseY >= rowY && mouseY < rowY + ROW_HEIGHT;
            ResourceLocation sprite = hovered ? BUTTON_HIGHLIGHTED : BUTTON;
            blitButton(guiGraphics, sprite, rowX, rowY, LIST_WIDTH, ROW_HEIGHT);

            int headX = rowX + 3;
            int headY = rowY + (ROW_HEIGHT - 12) / 2;
            renderPlayerHead(guiGraphics, entry.uuid(), headX, headY);

            int textY = rowY + (ROW_HEIGHT - this.font.lineHeight) / 2;
            guiGraphics.drawString(this.font, entry.name(), headX + 12 + 4, textY, 0xFFFFFF, true);
        }

        renderScroller(guiGraphics);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void blitButton(GuiGraphics guiGraphics, ResourceLocation sprite, int x, int y, int width, int height) {
        int b = BUTTON_BORDER;
        int tw = BUTTON_TEX_WIDTH;
        int th = BUTTON_TEX_HEIGHT;
        int midW = width - b * 2;
        int midH = height - b * 2;
        int texMidW = tw - b * 2;
        int texMidH = th - b * 2;

        guiGraphics.blit(sprite, x, y, b, b, 0.f, 0.f, b, b, tw, th);
        guiGraphics.blit(sprite, x + width - b, y, b, b, (float) (tw - b), 0.f, b, b, tw, th);
        guiGraphics.blit(sprite, x, y + height - b, b, b, 0.f, (float) (th - b), b, b, tw, th);
        guiGraphics.blit(sprite, x + width - b, y + height - b, b, b, (float) (tw - b), (float) (th - b), b, b, tw, th);

        guiGraphics.blit(sprite, x + b, y, midW, b, (float) b, 0.f, texMidW, b, tw, th);
        guiGraphics.blit(sprite, x + b, y + height - b, midW, b, (float) b, (float) (th - b), texMidW, b, tw, th);
        guiGraphics.blit(sprite, x, y + b, b, midH, 0.f, (float) b, b, texMidH, tw, th);
        guiGraphics.blit(sprite, x + width - b, y + b, b, midH, (float) (tw - b), (float) b, b, texMidH, tw, th);

        guiGraphics.blit(sprite, x + b, y + b, midW, midH, (float) b, (float) b, texMidW, texMidH, tw, th);
    }

    private void renderScroller(GuiGraphics guiGraphics) {
        boolean scrollable = this.maxScroll() > 0;
        ResourceLocation sprite = scrollable ? SCROLLER : SCROLLER_DISABLED;

        int scrollerX = this.leftPos + SCROLLER_X;
        int scrollerY = this.topPos + SCROLLER_Y;

        if (scrollable) {
            int travel = SCROLLER_TRACK_HEIGHT - SCROLLER_HEIGHT;
            scrollerY += (int) ((float) this.scrollOffset / (float) this.maxScroll() * (float) travel);
        }

        guiGraphics.blit(sprite, scrollerX, scrollerY, 0, 0, SCROLLER_WIDTH, SCROLLER_HEIGHT, SCROLLER_WIDTH, SCROLLER_HEIGHT);
    }

    private void renderPlayerHead(GuiGraphics guiGraphics, UUID uuid, int x, int y) {
        ResourceLocation skin = getSkin(uuid);

        RenderSystem.enableBlend();
        guiGraphics.blit(skin, x, y, 12, 12, 8.f, 8.f, 8, 8, 64, 64);
        guiGraphics.blit(skin, x, y, 12, 12, 40.f, 8.f, 8, 8, 64, 64);
        RenderSystem.disableBlend();
    }

    private ResourceLocation getSkin(UUID uuid) {
        if (this.minecraft.getConnection() != null) {
            PlayerInfo info = this.minecraft.getConnection().getPlayerInfo(uuid);
            if (info != null) { return info.getSkinLocation(); }
        }

        return DefaultPlayerSkin.getDefaultSkin(uuid);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}