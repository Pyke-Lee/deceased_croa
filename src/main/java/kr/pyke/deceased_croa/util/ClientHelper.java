package kr.pyke.deceased_croa.util;

import com.mojang.blaze3d.platform.InputConstants;
import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.client.gui.screen.TeleportScreen;
import kr.pyke.deceased_croa.client.key.ModKeyBinding;
import kr.pyke.deceased_croa.client.sound.NotificationSoundInstance;
import kr.pyke.deceased_croa.network.pakcet.s2c.S2C_PlayItemActivationPacket;
import kr.pyke.deceased_croa.network.pakcet.s2c.S2C_PlaySoundPacket;
import kr.pyke.deceased_croa.registry.tab.ModCreativeTabs;
import kr.pyke.deceased_croa.type.CREATIVE_MODE_TABS;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
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

    public static void registerItemActivationPacket() {
        ClientPlayNetworking.registerGlobalReceiver(S2C_PlayItemActivationPacket.ID, (client, handler, buf, responseSender) -> {
            ItemStack stack = buf.readItem();

            client.execute(() -> {
                if (stack.isEmpty()) { return; }

                client.gameRenderer.displayItemActivation(stack);
            });
        });
    }

    public static void rebuildCreativeModeTab(Minecraft client, CREATIVE_MODE_TABS tabs) {
        if (client.player == null || client.level == null) { return; }

        CreativeModeTab.ItemDisplayParameters parameters = new CreativeModeTab.ItemDisplayParameters(
            client.player.connection.enabledFeatures(),
            client.player.canUseGameMasterBlocks(),
            client.level.registryAccess()
        );

        switch(tabs) {
            case GENERAL:
                ModCreativeTabs.GENERAL.buildContents(parameters);
                break;

            case RANDOM_BOX:
                ModCreativeTabs.RANDOM_BOX.buildContents(parameters);
                break;

            case TELEPORT_RUNE:
                ModCreativeTabs.TELEPORT_RUNE.buildContents(parameters);
                break;
        }
    }

    public static void openTeleportScreen() {
        Minecraft.getInstance().setScreen(new TeleportScreen());
    }
}