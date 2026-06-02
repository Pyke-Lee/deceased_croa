package kr.pyke.deceased_croa.registry.mob_effect;

import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.registry.mob_effect.aggro.AggroEffect;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class ModEffects {
    public static MobEffect AGGRO = register("aggro", new AggroEffect(MobEffectCategory.HARMFUL, 0xFF0000));

    private ModEffects() { }

    public static void register() {

    }

    private static MobEffect register(String key, MobEffect effect) {
        return Registry.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(DeceasedCroa.MOD_ID, key), effect);
    }
}
