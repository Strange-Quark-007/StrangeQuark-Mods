package dev.strangequark.analogredstonelamp.mixin;

import dev.strangequark.analogredstonelamp.CommonClass;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.ToIntFunction;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateLightMixin {

    @Redirect(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Ljava/util/function/ToIntFunction;applyAsInt(Ljava/lang/Object;)I")
    )
    private int redirectRedstoneLampLightLevel(ToIntFunction<BlockState> original, Object stateObj) {
        BlockState state = (BlockState) stateObj;
        Block block = state.getBlock();

        if (block instanceof RedstoneLampBlock && state.hasProperty(CommonClass.ANALOG_LIGHT_LEVEL)) {
            return state.getValue(CommonClass.ANALOG_LIGHT_LEVEL);
        }

        return original.applyAsInt(state);
    }
}
