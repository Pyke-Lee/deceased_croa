package kr.pyke.deceased_croa.registry.item;

import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.registry.item.randombox.RandomBoxItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ModItems {
    public static final Item RANDOM_BOX = register("random_box", new RandomBoxItem(new Item.Properties().stacksTo(64)));

    private ModItems() { }

    public static void register() { }

    private static Item register(String id, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(DeceasedCroa.MOD_ID, id), item);
    }
}