package kr.pyke.deceased_croa.network.pakcet.c2s;

import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.client.gui.menu.MailboxMenu;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class C2S_SelectMailPacket {
    public static final ResourceLocation ID = new ResourceLocation(DeceasedCroa.MOD_ID, "select_mail");

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ID, (server, player, handler, buf, sender) -> {
            UUID mailUUID = buf.readUUID();

            server.execute(() -> {
                if (player.containerMenu instanceof MailboxMenu mailboxMenu) {
                    mailboxMenu.selectMail(mailUUID);
                }
            });
        });
    }

    public static void send(UUID mailUUID) {
        FriendlyByteBuf buf = PacketByteBufs.create();

        buf.writeUUID(mailUUID);

        ClientPlayNetworking.send(ID, buf);
    }
}