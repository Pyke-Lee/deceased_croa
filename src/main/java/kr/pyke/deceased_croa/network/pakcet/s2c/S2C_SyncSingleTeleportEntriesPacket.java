package kr.pyke.deceased_croa.network.pakcet.s2c;

import io.netty.buffer.Unpooled;
import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.client.cache.ClientCache;
import kr.pyke.deceased_croa.data.TeleportData;
import kr.pyke.deceased_croa.type.CREATIVE_MODE_TABS;
import kr.pyke.deceased_croa.util.ClientHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class S2C_SyncSingleTeleportEntriesPacket {
    public static final ResourceLocation ID = new ResourceLocation(DeceasedCroa.MOD_ID, "s2c_teleport_single");

    public static void send(ServerPlayer player, String id, TeleportData.TeleportEntry entry) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeUtf(id);
        buf.writeNbt(entry.toNbt());
        ServerPlayNetworking.send(player, ID, buf);
    }

    @Environment(EnvType.CLIENT)
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ID, (client, handler, buf, responseSender) -> {
            String id = buf.readUtf();
            CompoundTag entryTag = buf.readNbt();
            if (entryTag == null) { return; }

            TeleportData.TeleportEntry entry = TeleportData.TeleportEntry.fromNbt(entryTag);
            client.execute(() -> {
                ClientCache.setTeleportEntry(id, entry);
                ClientHelper.rebuildCreativeModeTab(client, CREATIVE_MODE_TABS.RANDOM_BOX);
            });
        });
    }
}