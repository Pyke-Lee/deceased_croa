package kr.pyke.deceased_croa.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import kr.pyke.PykeLib;
import kr.pyke.deceased_croa.data.TeleportData;
import kr.pyke.deceased_croa.network.pakcet.s2c.S2C_RemoveTeleportEntryPacket;
import kr.pyke.deceased_croa.network.pakcet.s2c.S2C_SyncSingleTeleportEntriesPacket;
import kr.pyke.util.constants.COLOR;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class TeleportCommand {
    private TeleportCommand() { }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal("워프")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("설정")
                .then(Commands.argument("id", StringArgumentType.string())
                    .then(Commands.argument("displayName", StringArgumentType.greedyString())
                        .executes(TeleportCommand::setTeleportEntry)
                    )
                )
            )
            .then(Commands.literal("제거")
                .then(Commands.argument("id", StringArgumentType.string())
                    .executes(TeleportCommand::removeTeleportEntry)
                )
            )
        );
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
