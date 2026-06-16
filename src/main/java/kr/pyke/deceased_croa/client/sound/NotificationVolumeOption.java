package kr.pyke.deceased_croa.client.sound;

import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class NotificationVolumeOption {
    private NotificationVolumeOption() { }

    public static OptionInstance<Double> create() {
        return new OptionInstance<>(
            "soundCategory.deceased_croa.notification",
            OptionInstance.noTooltip(),
            (component, value) -> value == 0d ? genericValueLabel(component, CommonComponents.OPTION_OFF) : percentValueLabel(component, value),
            OptionInstance.UnitDouble.INSTANCE,
            (double) NotificationVolume.get(),
            value -> NotificationVolume.set(value.floatValue())
        );
    }

    private static Component percentValueLabel(Component text, double value) {
        return Component.translatable("options.percent_value", text, (int) (value * 100d));
    }

    private static Component genericValueLabel(Component text, Component value) {
        return Component.translatable("options.generic_value", text, value);
    }
}