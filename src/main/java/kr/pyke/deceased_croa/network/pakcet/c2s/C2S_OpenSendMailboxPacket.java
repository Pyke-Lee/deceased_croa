package kr.pyke.deceased_croa.network.pakcet.c2s;

import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.util.MailboxOpener;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class C2S_OpenSendMailboxPacket {
    public static final ResourceLocation ID = new ResourceLocation(DeceasedCroa.MOD_ID, "open_send_mailbox");

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ID, (server, player, handler, buf, sender) -> server.execute(() -> {
            if (!player.hasPermissions(2)) { return; }

            MailboxOpener.openSend(player);
        }));
    }

    public static void send() {
        FriendlyByteBuf buf = PacketByteBufs.create();

        ClientPlayNetworking.send(ID, buf);
    }
}