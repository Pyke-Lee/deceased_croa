package kr.pyke.deceased_croa.network.pakcet.c2s;

import kr.pyke.deceased_croa.DeceasedCroa;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

import java.util.List;

public class C2S_ResponseAggroMobListPacket {
    public static final ResourceLocation ID = new ResourceLocation(DeceasedCroa.MOD_ID, "response_aggro_moblist");

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ID, (server, player, handler, buf, sender) -> {
            double radiusSqr = buf.readDouble();
            int duration = buf.readVarInt();
            int count = buf.readVarInt();
            int[] entityIDs = new int[count];
            for (int i = 0; i < count; i++) { entityIDs[i] = buf.readVarInt(); }

            server.execute(() -> {
                ServerLevel level = player.serverLevel();
                for (int id : entityIDs) {
                    Entity entity = level.getEntity(id);
                    if (entity instanceof Mob mob) {
                        if (mob.distanceToSqr(player) > radiusSqr) { continue; }

                        mob.setTarget(player);
                        mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, duration));
                    }
                }
            });
        });
    }

    public static void send(List<Integer> entityIDs, double radiusSqr, int duration) {
        FriendlyByteBuf buf = PacketByteBufs.create();

        buf.writeDouble(radiusSqr);
        buf.writeVarInt(duration);
        buf.writeVarInt(entityIDs.size());
        for (int id : entityIDs) { buf.writeVarInt(id); }

        ClientPlayNetworking.send(ID, buf);
    }
}