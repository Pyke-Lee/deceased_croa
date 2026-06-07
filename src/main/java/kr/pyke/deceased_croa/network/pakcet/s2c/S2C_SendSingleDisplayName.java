package kr.pyke.deceased_croa.network.pakcet.s2c;

import io.netty.buffer.Unpooled;
import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.client.cache.ClientCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class S2C_SendSingleDisplayName {
    public static final ResourceLocation ID = new ResourceLocation(DeceasedCroa.MOD_ID, "c2s_displayname_single");

    public static void send(ServerPlayer player, UUID uuid, String displayName) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeUUID(uuid);
        buf.writeUtf(displayName);
        ServerPlayNetworking.send(player, ID, buf);
    }

    @Environment(EnvType.CLIENT)
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ID, (client, handler, buf, responseSender) -> {
            UUID uuid = buf.readUUID();
            String displayName = buf.readUtf();
            client.execute(() -> ClientCache.setDisplayName(uuid, displayName));
        });
    }
}
