package kr.pyke.deceased_croa.mixin.server;

import kr.pyke.deceased_croa.client.cache.ClientCache;
import kr.pyke.deceased_croa.data.DisplayNameData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public class PlayerMixin {
    @Redirect(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getName()Lnet/minecraft/network/chat/Component;"))
    private Component redirectGetName(Player instance) {
        String originalName = instance.getGameProfile().getName();
        String displayName = "";

        if (instance.level().isClientSide()) { displayName = ClientCache.getDisplayNames().get(instance.getUUID()); }
        else if (instance.getServer() != null) {
            DisplayNameData data = DisplayNameData.getServerState(instance.getServer());
            displayName = data.getDisplayName(instance.getUUID());
        }

        if (null == displayName || displayName.isEmpty()) { return Component.literal(originalName); }

        return Component.literal(displayName);
    }
}
