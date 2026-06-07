package kr.pyke.deceased_croa.client.cache;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class ClientCache {
    private static Map<UUID, Integer> ranking = new LinkedHashMap<>();
    private static Map<UUID, String> displayNames = new HashMap<>();

    public static Map<UUID, Integer> getRanking() { return ranking; }

    public static Map<UUID, String> getDisplayNames() { return displayNames; }

    public static void setRanking(Map<UUID, Integer> ranking) {
        ClientCache.ranking = ranking;
    }

    public static void setDisplayName(UUID uuid, String displayName) { ClientCache.displayNames.put(uuid, displayName); }
    public static void setDisplayNames(Map<UUID, String> displayNames) { ClientCache.displayNames = displayNames; }
}
