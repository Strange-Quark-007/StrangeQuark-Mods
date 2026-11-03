package dev.strangequark.drenchedwings;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleType;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;

public class DrenchedWings implements ModInitializer {

    @Override
    public void onInitialize() {
        Constants.LOG.info("Initializing {}", Constants.MOD_NAME);

        CommonClass.DO_ELYTRA_NERF = registerBoolean("do_elytra_nerf", true);
        CommonClass.DISABLE_ELYTRA_IN_RAIN = registerBoolean("disable_elytra_in_rain", true);
        CommonClass.DISABLE_ELYTRA_IN_WATER = registerBoolean("disable_elytra_in_water", true);
        CommonClass.DISABLE_ELYTRA_IN_LAVA = registerBoolean("disable_elytra_in_lava", true);
        CommonClass.DISABLE_ROCKETS_IN_RAIN = registerBoolean("disable_rockets_in_rain", true);
        CommonClass.APPLY_ELYTRA_COOLDOWN_FROM_SPLASH_WATER = registerBoolean("apply_elytra_cooldown_from_splash_water", true);
        CommonClass.ELYTRA_COOLDOWN_SECONDS = registerInteger("elytra_cooldown_seconds", 20, 0, 30);

        CommonClass.init();

        ServerTickEvents.END_SERVER_TICK.register(CommonClass::handleServerTick);

        UseItemCallback.EVENT.register((player, level, interactionHand) -> {
            if (CommonClass.shouldCancelRocketUse(player, level, player.getItemInHand(interactionHand))) {
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });
    }

    private static GameRule<Boolean> registerBoolean(String name, boolean defaultValue) {
        GameRule<Boolean> rule = new GameRule<>(
                GameRuleCategory.PLAYER,
                GameRuleType.BOOL,
                BoolArgumentType.bool(),
                GameRuleTypeVisitor::visitBoolean,
                Codec.BOOL,
                value -> value ? 1 : 0,
                defaultValue,
                FeatureFlagSet.of()
        );
        return Registry.register(BuiltInRegistries.GAME_RULE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name), rule);
    }

    private static GameRule<Integer> registerInteger(String name, int defaultValue, int min, int max) {
        GameRule<Integer> rule = new GameRule<>(
                GameRuleCategory.PLAYER,
                GameRuleType.INT,
                IntegerArgumentType.integer(min, max),
                GameRuleTypeVisitor::visitInteger,
                Codec.intRange(min, max),
                Integer::intValue,
                defaultValue,
                FeatureFlagSet.of()
        );
        return Registry.register(BuiltInRegistries.GAME_RULE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name), rule);
    }
}
