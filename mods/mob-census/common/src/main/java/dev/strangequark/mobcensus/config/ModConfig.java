package dev.strangequark.mobcensus.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.platform.InputConstants;
import dev.strangequark.mobcensus.Constants;
import me.shedaniel.clothconfig2.api.Modifier;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static ModConfig INSTANCE = new ModConfig();

    public static ModifierKeyCode defaultHostileKey() {
        return ModifierKeyCode.of(InputConstants.Type.KEYBOARD.getOrCreate(InputConstants.KEY_H), Modifier.of(true, false, false));
    }

    public static ModifierKeyCode defaultPassiveKey() {
        return ModifierKeyCode.of(InputConstants.Type.KEYBOARD.getOrCreate(InputConstants.KEY_N), Modifier.of(true, false, false));
    }

    // General
    public int detectionRadius = 64;
    public int scanIntervalTicks = 5;
    public boolean showNamedMobs = true;

    // Appearance
    public int bgOpacity = 50;
    public int hudScale = 85; // 85% compact scale
    public int xOffset = 4;
    public int yOffset = 0; // 0 = centered vertically on right edge

    // Active state (toggled via hotkeys)
    public boolean hostileEnabled = true;
    public boolean passiveEnabled = true;

    // Keybindings stored for JSON serialization
    public KeyConfig hostileKeyBinding = KeyConfig.from(defaultHostileKey());
    public KeyConfig passiveKeyBinding = KeyConfig.from(defaultPassiveKey());

    // Runtime ModifierKeyCode objects
    public transient ModifierKeyCode hostileKey = defaultHostileKey();
    public transient ModifierKeyCode passiveKey = defaultPassiveKey();

    public static class KeyConfig {
        public String key;
        public boolean alt;
        public boolean control;
        public boolean shift;

        public KeyConfig() {
        }

        public KeyConfig(String key, boolean alt, boolean control, boolean shift) {
            this.key = key;
            this.alt = alt;
            this.control = control;
            this.shift = shift;
        }

        public ModifierKeyCode toModifierKeyCode() {
            InputConstants.Key inputKey;
            try {
                if (key != null && !key.isEmpty()) {
                    inputKey = InputConstants.getKey(key);
                } else {
                    inputKey = InputConstants.UNKNOWN;
                }
            } catch (Exception e) {
                inputKey = InputConstants.UNKNOWN;
            }
            if (inputKey == InputConstants.UNKNOWN) {
                return ModifierKeyCode.unknown();
            }
            return ModifierKeyCode.of(inputKey, Modifier.of(alt, control, shift));
        }

        public static KeyConfig from(ModifierKeyCode mkc) {
            KeyConfig config = new KeyConfig();
            if (mkc != null && !mkc.isUnknown() && mkc.getKeyCode() != null) {
                config.key = mkc.getKeyCode().getName();
                if (mkc.getModifier() != null) {
                    config.alt = mkc.getModifier().hasAlt();
                    config.control = mkc.getModifier().hasControl();
                    config.shift = mkc.getModifier().hasShift();
                }
            } else {
                config.key = "key.keyboard.unknown";
            }
            return config;
        }
    }

    private static File getConfigFile() {
        File dir = new File("config");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, "mobcensus.json");
    }

    public static void load() {
        File file = getConfigFile();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                INSTANCE = GSON.fromJson(reader, ModConfig.class);
            } catch (Exception e) {
                Constants.LOG.error("Failed to load Mob Census config, using defaults", e);
                INSTANCE = new ModConfig();
            }
        } else {
            INSTANCE = new ModConfig();
            save();
        }

        if (INSTANCE.hostileKeyBinding != null) {
            INSTANCE.hostileKey = INSTANCE.hostileKeyBinding.toModifierKeyCode();
        }
        if (INSTANCE.hostileKey == null || INSTANCE.hostileKey.isUnknown()) {
            INSTANCE.hostileKey = defaultHostileKey();
        }

        if (INSTANCE.passiveKeyBinding != null) {
            INSTANCE.passiveKey = INSTANCE.passiveKeyBinding.toModifierKeyCode();
        }
        if (INSTANCE.passiveKey == null || INSTANCE.passiveKey.isUnknown()) {
            INSTANCE.passiveKey = defaultPassiveKey();
        }
    }

    public static void save() {
        if (INSTANCE.hostileKey != null && !INSTANCE.hostileKey.isUnknown()) {
            INSTANCE.hostileKeyBinding = KeyConfig.from(INSTANCE.hostileKey);
        }
        if (INSTANCE.passiveKey != null && !INSTANCE.passiveKey.isUnknown()) {
            INSTANCE.passiveKeyBinding = KeyConfig.from(INSTANCE.passiveKey);
        }

        File file = getConfigFile();
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            Constants.LOG.error("Failed to save Mob Census config", e);
        }
    }
}
