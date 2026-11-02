package dev.strangequark.variablecopperbulbsignal.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CopperBulbBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CopperBulbBlock.class)
public class BulbBlockMixin {

    @Inject(method = "getAnalogOutputSignal", at = @At("HEAD"), cancellable = true)
    public void overrideComparatorOutput(BlockState state, Level level, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(state.getLightEmission());
        cir.cancel();
    }
}

