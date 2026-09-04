package cn.yufurry.client;

import cn.yufurry.client.config.ModConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

public class VoiceFlightHandler {

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            LocalPlayer player = client.player;
            ModConfig config = ModConfig.INSTANCE;
            if (player == null || !player.isAlive() || player.isPassenger() || !config.enabled) {
                return;
            }

            // Suppress fall damage on every tick while falling, not only while ascending,
            // otherwise damage accumulates again as soon as the voice drops below the threshold
            if (!config.fallDamage) {
                player.fallDistance = 0;
            }

            float volume = MicVolumeMonitor.get().getVolume();
            if (volume <= config.threshold) {
                return;
            }

            // Louder voice = faster ascent, linearly scaled between threshold and maxLift
            double strength = (volume - config.threshold) / (1.0 - config.threshold);
            double lift = Math.min(strength, 1.0) * config.maxLift;

            // Interpolate toward the target lift so vertical speed changes smoothly
            Vec3 velocity = player.getDeltaMovement();
            double newY = velocity.y + (lift - velocity.y) * 0.35;
            player.setDeltaMovement(velocity.x, newY, velocity.z);
        });
    }
}
