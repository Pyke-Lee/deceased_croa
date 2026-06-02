package kr.pyke.deceased_croa.data;

import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.UUID;

public record MailboxData(UUID mailUUID, String mailTitle, String senderName, long sentDate, String mailMessage, List<ItemStack> itemStackList) {
    public static MailboxData create(String title, String sender, String message, List<ItemStack> itemStackList) {
        return new MailboxData(UUID.randomUUID(), title, sender, System.currentTimeMillis(), message, itemStackList);
    }
}