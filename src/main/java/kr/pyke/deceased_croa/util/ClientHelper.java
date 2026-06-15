package kr.pyke.deceased_croa.util;

import com.mojang.blaze3d.platform.InputConstants;
import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.client.key.ModKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class ClientHelper {
    private static boolean lastState = false;

    public static boolean isKeyDown() {
        if (ModKeyBinding.detailsRandomBoxKey.isUnbound()) {
            if (lastState) {
                DeceasedCroa.LOGGER.info("단축키가 지정되지 않았습니다.");
                lastState = false;
            }

            return false;
        }

        InputConstants.Key key = KeyBindingHelper.getBoundKeyOf(ModKeyBinding.detailsRandomBoxKey);
        long window = Minecraft.getInstance().getWindow().getWindow();
        int keyCode = key.getValue();
        boolean isDown;

        if (key.getType() == InputConstants.Type.MOUSE) { isDown = GLFW.glfwGetMouseButton(window, keyCode) == GLFW.GLFW_PRESS; }
        else { isDown = InputConstants.isKeyDown(window, keyCode); }

        if (isDown != lastState) {
            lastState = isDown;
        }

        return isDown;
    }
}