package cn.chloeprime.wrenchable_vanilla.mixin.client;

import cn.chloeprime.wrenchable_vanilla.client.ClientConfig;
import cn.chloeprime.wrenchable_vanilla.common.WrenchContext;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {
    @ModifyExpressionValue(
            method = "levelEvent",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SoundType;getBreakSound()Lnet/minecraft/sounds/SoundEvent;"))
    private SoundEvent modifyWrenchBreakSound(SoundEvent original, int id, BlockPos pos, int data) {
        var context = WrenchContext.current().orElse(null);
        if (context == null || context.type() != WrenchContext.Type.BLOCK || !Objects.equals(pos, context.pos())) {
            return original;
        }
        var configured = ClientConfig.getBreakBlockSound().orElse(null);
        if (configured == null) {
            return original;
        }
        return configured.get();
    }

    @ModifyExpressionValue(
            method = "levelEvent",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SoundType;getPitch()F"))
    private float modifyWrenchBreakSoundPitch(float original, int id, BlockPos pos, int data) {
        var context = WrenchContext.current().orElse(null);
        if (context == null || context.type() != WrenchContext.Type.BLOCK || !Objects.equals(pos, context.pos())) {
            return original;
        }
        var configured = ClientConfig.getBreakBlockSound().orElse(null);
        if (configured == null) {
            return original;
        }
        return 1 / 0.8F;
    }

    @WrapOperation(
            method = "levelEvent",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;addDestroyBlockEffect(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private void hideBreakParticleIfConfigured(ClientLevel level, BlockPos pos, BlockState state, Operation<Void> original) {
        boolean exec;
        var context = WrenchContext.current().orElse(null);
        if (context == null || context.type() != WrenchContext.Type.BLOCK || !Objects.equals(pos, context.pos())) {
            exec = true;
        } else {
            exec = !ClientConfig.hideBreakParticle();
        }
        if (exec) {
            original.call(level, pos, state);
        }
    }
}
