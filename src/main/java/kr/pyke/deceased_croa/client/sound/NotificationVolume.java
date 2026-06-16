package kr.pyke.deceased_croa.client.sound;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.pyke.deceased_croa.DeceasedCroa;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Mth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class NotificationVolume {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(DeceasedCroa.MOD_ID + "_sound.json");

    private static float volume = 1.f;
    private static boolean loaded = false;

    private NotificationVolume() { }

    public static float get() {
        if (!loaded) { load(); }

        return volume;
    }

    public static void set(float value) {
        volume = Mth.clamp(value, 0.f, 1.f);
        save();
    }

    private static void load() {
        loaded = true;
        if (!Files.exists(CONFIG_PATH)) { return; }

        try {
            String content = Files.readString(CONFIG_PATH, StandardCharsets.UTF_8);
            JsonObject object = JsonParser.parseString(content).getAsJsonObject();
            if (object.has("notification_volume")) {
                volume = Mth.clamp(object.get("notification_volume").getAsFloat(), 0.f, 1.f);
            }
        }
        catch (IOException | RuntimeException exception) { DeceasedCroa.LOGGER.warn("알림 볼륨 설정을 불러오지 못했습니다.", exception); }
    }

    private static void save() {
        JsonObject object = new JsonObject();
        object.addProperty("notification_volume", volume);

        try { Files.writeString(CONFIG_PATH, object.toString(), StandardCharsets.UTF_8); }
        catch (IOException exception) { DeceasedCroa.LOGGER.warn("알림 볼륨 설정을 저장하지 못했습니다.", exception); }
    }
}