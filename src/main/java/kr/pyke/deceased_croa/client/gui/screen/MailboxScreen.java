package kr.pyke.deceased_croa.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.client.gui.menu.MailboxMenu;
import kr.pyke.deceased_croa.data.MailboxData;
import kr.pyke.deceased_croa.network.pakcet.c2s.C2S_ClaimMailPacket;
import kr.pyke.deceased_croa.network.pakcet.c2s.C2S_OpenSendMailboxPacket;
import kr.pyke.deceased_croa.network.pakcet.c2s.C2S_SelectMailPacket;
import kr.pyke.deceased_croa.registry.component.ModComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MailboxScreen extends AbstractContainerScreen<MailboxMenu> {
    private static final ResourceLocation MAILBOX_LOCATION = new ResourceLocation(DeceasedCroa.MOD_ID, "textures/gui/container/mailbox.png");
    private static final ResourceLocation SCROLLER_SPRITE = new ResourceLocation(DeceasedCroa.MOD_ID, "textures/gui/container/scroller.png");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE = new ResourceLocation(DeceasedCroa.MOD_ID, "textures/gui/container/scroller_disabled.png");

    private static final int TEXTURE_WIDTH = 512;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int NUMBER_OF_MAIL_BUTTONS = 8;
    private static final int MAIL_BUTTON_X = 5;
    private static final int MAIL_BUTTON_WIDTH = 88;
    private static final int MAIL_BUTTON_HEIGHT = 20;
    private static final int SCROLLER_WIDTH = 6;
    private static final int SCROLLER_HEIGHT = 27;
    private static final int SCROLL_BAR_HEIGHT = 160;
    private static final int SCROLL_BAR_START_X = 94;
    private static final int SCROLL_BAR_TOP_POS_Y = 18;
    private static final int SCROLLER_TRAVEL = SCROLL_BAR_HEIGHT - SCROLLER_HEIGHT;
    private static final int BODY_X = 107;
    private static final int BODY_Y = 27;
    private static final int HEADER_X = 107;
    private static final int HEADER_Y = 5;
    private static final int HEADER_WIDTH = 162;

    private final MailButton[] mailButtons = new MailButton[NUMBER_OF_MAIL_BUTTONS];
    private Button claimButton;
    int scrollOff;
    private int selected = -1;
    private boolean isDragging;

    public MailboxScreen(MailboxMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 276;
        this.imageHeight = 186;
        this.inventoryLabelX = 107;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    private List<MailboxData> mails() {
        List<MailboxData> list = new ArrayList<>(ModComponents.MAILBOX.get(this.minecraft.player).getMails());
        Collections.reverse(list);
        return list;
    }

    @Override
    protected void init() {
        super.init();
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        int k = j + 16 + 2;

        for(int l = 0; l < NUMBER_OF_MAIL_BUTTONS; ++l) {
            this.mailButtons[l] = this.addRenderableWidget(new MailButton(i + MAIL_BUTTON_X, k, l, button -> {
                if (button instanceof MailButton mailButton) {
                    int index = mailButton.getIndex() + this.scrollOff;
                    if (hasShiftDown()) { this.claimMail(index); }
                    else { this.select(index); }
                }
            }));
            k += MAIL_BUTTON_HEIGHT;
        }

        this.claimButton = this.addRenderableWidget(Button.builder(Component.translatable("menu.deceased_croa.mailbox.claim"), button -> {
            if (this.selected >= 0) {
                this.claimMail(this.selected);
            }
        }).bounds(i + 235, j + 81, 34, 18).build());
        this.claimButton.active = false;

        if (this.minecraft.player.hasPermissions(2)) {
            this.addRenderableWidget(Button.builder(Component.translatable("menu.deceased_croa.mailbox.send.open"), button -> C2S_OpenSendMailboxPacket.send()).bounds(i + this.imageWidth + 4, j + 4, 60, 18).build());
        }
    }

    private void select(int index) {
        List<MailboxData> mails = this.mails();
        if (index < 0 || index >= mails.size()) {
            return;
        }

        this.selected = index;
        this.claimButton.active = true;
        C2S_SelectMailPacket.send(mails.get(index).mailUUID());
    }

    private void claimMail(int index) {
        List<MailboxData> mails = this.mails();
        if (index < 0 || index >= mails.size()) {
            return;
        }

        C2S_ClaimMailPacket.send(mails.get(index).mailUUID());
        this.selected = -1;
        this.claimButton.active = false;
        this.scrollOff = Mth.clamp(this.scrollOff, 0, Math.max(0, mails.size() - 1 - NUMBER_OF_MAIL_BUTTONS));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(MAILBOX_LOCATION, i, j, 0, 0.f, 0.f, this.imageWidth, this.imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        this.renderScroller(guiGraphics, i, j);
    }

    private void renderScroller(GuiGraphics guiGraphics, int posX, int posY) {
        int count = this.mails().size();
        if (this.canScroll(count)) {
            int max = count - NUMBER_OF_MAIL_BUTTONS;
            int m = (int)((float)this.scrollOff / (float)max * (float)SCROLLER_TRAVEL);
            guiGraphics.blit(SCROLLER_SPRITE, posX + SCROLL_BAR_START_X, posY + SCROLL_BAR_TOP_POS_Y + m, 0, 0, SCROLLER_WIDTH, SCROLLER_HEIGHT, SCROLLER_WIDTH, SCROLLER_HEIGHT);
        }
        else {
            guiGraphics.blit(SCROLLER_DISABLED_SPRITE, posX + SCROLL_BAR_START_X, posY + SCROLL_BAR_TOP_POS_Y, 0, 0, SCROLLER_WIDTH, SCROLLER_HEIGHT, SCROLLER_WIDTH, SCROLLER_HEIGHT);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 41, 6, 4210752, false);

        List<MailboxData> mails = this.mails();
        if (this.selected >= 0 && this.selected < mails.size()) {
            MailboxData mail = mails.get(this.selected);
            guiGraphics.drawString(this.font, mail.mailTitle(), HEADER_X + 2, HEADER_Y + 2, 4210752, false);

            Component from = Component.translatable("menu.deceased_croa.mailbox.from", mail.senderName());
            guiGraphics.drawString(this.font, from, HEADER_X + 2, HEADER_Y + 12, 7368816, false);

            Component time = this.formatRelativeTime(mail.sentDate());
            int timeX = HEADER_X + HEADER_WIDTH - 2 - this.font.width(time);
            guiGraphics.drawString(this.font, time, timeX, HEADER_Y + 12, 7368816, false);

            List<FormattedCharSequence> lines = this.font.split(Component.literal(mail.mailMessage()), HEADER_WIDTH - 4);
            int line = 0;
            for(FormattedCharSequence seq : lines) {
                if (line >= 5) { break; }

                guiGraphics.drawString(this.font, seq, BODY_X + 2, BODY_Y + 2 + line * 10, 4210752, false);
                ++line;
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        List<MailboxData> mails = this.mails();
        for(MailButton mailButton : this.mailButtons) {
            int index = mailButton.getIndex() + this.scrollOff;
            mailButton.visible = index < mails.size();
            if (mailButton.visible) {
                mailButton.setMail(mails.get(index));
            }
        }

        this.renderTooltip(guiGraphics, mouseX, mouseY);

        for(MailButton mailButton : this.mailButtons) {
            if (mailButton.visible && mailButton.isHovered() && mailButton.getMail() != null && !mailButton.getMail().itemStackList().isEmpty()) {
                this.renderAttachmentTooltip(guiGraphics, mailButton.getMail(), mouseX, mouseY);
                break;
            }
        }

        RenderSystem.enableDepthTest();
    }

    private boolean canScroll(int count) { return count > NUMBER_OF_MAIL_BUTTONS; }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int count = this.mails().size();
        if (this.canScroll(count)) {
            int max = count - NUMBER_OF_MAIL_BUTTONS;
            this.scrollOff = Mth.clamp((int)((double)this.scrollOff - delta), 0, max);
        }

        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.isDragging) {
            int count = this.mails().size();
            int max = count - NUMBER_OF_MAIL_BUTTONS;
            int top = this.topPos + SCROLL_BAR_TOP_POS_Y;
            float f = ((float)mouseY - (float)top - (float)SCROLLER_HEIGHT / 2.f) / (float)(SCROLL_BAR_HEIGHT - SCROLLER_HEIGHT);
            this.scrollOff = Mth.clamp(Math.round(f * (float)max), 0, max);
            return true;
        }
        else { return super.mouseDragged(mouseX, mouseY, button, dragX, dragY); }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.isDragging = false;
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        if (this.canScroll(this.mails().size()) && mouseX > (double)(i + SCROLL_BAR_START_X) && mouseX < (double)(i + SCROLL_BAR_START_X + SCROLLER_WIDTH) && mouseY > (double)(j + SCROLL_BAR_TOP_POS_Y) && mouseY <= (double)(j + SCROLL_BAR_TOP_POS_Y + SCROLL_BAR_HEIGHT + 1)) {
            this.isDragging = true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    static class MailButton extends Button {
        private static final int TEXT_PADDING = 5;

        private final int index;
        private MailboxData mail;

        public MailButton(int x, int y, int index, Button.OnPress onPress) {
            super(x, y, MAIL_BUTTON_WIDTH, MAIL_BUTTON_HEIGHT, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
            this.index = index;
            this.visible = false;
        }

        public int getIndex() { return this.index; }

        public void setMail(MailboxData mail) {
            this.mail = mail;
            this.setMessage(Component.literal(mail.mailTitle()));
        }

        public MailboxData getMail() { return mail; }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            Minecraft minecraft = Minecraft.getInstance();
            guiGraphics.blitNineSliced(WIDGETS_LOCATION, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
            int color = this.active ? 16777215 : 10526880;
            int textX = this.getX() + TEXT_PADDING;
            int textY = this.getY() + (this.getHeight() - 8) / 2;
            int clipRight = this.getX() + this.getWidth() - TEXT_PADDING;
            guiGraphics.enableScissor(this.getX() + TEXT_PADDING, this.getY(), clipRight, this.getY() + this.getHeight());
            guiGraphics.drawString(minecraft.font, this.getMessage(), textX, textY, color | (Mth.ceil(this.alpha * 255.f) << 24));
            guiGraphics.disableScissor();
        }

        private int getTextureY() {
            int i = 1;
            if (!this.active) { i = 0; }
            else if (this.isHoveredOrFocused()) { i = 2; }

            return 46 + i * 20;
        }
    }

    private Component formatRelativeTime(long epochMillis) {
        long diff = System.currentTimeMillis() - epochMillis;
        if (diff < 0L) { diff = 0L; }

        long minutes = diff / 60000L;
        if (minutes < 1L) { return Component.translatable("menu.deceased_croa.mailbox.time.just_now"); }

        if (minutes < 60L) { return Component.translatable("menu.deceased_croa.mailbox.time.minutes", minutes); }

        long hours = minutes / 60L;
        if (hours < 24L) { return Component.translatable("menu.deceased_croa.mailbox.time.hours", hours); }

        long days = hours / 24L;
        return Component.translatable("menu.deceased_croa.mailbox.time.days", days);
    }

    private void renderAttachmentTooltip(GuiGraphics guiGraphics, MailboxData mail, int mouseX, int mouseY) {
        List<ItemStack> items = mail.itemStackList();

        int slotSize = 18;
        int padding = 1;
        int panelWidth = items.size() * slotSize + padding * 2;
        int panelHeight = slotSize + padding * 2;

        int panelX = mouseX + 6;
        if (panelX + panelWidth > this.width) { panelX = this.width - panelWidth; }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.f, 0.f, 400.f);

        guiGraphics.fill(panelX, mouseY, panelX + panelWidth, mouseY + panelHeight, 0xF0100010);
        guiGraphics.fill(panelX + 1, mouseY + 1, panelX + panelWidth - 1, mouseY + panelHeight - 1, 0xFF2D2D3A);
        for(int i = 0; i < items.size(); ++i) {
            int slotX = panelX + padding + i * slotSize + 1;
            int slotY = mouseY + padding + 1;
            guiGraphics.fill(slotX, slotY, slotX + 16, slotY + 16, 0xFF8B8B8B);

            if (i < items.size()) {
                ItemStack stack = items.get(i);
                if (!stack.isEmpty()) {
                    guiGraphics.renderFakeItem(stack, slotX, slotY);
                    guiGraphics.renderItemDecorations(this.font, stack, slotX, slotY);
                }
            }
        }

        guiGraphics.pose().popPose();
    }
}