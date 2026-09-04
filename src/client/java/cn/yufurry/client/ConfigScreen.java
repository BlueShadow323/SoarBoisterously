package cn.yufurry.client;

import cn.yufurry.client.config.ModConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.DoubleConsumer;

public class ConfigScreen extends Screen {

    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(Component.translatable("screen.soarboisterously.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        addRenderableWidget(Button.builder(toggleLabel(), button -> {
            ModConfig.INSTANCE.enabled = !ModConfig.INSTANCE.enabled;
            button.setMessage(toggleLabel());
        }).bounds(centerX - 100, 60, 200, 20).build());

        addRenderableWidget(Button.builder(fallDamageLabel(), button -> {
            ModConfig.INSTANCE.fallDamage = !ModConfig.INSTANCE.fallDamage;
            button.setMessage(fallDamageLabel());
        }).bounds(centerX - 100, 90, 200, 20).build());

        addRenderableWidget(Button.builder(volumeBarLabel(), button -> {
            ModConfig.INSTANCE.showVolumeBar = !ModConfig.INSTANCE.showVolumeBar;
            button.setMessage(volumeBarLabel());
        }).bounds(centerX - 100, 120, 200, 20).build());

        addRenderableWidget(new ValueSlider(centerX - 100, 150, 200, 20,
                "screen.soarboisterously.config.threshold",
                ModConfig.INSTANCE.threshold, 0.01, 0.50,
                v -> ModConfig.INSTANCE.threshold = v));

        addRenderableWidget(new ValueSlider(centerX - 100, 180, 200, 20,
                "screen.soarboisterously.config.lift",
                ModConfig.INSTANCE.maxLift, 0.1, 1.0,
                v -> ModConfig.INSTANCE.maxLift = v));

        addRenderableWidget(new ValueSlider(centerX - 100, 210, 200, 20,
                "screen.soarboisterously.config.sensitivity",
                ModConfig.INSTANCE.sensitivity, 1.0, 8.0,
                v -> ModConfig.INSTANCE.sensitivity = v));

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> onClose())
                .bounds(centerX - 100, this.height - 30, 200, 20).build());
    }

    private Component toggleLabel() {
        return Component.translatable("screen.soarboisterously.config.toggle",
                Component.translatable(ModConfig.INSTANCE.enabled
                        ? "screen.soarboisterously.config.on"
                        : "screen.soarboisterously.config.off"));
    }

    private Component fallDamageLabel() {
        return Component.translatable("screen.soarboisterously.config.fall_damage",
                Component.translatable(ModConfig.INSTANCE.fallDamage
                        ? "screen.soarboisterously.config.on"
                        : "screen.soarboisterously.config.off"));
    }

    private Component volumeBarLabel() {
        return Component.translatable("screen.soarboisterously.config.volume_bar",
                Component.translatable(ModConfig.INSTANCE.showVolumeBar
                        ? "screen.soarboisterously.config.on"
                        : "screen.soarboisterously.config.off"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        // Live readout so the player can tune the threshold while speaking
        String volumeText = Component.translatable("screen.soarboisterously.config.volume",
                String.format("%.3f", MicVolumeMonitor.get().getVolume())).getString();
        int textX = this.width / 2 - this.font.width(volumeText) / 2;
        graphics.text(this.font, volumeText, textX, 30, 0xFFFFFFFF, true);
    }

    @Override
    public void onClose() {
        ModConfig.save();
        if (this.minecraft != null) {
            //? if >=26.2 {
            this.minecraft.gui.setScreen(parent);
            //?} else {
            /*this.minecraft.setScreen(parent);
            *///?}
        }
    }

    private static class ValueSlider extends AbstractSliderButton {

        private final String labelKey;
        private final double min;
        private final double max;
        private final DoubleConsumer setter;

        ValueSlider(int x, int y, int width, int height, String labelKey,
                    double value, double min, double max, DoubleConsumer setter) {
            super(x, y, width, height, CommonComponents.EMPTY, (value - min) / (max - min));
            this.labelKey = labelKey;
            this.min = min;
            this.max = max;
            this.setter = setter;
            updateMessage();
        }

        private double currentValue() {
            return min + this.value * (max - min);
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.translatable(labelKey).append(": " + String.format("%.2f", currentValue())));
        }

        @Override
        protected void applyValue() {
            setter.accept(currentValue());
        }
    }
}
