package kr.pyke.deceased_croa.registry.component.mailbox;

import kr.pyke.PykeLib;
import kr.pyke.deceased_croa.data.MailboxData;
import kr.pyke.deceased_croa.registry.component.ModComponents;
import kr.pyke.util.constants.COLOR;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Mailbox implements IMailbox {
    private final Player player;
    List<MailboxData> mailboxData = new ArrayList<>();

    public Mailbox(Player player) {
        this.player = player;
    }

    @Override public List<MailboxData> getMails() { return this.mailboxData; }

    @Override
    public void addMail(MailboxData mail) {
        mailboxData.add(mail);
        PykeLib.sendSystemMessage((ServerPlayer) player, COLOR.YELLOW.getColor(), "새 우편이 도착했습니다!");
        this.player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        ModComponents.MAILBOX.sync(player);
    }

    @Override
    public void removeMail(MailboxData mail) {
        mailboxData.remove(mail);
        ModComponents.MAILBOX.sync(player);
    }

    @Override
    public void clearAll() {
        mailboxData.clear();
        ModComponents.MAILBOX.sync(player);
    }

    @Override
    public void claimMail(Player player, MailboxData mail) {
        for(ItemStack itemStack : mail.itemStackList()) {
            ItemStack copyItem = itemStack.copy();
            player.getInventory().add(copyItem);

            if (!copyItem.isEmpty()) {
                player.drop(copyItem, false);
            }
        }

        mailboxData.remove(mail);
        ModComponents.MAILBOX.sync(player);
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        mailboxData.clear();

        ListTag mailsTag = tag.getList("Mails", ListTag.TAG_COMPOUND);

        for (int i = 0; i < mailsTag.size(); ++i) {
            CompoundTag mailTag = mailsTag.getCompound(i);

            UUID uuid = mailTag.getUUID("UUID");
            String title = mailTag.getString("Title");
            String sender = mailTag.getString("Sender");
            long date = mailTag.getLong("Date");
            String message = mailTag.getString("Message");

            List<ItemStack> items = new ArrayList<>();
            ListTag itemsTag = mailTag.getList("Items", ListTag.TAG_COMPOUND);

            for (Tag value : itemsTag) {
                ItemStack itemStack = ItemStack.of((CompoundTag) value);
                if (!itemStack.isEmpty()) {
                    items.add(itemStack);
                }
            }

            mailboxData.add(new MailboxData(uuid, title, sender, date, message, items));
        }
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        ListTag mailsTag = new ListTag();

        for (MailboxData mail : mailboxData) {
            CompoundTag mailTag = new CompoundTag();

            mailTag.putUUID("UUID", mail.mailUUID());
            mailTag.putString("Title", mail.mailTitle());
            mailTag.putString("Sender", mail.senderName());
            mailTag.putLong("Date", mail.sentDate());
            mailTag.putString("Message", mail.mailMessage());

            ListTag itemsTag = new ListTag();
            for (ItemStack item : mail.itemStackList()) {
                if (!item.isEmpty()) {
                    itemsTag.add(item.save(new CompoundTag()));
                }
            }
            mailTag.put("Items", itemsTag);

            mailsTag.add(mailTag);
        }

        tag.put("Mails", mailsTag);
    }
}