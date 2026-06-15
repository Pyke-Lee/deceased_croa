package kr.pyke.deceased_croa;

import kr.pyke.deceased_croa.command.*;
import kr.pyke.deceased_croa.config.ConfigLoader;
import kr.pyke.deceased_croa.handler.DonationEventHandler;
import kr.pyke.deceased_croa.handler.ServerLivingEntityEventHandler;
import kr.pyke.deceased_croa.handler.ServerPlayConnectionEventHandler;
import kr.pyke.deceased_croa.handler.ServerTickEventHandler;
import kr.pyke.deceased_croa.manager.RandomBoxManager;
import kr.pyke.deceased_croa.network.DeceasedPacket;
import kr.pyke.deceased_croa.registry.item.ModItems;
import kr.pyke.deceased_croa.registry.menu.ModMenus;
import kr.pyke.deceased_croa.registry.mob_effect.ModEffects;
import kr.pyke.deceased_croa.registry.sound.ModSounds;
import kr.pyke.deceased_croa.registry.tab.ModCreativeTabs;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeceasedCroa implements ModInitializer {
	public static final String MOD_ID = "deceased_croa";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static MinecraftServer SERVER_INSTANCE;

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTING.register(server -> SERVER_INSTANCE = server);
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> SERVER_INSTANCE = null);

		ConfigLoader.load();

		ModEffects.register();
		ModItems.register();
		ModCreativeTabs.register();
		ModMenus.register();
		ModSounds.register();

		DeceasedPacket.registerServer();

		DonationEventHandler.register();
		ServerLivingEntityEventHandler.register();
		ServerPlayConnectionEventHandler.register();
		ServerTickEventHandler.register();

		ServerLifecycleEvents.SERVER_STARTING.register(server -> RandomBoxManager.reload());

		CommandRegistrationCallback.EVENT.register(RankingCommand::register);
		CommandRegistrationCallback.EVENT.register(MailboxCommand::register);
		CommandRegistrationCallback.EVENT.register(RandomBoxCommand::register);
		CommandRegistrationCallback.EVENT.register(DisplayNameCommand::register);
		CommandRegistrationCallback.EVENT.register(HordeCommand::register);
	}
}