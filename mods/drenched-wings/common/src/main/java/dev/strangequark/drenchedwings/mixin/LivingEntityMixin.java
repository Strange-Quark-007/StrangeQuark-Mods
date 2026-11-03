package dev.strangequark.drenchedwings.mixin;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "canGlide", at = @At("RETURN"), cancellable = true)
    private void drenchedwings$checkElytraCooldown(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            if ((Object) this instanceof Player player) {
                ItemStack elytraStack = player.getItemBySlot(EquipmentSlot.CHEST);
                if (player.getCooldowns().isOnCooldown(elytraStack)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
