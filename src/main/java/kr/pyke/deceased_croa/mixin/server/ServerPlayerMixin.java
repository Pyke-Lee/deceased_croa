package kr.pyke.deceased_croa.mixin.server;

import kr.pyke.deceased_croa.data.DisplayNameData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "getTabListDisplayName", at = @At("HEAD"), cancellable = true)
    private void overrideTabListName(CallbackInfoReturnable<Component> cir) {
        ServerPlayer self = (ServerPlayer) (Object) this;

        String displayName = DisplayNameData.getServerState(self.server).getDisplayName(self.getUUID());

        if (displayName != null && !displayName.isEmpty()) {
            cir.setReturnValue(Component.literal(displayName));
        }
    }
}
