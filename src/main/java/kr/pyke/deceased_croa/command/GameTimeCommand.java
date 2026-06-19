package kr.pyke.deceased_croa.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import kr.pyke.PykeLib;
import kr.pyke.deceased_croa.data.GameTimeController;
import kr.pyke.util.constants.COLOR;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ServerLevelData;

public class GameTimeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal("gametime")
            .requires(source -> source.hasPermission(2))

            .then(Commands.literal("scale")
                .then(Commands.argument("value", FloatArgumentType.floatArg(0.f)).executes(GameTimeCommand::setScale))
                .executes(GameTimeCommand::getScale))

            .then(Commands.literal("freeze")
                .then(Commands.argument("frozen", BoolArgumentType.bool()).executes(GameTimeCommand::setFreeze))
                .executes(GameTimeCommand::toggleFreeze))

            .then(Commands.literal("reset").executes(GameTimeCommand::reset))
        );
    }

    private static int setScale(CommandContext<CommandSourceStack> ctx) {
        float value = FloatArgumentType.getFloat(ctx, "value");
        GameTimeController.setScale(value);
        feedback(ctx, "게임타임 배율: " + value + "x");
        return 1;
    }

    private static int getScale(CommandContext<CommandSourceStack> ctx) {
        feedback(ctx, "현재 게임타임 배율: " + GameTimeController.getScale() + "x");
        return 1;
    }

    private static int setFreeze(CommandContext<CommandSourceStack> ctx) {
        boolean frozen = BoolArgumentType.getBool(ctx, "frozen");
        GameTimeController.setFrozen(frozen);
        feedback(ctx, "게임타임 정지: " + (frozen ? "ON" : "OFF"));
        return 1;
    }

    private static int toggleFreeze(CommandContext<CommandSourceStack> ctx) {
        boolean now = !GameTimeController.isFrozen();
        GameTimeController.setFrozen(now);
        feedback(ctx, "게임타임 정지: " + (now ? "ON" : "OFF"));
        return 1;
    }

    private static int reset(CommandContext<CommandSourceStack> ctx) {
        MinecraftServer server = ctx.getSource().getServer();
        GameTimeController.reset();
        for (ServerLevel level : server.getAllLevels()) {
            ((ServerLevelData) level.getLevelData()).setGameTime(0L);
        }
        feedback(ctx, "게임타임을 0으로 초기화했습니다.");
        return 1;
    }

    private static void feedback(CommandContext<CommandSourceStack> ctx, String message) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player != null) {
            PykeLib.sendSystemMessage(player, COLOR.LIME.getColor(), message);
        }
    }
}