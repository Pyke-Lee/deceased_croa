package kr.pyke.deceased_croa.registry.component.mailbox;

import kr.pyke.PykeLib;
import kr.pyke.deceased_croa.data.MailboxData;
import kr.pyke.deceased_croa.registry.component.ModComponents;
import kr.pyke.util.constants.COLOR;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
    public void addMail(MailboxData mail, boolean viewMessage) {
        if (mail.itemStack() == ItemStack.EMPTY || mail.itemStack().is(Items.AIR)) { return; }

        mailboxData.add(mail);
        if (viewMessage) { PykeLib.sendSystemMessage((ServerPlayer) player, COLOR.YELLOW.getColor(), "새 우편이 도착했습니다!"); }
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
        ItemStack copyItem = mail.itemStack().copy();
        player.getInventory().add(copyItem);

        if (!copyItem.isEmpty()) {
            player.drop(copyItem, false);
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
            long date = mailTag.getLong("Date");

            CompoundTag itemTag = mailTag.getCompound("Item");
            ItemStack itemStack = ItemStack.of(itemTag);

            mailboxData.add(new MailboxData(uuid, date, itemStack));
        }
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        ListTag mailsTag = new ListTag();

        for (MailboxData mail : mailboxData) {
            CompoundTag mailTag = new CompoundTag();

            mailTag.putUUID("UUID", mail.mailUUID());
            mailTag.putLong("Date", mail.sentDate());

            CompoundTag itemTag = new CompoundTag();
            mail.itemStack().save(itemTag);
            mailTag.put("Item", itemTag);

            mailsTag.add(mailTag);
        }

        tag.put("Mails", mailsTag);
    }
}