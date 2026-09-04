package cn.yufurry.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModConfig.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("soarboisterously.json");

    public static ModConfig INSTANCE = new ModConfig();

    public boolean enabled = true;

    // Whether the on-screen volume bar is drawn in the bottom-right corner
    public boolean showVolumeBar = true;

    // When false, fall damage is suppressed during and shortly after voice flight
    public boolean fallDamage = false;

    // Mic volume (0..1) above which voice flight activates
    public double threshold = 0.06;

    // Max upward velocity per tick (blocks/tick) at full volume, 0.4 ~= 8 blocks/s
    public double maxLift = 0.4;

    // Gain multiplier applied to the raw mic RMS before threshold comparison
    public double sensitivity = 3.0;

    public static void load() {
        if (Files.exists(PATH)) {
            try {
                INSTANCE = GSON.fromJson(Files.readString(PATH), ModConfig.class);
                if (INSTANCE == null) {
                    INSTANCE = new ModConfig();
                }
            } catch (Exception e) {
                LOGGER.warn("Failed to read config, using defaults", e);
                INSTANCE = new ModConfig();
            }
        }
    }

    public static void save() {
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(INSTANCE));
        } catch (Exception e) {
            LOGGER.warn("Failed to save config", e);
        }
    }
}
