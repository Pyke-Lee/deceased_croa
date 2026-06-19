package kr.pyke.deceased_croa.config;

import com.google.gson.annotations.SerializedName;
import kr.pyke.deceased_croa.DeceasedCroa;

import java.util.ArrayList;
import java.util.List;

public class HordeConfig {
    @SerializedName("normal_horde_mobs")
    public List<MobEntry> normal_hordeMobs = new ArrayList<>();

    @SerializedName("special_horde_mobs")
    public List<MobEntry> special_hordeMobs = new ArrayList<>();

    @SerializedName("server_horde_mobs")
    public List<MobEntry> server_horde_mobs = new ArrayList<>();

    @SerializedName("normal_spawn_interval")
    public int normal_spawn_interval = 10;

    @SerializedName("special_spawn_interval")
    public int special_spawn_interval = 10;

    @SerializedName("server_spawn_interval")
    public int server_spawn_interval = 10;

    @SerializedName("spawn_radius")
    public int spawnRadius = 16;

    @SerializedName("glow_duration")
    public int glowDuration = 600;

    public static class MobEntry {
        public String entity;
        public int min_spawns;
        public int max_spawns;

        public MobEntry(String id) {
            this(id, 1, 1);
        }

        public MobEntry(String entity, int min_spawns, int max_spawns) {
            this.entity = entity;
            this.min_spawns = min_spawns;
            this.max_spawns = max_spawns;

            if (this.min_spawns == 0 && this.max_spawns == 0) {
                this.min_spawns = 1;
                this.max_spawns = 1;
            }
        }
    }
}
