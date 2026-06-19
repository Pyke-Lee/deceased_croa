package kr.pyke.deceased_croa.registry.item.rune;

import kr.pyke.PykeLib;
import kr.pyke.deceased_croa.client.cache.ClientCache;
import kr.pyke.deceased_croa.data.MailboxData;
import kr.pyke.deceased_croa.data.TeleportData;
import kr.pyke.deceased_croa.registry.component.ModComponents;
import kr.pyke.deceased_croa.registry.item.ModItems;
import kr.pyke.deceased_croa.registry.item.chargeable.Chargeable;
import kr.pyke.util.constants.COLOR;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
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

public class WarpRune extends Chargeable {
    public static final String TELEPORT_ID_KEY = "teleport_id";

    public WarpRune(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        CompoundTag tag = itemStack.getTag();
        if (tag == null || !tag.contains(TELEPORT_ID_KEY)) { return InteractionResultHolder.fail(itemStack); }

        String id = tag.getString(TELEPORT_ID_KEY);
        if (id.isEmpty()) { return InteractionResultHolder.fail(itemStack); }

        if (level.isClientSide()) {
            if (ClientCache.getTeleportEntries().get(id) == null) { return InteractionResultHolder.fail(itemStack); }
        }
        else if (player instanceof ServerPlayer serverPlayer) {
            if (TeleportData.getServerState(serverPlayer.server).get(id) == null) { return InteractionResultHolder.fail(itemStack); }
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        if (!level.isClientSide() && livingEntity instanceof ServerPlayer serverPlayer) {
            CompoundTag tag = itemStack.getTag();
            if (tag == null || !tag.contains("teleport_id")) { return itemStack; }

            String id = tag.getString("teleport_id");
            if (id.isEmpty()) { return itemStack; }

            TeleportData data = TeleportData.getServerState(serverPlayer.server);

            TeleportData.TeleportEntry entry = data.get(id);
            if (entry == null) { return itemStack; }

            ModComponents.DECEASED_INFO.get(serverPlayer).setReturnTeleportEntry();
            if (entry.teleportTo(serverPlayer)) {
                if (!serverPlayer.getAbilities().instabuild) {
                    itemStack.shrink(1);

                    ModComponents.MAILBOX.get(serverPlayer).addMail(MailboxData.create(new ItemStack(ModItems.RED_RUNE)), false);
                    PykeLib.sendSystemMessage(serverPlayer, COLOR.LIME.getColor(), "귀환의 룬이 메일함으로 지급되었습니다.");
                }
            }

            serverPlayer.getCooldowns().addCooldown(itemStack.getItem(), 20);
        }

        return itemStack;
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        String teleportID = getTeleportID(stack);
        if (teleportID != null) {
            TeleportData.TeleportEntry entry = ClientCache.getTeleportEntries().get(teleportID);
            if (entry != null) { return Component.literal(entry.displayName() + " 이동의 룬"); }
        }

        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(itemStack, level, tooltip, flag);

        CompoundTag tag = itemStack.getTag();
        if (tag == null || !tag.contains("teleport_id")) { return; }

        String id = tag.getString("teleport_id");
        if (id.isEmpty()) { return; }

        TeleportData.TeleportEntry entry = ClientCache.getTeleportEntries().get(id);
        if (entry == null) {
            tooltip.add(Component.literal("알 수 없음").withStyle(ChatFormatting.RED));
            return;
        }

        tooltip.add(Component.literal(String.format("사용 시 %s(으)로 이동합니다.", entry.displayName())).withStyle(ChatFormatting.GRAY));
    }

    public static String getTeleportID(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TELEPORT_ID_KEY)) { return null; }

        return tag.getString(TELEPORT_ID_KEY);
    }
}
