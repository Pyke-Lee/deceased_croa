package kr.pyke.deceased_croa.client.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class ClientCache {
    private static Map<UUID, Integer> ranking = new LinkedHashMap<>();

    public static Map<UUID, Integer> getRanking() { return ranking; }

    public static void setRanking(Map<UUID, Integer> ranking) {
        ClientCache.ranking = ranking;
    }
}
