package kr.pyke.deceased_croa.registry.tab;

import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.data.RandomBoxDefinition;
import kr.pyke.deceased_croa.manager.RandomBoxManager;
import kr.pyke.deceased_croa.registry.item.ModItems;
import kr.pyke.deceased_croa.registry.item.randombox.RandomBoxItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeTabs {
    public static final CreativeModeTab RANDOM_BOX = Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        new ResourceLocation(DeceasedCroa.MOD_ID, "random_box.json"),
        FabricItemGroup.builder()
            .title(Component.literal("랜덤 상자"))
            .icon(() -> new ItemStack(ModItems.RANDOM_BOX))
            .displayItems((parameters, output) -> {
                for (RandomBoxDefinition definition : RandomBoxManager.getDefinitions().values()) {
                    output.accept(RandomBoxItem.createStack(definition));
                }
            })
            .build()
    );

    private ModCreativeTabs() { }

    public static void register() { }
}