package kr.pyke.deceased_croa.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kr.pyke.deceased_croa.DeceasedCroa;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("custom_horde.json");

    private static HordeConfig HORDE_CONFIG;

    public static HordeConfig getHordeConfig() {
        if (HORDE_CONFIG == null) { load(); }

        return HORDE_CONFIG;
    }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                HORDE_CONFIG = GSON.fromJson(json, HordeConfig.class);
                if (HORDE_CONFIG == null) { HORDE_CONFIG = new HordeConfig(); }
            }
            catch (IOException e) {
                DeceasedCroa.LOGGER.error("Failed to load config", e);
                HORDE_CONFIG = new HordeConfig();
            }
        }
        else {
            HORDE_CONFIG = new HordeConfig();
            save();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(HORDE_CONFIG));
        }
        catch (IOException e) { DeceasedCroa.LOGGER.error("Failed to save config", e); }
    }
}
