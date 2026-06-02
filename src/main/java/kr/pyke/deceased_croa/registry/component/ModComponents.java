package kr.pyke.deceased_croa.registry.component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.registry.component.info.DeceasedInfo;
import kr.pyke.deceased_croa.registry.component.info.IDeceasedInfo;
import kr.pyke.deceased_croa.registry.component.mailbox.IMailbox;
import kr.pyke.deceased_croa.registry.component.mailbox.Mailbox;
import net.minecraft.resources.ResourceLocation;

public class ModComponents implements EntityComponentInitializer {
    public static final ComponentKey<IDeceasedInfo> DECEASED_INFO = ComponentRegistry.getOrCreate(new ResourceLocation(DeceasedCroa.MOD_ID, "deceased_info"), IDeceasedInfo.class);
    public static final ComponentKey<IMailbox> MAILBOX = ComponentRegistry.getOrCreate(new ResourceLocation(DeceasedCroa.MOD_ID, "mailbox"), IMailbox.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(DECEASED_INFO, DeceasedInfo::new, RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(MAILBOX, Mailbox::new, RespawnCopyStrategy.ALWAYS_COPY);
    }
}
