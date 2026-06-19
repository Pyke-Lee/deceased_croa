package kr.pyke.deceased_croa.registry.sound;

import kr.pyke.deceased_croa.DeceasedCroa;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static final SoundEvent FANFARE = register("custom_sound.fanfare");
    public static final SoundEvent NORMAL_HORDES = register("custom_sound.normal_hordes");
    public static final SoundEvent ANC_SERVER = register("custom_sound.anc.server");
    public static final SoundEvent ANC_BROADCAST = register("custom_sound.anc.broadcast");
    public static final SoundEvent BOX_OPEN = register("custom_sound.box.open");
    public static final SoundEvent BOX_RIDE = register("custom_sound.box.ride");
    public static final SoundEvent YAR = register("custom_sound.yar");
    public static final SoundEvent KANGKANG = register("custom_sound.kangkang");
    public static final SoundEvent YEAH = register("custom_sound.yeah");
    public static final SoundEvent PEEP = register("custom_sound.peep");
    public static final SoundEvent MAGIC = register("custom_sound.magic");
    public static final SoundEvent KKOKKIO = register("custom_sound.kkokkio");
    public static final SoundEvent SIREN_0 = register("custom_sound.siren.0");
    public static final SoundEvent HORDE_SPAWN = register("custom_sound.horde_spawn");
    public static final SoundEvent HORDE = register("custom_sound.horde");
    public static final SoundEvent ACAU = register("custom_sound.bjacau");
    public static final SoundEvent MANDEUK = register("custom_sound.esstree");
    public static final SoundEvent PINGMAN = register("custom_sound.pingman");
    public static final SoundEvent JUNGRYEOK = register("custom_sound.jungryeok");
    public static final SoundEvent NUBUL = register("custom_sound.nubul");
    public static final SoundEvent SUNING = register("custom_sound.suning");
    public static final SoundEvent LITA = register("custom_sound.lita");
    public static final SoundEvent GARRY = register("custom_sound.garry");
    public static final SoundEvent COIN = register("custom_sound.coin");

    private ModSounds() { }

    public static void register() {

    }

    private static SoundEvent register(String id) {
        ResourceLocation resourceLocation = new ResourceLocation(DeceasedCroa.MOD_ID, id);

        return Registry.register(BuiltInRegistries.SOUND_EVENT, resourceLocation, SoundEvent.createVariableRangeEvent(resourceLocation));
    }
}
