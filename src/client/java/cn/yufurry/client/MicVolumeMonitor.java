package cn.yufurry.client;

import cn.yufurry.client.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public final class MicVolumeMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MicVolumeMonitor.class);

    // Mono 16-bit PCM from the system default microphone
    private static final AudioFormat FORMAT = new AudioFormat(44100f, 16, 1, true, false);
    private static final int BUFFER_SIZE = 4096;

    // One-pole high-pass coefficient: removes rumble below ~100 Hz (fans, HVAC, breath pops)
    private static final float HP_COEFF = 0.99f;

    // Noise floor rises very slowly toward the current level, but drops instantly,
    // so it always tracks the quietest background noise
    private static final float NOISE_FLOOR_RISE = 0.0005f;

    // Fast attack / slow release envelope: speech starts instantly,
    // pauses between words decay gently instead of cutting flight off
    private static final float ATTACK = 0.5f;
    private static final float RELEASE = 0.08f;

    private static MicVolumeMonitor instance;

    private Thread thread;
    private TargetDataLine line;
    private volatile float volume;
    private volatile boolean running;

    private float hpPrevIn;
    private float hpPrevOut;
    private float noiseFloor;
    private float envelope;

    private MicVolumeMonitor() {
    }

    public static MicVolumeMonitor get() {
        if (instance == null) {
            instance = new MicVolumeMonitor();
        }
        return instance;
    }

    // Smoothed, denoised mic volume in 0..1, refreshed roughly every ~23 ms
    public float getVolume() {
        return volume;
    }

    public synchronized void start() {
        if (running) {
            return;
        }
        running = true;
        resetFilters();
        thread = new Thread(this::run, "SoarBoisterously-Mic");
        thread.setDaemon(true);
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        // Closing the line unblocks the blocking read() in the capture loop
        if (line != null) {
            line.stop();
            line.close();
            line = null;
        }
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    private void resetFilters() {
        hpPrevIn = 0;
        hpPrevOut = 0;
        noiseFloor = 0;
        envelope = 0;
    }

    private void run() {
        try {
            line = AudioSystem.getTargetDataLine(FORMAT);
            line.open(FORMAT);
            line.start();
        } catch (LineUnavailableException | SecurityException e) {
            LOGGER.warn("Microphone unavailable, voice flight will stay inactive", e);
            line = null;
            return;
        }

        byte[] buffer = new byte[BUFFER_SIZE];
        while (running && line != null) {
            int read = line.read(buffer, 0, buffer.length);
            if (read <= 0) {
                continue;
            }

            float rms = processChunk(buffer, read);

            // Adaptive noise floor: subtract background noise so it never triggers flight
            if (rms < noiseFloor) {
                noiseFloor = rms;
            } else {
                noiseFloor += (rms - noiseFloor) * NOISE_FLOOR_RISE;
            }

            // Noise gate: anything barely above the floor is treated as silence
            float clean = Math.max(0.0f, rms - noiseFloor * 1.2f);
            float target = Math.min(1.0f, clean * (float) ModConfig.INSTANCE.sensitivity);

            // Asymmetric envelope smoothing for silky flight motion
            float factor = target > envelope ? ATTACK : RELEASE;
            envelope += (target - envelope) * factor;
            volume = envelope;
        }
    }

    // High-pass filters the chunk to strip low-frequency rumble, then returns its RMS in 0..1
    private float processChunk(byte[] data, int length) {
        long sum = 0;
        int count = 0;
        for (int i = 0; i + 1 < length; i += 2) {
            short raw = (short) ((data[i] & 0xFF) | (data[i + 1] << 8));
            float sample = raw / 32768.0f;

            // One-pole high-pass: y[n] = a * (y[n-1] + x[n] - x[n-1])
            float filtered = HP_COEFF * (hpPrevOut + sample - hpPrevIn);
            hpPrevIn = sample;
            hpPrevOut = filtered;

            sum += (long) (filtered * 32768.0f) * (long) (filtered * 32768.0f);
            count++;
        }
        if (count == 0) {
            return 0.0f;
        }
        return (float) (Math.sqrt((double) sum / count) / 32768.0);
    }
}
