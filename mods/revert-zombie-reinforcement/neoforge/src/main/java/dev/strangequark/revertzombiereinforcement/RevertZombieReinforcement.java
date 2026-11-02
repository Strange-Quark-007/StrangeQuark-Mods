package dev.strangequark.revertzombiereinforcement;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleType;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(Constants.MOD_ID)
public class RevertZombieReinforcement {

    public RevertZombieReinforcement(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegister);
    }

    private void onRegister(RegisterEvent event) {
        event.register(Registries.GAME_RULE, helper -> {
            CommonClass.RULE_REVERT_ZOMBIE_REINFORCEMENT = new GameRule<>(
                    GameRuleCategory.MOBS,
                    GameRuleType.BOOL,
                    BoolArgumentType.bool(),
                    GameRuleTypeVisitor::visitBoolean,
                    Codec.BOOL,
                    value -> value ? 1 : 0,
                    true,
                    FeatureFlagSet.of()
            );

            helper.register(Identifier.parse("revert_zombie_reinforcement"), CommonClass.RULE_REVERT_ZOMBIE_REINFORCEMENT);
        });
    }
}

