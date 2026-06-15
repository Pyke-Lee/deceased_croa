package kr.pyke.deceased_croa.registry.component.info;

import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.registry.component.ModComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class DeceasedInfo implements IDeceasedInfo {
    private final Player player;
    private int monsterKillCount;
    private int highMonsterKillCount;

    public DeceasedInfo(Player player) {
        this.player = player;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        this.monsterKillCount = tag.getInt("monsterKillCount");
        this.highMonsterKillCount = tag.getInt("highMonsterKillCount");
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putInt("monsterKillCount", monsterKillCount);
        tag.putInt("highMonsterKillCount", highMonsterKillCount);
    }

    public void addMonsterKillCount(int value) {
        if (value <= 0) { return; }

        this.monsterKillCount += value;

        if (this.monsterKillCount > this.highMonsterKillCount) {
            this.highMonsterKillCount = this.monsterKillCount;
            DeceasedCroa.LOGGER.info(String.valueOf(this.highMonsterKillCount));
        }

        ModComponents.DECEASED_INFO.sync(player);
    }

    public void subMonsterKillCount(int value) {
        if (value <= 0) { return; }

        this.monsterKillCount -= value;
        ModComponents.DECEASED_INFO.sync(player);
    }

    public void setMonsterKillCount(int value) {
        if (value < 0) { return; }

        this.monsterKillCount = value;

        if (this.monsterKillCount > this.highMonsterKillCount) {
            this.highMonsterKillCount = this.monsterKillCount;
        }

        ModComponents.DECEASED_INFO.sync(player);
    }

    public int getMonsterKillCount() { return this.monsterKillCount; }

    public void setHighMonsterKillCount(int value) {
        if (value < 0) { return; }

        this.highMonsterKillCount = value;
        ModComponents.DECEASED_INFO.sync(player);
    }

    public int getHighMonsterKillCount() { return this.highMonsterKillCount; }

    public void resetMonsterKillCount() {
        this.monsterKillCount = 0;
        ModComponents.DECEASED_INFO.sync(player);
    }
}
