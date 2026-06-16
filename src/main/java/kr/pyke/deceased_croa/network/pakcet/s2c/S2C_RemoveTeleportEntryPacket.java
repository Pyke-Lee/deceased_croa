package kr.pyke.deceased_croa.network.pakcet.s2c;

import io.netty.buffer.Unpooled;
import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.client.cache.ClientCache;
import kr.pyke.deceased_croa.type.CREATIVE_MODE_TABS;
import kr.pyke.deceased_croa.util.ClientHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class S2C_RemoveTeleportEntryPacket {
    public static final ResourceLocation ID = new ResourceLocation(DeceasedCroa.MOD_ID, "s2c_teleport_remove");

    public static void send(ServerPlayer player, String id) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeUtf(id);
        ServerPlayNetworking.send(player, ID, buf);
    }

    @Environment(EnvType.CLIENT)
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ID, (client, handler, buf, responseSender) -> {
            String id = buf.readUtf();
            client.execute(() -> {
                ClientCache.removeTeleportEntry(id);
                ClientHelper.rebuildCreativeModeTab(client, CREATIVE_MODE_TABS.TELEPORT_RUNE);
            });
        });
    }
}