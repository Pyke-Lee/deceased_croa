package kr.pyke.deceased_croa.client;

import kr.pyke.deceased_croa.client.gui.hud.ChargeGaugeHud;
import kr.pyke.deceased_croa.client.gui.hud.DeceasedHud;
import kr.pyke.deceased_croa.client.gui.hud.RankingOverlay;
import kr.pyke.deceased_croa.client.gui.hud.RankingSidebarOverlay;
import kr.pyke.deceased_croa.client.gui.screen.MailboxScreen;
import kr.pyke.deceased_croa.client.key.ModKeyBinding;
import kr.pyke.deceased_croa.network.DeceasedPacket;
import kr.pyke.deceased_croa.registry.menu.ModMenus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screens.MenuScreens;

public class DeceasedCroaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DeceasedPacket.registerClient();

        ModKeyBinding.register();

        HudRenderCallback.EVENT.register(new DeceasedHud());
        HudRenderCallback.EVENT.register(new RankingSidebarOverlay());
        HudRenderCallback.EVENT.register(new RankingOverlay());
        HudRenderCallback.EVENT.register(new ChargeGaugeHud());

        MenuScreens.register(ModMenus.MAILBOX, MailboxScreen::new);
    }
}
