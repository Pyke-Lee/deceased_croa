package kr.pyke.deceased_croa.client.key;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class ModKeyBinding {
    public static KeyMapping rankingKey;

    private ModKeyBinding() { }

    public static void register() {
        rankingKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.deceased_croa.ranking",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_L,
            "category.deceased_croa.general"
        ));
    }
}
