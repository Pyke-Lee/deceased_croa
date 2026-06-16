package kr.pyke.deceased_croa.mixin.client;

import kr.pyke.deceased_croa.client.sound.NotificationSoundInstance;
import kr.pyke.deceased_croa.client.sound.NotificationSoundRefresher;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin implements NotificationSoundRefresher {
    @Shadow private boolean loaded;

    @Shadow @Final private Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;

    @Shadow protected abstract float calculateVolume(SoundInstance soundInstance);

    @Override
    public void deceased_croa$refreshNotificationVolume() {
        if (!this.loaded) { return; }

        this.instanceToChannel.forEach((soundInstance, channelHandle) -> {
            if (soundInstance instanceof NotificationSoundInstance) {
                float volume = this.calculateVolume(soundInstance);
                channelHandle.execute(channel -> {
                    if (volume <= 0.f) { channel.stop(); }
                    else { channel.setVolume(volume); }
                });
            }
        });
    }
}