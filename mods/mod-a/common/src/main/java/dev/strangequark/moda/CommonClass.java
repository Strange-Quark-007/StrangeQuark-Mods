package dev.strangequark.moda;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;

public class CommonClass {

    public static void init() {
        Constants.LOG.info("Hello from Common init for {}!", Constants.MOD_NAME);
        Constants.LOG.info("The ID for diamonds is {}", BuiltInRegistries.ITEM.getKey(Items.DIAMOND));
    }
}
