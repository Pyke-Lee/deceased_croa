package kr.pyke.deceased_croa.network.pakcet.s2c;

import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.util.ClientHelper;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;

public class S2C_PlaySoundPacket {
    public static final ResourceLocation ID = new ResourceLocation(DeceasedCroa.MOD_ID, "play_sound");

    public static void register() {
        ClientHelper.registerSoundPacket();
    }

    public static void send(ServerPlayer player, SoundEvent sound, float volume, float pitch) {
        FriendlyByteBuf buf = PacketByteBufs.create();

        buf.writeResourceLocation(sound.getLocation());
        buf.writeFloat(volume);
        buf.writeFloat(pitch);

        ServerPlayNetworking.send(player, ID, buf);
    }
}