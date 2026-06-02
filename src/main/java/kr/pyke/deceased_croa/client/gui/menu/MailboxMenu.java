package kr.pyke.deceased_croa.client.gui.menu;

import kr.pyke.deceased_croa.data.MailboxData;
import kr.pyke.deceased_croa.registry.component.ModComponents;
import kr.pyke.deceased_croa.registry.component.mailbox.IMailbox;
import kr.pyke.deceased_croa.registry.menu.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MailboxMenu extends AbstractContainerMenu {
    public static final int PREVIEW_SLOT_COUNT = 7;
    private static final int PREVIEW_SLOT_X = 108;
    private static final int PREVIEW_SLOT_Y = 82;
    private static final int INVENTORY_START_X = 108;
    private static final int INVENTORY_START_Y = 104;
    private static final int HOTBAR_Y = 162;

    private final Player player;
    private final Container previewContainer = new SimpleContainer(PREVIEW_SLOT_COUNT) {
        @Override
        public boolean stillValid(Player player) {
            return true;
        }
    };

    public MailboxMenu(int containerID, Inventory playerInventory) {
        super(ModMenus.MAILBOX, containerID);
        this.player = playerInventory.player;

        for(int col = 0; col < PREVIEW_SLOT_COUNT; ++col) {
            this.addSlot(new PreviewSlot(this.previewContainer, col, PREVIEW_SLOT_X + col * 18, PREVIEW_SLOT_Y));
        }

        for(int row = 0; row < 3; ++row) {
            for(int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, INVENTORY_START_X + col * 18, INVENTORY_START_Y + row * 18));
            }
        }

        for(int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, INVENTORY_START_X + col * 18, HOTBAR_Y));
        }
    }

    private IMailbox mailbox() { return ModComponents.MAILBOX.get(this.player); }

    private Optional<MailboxData> findMail(UUID uuid) { return this.mailbox().getMails().stream().filter(mail -> mail.mailUUID().equals(uuid)).findFirst(); }

    public void selectMail(UUID uuid) {
        this.refreshPreview(uuid);
    }

    public void claimMail(Player player, UUID uuid) {
        Optional<MailboxData> mail = this.findMail(uuid);
        if (mail.isEmpty()) { return; }

        this.mailbox().claimMail(player, mail.get());
        this.refreshPreview(null);
    }

    private void refreshPreview(UUID uuid) {
        this.previewContainer.clearContent();
        if (uuid == null) {
            this.broadcastChanges();
            return;
        }

        Optional<MailboxData> mail = this.findMail(uuid);
        if (mail.isEmpty()) {
            this.broadcastChanges();
            return;
        }

        List<ItemStack> items = mail.get().itemStackList();
        for(int i = 0; i < items.size() && i < PREVIEW_SLOT_COUNT; ++i) {
            this.previewContainer.setItem(i, items.get(i).copy());
        }

        this.broadcastChanges();
    }

    @Override public @NotNull ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }

    @Override public boolean stillValid(Player player) { return true; }

    private static class PreviewSlot extends Slot {
        public PreviewSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override public boolean mayPlace(ItemStack stack) { return false; }

        @Override public boolean mayPickup(Player player) { return false; }
    }
}