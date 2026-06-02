package kr.pyke.deceased_croa.network.pakcet.s2c;

import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.client.cache.ClientCache;
import kr.pyke.deceased_croa.command.RankingCommand;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.LinkedHashMap;

public class S2C_SyncRankingPacket {
    public static final ResourceLocation ID = new ResourceLocation(DeceasedCroa.MOD_ID, "sync_ranking");

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ID, (client, listener, buf, sender) -> {
            var ranking = buf.readMap(LinkedHashMap::new, FriendlyByteBuf::readUUID, FriendlyByteBuf::readVarInt);
            client.execute(() -> ClientCache.setRanking(ranking));
        });
    }

    public static void send(ServerPlayer player) {
        FriendlyByteBuf buf = PacketByteBufs.create();

        buf.writeMap(RankingCommand.ranking, FriendlyByteBuf::writeUUID, FriendlyByteBuf::writeVarInt);

        ServerPlayNetworking.send(player, ID, buf);
    }
}
