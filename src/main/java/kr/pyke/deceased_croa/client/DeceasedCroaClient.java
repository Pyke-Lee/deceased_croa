package kr.pyke.deceased_croa.client;

import kr.pyke.deceased_croa.client.gui.hud.DeceasedHud;
import kr.pyke.deceased_croa.client.gui.hud.RankingOverlay;
import kr.pyke.deceased_croa.client.key.ModKeyBinding;
import kr.pyke.deceased_croa.network.DeceasedPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class DeceasedCroaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DeceasedPacket.registerClient();

        ModKeyBinding.register();

        HudRenderCallback.EVENT.register(new DeceasedHud());
        HudRenderCallback.EVENT.register(new RankingOverlay());
    }
}
