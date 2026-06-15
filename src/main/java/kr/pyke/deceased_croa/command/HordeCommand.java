package kr.pyke.deceased_croa.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import kr.pyke.PykeLib;
import kr.pyke.deceased_croa.config.ConfigLoader;
import kr.pyke.util.constants.COLOR;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class HordeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal("크로아")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("리로드").executes(HordeCommand::reloadConfig))
        );
    }

    private static int reloadConfig(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        ConfigLoader.load();
        PykeLib.sendSystemMessage(player, COLOR.LIME.getColor(), "&6[SYSTEM]&r CONFIG 로드가 완료되었습니다.");

        return 1;
    }
}
