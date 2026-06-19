package kr.pyke.deceased_croa.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class GameTimeController extends SavedData {
    private static final String FILE_NAME = "game_time_controller";

    private float scale = 1.f;
    private double accumulator = 0.d;
    private boolean frozen = false;

    public float getScale() { return this.scale; }
    public void setScale(float scale) { this.scale = Math.max(0.f, scale); }

    public boolean isFrozen() { return frozen; }
    public void setFrozen(boolean frozen) { this.frozen = frozen; }

    public long consumeDelta() {
        if (this.frozen) { return 0L; }

        this.accumulator += this.scale;
        long whole = (long) this.accumulator;
        this.accumulator -= whole;
        return whole;
    }

    public void reset() {
        this.accumulator = 0.d;
    }

    public static GameTimeController load(CompoundTag tag) {
        GameTimeController data = new GameTimeController();

        data.scale = tag.getFloat("scale");
        data.accumulator = tag.getDouble("accumulator");
        data.frozen = tag.getBoolean("frozen");

        return data;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putFloat("scale", this.scale);
        compoundTag.putDouble("accumulator", this.accumulator);
        compoundTag.putBoolean("frozen", this.frozen);

        return compoundTag;
    }

    public static GameTimeController getServerState(MinecraftServer server) {
        ServerLevel serverLevel = server.overworld();

        return serverLevel.getDataStorage().computeIfAbsent(GameTimeController::load, GameTimeController::new, FILE_NAME);
    }
}
