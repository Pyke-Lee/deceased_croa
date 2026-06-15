package kr.pyke.deceased_croa.registry.component.mailbox;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import kr.pyke.deceased_croa.data.MailboxData;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public interface IMailbox extends ComponentV3, AutoSyncedComponent {
    List<MailboxData> getMails();
    void addMail(MailboxData mail, boolean viewMessage);
    void removeMail(MailboxData mail);
    void clearAll();
    void claimMail(Player player, MailboxData mail);
}
