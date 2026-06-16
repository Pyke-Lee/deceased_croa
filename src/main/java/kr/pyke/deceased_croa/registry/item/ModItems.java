package kr.pyke.deceased_croa.registry.item;

import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.registry.item.coin.Coin;
import kr.pyke.deceased_croa.registry.item.randombox.RandomBoxItem;
import kr.pyke.deceased_croa.registry.item.rune.ReturnRune;
import kr.pyke.deceased_croa.registry.item.rune.TeleportRune;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ModItems {
    public static final Item RANDOM_BOX = register("random_box", new RandomBoxItem(new Item.Properties().stacksTo(64)));
    public static final Item CROA_COIN = register("coin/croa", new Coin(new Item.Properties().stacksTo(64)));
    public static final Item TELEPORT_RUNE = register("rune/teleport", new TeleportRune(new Item.Properties().stacksTo(64)));
    public static final Item RETURN_RUNE = register("rune/return", new ReturnRune(new Item.Properties().stacksTo(64)));

    private ModItems() { }

    public static void register() { }

    private static Item register(String id, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(DeceasedCroa.MOD_ID, id), item);
    }
}