package kr.pyke.deceased_croa.util;

import kr.pyke.PykeLib;
import kr.pyke.deceased_croa.data.DisplayNameData;
import kr.pyke.deceased_croa.network.pakcet.s2c.S2C_SendSingleDisplayName;
import kr.pyke.util.constants.COLOR;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.EnumSet;
import java.util.List;

public class Utils {
    private Utils() { }

    public static void refreshTabList(ServerPlayer player) {
        EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions = EnumSet.of(
            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME,
            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED
        );
        ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(actions, List.of(player));
        player.server.getPlayerList().broadcastAll(packet);
    }

    public static String stripColor(String displayName) {
        return displayName.replaceAll("(?i)[&§][0-9A-FK-OR]", "");
    }

    public static void updateDisplayName(ServerPlayer target, String displayName, ServerPlayer sender) {
        updateDisplayName(target, displayName);

        String targetName = target.getGameProfile().getName();
        PykeLib.sendSystemMessage(sender, COLOR.LIME.getColor(), String.format("&7%s&f님의 이름을 &7%s&f(으)로 변경하였습니다.", targetName, displayName));
    }

    public static void updateDisplayName(ServerPlayer target, String displayName) {
        MinecraftServer server = target.getServer();
        if (server == null) { return; }

        DisplayNameData data = DisplayNameData.getServerState(server);
        data.setDisplayName(target.getUUID(), displayName);

        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            S2C_SendSingleDisplayName.send(p, target.getUUID(), displayName);
        }

        refreshTabList(target);

        PykeLib.sendSystemMessage(target, COLOR.LIME.getColor(), String.format("&f이름이 &7%s&f(으)로 변경되었습니다.", displayName));
    }

    public static String stripFormatting(String value) {
        String stripped = ChatFormatting.stripFormatting(value);
        return stripped == null ? value : stripped;
    }
}
