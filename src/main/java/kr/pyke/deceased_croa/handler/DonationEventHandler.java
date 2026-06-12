package kr.pyke.deceased_croa.handler;

import kr.pyke.PykeLib;
import kr.pyke.integration.event.DonationReceivedCallback;
import kr.pyke.type.PLATFORM;
import kr.pyke.util.constants.COLOR;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class DonationEventHandler {
    private DonationEventHandler() { }

    public static void register() {
        DonationReceivedCallback.DONATION_RECEIVED.register((player, event) -> {
            String name = player.getDisplayName().getString();
            PLATFORM platform = event.platform();
            String sender = event.donor();
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

            }

            // 10만원 (1000개)
            if (100000 == krwAmount) {

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
}
