package kr.pyke.deceased_croa.network.pakcet.s2c;

import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.network.pakcet.c2s.C2S_ResponseAggroMobListPacket;
import kr.pyke.deceased_croa.registry.mob_effect.aggro.ClientAggro;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class S2C_RequestAggroMobListPacket {
    public static final ResourceLocation ID = new ResourceLocation(DeceasedCroa.MOD_ID, "request_aggro_moblist");

    public static void register() {
        ClientAggro.register();
    }

    public static void send(ServerPlayer player, float radius, int duration) {
        FriendlyByteBuf buf = PacketByteBufs.create();

        buf.writeFloat(radius);
        buf.writeInt(duration);

        ServerPlayNetworking.send(player, ID, buf);
    }
}
