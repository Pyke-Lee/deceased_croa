package kr.pyke.deceased_croa.registry.mob_effect.aggro;

import kr.pyke.deceased_croa.network.pakcet.c2s.C2S_ResponseAggroMobListPacket;
import kr.pyke.deceased_croa.network.pakcet.s2c.S2C_RequestAggroMobListPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class ClientAggro {
    private ClientAggro() { }

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(S2C_RequestAggroMobListPacket.ID, (client, listener, buf, sender) -> {
            float radius = buf.readFloat();
            int duration = buf.readInt();

            client.execute(() -> {
                LocalPlayer player = client.player;
                if (player == null || client.level == null) { return; }

                double radiusSqr = radius * radius;
                AABB aabb = player.getBoundingBox().inflate(radius);

                List<Integer> ids = new ArrayList<>();
                for (Monster mob : client.level.getEntitiesOfClass(Monster.class, aabb, m -> m.distanceToSqr(player) <= radiusSqr && (!m.hasEffect(MobEffects.GLOWING) || m.getTarget() != player))) {
                    ids.add(mob.getId());
                }

                C2S_ResponseAggroMobListPacket.send(ids, radiusSqr, duration);
            });
        });
    }
}
