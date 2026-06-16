package kr.pyke.deceased_croa.client.gui.menu;

import kr.pyke.deceased_croa.data.MailboxData;
import kr.pyke.deceased_croa.registry.component.ModComponents;
import kr.pyke.deceased_croa.registry.component.mailbox.IMailbox;
import kr.pyke.deceased_croa.registry.menu.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class MailboxMenu extends AbstractContainerMenu {
    private static final int INVENTORY_START_X = 12;
    private static final int INVENTORY_START_Y = 148;
    private static final int HOTBAR_Y = 206;

    private final Player player;

    public MailboxMenu(int containerID, Inventory playerInventory) {
        super(ModMenus.MAILBOX, containerID);
        this.player = playerInventory.player;

        for(int row = 0; row < 3; ++row) {
            for(int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, INVENTORY_START_X + col * 18, INVENTORY_START_Y + row * 18));
            }
        }

        for(int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, INVENTORY_START_X + col * 18, HOTBAR_Y));
        }
    }

    private IMailbox mailbox() {
        return ModComponents.MAILBOX.get(this.player);
    }

    private Optional<MailboxData> findMail(UUID uuid) {
        return this.mailbox().getMails().stream().filter(mail -> mail.mailUUID().equals(uuid)).findFirst();
    }

    public void claimMail(Player player, UUID uuid) {
        Optional<MailboxData> mail = this.findMail(uuid);
        if (mail.isEmpty()) { return; }

        this.mailbox().claimMail(player, mail.get());
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}