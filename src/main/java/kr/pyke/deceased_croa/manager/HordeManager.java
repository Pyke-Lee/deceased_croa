package kr.pyke.deceased_croa.manager;

import kr.pyke.deceased_croa.config.ConfigLoader;
import kr.pyke.deceased_croa.config.HordeConfig;
import kr.pyke.deceased_croa.type.HORDE_TYPE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HordeManager {
    private static final Map<UUID, Deque<ActiveHorde>> HORDE_QUEUES = new ConcurrentHashMap<>();

    private static class ActiveHorde {
        final ServerLevel level;
        final Player player;
        final List<HordeConfig.MobEntry> entries;
        final int interval;
        final int glowDuration;
        final int spawnRadius;
        int index = 0;
        int ticksUntilNext = 0;

        ActiveHorde(ServerLevel level, Player player, List<HordeConfig.MobEntry> entries, int interval, int glowDuration, int spawnRadius) {
            this.level = level;
            this.player = player;
            this.entries = entries;
            this.interval = interval;
            this.glowDuration = glowDuration;
            this.spawnRadius = spawnRadius;
        }
    }

    public static void startHorde(ServerLevel level, Player player, HORDE_TYPE hordeType) {
        HordeConfig config = ConfigLoader.getHordeConfig();

        List<HordeConfig.MobEntry> entries;
        int interval;
        if (hordeType == HORDE_TYPE.NORMAL) {
            entries = config.normal_hordeMobs;
            interval = config.normal_spawn_interval;
        }
        else if (hordeType == HORDE_TYPE.SPECIAL) {
            entries = config.special_hordeMobs;
            interval = config.special_spawn_interval;
        }
        else {
            entries = config.server_hordes;
            interval = config.server_spawn_interval;
        }
        if (entries.isEmpty()) { return; }

        ActiveHorde horde = new ActiveHorde(level, player, entries, interval, config.glowDuration, config.spawnRadius);
        horde.ticksUntilNext = interval;
        Deque<ActiveHorde> queue = HORDE_QUEUES.computeIfAbsent(player.getUUID(), key -> new ArrayDeque<>());
        queue.addLast(horde);
    }

    public static void tick() {
        Iterator<Deque<ActiveHorde>> iterator = HORDE_QUEUES.values().iterator();
        while (iterator.hasNext()) {
            Deque<ActiveHorde> queue = iterator.next();

            ActiveHorde horde = queue.peekFirst();
            if (horde == null) {
                iterator.remove();
                continue;
            }

            if (horde.ticksUntilNext > 0) {
                --horde.ticksUntilNext;
                continue;
            }

            spawnEntry(horde, horde.entries.get(horde.index));
            ++horde.index;

            if (horde.index >= horde.entries.size()) {
                queue.removeFirst();
                if (queue.isEmpty()) { iterator.remove(); }
            }
            else { horde.ticksUntilNext = horde.interval; }
        }
    }

    private static void spawnEntry(ActiveHorde horde, HordeConfig.MobEntry entry) {
        ServerLevel level = horde.level;
        Player player = horde.player;
        RandomSource random = level.getRandom();

        EntityType<?> type = resolveType(entry.entity);
        if (type == null) { return; }

        int count = entry.min_spawns + (entry.max_spawns > entry.min_spawns ? random.nextInt(entry.max_spawns - entry.min_spawns + 1) : 0);
        for (int i = 0; i < count; ++i) {
            BlockPos pos = findSpawnPos(level, player.blockPosition(), horde.spawnRadius, random);
            if (pos == null) { continue; }

            Entity entity = type.spawn(level, pos, MobSpawnType.SPAWNER);
            if (entity instanceof Monster monster) { monster.setTarget(player); }
            if (entity instanceof LivingEntity livingEntity) { livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, horde.glowDuration, 0, false, false, false)); }
            else if (entity != null) { entity.discard(); }
        }
    }

    private static EntityType<?> resolveType(String id) {
        ResourceLocation resourceLocation = ResourceLocation.tryParse(id);
        if (resourceLocation == null) { return null; }

        Optional<EntityType<?>> optional = BuiltInRegistries.ENTITY_TYPE.getOptional(resourceLocation);
        return optional.orElse(null);
    }

    private static BlockPos findSpawnPos(ServerLevel level, BlockPos center, int radius, RandomSource rand) {
        for (int i = 0; i < 12; ++i) {
            int dx = rand.nextInt(radius * 2 + 1) - radius;
            int dz = rand.nextInt(radius * 2 + 1) - radius;
            int x = center.getX() + dx;
            int z = center.getZ() + dz;
            if (dx * dx + dz * dz < 4) { continue; }

            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
            BlockPos pos = new BlockPos(x, y, z);
            if (level.getBlockState(pos).isAir() && level.getBlockState(pos.above()).isAir() && !level.getBlockState(pos.below()).isAir()) { return pos; }
        }

        return null;
    }
}