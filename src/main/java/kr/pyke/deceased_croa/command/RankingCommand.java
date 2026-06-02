package kr.pyke.deceased_croa.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import kr.pyke.deceased_croa.network.pakcet.s2c.S2C_SyncRankingPacket;
import kr.pyke.deceased_croa.registry.component.ModComponents;
import kr.pyke.deceased_croa.registry.component.info.IDeceasedInfo;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class RankingCommand {
    public static Map<UUID, Integer> ranking = new HashMap<>();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal("랭킹")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("갱신").executes(RankingCommand::updateRanking))
        );
    }

    private static int updateRanking(CommandContext<CommandSourceStack> context) {
        MinecraftServer server = context.getSource().getServer();
        PlayerList players = server.getPlayerList();

        ranking(players);

        players.broadcastSystemMessage(Component.literal("§6[SYSTEM]§r 몬스터 처치 랭킹이 갱신되었습니다."), false);

        return 1;
    }

    public static void ranking(PlayerList players) {
        ranking.clear();
        players.getPlayers().forEach(player -> {
            IDeceasedInfo info = ModComponents.DECEASED_INFO.get(player);

            int killCount = info.getMonsterKillCount();
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
