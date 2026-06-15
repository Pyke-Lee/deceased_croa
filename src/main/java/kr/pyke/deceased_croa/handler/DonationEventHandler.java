package kr.pyke.deceased_croa.handler;

import kr.pyke.PykeLib;
import kr.pyke.deceased_croa.data.MailboxData;
import kr.pyke.deceased_croa.data.RandomBoxDefinition;
import kr.pyke.deceased_croa.manager.HordeManager;
import kr.pyke.deceased_croa.manager.RandomBoxManager;
import kr.pyke.deceased_croa.registry.component.ModComponents;
import kr.pyke.deceased_croa.registry.item.randombox.RandomBoxItem;
import kr.pyke.deceased_croa.registry.mob_effect.ModEffects;
import kr.pyke.deceased_croa.type.HORDE_TYPE;
import kr.pyke.integration.event.DonationReceivedCallback;
import kr.pyke.type.PLATFORM;
import kr.pyke.util.constants.COLOR;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
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
                ModComponents.MAILBOX.get(player).addMail(mailboxData, false);
            }

            // 3만원 (300개)
            if (30000 == krwAmount) {
                player.addEffect(new MobEffectInstance(ModEffects.AGGRO, 20 * 30, 0, false, true, true));
                sendPersonalMessage(player, COLOR.RED.getColor(), "&6[SYSTEM]&r 주변에서 몬스터들이 물려옵니다.");
            }

            // 4.44만원 (444개)
            if (44400 == krwAmount) {
                HordeManager.spawnHorde(player.serverLevel(), player, HORDE_TYPE.NORMAL);
                sendServerMessage(player, COLOR.RED.getColor(), String.format("&6[SYSTEM]&r &7%s&r님 주변에서 몬스터들이 물려옵니다.", player.getDisplayName().getString()));
            }

            // 10만원 (1000개)
            if (100000 == krwAmount) {
                ItemStack itemStack = createRandomBox("croa_box", 11);

                MailboxData mailboxData = MailboxData.create("크로아 상자 x11", sender, message, List.of(itemStack.copy()));
                ModComponents.MAILBOX.get(player).addMail(mailboxData, false);
            }

            // 20만원 (2000개)
            if (200000 == krwAmount) {
                HordeManager.spawnHorde(player.serverLevel(), player, HORDE_TYPE.SPECIAL);
                broadcastMessage(player, COLOR.RED.getColor(), String.format("&6[SYSTEM]&r &7%s&r님 주변에서 몬스터들이 창궐합니다.", player.getDisplayName().getString()));
            }

            // 30만원 (3000개)
            if (300000 == krwAmount) {
                ItemStack itemStack = createRandomBox("croa_box", 12);

                player.serverLevel().getServer().getPlayerList().getPlayers().forEach(target -> {
                    MailboxData mailboxData = MailboxData.create("크로아 상자 x12", sender, message, List.of(itemStack.copy()));
                    ModComponents.MAILBOX.get(target).addMail(mailboxData, false);

                    sendTitle(target, "§a[!]", "전체 보상 지급", 20, 40, 20);
                });
            }
        });
    }

    private static void sendPersonalMessage(ServerPlayer player, String message) {
        PykeLib.sendSystemMessage(player, COLOR.LIME.getColor(), message);
    }

    private static void sendPersonalMessage(ServerPlayer player, int color, String message) {
        PykeLib.sendSystemMessage(player, color, message);
    }

    private static void sendServerMessage(ServerPlayer player, String message) {
        MinecraftServer server = player.level().getServer();
        if (server == null) { return; }

        PykeLib.sendSystemMessage(server.getPlayerList().getPlayers(), COLOR.LIME.getColor(), message);
    }

    private static void sendServerMessage(ServerPlayer player, int color, String message) {
        MinecraftServer server = player.level().getServer();
        if (server == null) { return; }

        PykeLib.sendSystemMessage(server.getPlayerList().getPlayers(), color, message);
    }

    private static void broadcastMessage(ServerPlayer player, String message) {
        MinecraftServer server = player.level().getServer();
        if (server == null) { return; }

        PykeLib.sendBroadcastMessage(server.getPlayerList().getPlayers(), COLOR.LIME.getColor(), message);
    }

    private static void broadcastMessage(ServerPlayer player, int color, String message) {
        MinecraftServer server = player.level().getServer();
        if (server == null) { return; }

        PykeLib.sendBroadcastMessage(server.getPlayerList().getPlayers(), color, message);
    }

    private static ItemStack createRandomBox(String boxID, int amount) {
        RandomBoxDefinition definition = RandomBoxManager.get(boxID);
        if (definition == null) { return ItemStack.EMPTY; }

        ItemStack itemStack = RandomBoxItem.createStack(definition);
        itemStack.setCount(amount);

        return itemStack;
    }

    private static void sendTitle(ServerPlayer player, String title, String sub, int fadeIn, int stay, int fadeOut) {
        player.connection.send(new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut));
        player.connection.send(new ClientboundSetTitleTextPacket(Component.nullToEmpty(title)));
        if (sub != null) { player.connection.send(new ClientboundSetSubtitleTextPacket(Component.nullToEmpty(sub))); }
    }
}
