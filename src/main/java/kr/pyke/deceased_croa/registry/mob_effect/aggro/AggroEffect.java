package kr.pyke.deceased_croa.registry.mob_effect.aggro;

import kr.pyke.deceased_croa.network.pakcet.s2c.S2C_RequestAggroMobListPacket;
import kr.pyke.deceased_croa.registry.sound.ModSounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class AggroEffect extends MobEffect {
    int duration = 0;
    int count = 3;

    public AggroEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override public boolean isDurationEffectTick(int duration, int amplifier) {
        this.duration = duration;

        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) { return; }

        if (entity instanceof ServerPlayer player) {
            if (player.tickCount % 20 == 0) {
                ++this.count;

                if (this.count >= 3) {
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.SIREN_0, SoundSource.PLAYERS, 1.f, 1.f);
                    this.count = 0;
                }
            }

            if (player.tickCount % 10 == 0) {
                S2C_RequestAggroMobListPacket.send(player, 48.f, this.duration);
            }
        }
    }
}
