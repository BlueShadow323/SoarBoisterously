package cn.yufurry.client;

import cn.yufurry.client.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class soarboisterouslyClient implements ClientModInitializer {

    public static final KeyMapping.Category KEY_CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("soarboisterously", "main"));

    private static KeyMapping openConfigKey;
    private static KeyMapping toggleFlightKey;

    @Override
    public void onInitializeClient() {
        ModConfig.load();
        MicVolumeMonitor.get().start();
        VoiceFlightHandler.register();

        openConfigKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.soarboisterously.open_config",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                KEY_CATEGORY
        ));

        toggleFlightKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.soarboisterously.toggle_flight",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                KEY_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openConfigKey.consumeClick()) {
                //? if >=26.2 {
                client.gui.setScreen(new ConfigScreen(null));
                //?} else {
                /*client.setScreen(new ConfigScreen(null));
                *///?}
            }
            while (toggleFlightKey.consumeClick()) {
                ModConfig.INSTANCE.enabled = !ModConfig.INSTANCE.enabled;
                ModConfig.save();
                if (client.player != null) {
                    boolean enabled = ModConfig.INSTANCE.enabled;
                    // Colored action bar text plus a distinct sound cue per state
                    client.player.sendOverlayMessage(Component.translatable(
                            "screen.soarboisterously.config.toggle",
                            Component.translatable(enabled
                                    ? "screen.soarboisterously.config.on"
                                    : "screen.soarboisterously.config.off"))
                            .withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED));
                    client.player.playSound(
                            enabled ? SoundEvents.EXPERIENCE_ORB_PICKUP : SoundEvents.FIRE_EXTINGUISH,
                            1.0f, enabled ? 1.5f : 0.8f);
                }
            }
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> MicVolumeMonitor.get().stop());
    }
}
