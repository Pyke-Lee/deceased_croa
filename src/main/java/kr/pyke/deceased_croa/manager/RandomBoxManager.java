package kr.pyke.deceased_croa.manager;

import com.google.gson.*;
import kr.pyke.PykeLib;
import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.data.MailboxData;
import kr.pyke.deceased_croa.data.RandomBoxDefinition;
import kr.pyke.deceased_croa.data.RandomBoxReward;
import kr.pyke.deceased_croa.network.pakcet.s2c.S2C_PlayItemActivationPacket;
import kr.pyke.deceased_croa.network.pakcet.s2c.S2C_PlaySoundPacket;
import kr.pyke.deceased_croa.registry.component.ModComponents;
import kr.pyke.deceased_croa.type.MESSAGE_TYPE;
import kr.pyke.util.constants.COLOR;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class RandomBoxManager {
    private static final Map<String, RandomBoxDefinition> DEFINITIONS = new LinkedHashMap<>();
    private static final RandomSource RANDOM = RandomSource.create();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private RandomBoxManager() { }

    public static Map<String, RandomBoxDefinition> getDefinitions() { return DEFINITIONS; }

    public static RandomBoxDefinition get(String boxID) { return DEFINITIONS.get(boxID); }

    public enum CreateResult {
        SUCCESS,
        ALREADY_EXISTS,
        EMPTY,
        ERROR
    }

    public enum ExtractResult {
        SUCCESS,
        NOT_FOUND,
        NOT_ENOUGH_SPACE
    }

    private static Path directory() {
        return FabricLoader.getInstance().getConfigDir().resolve("randombox");
    }

    public static void replaceAll(List<RandomBoxDefinition> definitions) {
        DEFINITIONS.clear();
        for (RandomBoxDefinition definition : definitions) {
            DEFINITIONS.put(definition.boxID(), definition);
        }
    }

    public static int reload() {
        DEFINITIONS.clear();

        Path directory = directory();
        try { Files.createDirectories(directory); }
        catch (IOException exception) {
            DeceasedCroa.LOGGER.error("랜덤 상자 폴더를 생성하지 못했습니다: {}", directory, exception);
            return 0;
        }

        try (Stream<Path> stream = Files.list(directory)) {
            List<Path> files = stream.filter(path -> path.toString().endsWith(".json")).sorted().toList();
            for (Path file : files) { loadFile(file); }
        }
        catch (IOException exception) { DeceasedCroa.LOGGER.error("랜덤 상자 폴더를 읽지 못했습니다: {}", directory, exception); }

        return DEFINITIONS.size();
    }

    public static CreateResult createFromContainer(String boxID, String displayName, List<ItemStack> contents) {
        Path file = directory().resolve(boxID + ".json");

        if (DEFINITIONS.containsKey(boxID) || Files.exists(file)) { return CreateResult.ALREADY_EXISTS; }

        JsonArray rewards = new JsonArray();
        for (ItemStack stack : contents) {
            if (stack.isEmpty()) { continue; }

            JsonObject reward = new JsonObject();
            reward.addProperty("item", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
            reward.addProperty("count", stack.getCount());
            reward.addProperty("weight", 1);

            CompoundTag tag = stack.getTag();
            if (tag != null && !tag.isEmpty()) { reward.addProperty("nbt", tag.toString()); }

            rewards.add(reward);
        }

        if (rewards.isEmpty()) { return CreateResult.EMPTY; }

        JsonObject root = getJsonObject(displayName);
        root.add("rewards", rewards);

        try {
            Files.createDirectories(directory());
            try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) { GSON.toJson(root, writer); }
        }
        catch (IOException exception) {
            DeceasedCroa.LOGGER.error("랜덤 상자 파일을 생성하지 못했습니다: {}", boxID, exception);
            return CreateResult.ERROR;
        }

        return CreateResult.SUCCESS;
    }

    private static @NotNull JsonObject getJsonObject(String displayName) {
        JsonObject root = new JsonObject();

        root.addProperty("custom_model_data", 0);
        root.addProperty("display_name", displayName);
        root.addProperty("mailbox", true);
        root.addProperty("pack", false);
        root.addProperty("open_sound", "deceased_croa:custom_sound.box.open");
        root.addProperty("sound_volume", 1.f);
        root.addProperty("open_message_type", "personal");
        root.addProperty("open_message", "§b%player%§r님이 상자에서 §a[ %item% ]§r(을)를 §e%count%§r개 획득하셨습니다!");

        return root;
    }

    public static ExtractResult extractToContainer(String boxID, Container container) {
        RandomBoxDefinition definition = DEFINITIONS.get(boxID);
        if (definition == null) { return ExtractResult.NOT_FOUND; }

        List<RandomBoxReward> rewards = definition.rewards();
        if (rewards.size() > container.getContainerSize()) { return ExtractResult.NOT_ENOUGH_SPACE; }

        container.clearContent();
        for (int i = 0; i < rewards.size(); ++i) {
            container.setItem(i, rewards.get(i).createStack());
        }

        return ExtractResult.SUCCESS;
    }

    private static void loadFile(Path file) {
        String fileName = file.getFileName().toString();
        String boxID = fileName.substring(0, fileName.length() - ".json".length());

        if (DEFINITIONS.containsKey(boxID)) {
            DeceasedCroa.LOGGER.warn("중복된 랜덤 상자 id 입니다. 무시합니다: {}", boxID);
            return;
        }

        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            RandomBoxDefinition definition = parseDefinition(boxID, root);
            if (definition == null) { return; }

            DEFINITIONS.put(boxID, definition);
        }
        catch (Exception exception) { DeceasedCroa.LOGGER.error("랜덤 상자 파일을 읽지 못했습니다: {}", fileName, exception); }
    }

    private static RandomBoxDefinition parseDefinition(String boxID, JsonObject root) {
        if (!root.has("custom_model_data")) {
            DeceasedCroa.LOGGER.warn("custom_model_data 가 없습니다. 무시합니다: {}", boxID);
            return null;
        }

        int customModelData = root.get("custom_model_data").getAsInt();
        String displayName = optString(root, "display_name", boxID);
        boolean mailbox = root.has("mailbox") && root.get("mailbox").getAsBoolean();
        String openSound = optString(root, "open_sound", null);
        float soundVolume = root.has("sound_volume") ? root.get("sound_volume").getAsFloat() : 1.f;
        MESSAGE_TYPE openMessageType = MESSAGE_TYPE.fromString(optString(root, "open_message_type", null));
        String openMessage = optString(root, "open_message", null);
        boolean isPackage = root.has("pack") && root.get("pack").getAsBoolean();

        List<RandomBoxReward> rewards = new ArrayList<>();
        if (root.has("rewards")) {
            JsonArray array = root.getAsJsonArray("rewards");
            for (JsonElement element : array) {
                RandomBoxReward reward = parseReward(boxID, element.getAsJsonObject());
                if (reward != null) {
                    rewards.add(reward);
                }
            }
        }

        if (rewards.isEmpty()) {
            DeceasedCroa.LOGGER.warn("보상이 비어 있습니다. 무시합니다: {}", boxID);
            return null;
        }

        return new RandomBoxDefinition(boxID, customModelData, displayName, mailbox, isPackage, openSound, soundVolume, openMessageType, openMessage, rewards);
    }

    private static RandomBoxReward parseReward(String boxID, JsonObject object) {
        if (!object.has("item")) {
            DeceasedCroa.LOGGER.warn("보상에 item 이 없습니다. 무시합니다: {}", boxID);
            return null;
        }

        ResourceLocation item = ResourceLocation.tryParse(object.get("item").getAsString());
        if (item == null || !BuiltInRegistries.ITEM.containsKey(item)) {
            DeceasedCroa.LOGGER.warn("존재하지 않는 아이템 입니다. 보상을 무시합니다: {} ({})", object.get("item").getAsString(), boxID);
            return null;
        }

        int count = object.has("count") ? object.get("count").getAsInt() : 1;
        int weight = object.has("weight") ? object.get("weight").getAsInt() : 1;

        CompoundTag nbt = null;
        if (object.has("nbt")) {
            String snbt = object.get("nbt").getAsString();
            try { nbt = TagParser.parseTag(snbt); }
            catch (Exception exception) { DeceasedCroa.LOGGER.warn("보상 nbt 를 해석하지 못했습니다: {} ({})", snbt, boxID); }
        }

        String openSound = object.has("open_sound") ? object.get("open_sound").getAsString() : null;
        float soundVolume = object.has("sound_volume") ? object.get("sound_volume").getAsFloat() : 0.f;
        MESSAGE_TYPE openMessageType = object.has("open_message_type") ? MESSAGE_TYPE.fromString(object.get("open_message_type").getAsString()) : null;
        String openMessage = object.has("open_message") ? object.get("open_message").getAsString() : null;

        return new RandomBoxReward(item, count, weight, nbt, openSound, soundVolume, openMessageType, openMessage);
    }

    private static String optString(JsonObject object, String key, String fallback) {
        if (object.has(key) && !object.get(key).isJsonNull()) { return object.get(key).getAsString(); }

        return fallback;
    }

    public static void grant(ServerPlayer player, RandomBoxDefinition box) {
        if (box.pack()) {
            List<RandomBoxReward> rewards = box.rewards();
            if (rewards.isEmpty()) { return; }

            for (RandomBoxReward reward : rewards) {
                ItemStack itemStack = reward.createStack();
                if (box.mailbox()) { sendToMailbox(player, box, itemStack); }
                else { giveToInventory(player, itemStack); }
            }

            playSound(player, box);
            PykeLib.sendSystemMessage(player, COLOR.GOLD.getColor(), box.openMessage());
        }
        else {
            RandomBoxReward reward = box.roll(RANDOM);
            if (reward == null) { return; }

            ItemStack stack = reward.createStack();
            if (box.mailbox()) { sendToMailbox(player, box, stack); }
            else { giveToInventory(player, stack); }

            playSound(player, box, reward);
            playItemActivation(player, box, reward, stack);
            sendMessage(player, box, reward, stack);
        }
    }

    private static void sendToMailbox(ServerPlayer player, RandomBoxDefinition box, ItemStack stack) {
        MailboxData mail = MailboxData.create(stack.copy());
        ModComponents.MAILBOX.get(player).addMail(mail, false);
    }

    private static void giveToInventory(ServerPlayer player, ItemStack stack) {
        ItemStack copy = stack.copy();
        player.getInventory().add(copy);

        if (!copy.isEmpty()) {
            player.drop(copy, false);
        }
    }

    private static void playSound(ServerPlayer player, RandomBoxDefinition box, RandomBoxReward reward) {
        String soundID = reward.resolveSound(box);
        if (soundID == null || soundID.isBlank()) { return; }

        ResourceLocation soundLocation = ResourceLocation.tryParse(soundID);
        if (soundLocation == null) { return; }

        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(soundLocation);

        S2C_PlaySoundPacket.send(player, soundEvent, reward.resolveSoundVolume(box), 1.f);
    }

    private static void playSound(ServerPlayer player, RandomBoxDefinition box) {
        String soundID = box.openSound();
        if (soundID == null || soundID.isBlank()) { return; }

        ResourceLocation soundLocation = ResourceLocation.tryParse(soundID);
        if (soundLocation == null) { return; }

        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(soundLocation);

        S2C_PlaySoundPacket.send(player, soundEvent, box.soundVolume(), 1.f);
    }

    private static void sendMessage(ServerPlayer player, RandomBoxDefinition box, RandomBoxReward reward, ItemStack stack) {
        String template = reward.resolveMessage(box);
        if (template == null || template.isBlank()) { return; }

        String itemName = stack.getHoverName().getString();
        String message = template
            .replace("%player%", player.getDisplayName().getString())
            .replace("%item%", itemName.strip())
            .replace("%count%", String.valueOf(stack.getCount()));

        MESSAGE_TYPE messageType = reward.resolveMessageType(box);
        messageType.send(player, COLOR.GOLD.getColor(), message);
    }

    private static String stripFormatting(String value) {
        String stripped = ChatFormatting.stripFormatting(value);
        return stripped == null ? value : stripped;
    }

    private static void playItemActivation(ServerPlayer player, RandomBoxDefinition box, RandomBoxReward reward, ItemStack stack) {
        MESSAGE_TYPE messageType = reward.resolveMessageType(box);
        if (messageType == MESSAGE_TYPE.PERSONAL) { return; }

        S2C_PlayItemActivationPacket.send(player, stack.copy());
        spawnTotemParticles(player);
    }

    private static void spawnTotemParticles(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) { return; }

        level.sendParticles(player, ParticleTypes.TOTEM_OF_UNDYING, false, player.getX(), player.getY() + 1.f, player.getZ(), 30, 0.5f, 0.5f, 0.5f, 0.3f);
    }
}