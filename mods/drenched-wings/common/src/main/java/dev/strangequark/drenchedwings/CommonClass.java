package dev.strangequark.drenchedwings;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRule;

public class CommonClass {

    public static GameRule<Boolean> DO_ELYTRA_NERF;
    public static GameRule<Boolean> DISABLE_ELYTRA_IN_RAIN;
    public static GameRule<Boolean> DISABLE_ELYTRA_IN_WATER;
    public static GameRule<Boolean> DISABLE_ELYTRA_IN_LAVA;
    public static GameRule<Boolean> DISABLE_ROCKETS_IN_RAIN;
    public static GameRule<Boolean> APPLY_ELYTRA_COOLDOWN_FROM_SPLASH_WATER;
    public static GameRule<Integer> ELYTRA_COOLDOWN_SECONDS;

    public static void init() {
    }

    public static void handleServerTick(MinecraftServer server) {
        if (!server.getGameRules().get(DO_ELYTRA_NERF)) {
            return;
        }

        int cooldownSeconds = Math.clamp(server.getGameRules().get(ELYTRA_COOLDOWN_SECONDS), 0, 30);
        int cooldownTicks = cooldownSeconds * 20;

        boolean disableRain = server.getGameRules().get(DISABLE_ELYTRA_IN_RAIN);
        boolean disableWater = server.getGameRules().get(DISABLE_ELYTRA_IN_WATER);
        boolean disableLava = server.getGameRules().get(DISABLE_ELYTRA_IN_LAVA);
        boolean isOverworldRaining = server.overworld().isRaining();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (disableRain && isOverworldRaining && server.overworld().canSeeSky(player.blockPosition())) {
                player.getCooldowns().addCooldown(new ItemStack(Items.ELYTRA), cooldownTicks);
            }
            if (disableWater && player.isInWater()) {
                player.getCooldowns().addCooldown(new ItemStack(Items.ELYTRA), cooldownTicks);
            }
            if (disableLava && player.isInLava()) {
                player.getCooldowns().addCooldown(new ItemStack(Items.ELYTRA), cooldownTicks);
            }
        }
    }

    public static boolean shouldCancelRocketUse(Player player, Level level, ItemStack stack) {
        if (!(level instanceof ServerLevel world) || !world.getGameRules().get(DO_ELYTRA_NERF)) {
            return false;
        }
        if (stack.getItem() == Items.FIREWORK_ROCKET) {
            return world.getGameRules().get(DISABLE_ROCKETS_IN_RAIN) && world.isRaining() && world.canSeeSky(player.blockPosition());
        }
        return false;
    }
}
