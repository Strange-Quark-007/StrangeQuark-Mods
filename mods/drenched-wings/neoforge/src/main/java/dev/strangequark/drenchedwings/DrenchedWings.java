package dev.strangequark.drenchedwings;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleType;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(Constants.MOD_ID)
public class DrenchedWings {

    public DrenchedWings(IEventBus modEventBus) {
        Constants.LOG.info("Initializing {} for NeoForge", Constants.MOD_NAME);
        modEventBus.addListener(this::onRegister);

        NeoForge.EVENT_BUS.addListener(ServerTickEvent.Post.class, event -> {
            CommonClass.handleServerTick(event.getServer());
        });

        NeoForge.EVENT_BUS.addListener(PlayerInteractEvent.RightClickItem.class, event -> {
            Player player = event.getEntity();
            Level level = event.getLevel();
            if (CommonClass.shouldCancelRocketUse(player, level, event.getItemStack())) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.FAIL);
            }
        });
    }

    private void onRegister(RegisterEvent event) {
        event.register(Registries.GAME_RULE, helper -> {
            CommonClass.DO_ELYTRA_NERF = registerBoolean(helper, "do_elytra_nerf", true);
            CommonClass.DISABLE_ELYTRA_IN_RAIN = registerBoolean(helper, "disable_elytra_in_rain", true);
            CommonClass.DISABLE_ELYTRA_IN_WATER = registerBoolean(helper, "disable_elytra_in_water", true);
            CommonClass.DISABLE_ELYTRA_IN_LAVA = registerBoolean(helper, "disable_elytra_in_lava", true);
            CommonClass.DISABLE_ROCKETS_IN_RAIN = registerBoolean(helper, "disable_rockets_in_rain", true);
            CommonClass.APPLY_ELYTRA_COOLDOWN_FROM_SPLASH_WATER = registerBoolean(helper, "apply_elytra_cooldown_from_splash_water", true);
            CommonClass.ELYTRA_COOLDOWN_SECONDS = registerInteger(helper, "elytra_cooldown_seconds", 20, 0, 30);
        });
    }

    private static GameRule<Boolean> registerBoolean(RegisterEvent.RegisterHelper<GameRule<?>> helper, String name, boolean defaultValue) {
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
        helper.register(Identifier.fromNamespaceAndPath(Constants.MOD_ID, name), rule);
        return rule;
    }

    private static GameRule<Integer> registerInteger(RegisterEvent.RegisterHelper<GameRule<?>> helper, String name, int defaultValue, int min, int max) {
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
        helper.register(Identifier.fromNamespaceAndPath(Constants.MOD_ID, name), rule);
        return rule;
    }
}
