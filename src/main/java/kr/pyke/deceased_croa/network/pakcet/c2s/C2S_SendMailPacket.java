package kr.pyke.deceased_croa.network.pakcet.c2s;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.client.gui.menu.SendMailboxMenu;
import kr.pyke.deceased_croa.data.MailboxData;
import kr.pyke.deceased_croa.registry.component.ModComponents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class C2S_SendMailPacket {
    public static final ResourceLocation ID = new ResourceLocation(DeceasedCroa.MOD_ID, "send_mail");

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ID, (server, player, handler, buf, sender) -> {
            String mailTitle = buf.readUtf();
            String recipient = buf.readUtf();
            String senderName = buf.readUtf();
            String message = buf.readUtf();

            server.execute(() -> {
                if (!(player.containerMenu instanceof SendMailboxMenu sendMailboxMenu)) { return; }

                List<ItemStack> attachments = sendMailboxMenu.collectAttachments();
                List<ServerPlayer> targets = resolveTargets(player, recipient);
                if (targets.isEmpty()) { return; }

                for(ServerPlayer target : targets) {
                    List<ItemStack> copied = new ArrayList<>();
                    for(ItemStack stack : attachments) { copied.add(stack.copy()); }

                    MailboxData mail = MailboxData.create(mailTitle, senderName, message, copied);
                    ModComponents.MAILBOX.get(target).addMail(mail);
                }

                sendMailboxMenu.clearAttachments();
            });
        });
    }

    private static List<ServerPlayer> resolveTargets(ServerPlayer player, String recipient) {
        boolean isAdmin = player.hasPermissions(2);
        if (!isAdmin) {
            ServerPlayer target = player.server.getPlayerList().getPlayerByName(recipient);
            if (target == null) { return List.of(); }

            return List.of(target);
        }

        CommandSourceStack source = player.createCommandSourceStack().withPermission(2);
        try {
            EntitySelectorParser parser = new EntitySelectorParser(new com.mojang.brigadier.StringReader(recipient));
            EntitySelector selector = parser.parse();
            return new ArrayList<>(selector.findPlayers(source));
        }
        catch (CommandSyntaxException exception) {
            ServerPlayer target = player.server.getPlayerList().getPlayerByName(recipient);
            if (target == null) { return List.of(); }

            return List.of(target);
        }
    }

    public static void send(String mailTitle, String recipient, String senderName, String message) {
        FriendlyByteBuf buf = PacketByteBufs.create();

        buf.writeUtf(mailTitle);
        buf.writeUtf(recipient);
        buf.writeUtf(senderName);
        buf.writeUtf(message);

        ClientPlayNetworking.send(ID, buf);
    }
}