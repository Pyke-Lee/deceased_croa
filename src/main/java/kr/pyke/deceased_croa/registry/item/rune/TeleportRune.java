package kr.pyke.deceased_croa.registry.item.rune;

import kr.pyke.deceased_croa.client.cache.ClientCache;
import kr.pyke.deceased_croa.data.TeleportData;
import kr.pyke.deceased_croa.registry.item.chargeable.Chargeable;
import kr.pyke.deceased_croa.util.ClientHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TeleportRune extends Chargeable {
    public TeleportRune(Properties properties) {
        super(properties);
    }

    @Override public int getUseDuration(ItemStack stack) { return 60; }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        if (level.isClientSide()) {
            ClientHelper.openTeleportScreen();
        }

        return itemStack;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(itemStack, level, tooltip, flag);

        tooltip.add(Component.literal("사용 시 다른 플레이어에게 이동이 가능 합니다.").withStyle(ChatFormatting.GRAY));
    }
}
