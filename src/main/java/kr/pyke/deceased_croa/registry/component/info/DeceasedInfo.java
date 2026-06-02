package kr.pyke.deceased_croa.registry.component.info;

import kr.pyke.deceased_croa.registry.component.ModComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class DeceasedInfo implements IDeceasedInfo {
    private final Player player;
    private int monsterKillCount;

    public DeceasedInfo(Player player) {
        this.player = player;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        this.monsterKillCount = tag.getInt("monsterKillCount");
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putInt("monsterKillCount", monsterKillCount);
    }

    public void addMonsterKillCount(int value) {
        if (value <= 0) { return; }

        this.monsterKillCount += value;
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
        ModComponents.DECEASED_INFO.sync(player);
    }

    public int getMonsterKillCount() { return this.monsterKillCount; }
}
