package kr.pyke.deceased_croa.client.sound;

import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

public class NotificationSoundInstance extends SimpleSoundInstance {
    private final float baseVolume;

    public NotificationSoundInstance(ResourceLocation soundLocation, float volume, float pitch) {
        super(soundLocation, SoundSource.MASTER, volume, pitch, SoundInstance.createUnseededRandom(), false, 0, SimpleSoundInstance.Attenuation.NONE, 0.f, 0.f, 0.f, true);
        this.baseVolume = volume;
    }

    @Override
    public float getVolume() {
        return this.baseVolume * NotificationVolume.get();
    }
}