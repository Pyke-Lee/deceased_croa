package kr.pyke.deceased_croa.registry.item.chargeable;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import org.jetbrains.annotations.NotNull;

public class Chargeable extends Item {
    public Chargeable(Properties properties) {
        super(properties);
    }

    @Override public @NotNull UseAnim getUseAnimation(ItemStack stack) { return UseAnim.BOW; }

    @Override public int getUseDuration(ItemStack stack) { return 100; }
}
