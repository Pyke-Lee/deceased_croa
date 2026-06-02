package kr.pyke.deceased_croa.data;

import kr.pyke.deceased_croa.type.MESSAGE_TYPE;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record RandomBoxReward(ResourceLocation item, int count, int weight, CompoundTag nbt, String openSound, MESSAGE_TYPE openMessageType, String openMessage) {
    public ItemStack createStack() {
        Item resolved = BuiltInRegistries.ITEM.get(this.item);
        ItemStack stack = new ItemStack(resolved, this.count);

        if (this.nbt != null) { stack.setTag(this.nbt.copy()); }

        return stack;
    }

    public String resolveSound(RandomBoxDefinition box) {
        if (this.openSound != null) { return this.openSound; }

        return box.openSound();
    }

    public MESSAGE_TYPE resolveMessageType(RandomBoxDefinition box) {
        if (this.openMessageType != null) { return this.openMessageType; }

        return box.openMessageType();
    }

    public String resolveMessage(RandomBoxDefinition box) {
        if (this.openMessage != null) { return this.openMessage; }

        return box.openMessage();
    }
}