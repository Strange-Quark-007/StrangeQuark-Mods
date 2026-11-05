package dev.strangequark.mobcensus.scanner;

import dev.strangequark.mobcensus.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MobCensusScanner {

    public record MobEntry(String name, int count, boolean isNamed) {
    }

    private record GroupKey(String name, boolean isNamed) {
    }

    public record CensusResult(
            List<MobEntry> hostileList,
            int totalHostile,
            List<MobEntry> passiveList,
            int totalPassive
    ) {
        public static final CensusResult EMPTY = new CensusResult(Collections.emptyList(), 0, Collections.emptyList(), 0);
    }

    public static CensusResult LATEST = CensusResult.EMPTY;
    private static int tickCounter = 0;

    public static void tick() {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.player == null) {
            LATEST = CensusResult.EMPTY;
            return;
        }

        tickCounter++;
        if (tickCounter >= ModConfig.INSTANCE.scanIntervalTicks) {
            tickCounter = 0;
            scan(client);
        }
    }

    private static void scan(Minecraft client) {
        ClientLevel level = client.level;
        LocalPlayer player = client.player;
        if (level == null || player == null) {
            LATEST = CensusResult.EMPTY;
            return;
        }

        double radius = ModConfig.INSTANCE.detectionRadius;
        double radiusSq = radius * radius;
        Vec3 playerPos = player.position();

        AABB aabb = new AABB(
                playerPos.x - radius, playerPos.y - radius, playerPos.z - radius,
                playerPos.x + radius, playerPos.y + radius, playerPos.z + radius
        );

        List<Mob> entities = level.getEntitiesOfClass(Mob.class, aabb, mob ->
                mob.isAlive() && mob.distanceToSqr(player) <= radiusSq
        );

        Map<GroupKey, Integer> hostileCounts = new HashMap<>();
        Map<GroupKey, Integer> passiveCounts = new HashMap<>();
        int hostileTotal = 0;
        int passiveTotal = 0;

        for (Mob mob : entities) {
            Component customName = mob.getCustomName();
            boolean isNamed = mob.hasCustomName() && customName != null;

            if (!ModConfig.INSTANCE.showNamedMobs && isNamed) {
                continue; // Exclude name-tagged mobs if user turned off the setting
            }

            MobCategory category = MobClassifier.classify(mob);
            if (category == MobCategory.IGNORED) {
                continue;
            }

            String displayName;

            if (isNamed) {
                displayName = customName.getString();
            } else {
                displayName = mob.getType().getDescription().getString();
            }

            GroupKey key = new GroupKey(displayName, isNamed);

            if (category == MobCategory.HOSTILE) {
                hostileCounts.merge(key, 1, Integer::sum);
                hostileTotal++;
            } else if (category == MobCategory.PASSIVE) {
                passiveCounts.merge(key, 1, Integer::sum);
                passiveTotal++;
            }
        }

        List<MobEntry> hostileList = hostileCounts.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .map(e -> new MobEntry(e.getKey().name(), e.getValue(), e.getKey().isNamed()))
                .collect(Collectors.toList());

        List<MobEntry> passiveList = passiveCounts.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .map(e -> new MobEntry(e.getKey().name(), e.getValue(), e.getKey().isNamed()))
                .collect(Collectors.toList());

        LATEST = new CensusResult(hostileList, hostileTotal, passiveList, passiveTotal);
    }
}
