package dev.strangequark.mobcensus;

import dev.strangequark.mobcensus.input.KeybindHandler;
import dev.strangequark.mobcensus.scanner.MobCensusScanner;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class MobCensus implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Constants.LOG.info("Initializing {}", Constants.MOD_NAME);
        CommonClass.init();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            MobCensusScanner.tick();
            KeybindHandler.handleInput();
        });
    }
}
