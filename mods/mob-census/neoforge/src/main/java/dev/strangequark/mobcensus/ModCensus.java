package dev.strangequark.mobcensus;

import dev.strangequark.mobcensus.config.ClothConfigScreenFactory;
import dev.strangequark.mobcensus.input.KeybindHandler;
import dev.strangequark.mobcensus.scanner.MobCensusScanner;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Constants.MOD_ID)
public class ModCensus {

    public ModCensus(IEventBus modEventBus, ModContainer container) {
        Constants.LOG.info("Initializing {} for NeoForge", Constants.MOD_NAME);
        CommonClass.init();
        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) -> ClothConfigScreenFactory.create(parent));

        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, event -> {
            MobCensusScanner.tick();
            KeybindHandler.handleInput();
        });
    }
}
