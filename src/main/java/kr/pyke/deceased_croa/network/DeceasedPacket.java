package kr.pyke.deceased_croa.network;

import kr.pyke.deceased_croa.network.pakcet.c2s.C2S_ClaimMailPacket;
import kr.pyke.deceased_croa.network.pakcet.c2s.C2S_OpenMailboxPacket;
import kr.pyke.deceased_croa.network.pakcet.c2s.C2S_ResponseAggroMobListPacket;
import kr.pyke.deceased_croa.network.pakcet.s2c.*;

public class DeceasedPacket {
    private DeceasedPacket() { }

    public static void registerServer() {
        C2S_ResponseAggroMobListPacket.register();
        C2S_OpenMailboxPacket.register();
        C2S_ClaimMailPacket.register();
    }

    public static void registerClient() {
        S2C_RequestAggroMobListPacket.register();
        S2C_SyncRankingPacket.register();
        S2C_SyncRandomBoxPacket.register();
        S2C_SendSingleDisplayName.register();
        S2C_SendBulkDisplayName.register();
        S2C_PlaySoundPacket.register();
    }
}
