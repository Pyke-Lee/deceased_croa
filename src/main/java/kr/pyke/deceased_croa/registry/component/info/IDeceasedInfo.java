package kr.pyke.deceased_croa.registry.component.info;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.server.level.ServerPlayer;

public interface IDeceasedInfo extends ComponentV3, AutoSyncedComponent {
    void addMonsterKillCount(int value);
    void subMonsterKillCount(int value);

    void setMonsterKillCount(int value);
    void setHighMonsterKillCount(int value);

    int getMonsterKillCount();
    int getHighMonsterKillCount();

    void resetMonsterKillCount();

    void setReturnTeleportEntry();

    boolean teleportToReturnTeleportEntry();
    boolean isReturnable();

    void saveItems();
    void loadItems();
}
