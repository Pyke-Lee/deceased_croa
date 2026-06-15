package kr.pyke.deceased_croa.mixin.server;

import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SoundSource.class)
enum SoundSourceMixin {
    DECEASED_CROA_NOTIFICATION("notification");

    @Shadow SoundSourceMixin(String name) { }
}
