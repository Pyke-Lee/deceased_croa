package kr.pyke.deceased_croa.handler;

import kr.pyke.deceased_croa.command.RankingCommand;
import kr.pyke.deceased_croa.manager.HordeManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class ServerTickEventHandler {
    private static int tickCounter = 0;

    private ServerTickEventHandler() { }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            tickCounter++;
            if (tickCounter % 20 == 0) {
                tickCounter = 0;
                RankingCommand.ranking(server.getPlayerList());
            }

            HordeManager.tick();
        });
    }
}
