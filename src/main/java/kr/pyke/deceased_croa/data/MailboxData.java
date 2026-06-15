package kr.pyke.deceased_croa.data;

import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public record MailboxData(UUID mailUUID, long sentDate, ItemStack itemStack) {
    public static MailboxData create(ItemStack itemStack) {
        return new MailboxData(UUID.randomUUID(), System.currentTimeMillis(), itemStack);
    }
}