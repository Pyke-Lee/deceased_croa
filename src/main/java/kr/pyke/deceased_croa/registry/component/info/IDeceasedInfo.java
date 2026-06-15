package kr.pyke.deceased_croa.registry.component.info;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;

public interface IDeceasedInfo extends ComponentV3, AutoSyncedComponent {
    void addMonsterKillCount(int value);
    void subMonsterKillCount(int value);
    void setMonsterKillCount(int value);

    int getMonsterKillCount();

    void setHighMonsterKillCount(int value);

    int getHighMonsterKillCount();

    void resetMonsterKillCount();
}
