package kr.pyke.deceased_croa.handler;

import kr.pyke.deceased_croa.data.DisplayNameData;
import kr.pyke.deceased_croa.network.pakcet.s2c.S2C_SendBulkDisplayName;
import kr.pyke.deceased_croa.network.pakcet.s2c.S2C_SyncRandomBoxPacket;
import kr.pyke.deceased_croa.network.pakcet.s2c.S2C_SyncRankingPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;

public class ServerPlayConnectionEventHandler {
    private ServerPlayConnectionEventHandler() { }

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer serverPlayer = handler.getPlayer();

            S2C_SyncRankingPacket.send(serverPlayer);
            S2C_SyncRandomBoxPacket.send(serverPlayer);
            S2C_SendBulkDisplayName.send(serverPlayer, DisplayNameData.getServerState(server).getDisplayNames());
        });
    }
}
