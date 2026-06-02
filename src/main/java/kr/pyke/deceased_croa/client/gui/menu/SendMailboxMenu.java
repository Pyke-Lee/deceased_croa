package kr.pyke.deceased_croa.client.gui.menu;

import kr.pyke.deceased_croa.registry.menu.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SendMailboxMenu extends AbstractContainerMenu {
    public static final int ATTACHMENT_SLOT_COUNT = 7;
    private static final int ATTACHMENT_SLOT_X = 8;
    private static final int ATTACHMENT_SLOT_Y = 61;
    private static final int INVENTORY_START_X = 8;
    private static final int INVENTORY_START_Y = 83;
    private static final int HOTBAR_Y = 141;

    private final Player player;
    private final Container attachmentContainer = new SimpleContainer(ATTACHMENT_SLOT_COUNT) {
        @Override
        public boolean stillValid(Player player) {
            return true;
        }
    };

    public SendMailboxMenu(int containerID, Inventory playerInventory) {
        super(ModMenus.SEND_MAILBOX, containerID);
        this.player = playerInventory.player;

        for(int col = 0; col < ATTACHMENT_SLOT_COUNT; ++col) {
            this.addSlot(new Slot(this.attachmentContainer, col, ATTACHMENT_SLOT_X + col * 18, ATTACHMENT_SLOT_Y));
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

    public List<ItemStack> collectAttachments() {
        List<ItemStack> items = new ArrayList<>();
        for(int i = 0; i < ATTACHMENT_SLOT_COUNT; ++i) {
            ItemStack stack = this.attachmentContainer.getItem(i);
            if (!stack.isEmpty()) { items.add(stack.copy()); }
        }

        return items;
    }

    public void clearAttachments() {
        this.attachmentContainer.clearContent();
        this.broadcastChanges();
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            copy = stack.copy();
            if (index < ATTACHMENT_SLOT_COUNT) {
                if (!this.moveItemStackTo(stack, ATTACHMENT_SLOT_COUNT, this.slots.size(), true)) { return ItemStack.EMPTY; }
            }
            else {
                if (!this.moveItemStackTo(stack, 0, ATTACHMENT_SLOT_COUNT, false)) { return ItemStack.EMPTY; }
            }

            if (stack.isEmpty()) { slot.set(ItemStack.EMPTY); }
            else { slot.setChanged(); }
        }

        return copy;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide()) {
            for(int i = 0; i < ATTACHMENT_SLOT_COUNT; ++i) {
                ItemStack stack = this.attachmentContainer.getItem(i);
                if (!stack.isEmpty()) {
                    player.getInventory().add(stack);

                    if (!stack.isEmpty()) { player.drop(stack, false); }
                }
            }

            this.attachmentContainer.clearContent();
        }
    }

    @Override public boolean stillValid(Player player) { return true; }
}