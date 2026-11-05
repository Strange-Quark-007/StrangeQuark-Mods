package dev.strangequark.mobcensus.scanner;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;

public class MobClassifier {

    public static MobCategory classify(Entity entity) {
        if (!(entity instanceof Mob)) {
            return MobCategory.IGNORED;
        }

        if (entity instanceof Enemy) {
            return MobCategory.HOSTILE;
        }

        return MobCategory.PASSIVE;
    }
}

