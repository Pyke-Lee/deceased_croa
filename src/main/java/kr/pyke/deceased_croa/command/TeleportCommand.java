package kr.pyke.deceased_croa.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import kr.pyke.PykeLib;
import kr.pyke.deceased_croa.data.TeleportData;
import kr.pyke.deceased_croa.network.pakcet.s2c.S2C_RemoveTeleportEntryPacket;
import kr.pyke.deceased_croa.network.pakcet.s2c.S2C_SyncSingleTeleportEntriesPacket;
import kr.pyke.deceased_croa.registry.item.ModItems;
import kr.pyke.util.constants.COLOR;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class TeleportCommand {
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_TELEPORT_ID = (context, builder) -> SharedSuggestionProvider.suggest(TeleportData.getServerState(context.getSource().getServer()).getEntries().keySet(), builder);

    private TeleportCommand() { }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal("워프")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("설정")
                .then(Commands.argument("id", StringArgumentType.string()).suggests(SUGGEST_TELEPORT_ID)
                    .then(Commands.argument("displayName", StringArgumentType.greedyString())
                        .executes(TeleportCommand::setTeleportEntry)
                    )
                )
            )
            .then(Commands.literal("제거")
                .then(Commands.argument("id", StringArgumentType.string()).suggests(SUGGEST_TELEPORT_ID)
                    .executes(TeleportCommand::removeTeleportEntry)
                )
            )
        );

        dispatcher.register(Commands.literal("상점").executes(TeleportCommand::giveShopTeleportRune));
    }

    private static int giveShopTeleportRune(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        ItemStack itemStack = new ItemStack(ModItems.TELEPORT_RUNE);
        itemStack.getOrCreateTag().putString("teleport_id", "shop");

        if (player.addItem(itemStack)) {
            player.drop(itemStack, true);
        }
        PykeLib.sendSystemMessage(player, COLOR.LIME.getColor(), "상점 이동의 룬을 지급 받으셨습니다.");

        return 1;
    }

    private static int setTeleportEntry(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String id = StringArgumentType.getString(context, "id");
        String displayName = StringArgumentType.getString(context, "displayName");

        TeleportData.TeleportEntry teleportEntry = new TeleportData.TeleportEntry(displayName, player.level().dimension(), player.position(), player.getYRot());
        TeleportData teleportData = TeleportData.getServerState(context.getSource().getServer());

        teleportData.put(id, teleportEntry);
        PykeLib.sendSystemMessage(player, COLOR.LIME.getColor(), String.format("순간이동 지점 %s(이)가 추가 되었습니다. (id: %s)", displayName, id));
        S2C_SyncSingleTeleportEntriesPacket.send(player, id, teleportEntry);
        return 1;
    }

    private static int removeTeleportEntry(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String id = StringArgumentType.getString(context, "id");

        TeleportData teleportData = TeleportData.getServerState(context.getSource().getServer());

        TeleportData.TeleportEntry teleportEntry = teleportData.get(id);
        if (teleportEntry == null) {
            PykeLib.sendSystemMessage(player, COLOR.RED.getColor(), String.format("순간이동 지점이 존재하지 않습니다. (id: %s)", id));
            return 0;
        }

        teleportData.remove(id);
        S2C_RemoveTeleportEntryPacket.send(player, id);
        PykeLib.sendSystemMessage(player, COLOR.RED.getColor(), String.format("순간이동 지점 %s(이)가 삭제 되었습니다. (id: %s)", teleportEntry.displayName(), id));
        return 1;
    }
}
