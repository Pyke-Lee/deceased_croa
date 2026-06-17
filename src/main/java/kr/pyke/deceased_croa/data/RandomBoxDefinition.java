package kr.pyke.deceased_croa.data;

import kr.pyke.deceased_croa.type.MESSAGE_TYPE;
import net.minecraft.util.RandomSource;

import java.util.List;

public record RandomBoxDefinition(String boxID, int customModelData, String displayName, boolean mailbox, boolean pack, String openSound, float soundVolume, MESSAGE_TYPE openMessageType, String openMessage, List<RandomBoxReward> rewards) {
    public int totalWeight() {
        int total = 0;
        for (RandomBoxReward reward : this.rewards) {
            total += Math.max(0, reward.weight());
        }

        return total;
    }

    public RandomBoxReward roll(RandomSource random) {
        if (this.rewards.isEmpty()) { return null; }

        int totalWeight = this.totalWeight();
        if (totalWeight <= 0) { return this.rewards.get(0); }

        int roll = random.nextInt(totalWeight);
        for (RandomBoxReward reward : this.rewards) {
            roll -= Math.max(0, reward.weight());
            if (roll < 0) { return reward; }
        }

        return this.rewards.get(this.rewards.size() - 1);
    }
}