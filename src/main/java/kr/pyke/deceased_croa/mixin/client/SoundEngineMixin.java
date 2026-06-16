package kr.pyke.deceased_croa.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import kr.pyke.deceased_croa.client.sound.NotificationSoundInstance;
import kr.pyke.deceased_croa.client.sound.NotificationVolume;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
    @ModifyReturnValue(method = "calculateVolume(Lnet/minecraft/client/resources/sounds/SoundInstance;)F", at = @At("RETURN"))
    private float applyNotificationVolume(float original, SoundInstance soundInstance) {
        if (soundInstance instanceof NotificationSoundInstance) {
            return original * NotificationVolume.get();
        }

        return original;
    }
}