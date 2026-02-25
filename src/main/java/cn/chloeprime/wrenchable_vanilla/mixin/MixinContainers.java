package cn.chloeprime.wrenchable_vanilla.mixin;

import cn.chloeprime.wrenchable_vanilla.common.WrenchBlockLogic;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Containers.class)
public class MixinContainers {
    @Inject(method = "dropItemStack", at = @At("HEAD"), cancellable = true)
    private static void captureDropWhenWrenching(Level level, double x, double y, double z, ItemStack stack, CallbackInfo ci) {
        if (WrenchBlockLogic.tryCaptureContainerDrop(level, new Vec3(x, y, z), stack)) {
            ci.cancel();
        }
    }
}
