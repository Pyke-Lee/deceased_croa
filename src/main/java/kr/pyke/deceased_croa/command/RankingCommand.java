package kr.pyke.deceased_croa.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import kr.pyke.PykeLib;
import kr.pyke.deceased_croa.network.pakcet.s2c.S2C_SyncRankingPacket;
import kr.pyke.deceased_croa.registry.component.ModComponents;
import kr.pyke.deceased_croa.registry.component.info.IDeceasedInfo;
import kr.pyke.util.constants.COLOR;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

import java.util.*;
import java.util.stream.Collectors;

public class RankingCommand {
    public static Map<UUID, Integer> ranking = new HashMap<>();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal("랭킹")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("갱신").executes(RankingCommand::updateRanking))
        );

        dispatcher.register(Commands.literal("킬설정")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("현재")
                .then(Commands.argument("targets", EntityArgument.players())
                    .then(Commands.argument("value", IntegerArgumentType.integer(0))
                        .executes(RankingCommand::setKillCount)
                    )
                )
            )
            .then(Commands.literal("최대")
                .then(Commands.argument("targets", EntityArgument.players())
                    .then(Commands.argument("value", IntegerArgumentType.integer(0))
                        .executes(RankingCommand::setMaxKillCount)
                    )
                )
            )
            .then(Commands.literal("정보")
                .then(Commands.argument("targets", EntityArgument.players())
                    .executes(RankingCommand::getKillCount)
                )
            )
        );
    }

    private static int setKillCount(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer serverPlayer = context.getSource().getPlayerOrException();
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
        int value = IntegerArgumentType.getInteger(context, "value");

        for (ServerPlayer target : targets) {
            ModComponents.DECEASED_INFO.get(target).setMonsterKillCount(value);
        }
        PykeLib.sendSystemMessage(serverPlayer, COLOR.LIME.getColor(), String.format("§e%s§r명의 킬 카운트를 설정하였습니다.", targets.size()));

        return 1;
    }

    private static int setMaxKillCount(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer serverPlayer = context.getSource().getPlayerOrException();
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
        int value = IntegerArgumentType.getInteger(context, "value");

        for (ServerPlayer target : targets) {
            ModComponents.DECEASED_INFO.get(target).setHighMonsterKillCount(value);
        }
        PykeLib.sendSystemMessage(serverPlayer, COLOR.LIME.getColor(), String.format("§e%s§r명의 최대 킬 카운트를 설정하였습니다.", targets.size()));

        return 1;
    }

    private static int getKillCount(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer serverPlayer = context.getSource().getPlayerOrException();
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");

        PykeLib.sendSystemMessage(serverPlayer, COLOR.LIME.getColor(), "");
        for (ServerPlayer target : targets) {
            IDeceasedInfo info = ModComponents.DECEASED_INFO.get(target);
            PykeLib.sendSystemMessage(serverPlayer, COLOR.LIME.getColor(), String.format("§b%s§r의 정보 | 현재: §e%s§r마리 / 최대: §e%s§r마리", target.getDisplayName().getString(), info.getMonsterKillCount(), info.getHighMonsterKillCount()));
        }
        PykeLib.sendSystemMessage(serverPlayer, COLOR.LIME.getColor(), "");

        return 1;
    }

    private static int updateRanking(CommandContext<CommandSourceStack> context) {
        MinecraftServer server = context.getSource().getServer();
        PlayerList players = server.getPlayerList();

        ranking(players);

        PykeLib.sendSystemMessage(players.getPlayers(), COLOR.LIME.getColor(), "§6[SYSTEM]§r 몬스터 처치 랭킹이 갱신되었습니다.");

        return 1;
    }

    public static void ranking(PlayerList players) {
        ranking.clear();
        players.getPlayers().forEach(player -> {
            IDeceasedInfo info = ModComponents.DECEASED_INFO.get(player);

            int killCount = info.getHighMonsterKillCount();
            if (killCount > 0 && !(player.isCreative() || player.isSpectator())) { ranking.put(player.getUUID(), killCount); }
        });

        ranking = ranking.entrySet().stream()
            .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (oldValue, newValue) -> oldValue,
                LinkedHashMap::new
            ));

        players.getPlayers().forEach(S2C_SyncRankingPacket::send);
    }
}
