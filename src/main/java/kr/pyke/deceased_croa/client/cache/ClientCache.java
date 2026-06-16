package kr.pyke.deceased_croa.client.cache;

import kr.pyke.deceased_croa.data.TeleportData;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class ClientCache {
    private static Map<UUID, Integer> ranking = new LinkedHashMap<>();
    private static Map<UUID, String> displayNames = new HashMap<>();
    private static Map<String, TeleportData.TeleportEntry> teleportEntries = new HashMap<>();

    public static Map<UUID, Integer> getRanking() { return ranking; }

    public static Map<UUID, String> getDisplayNames() { return displayNames; }

    public static Map<String, TeleportData.TeleportEntry> getTeleportEntries() { return teleportEntries; }

    public static void setRanking(Map<UUID, Integer> ranking) {
        ClientCache.ranking = ranking;
    }

    public static void setDisplayName(UUID uuid, String displayName) { ClientCache.displayNames.put(uuid, displayName); }
    public static void setDisplayNames(Map<UUID, String> displayNames) { ClientCache.displayNames = displayNames; }

    public static void setTeleportEntries(Map<String, TeleportData.TeleportEntry> teleportEntries) { ClientCache.teleportEntries = teleportEntries; }
    public static void setTeleportEntry(String id, TeleportData.TeleportEntry teleportEntry) { ClientCache.teleportEntries.put(id, teleportEntry); }
    public static void removeTeleportEntry(String id) { ClientCache.teleportEntries.remove(id); }
}
