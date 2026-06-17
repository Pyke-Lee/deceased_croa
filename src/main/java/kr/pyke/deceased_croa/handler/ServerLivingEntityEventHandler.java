package kr.pyke.deceased_croa.handler;

import kr.pyke.deceased_croa.data.MailboxData;
import kr.pyke.deceased_croa.registry.component.ModComponents;
import kr.pyke.deceased_croa.registry.component.info.IDeceasedInfo;
import kr.pyke.deceased_croa.registry.item.ModItems;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ServerLivingEntityEventHandler {
    private ServerLivingEntityEventHandler() { }

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof Monster) {
                if (damageSource.getEntity() instanceof Player player) {
                    IDeceasedInfo info = ModComponents.DECEASED_INFO.get(player);
                    info.addMonsterKillCount(1);

                    if (info.getMonsterKillCount() % 50 == 0) {
                        ModComponents.MAILBOX.get(player).addMail(MailboxData.create(new ItemStack(ModItems.CROA_COIN)), true);
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
                ModComponents.DECEASED_INFO.get(player).setReturnTeleportEntry();
            }

            return true;
        }));

        ServerPlayerEvents.AFTER_RESPAWN.register(((oldPlayer, newPlayer, alive) -> {
            ItemStack itemStack = new ItemStack(ModItems.TELEPORT_RUNE);
            itemStack.getOrCreateTag().putString("teleport_id", "shop");

            newPlayer.addItem(itemStack);
        }));
    }
}
