package kr.pyke.deceased_croa.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import kr.pyke.PykeLib;
import kr.pyke.deceased_croa.registry.component.ModComponents;
import kr.pyke.util.constants.COLOR;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class NoticeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal("공지")
            .then(Commands.argument("message", StringArgumentType.greedyString()).executes(NoticeCommand::notice))
        );

        dispatcher.register(Commands.literal("강제복귀")
            .then(Commands.argument("targets", EntityArgument.players()).executes(NoticeCommand::returnTargets))
        );
    }

    private static int notice(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        MinecraftServer server = context.getSource().getServer();
        String message = StringArgumentType.getString(context, "message");

        PykeLib.sendBroadcastMessage(server.getPlayerList().getPlayers(), COLOR.AQUA.getColor(), message);

        return 1;
    }

    private static int returnTargets(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
        for (ServerPlayer target : targets) {
            ModComponents.DECEASED_INFO.get(target).teleportToReturnTeleportEntry();
            PykeLib.sendSystemMessage(target, COLOR.LIME.getColor(), "관리자에 의해 강제로 귀환되었습니다.");
        }

        return 1;
    }
}
