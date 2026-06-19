package kr.pyke.deceased_croa.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import kr.pyke.PykeLib;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class HelpCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal("도움").executes(HelpCommand::helpRequest));
    }

    private static int helpRequest(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        MinecraftServer server = context.getSource().getServer();
        ServerPlayer player = context.getSource().getPlayerOrException();

        PykeLib.sendBroadcastMessage(server.getPlayerList().getPlayers(), 16725868, String.format("§b%s§r님이 도움을 요청합니다!", player.getDisplayName().getString()));

        return 1;
    }
}
