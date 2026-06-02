package kr.pyke.deceased_croa.network.pakcet.s2c;

import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.data.RandomBoxDefinition;
import kr.pyke.deceased_croa.data.RandomBoxReward;
import kr.pyke.deceased_croa.manager.RandomBoxManager;
import kr.pyke.deceased_croa.type.MESSAGE_TYPE;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class S2C_SyncRandomBoxPacket {
    public static final ResourceLocation ID = new ResourceLocation(DeceasedCroa.MOD_ID, "sync_random_box");

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ID, (client, listener, buf, sender) -> {
            List<RandomBoxDefinition> definitions = readDefinitions(buf);
            client.execute(() -> RandomBoxManager.replaceAll(definitions));
        });
    }

    public static void send(ServerPlayer player) {
        FriendlyByteBuf buf = PacketByteBufs.create();

        writeDefinitions(buf, new ArrayList<>(RandomBoxManager.getDefinitions().values()));

        ServerPlayNetworking.send(player, ID, buf);
    }

    private static void writeDefinitions(FriendlyByteBuf buf, List<RandomBoxDefinition> definitions) {
        buf.writeVarInt(definitions.size());
        for (RandomBoxDefinition definition : definitions) {
            buf.writeUtf(definition.boxID());
            buf.writeVarInt(definition.customModelData());
            buf.writeUtf(definition.displayName());
            buf.writeBoolean(definition.mailbox());
            writeNullableString(buf, definition.openSound());
            writeNullableMessageType(buf, definition.openMessageType());
            writeNullableString(buf, definition.openMessage());

            buf.writeVarInt(definition.rewards().size());
            for (RandomBoxReward reward : definition.rewards()) {
                buf.writeResourceLocation(reward.item());
                buf.writeVarInt(reward.count());
                buf.writeVarInt(reward.weight());
                buf.writeNbt(reward.nbt());
                writeNullableString(buf, reward.openSound());
                writeNullableMessageType(buf, reward.openMessageType());
                writeNullableString(buf, reward.openMessage());
            }
        }
    }

    private static List<RandomBoxDefinition> readDefinitions(FriendlyByteBuf buf) {
        int definitionCount = buf.readVarInt();
        List<RandomBoxDefinition> definitions = new ArrayList<>();

        for (int i = 0; i < definitionCount; ++i) {
            String boxID = buf.readUtf();
            int customModelData = buf.readVarInt();
            String displayName = buf.readUtf();
            boolean mailbox = buf.readBoolean();
            String openSound = readNullableString(buf);
            MESSAGE_TYPE openMessageType = readNullableMessageType(buf);
            String openMessage = readNullableString(buf);

            int rewardCount = buf.readVarInt();
            List<RandomBoxReward> rewards = new ArrayList<>();

            for (int j = 0; j < rewardCount; ++j) {
                ResourceLocation item = buf.readResourceLocation();
                int count = buf.readVarInt();
                int weight = buf.readVarInt();
                CompoundTag nbt = buf.readNbt();
                String rewardSound = readNullableString(buf);
                MESSAGE_TYPE rewardMessageType = readNullableMessageType(buf);
                String rewardMessage = readNullableString(buf);

                rewards.add(new RandomBoxReward(item, count, weight, nbt, rewardSound, rewardMessageType, rewardMessage));
            }

            definitions.add(new RandomBoxDefinition(boxID, customModelData, displayName, mailbox, openSound, openMessageType, openMessage, rewards));
        }

        return definitions;
    }

    private static void writeNullableString(FriendlyByteBuf buf, String value) {
        buf.writeBoolean(value != null);
        if (value != null) {
            buf.writeUtf(value);
        }
    }

    private static String readNullableString(FriendlyByteBuf buf) {
        if (buf.readBoolean()) { return buf.readUtf(); }

        return null;
    }

    private static void writeNullableMessageType(FriendlyByteBuf buf, MESSAGE_TYPE value) {
        buf.writeBoolean(value != null);
        if (value != null) {
            buf.writeVarInt(value.ordinal());
        }
    }

    private static MESSAGE_TYPE readNullableMessageType(FriendlyByteBuf buf) {
        if (buf.readBoolean()) { return MESSAGE_TYPE.values()[buf.readVarInt()]; }

        return null;
    }
}