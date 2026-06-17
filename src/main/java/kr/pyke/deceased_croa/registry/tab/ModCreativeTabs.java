package kr.pyke.deceased_croa.registry.tab;

import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.client.cache.ClientCache;
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
        new ResourceLocation(DeceasedCroa.MOD_ID, "random_box"),
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

    public static final CreativeModeTab GENERAL = Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        new ResourceLocation(DeceasedCroa.MOD_ID, "general"),
        FabricItemGroup.builder()
            .title(Component.literal("크로아"))
            .icon(() -> new ItemStack(ModItems.CROA_COIN))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.CROA_COIN);
                output.accept(ModItems.GALIC_BREAD);
            })
            .build()
    );

    public static final CreativeModeTab TELEPORT_RUNE = Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        new ResourceLocation(DeceasedCroa.MOD_ID, "teleport_rune"),
        FabricItemGroup.builder()
            .title(Component.literal("이동의 룬"))
            .icon(() -> new ItemStack(ModItems.TELEPORT_RUNE))
            .displayItems((parameters, output) -> {
                for (String id : ClientCache.getTeleportEntries().keySet()) {
                    ItemStack stack = new ItemStack(ModItems.TELEPORT_RUNE);
                    stack.getOrCreateTag().putString("teleport_id", id);

                    output.accept(stack);
                }
                output.accept(ModItems.RETURN_RUNE);
            })
            .build()
    );

    private ModCreativeTabs() { }

    public static void register() { }
}