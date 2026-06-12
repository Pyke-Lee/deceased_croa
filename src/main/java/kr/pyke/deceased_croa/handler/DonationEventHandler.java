package kr.pyke.deceased_croa.handler;

import kr.pyke.PykeLib;
import kr.pyke.deceased_croa.data.MailboxData;
import kr.pyke.deceased_croa.data.RandomBoxDefinition;
import kr.pyke.deceased_croa.manager.RandomBoxManager;
import kr.pyke.deceased_croa.registry.component.ModComponents;
import kr.pyke.deceased_croa.registry.item.randombox.RandomBoxItem;
import kr.pyke.integration.event.DonationReceivedCallback;
import kr.pyke.type.PLATFORM;
import kr.pyke.util.constants.COLOR;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DonationEventHandler {
    private DonationEventHandler() { }

    public static void register() {
        DonationReceivedCallback.DONATION_RECEIVED.register((player, event) -> {
            String name = player.getDisplayName().getString();
            PLATFORM platform = event.platform();
            String sender = event.donor();
            String message = event.donationMessage();
            int amount = event.getAmount();
            int krwAmount = amount;
            String notification = "";

            if (platform == PLATFORM.SOOP) {
                krwAmount *= 100;
                notification = String.format("&7%s&r님이 &e별풍선 %,d&개&r를 후원 받으셨습니다.", name, amount);
            }
            else if (platform == PLATFORM.CHZZK) {
                notification = String.format("&7%s&r님이 &e%,d 치즈&r를 후원 받으셨습니다.", name, amount);
            }

            // 1만원 (100개)
            if (10000 == krwAmount) {
                ItemStack itemStack = createRandomBox("croa_box", 1);

                MailboxData mailboxData = MailboxData.create("크로아 상자", sender, message, List.of(itemStack.copy()));
                ModComponents.MAILBOX.get(player).addMail(mailboxData);
            }

            // 10만원 (1000개)
            if (100000 == krwAmount) {
                ItemStack itemStack = createRandomBox("croa_box", 11);

                MailboxData mailboxData = MailboxData.create("크로아 상자", sender, message, List.of(itemStack.copy()));
                ModComponents.MAILBOX.get(player).addMail(mailboxData);
            }
        });
    }

    private static void sendPersonalMessage(ServerPlayer player, String message) {
        PykeLib.sendSystemMessage(player, COLOR.LIME.getColor(), message);
    }

    private static void sendServerMessage(ServerPlayer player, String message) {
        MinecraftServer server = player.level().getServer();
        if (server == null) { return; }

        PykeLib.sendSystemMessage(server.getPlayerList().getPlayers(), COLOR.LIME.getColor(), message);
    }

    private static void broadcastMessage(ServerPlayer player, String message) {
        MinecraftServer server = player.level().getServer();
        if (server == null) { return; }

        PykeLib.sendBroadcastMessage(server.getPlayerList().getPlayers(), COLOR.LIME.getColor(), message);
    }

    private static ItemStack createRandomBox(String boxID, int amount) {
        RandomBoxDefinition definition = RandomBoxManager.get(boxID);
        if (definition == null) { return ItemStack.EMPTY; }

        ItemStack itemStack = RandomBoxItem.createStack(definition);
        itemStack.setCount(amount);

        return itemStack;
    }
}
