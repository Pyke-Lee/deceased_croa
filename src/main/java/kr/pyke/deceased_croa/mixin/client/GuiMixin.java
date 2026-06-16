package kr.pyke.deceased_croa.mixin.client;

import kr.pyke.deceased_croa.registry.item.chargeable.Chargeable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void renderCrosshair(GuiGraphics guiGraphics, CallbackInfo ci) {
        LocalPlayer player = minecraft.player;
        if (player == null) { return; }

        ItemStack useStack = player.getUseItem();
        if (!(useStack.getItem() instanceof Chargeable)) { return; }

        float remainingTicks = (float) player.getUseItemRemainingTicks();
        if (remainingTicks > 0.f) { ci.cancel(); }
    }
}
