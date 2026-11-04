package dev.strangequark.mobcensus.input;

import dev.strangequark.mobcensus.config.ModConfig;
import net.minecraft.client.Minecraft;

public class KeybindHandler {

    private static boolean hostileWasDown = false;
    private static boolean passiveWasDown = false;

    public static void handleInput() {
        Minecraft client = Minecraft.getInstance();
        if (client.screen != null) {
            // Do not toggle while in chat, inventory, or menus
            return;
        }

        if (ModConfig.INSTANCE.hostileKey != null && !ModConfig.INSTANCE.hostileKey.isUnknown()) {
            boolean hostileDown = ModConfig.INSTANCE.hostileKey.matchesCurrentKey();
            if (hostileDown && !hostileWasDown) {
                ModConfig.INSTANCE.hostileEnabled = !ModConfig.INSTANCE.hostileEnabled;
                ModConfig.save();
            }
            hostileWasDown = hostileDown;
        }

        if (ModConfig.INSTANCE.passiveKey != null && !ModConfig.INSTANCE.passiveKey.isUnknown()) {
            boolean passiveDown = ModConfig.INSTANCE.passiveKey.matchesCurrentKey();
            if (passiveDown && !passiveWasDown) {
                ModConfig.INSTANCE.passiveEnabled = !ModConfig.INSTANCE.passiveEnabled;
                ModConfig.save();
            }
            passiveWasDown = passiveDown;
        }
    }
}

