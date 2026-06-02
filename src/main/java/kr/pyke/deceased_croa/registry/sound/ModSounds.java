package kr.pyke.deceased_croa.registry.sound;

import kr.pyke.deceased_croa.DeceasedCroa;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    private ModSounds() { }

    public static void register() {

    }

    private static SoundEvent register(String id) {
        ResourceLocation resourceLocation = new ResourceLocation(DeceasedCroa.MOD_ID, id);

        return Registry.register(BuiltInRegistries.SOUND_EVENT, resourceLocation, SoundEvent.createVariableRangeEvent(resourceLocation));
    }
}
