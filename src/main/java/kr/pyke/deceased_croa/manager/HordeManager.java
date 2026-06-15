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

import java.util.List;
import java.util.Optional;

public class HordeManager {
    public static void spawnHorde(ServerLevel level, Player player, HORDE_TYPE hordeType) {
        HordeConfig config = ConfigLoader.getHordeConfig();

        int totalWeight;
        if (hordeType == HORDE_TYPE.NORMAL) { totalWeight = config.normal_hordeMobs.stream().mapToInt(mob -> mob.weight).sum(); }
        else { totalWeight = config.special_hordeMobs.stream().mapToInt(mob -> mob.weight).sum(); }
        if (totalWeight <= 0) { return; }

        RandomSource random = level.getRandom();

        for (int attempt = 0; attempt < config.spawnAttempts; ++attempt) {
            HordeConfig.MobEntry entry;
            if (hordeType == HORDE_TYPE.NORMAL) { entry = pickWeighted(config.normal_hordeMobs, random.nextInt(totalWeight)); }
            else { entry = pickWeighted(config.special_hordeMobs, random.nextInt(totalWeight)); }

            EntityType<?> type = resolveType(entry.entity);
            if (type == null) { continue; }

            BlockPos pos = findSpawnPos(level, player.blockPosition(), config.spawnRadius, random);
            if (pos == null) { continue; }

            int count = entry.min_spawns + (entry.max_spawns > entry.min_spawns ? random.nextInt(entry.max_spawns - entry.min_spawns + 1) : 0);
            for (int i = 0; i < count; ++i) {
                Entity entity = type.spawn(level, pos, MobSpawnType.SPAWNER);
                if (entity instanceof Monster monster) { monster.setTarget(player); }
                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, config.glowDuration, 0, false, false, false));
                }
                else if (entity != null) { entity.discard(); }
            }
        }
    }

    private static HordeConfig.MobEntry pickWeighted(List<HordeConfig.MobEntry> mobs, int roll) {
        int cursor = 0;
        for (var mob : mobs) {
            cursor += mob.weight;
            if (roll < cursor) { return mob; }
        }

        return mobs.get(mobs.size() - 1);
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

            if (level.getBlockState(pos).isAir() && level.getBlockState(pos.above()).isAir() && !level.getBlockState(pos.below()).isAir()) {
                return pos;
            }
        }

        return null;
    }
}
