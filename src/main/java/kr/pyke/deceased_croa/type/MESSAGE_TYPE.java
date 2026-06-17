package kr.pyke.deceased_croa.type;

import kr.pyke.PykeLib;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public enum MESSAGE_TYPE {
    PERSONAL,
    SERVER,
    BROADCAST;

    public static MESSAGE_TYPE fromString(String value) {
        if (value == null) { return PERSONAL; }

        return switch (value.toLowerCase()) {
            case "server" -> SERVER;
            case "broadcast" -> BROADCAST;
            default -> PERSONAL;
        };
    }

    public void send(ServerPlayer player, int color, String message) {
        MinecraftServer server = player.level().getServer();

        switch (this) {
            case SERVER: {
                if (server == null) {
                    PykeLib.sendSystemMessage(player, color, message);
                    return;
                }

                PykeLib.sendSystemMessage(server.getPlayerList().getPlayers(), color, message);
                break;
            }
            case BROADCAST: {
                if (server == null) {
                    PykeLib.sendSystemMessage(player, color, message);
                    return;
                }

                PykeLib.sendBroadcastMessage(server.getPlayerList().getPlayers(), color, message);
                break;
            }
            default: {
                PykeLib.sendSystemMessage(player, color, message);
                break;
            }
        }
    }

    public List<ServerPlayer> soundTargets(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        if (this != BROADCAST || server == null) { return List.of(player); }

        return server.getPlayerList().getPlayers();
    }
}