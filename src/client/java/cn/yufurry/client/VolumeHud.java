package cn.yufurry.client;

import cn.yufurry.client.config.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public class VolumeHud {

    private static final int WIDTH = 6;
    private static final int HEIGHT = 96;
    private static final int MARGIN = 8;
    private static final int COLOR_BACKGROUND = 0x90000000;
    private static final int COLOR_QUIET = 0xFF606060;
    private static final int COLOR_ACTIVE = 0xFF3DDC84;
    private static final int COLOR_THRESHOLD = 0xFFFF5555;

    public static void register() {
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("soarboisterously", "volume_bar"), VolumeHud::extract);
    }

    private static void extract(GuiGraphicsExtractor graphics, DeltaTracker delta) {
        if (!ModConfig.INSTANCE.enabled || !ModConfig.INSTANCE.showVolumeBar) {
            return;
        }

        int x = graphics.guiWidth() - MARGIN - WIDTH;
        int y = graphics.guiHeight() - MARGIN - HEIGHT;

        graphics.fill(x - 1, y - 1, x + WIDTH + 1, y + HEIGHT + 1, COLOR_BACKGROUND);

        float volume = MicVolumeMonitor.get().getVolume();
        int fill = (int) (volume * HEIGHT);
        if (fill > 0) {
            int color = volume > ModConfig.INSTANCE.threshold ? COLOR_ACTIVE : COLOR_QUIET;
            graphics.fill(x, y + HEIGHT - fill, x + WIDTH, y + HEIGHT, color);
        }

        int thresholdY = y + HEIGHT - (int) (ModConfig.INSTANCE.threshold * HEIGHT);
        graphics.fill(x - 1, thresholdY, x + WIDTH + 1, thresholdY + 1, COLOR_THRESHOLD);
    }
}
