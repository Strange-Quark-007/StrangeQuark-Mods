package dev.strangequark.revertzombiereinforcement;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleType;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;

public class CommonClass {

    public static GameRule<Boolean> RULE_REVERT_ZOMBIE_REINFORCEMENT;

    public static void init() {
        RULE_REVERT_ZOMBIE_REINFORCEMENT = Registry.register(
                BuiltInRegistries.GAME_RULE,
                "revert_zombie_reinforcement",
                new GameRule<>(
                        GameRuleCategory.MOBS,
                        GameRuleType.BOOL,
                        BoolArgumentType.bool(),
                        GameRuleTypeVisitor::visitBoolean,
                        Codec.BOOL,
                        value -> value ? 1 : 0,
                        true,
                        FeatureFlagSet.of()
                )
        );
    }
}
