package dev.strangequark.variablecopperbulbsignal;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class VariableCopperBulbSignal {

    public VariableCopperBulbSignal(IEventBus eventBus) {
        CommonClass.init();
    }
}

