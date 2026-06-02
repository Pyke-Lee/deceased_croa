package kr.pyke.deceased_croa.registry.menu;

import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.client.gui.menu.MailboxMenu;
import kr.pyke.deceased_croa.client.gui.menu.SendMailboxMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class ModMenus {
    public static final MenuType<MailboxMenu> MAILBOX = Registry.register(BuiltInRegistries.MENU, new ResourceLocation(DeceasedCroa.MOD_ID, "mailbox"), new MenuType<>(MailboxMenu::new, FeatureFlags.VANILLA_SET));
    public static final MenuType<SendMailboxMenu> SEND_MAILBOX = Registry.register(BuiltInRegistries.MENU, new ResourceLocation(DeceasedCroa.MOD_ID, "mailbox_send"), new MenuType<>(SendMailboxMenu::new, FeatureFlags.VANILLA_SET));

    private ModMenus() { }

    public static void register() {

    }
}
