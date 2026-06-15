package kr.pyke.deceased_croa.client.key;

import com.mojang.blaze3d.platform.InputConstants;
import kr.pyke.deceased_croa.client.DeceasedCroaClient;
import kr.pyke.deceased_croa.network.DeceasedPacket;
import kr.pyke.deceased_croa.network.pakcet.c2s.C2S_OpenMailboxPacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class ModKeyBinding {
    public static KeyMapping rankingKey;
    public static KeyMapping openMailboxKey;
    public static KeyMapping detailsRandomBoxKey;

    private ModKeyBinding() { }

    public static void register() {
        rankingKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.deceased_croa.ranking",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_L,
            "category.deceased_croa.general"
        ));

        openMailboxKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.deceased_croa.mail.open",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            "category.deceased_croa.mailbox"
        ));

        detailsRandomBoxKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.deceased_croa.randombox.details",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            "category.deceased_croa.randombox"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMailboxKey.consumeClick()) {
                C2S_OpenMailboxPacket.send();
            }
        });
    }
}
