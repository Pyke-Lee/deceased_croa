package kr.pyke.deceased_croa.network.pakcet.c2s;

import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.registry.item.rune.TeleportRune;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.Set;
import java.util.UUID;

public class C2S_TeleportToPlayerPacket {
    public static final ResourceLocation ID = new ResourceLocation(DeceasedCroa.MOD_ID, "teleport_to_player");

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ID, (server, player, handler, buf, sender) -> {
            UUID targetUUID = buf.readUUID();

            server.execute(() -> {
                if (targetUUID.equals(player.getUUID())) { return; }

                ItemStack runeStack = findRune(player);
                if (runeStack == null) { return; }

                ServerPlayer target = server.getPlayerList().getPlayer(targetUUID);
                if (target == null) { return; }

                ServerLevel level = target.serverLevel();
                player.teleportTo(level, target.getX(), target.getY(), target.getZ(), Set.of(), target.getYRot(), target.getXRot());
                if (!player.getAbilities().instabuild) {
                    runeStack.shrink(1);
                }
            });
        });
    }

    private static ItemStack findRune(ServerPlayer player) {
        ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (mainHand.getItem() instanceof TeleportRune) { return mainHand; }

        ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);
        if (offHand.getItem() instanceof TeleportRune) { return offHand; }

        return null;
    }

    public static void send(UUID targetUUID) {
        FriendlyByteBuf buf = PacketByteBufs.create();

        buf.writeUUID(targetUUID);

        ClientPlayNetworking.send(ID, buf);
    }
}