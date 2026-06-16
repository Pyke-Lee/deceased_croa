package kr.pyke.deceased_croa.network.pakcet.s2c;

import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.util.ClientHelper;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class S2C_PlayItemActivationPacket {
    public static final ResourceLocation ID = new ResourceLocation(DeceasedCroa.MOD_ID, "play_item_activation");

    public static void register() {
        ClientHelper.registerItemActivationPacket();
    }

    public static void send(ServerPlayer player, ItemStack stack) {
        FriendlyByteBuf buf = PacketByteBufs.create();

        buf.writeItem(stack);

        ServerPlayNetworking.send(player, ID, buf);
    }
}