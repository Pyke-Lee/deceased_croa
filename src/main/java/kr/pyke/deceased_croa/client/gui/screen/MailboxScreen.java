package kr.pyke.deceased_croa.client.gui.screen;

import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.client.gui.menu.MailboxMenu;
import kr.pyke.deceased_croa.data.MailboxData;
import kr.pyke.deceased_croa.network.pakcet.c2s.C2S_ClaimMailPacket;
import kr.pyke.deceased_croa.registry.component.ModComponents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MailboxScreen extends AbstractContainerScreen<MailboxMenu> {
    private static final ResourceLocation MAILBOX_LOCATION = new ResourceLocation(DeceasedCroa.MOD_ID, "textures/gui/container/mailbox.png");
    private static final ResourceLocation PREV_LOCATION = new ResourceLocation(DeceasedCroa.MOD_ID, "textures/gui/container/prev.png");
    private static final ResourceLocation NEXT_LOCATION = new ResourceLocation(DeceasedCroa.MOD_ID, "textures/gui/container/next.png");

    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;

    private static final int GRID_START_X = 11;
    private static final int GRID_START_Y = 23;
    private static final int GRID_COLS = 9;
    private static final int GRID_ROWS = 5;
    private static final int SLOT_SIZE = 18;
    private static final int MAILS_PER_PAGE = GRID_COLS * GRID_ROWS;

    private static final int ARROW_WIDTH = 9;
    private static final int ARROW_HEIGHT = 14;
    private static final int PREV_X = 65;
    private static final int NEXT_X = 111;
    private static final int ARROW_Y = 118;
    private static final int PAGE_TEXT_CENTER_X = 93;
    private static final int PAGE_TEXT_Y = 121;

    private ArrowButton prevButton;
    private ArrowButton nextButton;
    private int page;

    public MailboxScreen(MailboxMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 212;
        this.imageHeight = 233;
    }

    private List<MailboxData> mails() {
        List<MailboxData> list = new ArrayList<>(ModComponents.MAILBOX.get(this.minecraft.player).getMails());
        Collections.reverse(list);
        return list;
    }

    private int pageCount() {
        int count = this.mails().size();
        return Math.max(1, (count + MAILS_PER_PAGE - 1) / MAILS_PER_PAGE);
    }

    @Override
    protected void init() {
        super.init();
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        this.prevButton = this.addRenderableWidget(new ArrowButton(i + PREV_X, j + ARROW_Y, PREV_LOCATION, button -> this.changePage(-1)));
        this.nextButton = this.addRenderableWidget(new ArrowButton(i + NEXT_X, j + ARROW_Y, NEXT_LOCATION, button -> this.changePage(1)));

        this.updateArrows();
    }

    private void changePage(int delta) {
        int next = this.page + delta;
        if (next < 0 || next >= this.pageCount()) { return; }

        this.page = next;
        this.updateArrows();
    }

    private void updateArrows() {
        int pageCount = this.pageCount();
        if (this.page >= pageCount) { this.page = pageCount - 1; }

        this.prevButton.visible = this.page > 0;
        this.nextButton.visible = this.page < pageCount - 1;
    }

    private void claimMail(MailboxData mail) {
        C2S_ClaimMailPacket.send(mail.mailUUID());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(MAILBOX_LOCATION, i, j, 0, 0.f, 0.f, this.imageWidth, this.imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        List<MailboxData> mails = this.mails();
        int start = this.page * MAILS_PER_PAGE;
        for(int slot = 0; slot < MAILS_PER_PAGE; ++slot) {
            int index = start + slot;
            if (index >= mails.size()) { break; }

            int col = slot % GRID_COLS;
            int row = slot / GRID_COLS;
            int x = i + GRID_START_X + col * SLOT_SIZE + 1;
            int y = j + GRID_START_Y + row * SLOT_SIZE + 1;

            ItemStack stack = mails.get(index).itemStack();
            guiGraphics.renderFakeItem(stack, x, y);
            guiGraphics.renderItemDecorations(this.font, stack, x, y);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Component pageText = Component.literal((this.page + 1) + " / " + this.pageCount());
        int textX = PAGE_TEXT_CENTER_X - this.font.width(pageText) / 2;
        guiGraphics.drawString(this.font, pageText, textX, PAGE_TEXT_Y, 4210752, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        List<MailboxData> mails = this.mails();
        int start = this.page * MAILS_PER_PAGE;

        for(int slot = 0; slot < MAILS_PER_PAGE; ++slot) {
            int index = start + slot;
            if (index >= mails.size()) { break; }

            int col = slot % GRID_COLS;
            int row = slot / GRID_COLS;
            int x = i + GRID_START_X + col * SLOT_SIZE + 1;
            int y = j + GRID_START_Y + row * SLOT_SIZE + 1;

            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                AbstractContainerScreen.renderSlotHighlight(guiGraphics, x, y, 0);
                guiGraphics.renderTooltip(this.font, mails.get(index).itemStack(), mouseX, mouseY);
                break;
            }
        }
    }

    private MailboxData getHoveredMail(int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        List<MailboxData> mails = this.mails();
        int start = this.page * MAILS_PER_PAGE;

        for(int slot = 0; slot < MAILS_PER_PAGE; ++slot) {
            int index = start + slot;
            if (index >= mails.size()) { break; }

            int col = slot % GRID_COLS;
            int row = slot / GRID_COLS;
            int x = i + GRID_START_X + col * SLOT_SIZE + 1;
            int y = j + GRID_START_Y + row * SLOT_SIZE + 1;

            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) { return mails.get(index); }
        }

        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            MailboxData hovered = this.getHoveredMail((int) mouseX, (int) mouseY);
            if (hovered != null) {
                this.claimMail(hovered);
                this.updateArrows();
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private static class ArrowButton extends Button {
        private final ResourceLocation texture;

        public ArrowButton(int x, int y, ResourceLocation texture, Button.OnPress onPress) {
            super(x, y, ARROW_WIDTH, ARROW_HEIGHT, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
            this.texture = texture;
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int u = this.isHovered() ? ARROW_WIDTH : 0;
            guiGraphics.blit(this.texture, this.getX(), this.getY(), u, 0, ARROW_WIDTH, ARROW_HEIGHT, ARROW_WIDTH * 2, ARROW_HEIGHT);
        }
    }
}