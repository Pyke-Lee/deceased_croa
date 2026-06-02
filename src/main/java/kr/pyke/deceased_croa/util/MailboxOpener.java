package kr.pyke.deceased_croa.util;

import kr.pyke.deceased_croa.client.gui.menu.MailboxMenu;
import kr.pyke.deceased_croa.client.gui.menu.SendMailboxMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

public class MailboxOpener {
    public static void open(ServerPlayer player) {
        player.openMenu(new MenuProvider() {
            @Override public @NotNull Component getDisplayName() { return Component.translatable("menu.deceased_croa.mailbox.title"); }

            @Override public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) { return new MailboxMenu(containerId, playerInventory); }
        });
    }

    public static void openSend(ServerPlayer player) {
        player.openMenu(new MenuProvider() {
            @Override public @NotNull Component getDisplayName() { return Component.translatable("menu.deceased_croa.mailbox.send.title"); }

            @Override public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) { return new SendMailboxMenu(containerId, playerInventory); }
        });
    }
}
