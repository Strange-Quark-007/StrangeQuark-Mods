package dev.strangequark.allayitemreservation.mixin;

import dev.strangequark.allayitemreservation.ItemReservations;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.NearestItemSensor;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Mixin(NearestItemSensor.class)
public class NearestItemSensorMixin {

    @Inject(method = "doTick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Mob;)V", at = @At("HEAD"), cancellable = true)
    private void allayitemreservation$onTick(ServerLevel level, Mob body, CallbackInfo ci) {
        if (!(body instanceof Allay)) {
            return;
        }

        Brain<?> brain = body.getBrain();
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, body.getBoundingBox().inflate(32.0, 16.0, 32.0), (item) -> true);
        items.sort(Comparator.comparingDouble(body::distanceToSqr));
        Optional<ItemEntity> nearestAvailableItem = items.stream()
                .filter(itemEntity -> body.wantsToPickUp(level, itemEntity.getItem()))
                .filter(itemEntity -> itemEntity.closerThan(body, 32.0))
                .filter(body::hasLineOfSight)
                .filter(item -> !ItemReservations.isReservedByOther(level, item.getUUID(), body.getUUID()))
                .findFirst();

        allayitemreservation$updateReservation(brain, body, nearestAvailableItem.orElse(null));
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, nearestAvailableItem);

        ci.cancel();
    }

    @Unique
    private void allayitemreservation$updateReservation(Brain<?> brain, Mob body, ItemEntity target) {
        Optional<ItemEntity> previous = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);

        if (previous.isPresent() && previous.get() != target) {
            ItemReservations.release(previous.get().getUUID(), body.getUUID());
        }

        if (target != null) {
            ItemReservations.reserve(target.getUUID(), body.getUUID());
        }
    }
}
