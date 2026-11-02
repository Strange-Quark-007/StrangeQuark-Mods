package dev.strangequark.revertzombiereinforcement.mixin;

import dev.strangequark.revertzombiereinforcement.CommonClass;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.zombie.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Zombie.class)
public class ZombieMixin {

    @Redirect(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/zombie/Zombie;getType()Lnet/minecraft/world/entity/EntityType;"))
    public EntityType<? extends Zombie> redirectGetType(Zombie zombie) {
        var level = zombie.level();

        if (!(level instanceof ServerLevel serverLevel)) {
            return zombie.getType();
        }

        if (!serverLevel.getGameRules().get(CommonClass.RULE_REVERT_ZOMBIE_REINFORCEMENT)) {
            return zombie.getType();
        }

        return EntityType.ZOMBIE;
    }
}

