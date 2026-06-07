package kr.pyke.deceased_croa.network;

import kr.pyke.deceased_croa.network.pakcet.c2s.*;
import kr.pyke.deceased_croa.network.pakcet.s2c.*;

public class DeceasedPacket {
    private DeceasedPacket() { }

    public static void registerServer() {
        C2S_ResponseAggroMobListPacket.register();
        C2S_OpenMailboxPacket.register();
        C2S_ClaimMailPacket.register();
        C2S_SelectMailPacket.register();
        C2S_SendMailPacket.register();
        C2S_OpenSendMailboxPacket.register();
    }

    public static void registerClient() {
        S2C_RequestAggroMobListPacket.register();
        S2C_SyncRankingPacket.register();
        S2C_SyncRandomBoxPacket.register();
        S2C_SendSingleDisplayName.register();
        S2C_SendBulkDisplayName.register();
    }
}
