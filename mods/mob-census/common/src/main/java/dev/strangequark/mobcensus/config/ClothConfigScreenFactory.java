package dev.strangequark.mobcensus.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClothConfigScreenFactory {

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.mobcensus.title"));

        builder.setSavingRunnable(ModConfig::save);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // General Category
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.mobcensus.category.general"));

        // Detection Radius in clean steps of 4 blocks (4 to 128)
        general.addEntry(entryBuilder.startIntSlider(
                        Component.translatable("config.mobcensus.detection_radius"),
                        Math.clamp(ModConfig.INSTANCE.detectionRadius / 4, 1, 32), 1, 32)
                .setDefaultValue(16) // 16 * 4 = 64 blocks
                .setTextGetter(val -> Component.literal((val * 4) + " blocks"))
                .setTooltip(Component.translatable("config.mobcensus.detection_radius.tooltip"))
                .setSaveConsumer(val -> ModConfig.INSTANCE.detectionRadius = val * 4)
                .build());

        general.addEntry(entryBuilder.startIntSlider(
                        Component.translatable("config.mobcensus.scan_interval"),
                        ModConfig.INSTANCE.scanIntervalTicks, 1, 60)
                .setDefaultValue(5)
                .setTextGetter(val -> Component.literal(val + " ticks"))
                .setTooltip(Component.translatable("config.mobcensus.scan_interval.tooltip"))
                .setSaveConsumer(val -> ModConfig.INSTANCE.scanIntervalTicks = val)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("config.mobcensus.show_named_mobs"),
                        ModConfig.INSTANCE.showNamedMobs)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.mobcensus.show_named_mobs.tooltip"))
                .setSaveConsumer(val -> ModConfig.INSTANCE.showNamedMobs = val)
                .build());

        // Appearance Category
        ConfigCategory appearance = builder.getOrCreateCategory(Component.translatable("config.mobcensus.category.appearance"));

        // Background Opacity in clean steps of 5% (0% to 100%)
        appearance.addEntry(entryBuilder.startIntSlider(
                        Component.translatable("config.mobcensus.bg_opacity"),
                        Math.clamp(ModConfig.INSTANCE.bgOpacity / 5, 0, 20), 0, 20)
                .setDefaultValue(10) // 10 * 5 = 50%
                .setTextGetter(val -> Component.literal((val * 5) + "%"))
                .setTooltip(Component.translatable("config.mobcensus.bg_opacity.tooltip"))
                .setSaveConsumer(val -> ModConfig.INSTANCE.bgOpacity = val * 5)
                .build());

        // HUD Scale in clean steps of 5% (50% to 150%)
        appearance.addEntry(entryBuilder.startIntSlider(
                        Component.translatable("config.mobcensus.hud_scale"),
                        Math.clamp(ModConfig.INSTANCE.hudScale / 5, 10, 30), 10, 30)
                .setDefaultValue(17) // 17 * 5 = 85%
                .setTextGetter(val -> Component.literal((val * 5) + "%"))
                .setTooltip(Component.translatable("config.mobcensus.hud_scale.tooltip"))
                .setSaveConsumer(val -> ModConfig.INSTANCE.hudScale = val * 5)
                .build());

        appearance.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.mobcensus.x_offset"),
                        ModConfig.INSTANCE.xOffset)
                .setDefaultValue(4)
                .setMin(0)
                .setMax(500)
                .setTooltip(Component.translatable("config.mobcensus.x_offset.tooltip"))
                .setSaveConsumer(val -> ModConfig.INSTANCE.xOffset = val)
                .build());

        appearance.addEntry(entryBuilder.startIntField(
                        Component.translatable("config.mobcensus.y_offset"),
                        ModConfig.INSTANCE.yOffset)
                .setDefaultValue(0)
                .setMin(-500)
                .setMax(500)
                .setTooltip(Component.translatable("config.mobcensus.y_offset.tooltip"))
                .setSaveConsumer(val -> ModConfig.INSTANCE.yOffset = val)
                .build());

        // Keybinds Category
        ConfigCategory keybinds = builder.getOrCreateCategory(Component.translatable("config.mobcensus.category.keybinds"));

        keybinds.addEntry(entryBuilder.startModifierKeyCodeField(
                        Component.translatable("config.mobcensus.key_toggle_hostile"),
                        ModConfig.INSTANCE.hostileKey)
                .setDefaultValue(ModConfig.defaultHostileKey())
                .setTooltip(Component.translatable("config.mobcensus.key_toggle_hostile.tooltip"))
                .setModifierSaveConsumer(val -> ModConfig.INSTANCE.hostileKey = val)
                .build());

        keybinds.addEntry(entryBuilder.startModifierKeyCodeField(
                        Component.translatable("config.mobcensus.key_toggle_passive"),
                        ModConfig.INSTANCE.passiveKey)
                .setDefaultValue(ModConfig.defaultPassiveKey())
                .setTooltip(Component.translatable("config.mobcensus.key_toggle_passive.tooltip"))
                .setModifierSaveConsumer(val -> ModConfig.INSTANCE.passiveKey = val)
                .build());

        return builder.build();
    }
}
