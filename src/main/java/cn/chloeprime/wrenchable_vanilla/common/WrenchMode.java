package cn.chloeprime.wrenchable_vanilla.common;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PlayerHeadBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public enum WrenchMode {
    /**
     * Break the block, its content will drop all over the ground :P
     */
    BREAK(false) {
        @Override
        public List<ItemStack> getBlockDrops(ServerLevel level, BlockHitResult hit, @Nullable Entity user, @Nullable ItemStack tool) {
            var pos = hit.getBlockPos();
            var state = level.getBlockState(pos);
            var be = level.getBlockEntity(pos);
            if (state.getBlock() instanceof PlayerHeadBlock) {
                return PICK_AND_SAVE.getBlockDrops(level, hit, user, tool);
            }
            ItemStack actualTool;
            if (tool == null) {
                if (user instanceof LivingEntity miner) {
                    actualTool = miner.getItemInHand(miner.getUsedItemHand());
                } else {
                    actualTool = ItemStack.EMPTY;
                }
            } else {
                actualTool = tool;
            }
            return Block.getDrops(state, level, pos, be, user, actualTool);
        }

        @Override
        public List<ItemStack> getEntityDrops(ServerLevel level, Entity target, @Nullable Entity user, @Nullable ItemStack tool) {
            // 这个扳手主要的设计目标：非战斗用途机器实体，使用的掉落方法千奇百怪，无统一 API，
            // 所以不在这里捕获掉落。
            return List.of();
        }
    },

    /**
     * Keep block entity NBT,
     * just like Ctrl + middle click in creative mode.
     */
    PICK_AND_SAVE(true) {
        @Override
        @SuppressWarnings("deprecation")
        public List<ItemStack> getBlockDrops(ServerLevel level, BlockHitResult hit, @Nullable Entity user, @Nullable ItemStack tool) {
            var pos = hit.getBlockPos();
            var state = level.getBlockState(pos);
            var be = level.getBlockEntity(pos);
            var result = user instanceof Player player
                    ? state.getCloneItemStack(hit, level, pos, player)
                    : state.getBlock().getCloneItemStack(level, pos, state);
            if (be != null) {
                saveBlockEntityData(result, be);
            }
            return List.of(result);
        }

        @Override
        public List<ItemStack> getEntityDrops(ServerLevel level, Entity target, @Nullable Entity user, @Nullable ItemStack tool) {
            var picked = target.getPickResult();
            return picked == null ? List.of() : List.of(picked);
        }
    };

    private final boolean interceptContentDropping;

    WrenchMode(boolean interceptContentDropping) {
        this.interceptContentDropping = interceptContentDropping;
    }

    /**
     * Get the loot result of this wrench operation
     * @return The looted item, does not include dropped container content.
     */
    public abstract List<ItemStack> getBlockDrops(ServerLevel level, BlockHitResult hit, @Nullable Entity user, @Nullable ItemStack tool);
    public abstract List<ItemStack> getEntityDrops(ServerLevel level, Entity target, @Nullable Entity user, @Nullable ItemStack tool);

    /**
     * Intercept dropping of container contents.
     */
    public boolean interceptContentDropping() {
        return this.interceptContentDropping;
    }

    private static void saveBlockEntityData(ItemStack stack, BlockEntity be) {
        var saved = be.saveWithId();
        BlockItem.setBlockEntityData(stack, be.getType(), be.saveWithId());
        if (stack.getItem() instanceof PlayerHeadItem && saved.contains("SkullOwner")) {
            var itemTag = stack.getOrCreateTag();
            itemTag.put("SkullOwner", Objects.requireNonNull(saved.get("SkullOwner")));
            Optional
                    .of(itemTag.getCompound("BlockEntityTag"))
                    .ifPresent(beTag -> beTag.remove("SkullOwner"));
        }
    }
}
