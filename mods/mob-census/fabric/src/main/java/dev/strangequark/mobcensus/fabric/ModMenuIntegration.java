package dev.strangequark.mobcensus.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.strangequark.mobcensus.config.ClothConfigScreenFactory;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ClothConfigScreenFactory::create;
    }
}

