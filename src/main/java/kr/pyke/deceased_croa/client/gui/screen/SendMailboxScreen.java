package kr.pyke.deceased_croa.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.client.gui.menu.SendMailboxMenu;
import kr.pyke.deceased_croa.network.pakcet.c2s.C2S_SendMailPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SendMailboxScreen extends AbstractContainerScreen<SendMailboxMenu> {
    private static final ResourceLocation MAILBOX_SEND_LOCATION = new ResourceLocation(DeceasedCroa.MOD_ID, "textures/gui/container/mailbox_send.png");

    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int TITLE_X = 8;
    private static final int TITLE_Y = 6;
    private static final int FIELD_ROW_Y = 21;
    private static final int RECIPIENT_X = 8;
    private static final int RECIPIENT_WIDTH = 78;
    private static final int SENDER_X = 90;
    private static final int SENDER_WIDTH = 78;
    private static final int FIELD_WIDTH = 160;
    private static final int FIELD_HEIGHT = 12;
    private static final int MESSAGE_X = 8;
    private static final int MESSAGE_Y = 36;
    private static final int MESSAGE_WIDTH = 162;
    private static final int MESSAGE_HEIGHT = 21;
    private static final int SUBMIT_BUTTON_X = 137;
    private static final int SUBMIT_BUTTON_Y = 61;
    private static final int SUBMIT_BUTTON_WIDTH = 30;
    private static final int SUBMIT_BUTTON_HEIGHT = 18;

    private final boolean isAdmin;
    private final String defaultSender;

    private EditBox titleBox;
    private EditBox recipientBox;
    private EditBox senderBox;
    private MultiLineEditBox messageBox;

    public SendMailboxScreen(SendMailboxMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.isAdmin = this.isAdminPlayer(playerInventory);
        this.defaultSender = playerInventory.player.getDisplayName().getString();
    }

    private boolean isAdminPlayer(Inventory playerInventory) {
        return playerInventory.player.hasPermissions(2);
    }

    @Override
    protected void init() {
        super.init();
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        this.titleBox = new EditBox(this.font, i + TITLE_X, j + TITLE_Y, FIELD_WIDTH, FIELD_HEIGHT, Component.translatable("menu.deceased_croa.mailbox.send.title_field"));
        this.titleBox.setMaxLength(64);
        this.titleBox.setHint(Component.translatable("menu.deceased_croa.mailbox.send.title_hint"));
        this.addRenderableWidget(this.titleBox);

        this.recipientBox = new EditBox(this.font, i + RECIPIENT_X, j + FIELD_ROW_Y, RECIPIENT_WIDTH, FIELD_HEIGHT, Component.translatable("menu.deceased_croa.mailbox.send.recipient_field"));
        this.recipientBox.setMaxLength(128);
        this.recipientBox.setHint(Component.translatable("menu.deceased_croa.mailbox.send.recipient_hint"));
        this.addRenderableWidget(this.recipientBox);

        this.senderBox = new EditBox(this.font, i + SENDER_X, j + FIELD_ROW_Y, SENDER_WIDTH, FIELD_HEIGHT, Component.translatable("menu.deceased_croa.mailbox.send.sender_field"));
        this.senderBox.setMaxLength(64);
        this.senderBox.setValue(this.defaultSender);
        if (!this.isAdmin) { this.senderBox.setEditable(false); }
        this.addRenderableWidget(this.senderBox);

        this.messageBox = new MultiLineEditBox(this.font, i + MESSAGE_X, j + MESSAGE_Y, MESSAGE_WIDTH, MESSAGE_HEIGHT, Component.translatable("menu.deceased_croa.mailbox.send.message_hint"), Component.translatable("menu.deceased_croa.mailbox.send.message_field"));
        this.addRenderableWidget(this.messageBox);

        this.addRenderableWidget(Button.builder(Component.translatable("menu.deceased_croa.mailbox.send.submit"), button -> this.submit()).bounds(i + SUBMIT_BUTTON_X, j + SUBMIT_BUTTON_Y, SUBMIT_BUTTON_WIDTH, SUBMIT_BUTTON_HEIGHT).build());
    }

    private void submit() {
        String mailTitle = this.titleBox.getValue().trim();
        String recipient = this.recipientBox.getValue().trim();
        String sender = this.senderBox.getValue().trim();
        String message = this.messageBox.getValue();
        if (message.length() > 1024) { message = message.substring(0, 1024); }

        if (sender.isEmpty()) { sender = this.defaultSender; }

        if (mailTitle.isEmpty() || recipient.isEmpty()) { return; }

        C2S_SendMailPacket.send(mailTitle, recipient, sender, message);
        this.onClose();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(MAILBOX_SEND_LOCATION, i, j, 0, 0.f, 0.f, this.imageWidth, this.imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        RenderSystem.enableDepthTest();
    }

    @Override
    public void containerTick() {
        this.titleBox.tick();
        this.recipientBox.tick();
        this.senderBox.tick();
        this.messageBox.tick();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { return super.keyPressed(keyCode, scanCode, modifiers); }

        if (this.titleBox.isFocused() || this.recipientBox.isFocused() || this.senderBox.isFocused() || this.messageBox.isFocused()) {
            return this.getFocused() != null && this.getFocused().keyPressed(keyCode, scanCode, modifiers);
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.getFocused() != null) { return this.getFocused().charTyped(codePoint, modifiers); }

        return super.charTyped(codePoint, modifiers);
    }
}