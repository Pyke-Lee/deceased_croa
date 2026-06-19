package kr.pyke.deceased_croa.registry.component.info;

import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.data.TeleportData;
import kr.pyke.deceased_croa.registry.component.ModComponents;
import kr.pyke.deceased_croa.registry.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DeceasedInfo implements IDeceasedInfo {
    private final Player player;
    private int monsterKillCount;
    private int highMonsterKillCount;

    private TeleportData.TeleportEntry teleportEntry;

    private final List<ItemStack> savedItems = new ArrayList<>();

    public DeceasedInfo(Player player) {
        this.player = player;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        this.monsterKillCount = tag.getInt("monsterKillCount");
        this.highMonsterKillCount = tag.getInt("highMonsterKillCount");

        if (tag.contains("teleportEntry")) {
            this.teleportEntry = TeleportData.TeleportEntry.fromNbt(tag.getCompound("teleportEntry"));
        }

        savedItems.clear();
        ListTag savedList = tag.getList("savedItems", Tag.TAG_COMPOUND);
        for (int i = 0; i < savedList.size(); i++) {
            savedItems.add(ItemStack.of(savedList.getCompound(i)));
        }
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putInt("monsterKillCount", monsterKillCount);
        tag.putInt("highMonsterKillCount", highMonsterKillCount);

        if (this.teleportEntry != null) {
            tag.put("teleportEntry", this.teleportEntry.toNbt());
        }

        ListTag savedList = new ListTag();
        for (ItemStack itemStack : savedItems) {
            savedList.add(itemStack.save(new CompoundTag()));
        }
        tag.put("savedItems", savedList);
    }

    public void addMonsterKillCount(int value) {
        if (value <= 0) { return; }

        this.monsterKillCount += value;

        if (this.monsterKillCount > this.highMonsterKillCount) {
            this.highMonsterKillCount = this.monsterKillCount;
        }

        ModComponents.DECEASED_INFO.sync(player);
    }

    public void subMonsterKillCount(int value) {
        if (value <= 0) { return; }

        this.monsterKillCount -= value;
        ModComponents.DECEASED_INFO.sync(player);
    }

    public void setMonsterKillCount(int value) {
        if (value < 0) { return; }

        this.monsterKillCount = value;

        if (this.monsterKillCount > this.highMonsterKillCount) {
            this.highMonsterKillCount = this.monsterKillCount;
        }

        ModComponents.DECEASED_INFO.sync(player);
    }

    public void setHighMonsterKillCount(int value) {
        if (value < 0) { return; }

        this.highMonsterKillCount = value;
        ModComponents.DECEASED_INFO.sync(player);
    }

    public int getMonsterKillCount() { return this.monsterKillCount; }

    public int getHighMonsterKillCount() { return this.highMonsterKillCount; }

    public void resetMonsterKillCount() {
        this.monsterKillCount = 0;
        ModComponents.DECEASED_INFO.sync(player);
    }

    public void setReturnTeleportEntry() {
        this.teleportEntry = new TeleportData.TeleportEntry("이전 위치", player.level().dimension(), player.position(), player.getYRot(), player.getXRot());
        ModComponents.DECEASED_INFO.sync(player);
    }

    public boolean teleportToReturnTeleportEntry() {
        if (player.isLocalPlayer()) { return false; }
        if (this.teleportEntry == null) { return false; }

        MinecraftServer server = player.level().getServer();
        if (server == null) { return false; }

        ServerLevel level = server.getLevel(this.teleportEntry.dim());
        if (level == null) { return false; }

        player.teleportTo(level, this.teleportEntry.pos().x(), this.teleportEntry.pos().y(), this.teleportEntry.pos().z(), Set.of(), this.teleportEntry.yaw(), this.teleportEntry.pitch());
        this.teleportEntry = null;
        ModComponents.DECEASED_INFO.sync(player);
        return true;
    }

    public boolean isReturnable() {
        if (this.teleportEntry == null) { return false; }

        MinecraftServer server = player.level().getServer();
        if (server == null) { return false; }

        ServerLevel level = server.getLevel(this.teleportEntry.dim());
        return level != null;
    }

    public void saveItems() {
        if (player.isLocalPlayer()) { return; }

        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.items.size(); i++) {
            ItemStack itemStack = inventory.items.get(i);
            if (itemStack.isEmpty()) { continue; }

            if (itemStack.is(ModItems.KEEP_ITEM)) {
                savedItems.add(itemStack.copy());
                inventory.items.set(i, ItemStack.EMPTY);
            }
        }

        ModComponents.DECEASED_INFO.sync(player);
    }

    public void loadItems() {
        if (player.isLocalPlayer()) { return; }

        Inventory inventory = player.getInventory();
        for (ItemStack itemStack : savedItems) {
            inventory.add(itemStack);
        }

        savedItems.clear();
        ModComponents.DECEASED_INFO.sync(player);
    }
}
