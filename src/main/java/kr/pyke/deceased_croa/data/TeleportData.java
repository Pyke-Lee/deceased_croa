package kr.pyke.deceased_croa.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class TeleportData extends SavedData {
    private static final String FILE_NAME = "teleport_data";
    private final Map<String, TeleportEntry> teleportEntries = new HashMap<>();

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag) {
        CompoundTag dataTag = new CompoundTag();

        for (Map.Entry<String, TeleportEntry> entry : teleportEntries.entrySet()) {
            dataTag.put(entry.getKey(), entry.getValue().toNbt());
        }
        compoundTag.put("teleportEntries", dataTag);

        return compoundTag;
    }

    public static TeleportData load(CompoundTag tag) {
        TeleportData data = new TeleportData();

        CompoundTag dataTag = tag.getCompound("teleportEntries");
        for (String key : dataTag.getAllKeys()) {
            data.teleportEntries.put(key, TeleportEntry.fromNbt(dataTag.getCompound(key)));
        }

        return data;
    }

    public static TeleportData getServerState(MinecraftServer server) {
        ServerLevel serverLevel = server.overworld();

        return serverLevel.getDataStorage().computeIfAbsent(TeleportData::load, TeleportData::new, FILE_NAME);
    }

    public TeleportEntry get(String key) { return teleportEntries.get(key); }

    public void put(String key, TeleportEntry entry) {
        teleportEntries.put(key, entry);
        setDirty();
    }

    public void remove(String key) {
        teleportEntries.remove(key);
        setDirty();
    }

    public Map<String, TeleportEntry> getEntries() { return teleportEntries; }

    public record TeleportEntry(String displayName, ResourceKey<Level> dim, Vec3 pos, float yaw, float pitch) {
        public TeleportEntry(String displayName, ResourceKey<Level> dim, Vec3 pos, float yaw) {
            this(displayName, dim, pos, yaw, 0.f);
        }

        public boolean teleportTo(ServerPlayer player) {
            MinecraftServer server = player.getServer();
            if (server == null) { return false; }

            ServerLevel level = server.getLevel(dim);
            if (level == null) { return false; }

            player.teleportTo(level, Math.floor(pos.x) + 0.5d, Math.floor(pos.y), Math.floor(pos.z) + 0.5d, yaw, pitch);
            return true;
        }

        public CompoundTag toNbt() {
            CompoundTag tag = new CompoundTag();

            tag.putString("displayName", displayName);
            tag.putString("dim", dim.location().toString());
            tag.putDouble("x", pos.x);
            tag.putDouble("y", pos.y);
            tag.putDouble("z", pos.z);
            tag.putFloat("yaw", yaw);
            tag.putFloat("pitch", pitch);

            return tag;
        }

        public static TeleportEntry fromNbt(CompoundTag tag) {
            String displayName = tag.getString("displayName");
            ResourceKey<Level> dim = ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, new ResourceLocation(tag.getString("dim")));
            Vec3 pos = new Vec3(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
            float yaw = tag.getFloat("yaw");
            float pitch = tag.getFloat("pitch");

            return new TeleportEntry(displayName, dim, pos, yaw, pitch);
        }
    }
}
