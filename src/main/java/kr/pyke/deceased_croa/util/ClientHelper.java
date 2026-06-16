package kr.pyke.deceased_croa.util;

import com.mojang.blaze3d.platform.InputConstants;
import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.client.key.ModKeyBinding;
import kr.pyke.deceased_croa.client.sound.NotificationSoundInstance;
import kr.pyke.deceased_croa.network.pakcet.s2c.S2C_PlaySoundPacket;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
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

    public static void registerSoundPacket() {
        ClientPlayNetworking.registerGlobalReceiver(S2C_PlaySoundPacket.ID, (client, handler, buf, responseSender) -> {
            ResourceLocation soundID = buf.readResourceLocation();
            float volume = buf.readFloat();
            float pitch = buf.readFloat();

            client.execute(() -> {
                SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(soundID);
                if (sound == null) { return; }

                client.getSoundManager().play(new NotificationSoundInstance(sound.getLocation(), volume, pitch));
            });
        });
    }
}