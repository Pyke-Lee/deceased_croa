package kr.pyke.deceased_croa.registry.item;

import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.registry.item.coin.Coin;
import kr.pyke.deceased_croa.registry.item.food.ModFoods;
import kr.pyke.deceased_croa.registry.item.food.galic_bread.GalicBread;
import kr.pyke.deceased_croa.registry.item.randombox.RandomBoxItem;
import kr.pyke.deceased_croa.registry.item.rune.ReturnRune;
import kr.pyke.deceased_croa.registry.item.rune.TeleportRune;
import kr.pyke.deceased_croa.registry.item.rune.WarpRune;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItems {
    public static final Item RANDOM_BOX = register("random_box", new RandomBoxItem(new Item.Properties().stacksTo(64)));
    public static final Item CROA_COIN = register("coin/croa", new Coin(new Item.Properties().stacksTo(64)));
    public static final Item GREEN_RUNE = register("rune/green", new WarpRune(new Item.Properties().stacksTo(64)));
    public static final Item RED_RUNE = register("rune/red", new ReturnRune(new Item.Properties().stacksTo(64)));
    public static final Item YELLOW_RUNE = register("rune/yellow", new TeleportRune(new Item.Properties().stacksTo(64)));
    public static final Item TICKET = register("ticket", new Item(new Item.Properties().stacksTo(64)));
    public static final Item GALIC_BREAD = register("food/galic_bread", new GalicBread(new Item.Properties().stacksTo(64).food(ModFoods.GALIC_BREAD)));

    public static final TagKey<Item> KEEP_ITEM = TagKey.create(Registries.ITEM, new ResourceLocation(DeceasedCroa.MOD_ID, "keep_item"));

    private ModItems() { }

    public static void register() { }

    private static Item register(String id, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(DeceasedCroa.MOD_ID, id), item);
    }
}