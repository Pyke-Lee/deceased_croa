package kr.pyke.deceased_croa.handler;

import kr.pyke.PykeLib;
import kr.pyke.deceased_croa.data.MailboxData;
import kr.pyke.deceased_croa.registry.component.ModComponents;
import kr.pyke.deceased_croa.registry.component.info.IDeceasedInfo;
import kr.pyke.deceased_croa.registry.item.ModItems;
import kr.pyke.util.constants.COLOR;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ServerLivingEntityEventHandler {
    private ServerLivingEntityEventHandler() { }

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof Monster) {
                if (damageSource.getEntity() instanceof ServerPlayer player) {
                    IDeceasedInfo info = ModComponents.DECEASED_INFO.get(player);
                    info.addMonsterKillCount(1);

                    if (info.getMonsterKillCount() % 50 == 0) {
                        ModComponents.MAILBOX.get(player).addMail(MailboxData.create(new ItemStack(ModItems.CROA_COIN)), false);
                        PykeLib.sendSystemMessage(player, COLOR.LIME.getColor(), "몬스터를 §e50§r마리 처치하여 §d[ 크로아 코인 ]§r(을)를 획득합니다.");
                    }
                }
            }

            if (entity instanceof Player player) {
                IDeceasedInfo info = ModComponents.DECEASED_INFO.get(player);
                info.resetMonsterKillCount();
            }
        });

        ServerLivingEntityEvents.ALLOW_DEATH.register(((entity, damageSource, damageAmount) -> {
            if (entity instanceof Player player) {
                IDeceasedInfo info = ModComponents.DECEASED_INFO.get(player);
                info.setReturnTeleportEntry();
                info.saveItems();
            }

            return true;
        }));

        ServerPlayerEvents.AFTER_RESPAWN.register(((oldPlayer, newPlayer, alive) -> {
            ModComponents.DECEASED_INFO.get(newPlayer).loadItems();

            newPlayer.addEffect(new MobEffectInstance(MobEffects.GLOWING, MobEffectInstance.INFINITE_DURATION, 0, false, false, false));
        }));
    }
}
