package kr.pyke.deceased_croa.handler;

import kr.pyke.PykeLib;
import kr.pyke.deceased_croa.data.MailboxData;
import kr.pyke.deceased_croa.data.RandomBoxDefinition;
import kr.pyke.deceased_croa.manager.HordeManager;
import kr.pyke.deceased_croa.manager.RandomBoxManager;
import kr.pyke.deceased_croa.network.pakcet.s2c.S2C_PlaySoundPacket;
import kr.pyke.deceased_croa.registry.component.ModComponents;
import kr.pyke.deceased_croa.registry.item.ModItems;
import kr.pyke.deceased_croa.registry.item.randombox.RandomBoxItem;
import kr.pyke.deceased_croa.registry.mob_effect.ModEffects;
import kr.pyke.deceased_croa.registry.sound.ModSounds;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
                notification = String.format("별풍선 %,d개", amount);
            }
            else if (platform == PLATFORM.CHZZK) {
                notification = String.format("%,d 치즈", amount);
            }

            // 5천원 (50개)
            if (5000 == krwAmount) {
                ItemStack itemStack = new ItemStack(ModItems.GALIC_BREAD);
                itemStack.setCount(3);

                MailboxData mailboxData = MailboxData.create(itemStack.copy());
                ModComponents.MAILBOX.get(player).addMail(mailboxData, false);
                sendPersonalMessage(player, 16717676, String.format("§a%s§r님이 §b%s§r님에게 §e%s§r로 §6[ 마늘 빵 3개 ]§r를 후원합니다.", sender, name, notification));
                S2C_PlaySoundPacket.send(player, ModSounds.BOX_OPEN, 2.f, 1.f);
            }

            // 1만원 (100개)
            if (10000 == krwAmount) {
                ItemStack itemStack = createRandomBox("croa_box", 1);

                MailboxData mailboxData = MailboxData.create(itemStack.copy());
                ModComponents.MAILBOX.get(player).addMail(mailboxData, false);
                sendPersonalMessage(player, 16717676, String.format("§a%s§r님이 §b%s§r님에게 §e%s§r로 §6[ 크로아 상자 ]§r를 후원합니다.", sender, name, notification));
                S2C_PlaySoundPacket.send(player, ModSounds.ANC_BROADCAST, 2.f, 1.f);
            }

            // 3만원 (300개)
            if (30000 == krwAmount) {
                player.addEffect(new MobEffectInstance(ModEffects.AGGRO, 20 * 30, 0, false, true, true));
                sendServerMessage(player, COLOR.RED.getColor(), String.format("§a%s§r님이 §b%s§r님에게 §e%s§r로 §c[ 인간 사이렌 ]§r 이벤트를 후원합니다.", sender, name, notification));
                sendPersonalMessage(player, COLOR.RED.getColor(), "지속적으로 소음이 발생하여 주변 몬스터들의 주의를 끕니다.");
                sendTitle(player, "§c[!] 경고 [!]", "§c지속적으로 사이렌이 울립니다.", 20, 60, 20);
            }

            // 4.4만원 (444개)
            if ((44400 == krwAmount && platform == PLATFORM.SOOP) || (44000 == krwAmount && platform == PLATFORM.CHZZK)) {
                HordeManager.startHorde(player.serverLevel(), player, HORDE_TYPE.NORMAL);
                sendServerMessage(player, COLOR.RED.getColor(), String.format("§a%s§r님이 §b%s§r님에게 §e%s§r로 §c[ 호드 나이트 ]§r를 발동 시킵니다.", sender, name, notification));
                sendTitle(player, "§c[!] 경고 [!]", "§c호드 나이트가 시작됩니다.", 20, 60, 20);
                S2C_PlaySoundPacket.send(player, ModSounds.NORMAL_HORDES, 1.f, 1.f);
                player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20 * 5, 0, false, true, true));
            }

            // 10만원 (1000개)
            if (100000 == krwAmount) {
                ItemStack itemStack = createRandomBox("croa_box", 12);

                MailboxData mailboxData = MailboxData.create(itemStack.copy());
                ModComponents.MAILBOX.get(player).addMail(mailboxData, false);
                sendServerMessage(player, 16717676, String.format("§a%s§r님이 §b%s§r님에게 §e%s§r로 §6[ 크로아 상자 12개 ]§r를 후원합니다.", sender, name, notification));
                sendTitle(player, "§d[!] 알림 [!]", (platform == PLATFORM.SOOP) ? "§b별풍선 1,000개§r 감사합니다!" : "§e10만 치즈§r 감사합니다!", 10, 30, 10);
                S2C_PlaySoundPacket.send(player, ModSounds.COIN, 2.f, 1.f);
            }

            // 20만원 (2000개)
            if (200000 == krwAmount) {
                HordeManager.startHorde(player.serverLevel(), player, HORDE_TYPE.SPECIAL);
                broadcastMessage(player, COLOR.RED.getColor(), String.format("§a%s§r님이 §b%s§r님에게 §e%s§r로 §c[ 좀비 대재앙 ]§r을 발동 시킵니다.", sender, name, notification));
                sendTitle(player, "§c[!] 경고 [!]", "§c좀비 대재앙이 발생합니다.", 20, 60, 20);
                S2C_PlaySoundPacket.send(player, SoundEvents.WITHER_DEATH, 1.f, 1.f);
                player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20 * 5, 0, false, true, true));
            }

            // 44만원 (4444개)
            if ((444400 == krwAmount && platform == PLATFORM.SOOP) || (440000 == krwAmount && platform == PLATFORM.CHZZK)) {
                MinecraftServer server = player.getServer();
                if (server == null) { return; }

                for (ServerPlayer target : server.getPlayerList().getPlayers()) {
                    if (target.isCreative() || target.isSpectator()) { continue; }

                    HordeManager.startHorde(target.serverLevel(), target, HORDE_TYPE.SERVER);
                    sendTitle(target, "§4[!] 경고 [!]", "§4땅울림이 시작 됩니다.", 20, 60, 20);
                    S2C_PlaySoundPacket.send(target, ModSounds.HORDE_SPAWN, 1.f, 1.f);
                    S2C_PlaySoundPacket.send(target, ModSounds.HORDE, 3.f, 1.f);
                    target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20 * 5, 0, false, true, true));
                }
                broadcastMessage(player, COLOR.RED.getColor(), String.format("§a%s§r님이 §b%s§r님에게 §e%s§r로 §4[ 전체 땅울림 ]§r을 발동 시킵니다.", sender, name, notification));
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

    public static void sendTitle(ServerPlayer player, String title, String sub, int fadeIn, int stay, int fadeOut) {
        player.connection.send(new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut));
        player.connection.send(new ClientboundSetTitleTextPacket(Component.nullToEmpty(title)));
        if (sub != null) { player.connection.send(new ClientboundSetSubtitleTextPacket(Component.nullToEmpty(sub))); }
    }
}
