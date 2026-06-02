package kr.pyke.deceased_croa.network;

import kr.pyke.deceased_croa.network.pakcet.c2s.C2S_ResponseAggroMobListPacket;
import kr.pyke.deceased_croa.network.pakcet.s2c.S2C_RequestAggroMobListPacket;
import kr.pyke.deceased_croa.network.pakcet.s2c.S2C_SyncRankingPacket;

public class DeceasedPacket {
    private DeceasedPacket() { }

    public static void registerServer() {
        C2S_ResponseAggroMobListPacket.register();
    }

    public static void registerClient() {
        S2C_RequestAggroMobListPacket.register();
        S2C_SyncRankingPacket.register();
    }
}
