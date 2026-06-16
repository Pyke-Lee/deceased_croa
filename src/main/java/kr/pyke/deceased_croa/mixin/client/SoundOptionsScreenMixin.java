package kr.pyke.deceased_croa.mixin.client;

import kr.pyke.deceased_croa.client.sound.NotificationVolumeOption;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundOptionsScreen.class)
public class SoundOptionsScreenMixin {
    @Shadow private OptionsList list;

    @Inject(method = "init", at = @At("TAIL"))
    private void addNotificationOption(CallbackInfo ci) {
        this.list.addSmall(new OptionInstance[]{ NotificationVolumeOption.create() });
    }
}