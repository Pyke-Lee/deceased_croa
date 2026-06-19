package kr.pyke.deceased_croa.mixin.server;

import kr.pyke.deceased_croa.data.GameTimeController;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Shadow @Final private MinecraftServer server;

    @Redirect(
        method = "tickTime",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/storage/ServerLevelData;setGameTime(J)V"
        )
    )
    private void deceased_croa$scaleGameTime(ServerLevelData data, long vanillaNewTime) {
        long current = vanillaNewTime - 1L;
        long delta = GameTimeController.getServerState(this.server).consumeDelta();
        data.setGameTime(current + delta);
    }
}