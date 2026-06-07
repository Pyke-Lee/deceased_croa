package kr.pyke.deceased_croa.data;

import com.mojang.authlib.GameProfile;
import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.util.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DisplayNameData extends SavedData {
    private static final String FILE_NAME = "displayname";
    private final Map<UUID, String> displayNames = new HashMap<>();
    private final Map<String, UUID> displayNamesReverse = new HashMap<>();

    public static DisplayNameData load(CompoundTag tag) {
        DisplayNameData data = new DisplayNameData();

        CompoundTag displayNameTag = tag.getCompound("displayNames");
        for (String key : displayNameTag.getAllKeys()) {
            try {
                UUID uuid = UUID.fromString(key);
                String displayName = displayNameTag.getString(key);
                data.displayNames.put(uuid, displayName);
                data.displayNamesReverse.put(displayName, uuid);
            }
            catch(Exception e) { DeceasedCroa.LOGGER.error("", e); }
        }

        return data;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        CompoundTag displayNameTag = new CompoundTag();

        displayNames.forEach((uuid, displayName) -> displayNameTag.putString(uuid.toString(), displayName));
        tag.put("displayNames", displayNameTag);

        return tag;
    }

    public static DisplayNameData getServerState(MinecraftServer server) {
        ServerLevel serverLevel = server.overworld();

        return serverLevel.getDataStorage().computeIfAbsent(DisplayNameData::load, DisplayNameData::new, FILE_NAME);
    }

    public String getDisplayName(UUID uuid) { return displayNames.get(uuid); }

    public Map<UUID, String> getDisplayNames() { return this.displayNames; }

    public String getRealName(String displayName) {
        UUID uuid = displayNamesReverse.get(displayName);
        if (null == uuid) { return displayName; }

        MinecraftServer server = DeceasedCroa.SERVER_INSTANCE;
        if (null == server) { return displayName; }

        ServerPlayer player = server.getPlayerList().getPlayer(uuid);
        if (null != player) { return player.getGameProfile().getName(); }

        Optional<GameProfile> profile = Objects.requireNonNull(server.getProfileCache()).get(uuid);
        return profile.map(GameProfile::getName).orElse(displayName);
    }

    public void setDisplayName(UUID uuid, String displayName) {
        String stripDisplayName = Utils.stripColor(displayName);

        if (displayNames.containsKey(uuid)) {
            String oldDisplayName = displayNames.get(uuid);
            displayNamesReverse.remove(oldDisplayName);
        }

        displayNames.put(uuid, stripDisplayName);
        displayNamesReverse.put(stripDisplayName, uuid);

        this.setDirty();
    }
}