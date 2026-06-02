package kr.pyke.deceased_croa.util;

import kr.pyke.deceased_croa.client.gui.menu.MailboxMenu;
import kr.pyke.deceased_croa.client.gui.menu.SendMailboxMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;

public class MailboxOpener {
    public static void open(ServerPlayer serverPlayer) {
        serverPlayer.openMenu(new SimpleMenuProvider(
            (containerID, playerInventory, player) -> new MailboxMenu(containerID, playerInventory),
            Component.translatable("menu.deceased_croa.mailbox.title")
        ));
    }

    public static void openSend(ServerPlayer serverPlayer) {
        serverPlayer.openMenu(new SimpleMenuProvider(
            (containerID, playerInventory, player) -> new SendMailboxMenu(containerID, playerInventory),
            Component.translatable("menu.deceased_croa.mailbox.send.title")
        ));
    }
}