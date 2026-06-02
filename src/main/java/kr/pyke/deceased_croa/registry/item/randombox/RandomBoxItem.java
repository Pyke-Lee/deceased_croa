package kr.pyke.deceased_croa.registry.item.randombox;

import kr.pyke.deceased_croa.data.RandomBoxDefinition;
import kr.pyke.deceased_croa.data.RandomBoxReward;
import kr.pyke.deceased_croa.manager.RandomBoxManager;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RandomBoxItem extends Item {
    public static final String BOX_ID_KEY = "box_id";
    public static final String CUSTOM_MODEL_DATA_KEY = "CustomModelData";
    private static final int TOOLTIP_REWARD_LIMIT = 5;

    public RandomBoxItem(Item.Properties properties) {
        super(properties);
    }

    public static ItemStack createStack(RandomBoxDefinition definition) {
        ItemStack stack = new ItemStack(kr.pyke.deceased_croa.registry.item.ModItems.RANDOM_BOX);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(BOX_ID_KEY, definition.boxID());
        tag.putInt(CUSTOM_MODEL_DATA_KEY, definition.customModelData());

        return stack;
    }

    public static String getBoxID(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(BOX_ID_KEY)) { return null; }

        return tag.getString(BOX_ID_KEY);
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        String boxID = getBoxID(stack);
        if (boxID != null) {
            RandomBoxDefinition definition = RandomBoxManager.get(boxID);
            if (definition != null) { return Component.literal(definition.displayName()); }
        }

        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        String boxID = getBoxID(stack);
        if (boxID == null) { return; }

        RandomBoxDefinition definition = RandomBoxManager.get(boxID);
        if (definition == null) { return; }

        List<RandomBoxReward> rewards = definition.rewards();
        int totalWeight = definition.totalWeight();
        int shown = Math.min(rewards.size(), TOOLTIP_REWARD_LIMIT);

        for (int i = 0; i < shown; ++i) {
            RandomBoxReward reward = rewards.get(i);
            String itemName = reward.createStack().getHoverName().getString();
            double percent = totalWeight > 0 ? (double) Math.max(0, reward.weight()) / (double) totalWeight * 100.0 : 0.0;
            String line = String.format("%s x%d - %.1f%%", itemName, reward.count(), percent);
            tooltip.add(Component.literal(line).withStyle(ChatFormatting.GRAY));
        }

        int remaining = rewards.size() - shown;
        if (remaining > 0) {
            tooltip.add(Component.literal("외 " + remaining + "개").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide()) { return InteractionResultHolder.success(stack); }

        String boxID = getBoxID(stack);
        if (boxID == null) { return InteractionResultHolder.pass(stack); }

        RandomBoxDefinition definition = RandomBoxManager.get(boxID);
        if (definition == null) { return InteractionResultHolder.pass(stack); }

        if (player instanceof ServerPlayer serverPlayer) {
            RandomBoxManager.grant(serverPlayer, definition);
            if (!serverPlayer.getAbilities().instabuild) { stack.shrink(1); }
        }

        return InteractionResultHolder.success(stack);
    }
}