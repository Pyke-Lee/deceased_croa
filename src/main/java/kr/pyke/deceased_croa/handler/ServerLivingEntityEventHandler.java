package kr.pyke.deceased_croa.handler;

import kr.pyke.deceased_croa.registry.component.ModComponents;
import kr.pyke.deceased_croa.registry.component.info.IDeceasedInfo;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

public class ServerLivingEntityEventHandler {
    private ServerLivingEntityEventHandler() { }

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof Monster) {
                if (damageSource.getEntity() instanceof Player player) {
                    IDeceasedInfo info = ModComponents.DECEASED_INFO.get(player);
                    info.addMonsterKillCount(1);
                }
            }

            if (entity instanceof Player player) {
                IDeceasedInfo info = ModComponents.DECEASED_INFO.get(player);
                info.resetMonsterKillCount();
            }
        });
    }
}
