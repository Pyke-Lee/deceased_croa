package kr.pyke.deceased_croa.registry.item.rune;

import kr.pyke.deceased_croa.registry.component.ModComponents;
import kr.pyke.deceased_croa.registry.component.info.IDeceasedInfo;
import kr.pyke.deceased_croa.registry.item.chargeable.Chargeable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReturnRune extends Chargeable {
    public ReturnRune(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (ModComponents.DECEASED_INFO.get(player).isReturnable()) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemStack);
        }

        return InteractionResultHolder.fail(itemStack);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        if (!level.isClientSide() && livingEntity instanceof ServerPlayer serverPlayer) {
            IDeceasedInfo info = ModComponents.DECEASED_INFO.get(serverPlayer);
            if (info.teleportToReturnTeleportEntry()) {
                if (!serverPlayer.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
            }
        }

        return itemStack;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(itemStack, level, tooltip, flag);

        tooltip.add(Component.literal("사용 시 이전 위치로 이동합니다.").withStyle(ChatFormatting.GRAY));
    }
}
