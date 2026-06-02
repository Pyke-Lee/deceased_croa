package kr.pyke.deceased_croa;

import kr.pyke.deceased_croa.command.MailboxCommand;
import kr.pyke.deceased_croa.command.RankingCommand;
import kr.pyke.deceased_croa.handler.DonationEventHandler;
import kr.pyke.deceased_croa.handler.ServerLivingEntityEventHandler;
import kr.pyke.deceased_croa.handler.ServerPlayConnectionEventHandler;
import kr.pyke.deceased_croa.handler.ServerTickEventHandler;
import kr.pyke.deceased_croa.network.DeceasedPacket;
import kr.pyke.deceased_croa.registry.mob_effect.ModEffects;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeceasedCroa implements ModInitializer {
	public static final String MOD_ID = "deceased_croa";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModEffects.register();

		DeceasedPacket.registerServer();

		DonationEventHandler.register();
		ServerLivingEntityEventHandler.register();
		ServerPlayConnectionEventHandler.register();
		ServerTickEventHandler.register();

		CommandRegistrationCallback.EVENT.register(RankingCommand::register);
		CommandRegistrationCallback.EVENT.register(MailboxCommand::register);
	}
}