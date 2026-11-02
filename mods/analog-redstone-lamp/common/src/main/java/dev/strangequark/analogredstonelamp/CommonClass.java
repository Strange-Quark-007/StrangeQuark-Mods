package dev.strangequark.analogredstonelamp;

import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class CommonClass {

    public static final IntegerProperty ANALOG_LIGHT_LEVEL = IntegerProperty.create("level", 0, 15);

    public static void init() {
    }
}

