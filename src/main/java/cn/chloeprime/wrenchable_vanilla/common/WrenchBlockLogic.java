package cn.chloeprime.wrenchable_vanilla.common;

import cn.chloeprime.wrenchable_vanilla.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class WrenchBlockLogic {
    public static final double CONTAINER_DROP_CAPTURE_DISTANCE = 1.5;
    public static final double CONTAINER_DROP_CAPTURE_DISTANCE_SQR = CONTAINER_DROP_CAPTURE_DISTANCE * CONTAINER_DROP_CAPTURE_DISTANCE;

    public static boolean isWrench(ItemStack stack) {
        return stack.is(WrenchTags.WRENCHES);
    }

    public static boolean isWrenchable(BlockState block) {
        return block.is(WrenchTags.WRENCHABLE);
    }

    public static WrenchMode getWrenchModeFor(BlockState block) {
        return block.is(WrenchTags.FORCE_PICK) ? WrenchMode.PICK_AND_SAVE : CommonConfig.getBlockMode();
    }

    @SuppressWarnings("ConstantValue")
    public static boolean checkBlockPermission(Player player, BlockPos pos) {
        var level = player.level();
        if (level == null) {
            return false;
        }
        return level.mayInteract(player, pos);
    }

    public static InteractionResult wrenchImpl(
            LivingEntity user,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        var level = user.level();
        var pos = hit.getBlockPos();
        var state = level.getBlockState(pos);
        var block = state.getBlock();
        var mode = getWrenchModeFor(state);

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            var loots = mode.getBlockDrops(serverLevel, hit, user, user.getItemInHand(hand));
            if (user instanceof Player player) {
                var inv = player.getInventory();
                for (var loot : loots) {
                    inv.placeItemBackInInventory(loot, true);
                }
            } else {
                for (var loot : loots) {
                    Containers.dropItemStack(serverLevel, user.getX(), user.getEyeY(), user.getZ(), loot);
                }
            }
        }

        WrenchContext.wrap(user, pos, mode, () -> {
            if (user instanceof Player player) {
                block.playerWillDestroy(level, pos, state, player);
            }
            level.removeBlock(pos, false);
            block.destroy(level, pos, state);
        });

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    public static boolean tryCaptureContainerDrop(Level level, Vec3 dropPos, ItemStack stack) {
        if (level.isClientSide()) {
            return false;
        }
        var context = WrenchContext.current().orElse(null);
        if (context != null && context.type() == WrenchContext.Type.BLOCK && context.userPlayer() != null) {
            var center = context.pos().getCenter();
            if (center.distanceToSqr(dropPos) <= WrenchBlockLogic.CONTAINER_DROP_CAPTURE_DISTANCE_SQR) {
                if (!context.mode().interceptContentDropping()) {
                    context.userPlayer().getInventory().placeItemBackInInventory(stack, true);
                }
                return true;
            }
        }
        return false;
    }

    private WrenchBlockLogic() {
    }
}
