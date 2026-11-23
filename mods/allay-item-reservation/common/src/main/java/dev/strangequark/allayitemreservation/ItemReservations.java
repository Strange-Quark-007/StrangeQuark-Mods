package dev.strangequark.allayitemreservation;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ItemReservations {

    private static final Map<UUID, UUID> RESERVATIONS = new ConcurrentHashMap<>();

    private ItemReservations() {
    }

    public static boolean isReservedByOther(ServerLevel level, UUID itemId, UUID allayId) {
        UUID ownerId = RESERVATIONS.get(itemId);

        if (ownerId == null || ownerId.equals(allayId)) {
            return false;
        }

        Entity ownerEntity = level.getEntity(ownerId);

        if (ownerEntity == null || ownerEntity.isRemoved() || !(ownerEntity instanceof Allay allayEntity)) {
            RESERVATIONS.remove(itemId, ownerId);
            return false;
        }

        Optional<ItemEntity> currentTarget = allayEntity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);

        if (currentTarget.isEmpty() || !currentTarget.get().getUUID().equals(itemId)) {
            RESERVATIONS.remove(itemId, ownerId);
            return false;
        }

        return true;
    }

    public static void reserve(UUID itemId, UUID allayId) {
        RESERVATIONS.put(itemId, allayId);
    }

    public static void release(UUID itemId, UUID allayId) {
        RESERVATIONS.remove(itemId, allayId);
    }
}
