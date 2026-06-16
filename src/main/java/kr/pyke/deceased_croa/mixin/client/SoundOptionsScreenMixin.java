package kr.pyke.deceased_croa.mixin.client;

import kr.pyke.deceased_croa.client.sound.NotificationVolumeOption;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Arrays;

@Mixin(SoundOptionsScreen.class)
public class SoundOptionsScreenMixin {
    @ModifyArg(
        method = "init",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/OptionsList;addSmall([Lnet/minecraft/client/OptionInstance;)V",
            ordinal = 0
        ),
        index = 0
    )
    private OptionInstance<?>[] addNotificationOption(OptionInstance<?>[] options) {
        OptionInstance<?>[] modified = Arrays.copyOf(options, options.length + 1);
        modified[options.length] = NotificationVolumeOption.create();
        return modified;
    }
}