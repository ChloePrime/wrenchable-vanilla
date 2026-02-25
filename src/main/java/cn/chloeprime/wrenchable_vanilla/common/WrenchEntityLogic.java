package cn.chloeprime.wrenchable_vanilla.common;

import cn.chloeprime.wrenchable_vanilla.CommonConfig;
import cn.chloeprime.wrenchable_vanilla.WrenchableVanillaMod;
import cn.chloeprime.wrenchable_vanilla.util.ContextStack;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.PlayLevelSoundEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Objects;

@Mod.EventBusSubscriber
public class WrenchEntityLogic {
    public static boolean isWrench(ItemStack stack) {
        return stack.is(WrenchTags.WRENCHES);
    }

    public static boolean isWrenchable(Entity entity) {
        return entity.getType().is(WrenchTags.WRENCHABLE_ENTITY);
    }

    public static WrenchMode getWrenchModeFor(Entity entity) {
        return entity.getType().is(WrenchTags.FORCE_PICK_ENTITY) ? WrenchMode.PICK_AND_SAVE : CommonConfig.getEntityMode();
    }

    @SuppressWarnings("ConstantValue")
    public static boolean checkEntityPermission(Player player, Entity target) {
        var level = player.level();
        if (level == null) {
            return false;
        }
        return !target.hasIndirectPassenger(player) && !target.isInvulnerableTo(player.damageSources().playerAttack(player));
    }

    public static InteractionResult wrenchImpl(
            LivingEntity user,
            InteractionHand hand,
            Entity target
    ) {
        var level = user.level();
        var mode = getWrenchModeFor(target);

        boolean isClient;
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            isClient = false;
            var loots = mode.getEntityDrops(serverLevel, target, user, user.getItemInHand(hand));
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
        } else {
            isClient = true;
        }

        WrenchContext.wrap(user, target, mode, () -> {
            if (mode == WrenchMode.BREAK && user instanceof Player player) {
                // 不重复伤害就没法速拆盔甲架（悲）
                for (int i = 0; i < 5; i++) {
                    target.hurt(user.damageSources().playerAttack(player), isClient ? 0 : Integer.MAX_VALUE);
                    if (!target.isAlive()) {
                        break;
                    }
                }
            }
            if (!level.isClientSide()) {
                tryPlayWrenchedSound(level, target.getEyePosition(), user.getSoundSource());
            }
            if (mode == WrenchMode.PICK_AND_SAVE) {
                target.discard();
            }
        });

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    public static boolean tryCaptureContainerDrop(Level level, ItemStack stack) {
        if (level.isClientSide()) {
            return false;
        }
        var context = WrenchContext.current().orElse(null);
        if (context != null && context.type() == WrenchContext.Type.ENTITY && context.userPlayer() != null) {
            context.userPlayer().getInventory().placeItemBackInInventory(stack, true);
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void captureDropOnItemEntityCreated(EntityJoinLevelEvent event) {
        var entity = event.getEntity();
        if (entity.level().isClientSide() || entity.getType() != EntityType.ITEM || !(entity instanceof ItemEntity item)) {
            return;
        }
        if (tryCaptureContainerDrop(item.level(), item.getItem())) {
            event.cancel();
        }
    }

    private static final ContextStack<Boolean> SILENCER_MERCY = new ContextStack<>();

    @SubscribeEvent
    public static void manageSoundWhenWrenching(PlayLevelSoundEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (SILENCER_MERCY.current().orElse(false)) {
            return;
        }
        if (tryPlayWrenchedSound(event.getLevel(), null, event.getSource())) {
            event.cancel();
        }
    }

    /**
     * @return If true, silence the original sound.
     */
    private static boolean tryPlayWrenchedSound(Level level, @Nullable Vec3 pos, SoundSource source) {
        var context = WrenchContext.current().orElse(null);
        if (context == null || context.type() != WrenchContext.Type.ENTITY) {
            return false;
        }
        if (!context.soundPlayed().getAndSet(true)) {
            Holder<SoundEvent> customSound = CommonConfig.getBreakEntitySound().orElse(null);
            if (customSound == null) {
                // Let the original sound play
                return false;
            }
            float volume = 1;
            float pitch = 1;
            var realPos = Objects.requireNonNullElse(pos, context.pos().getCenter());
            SILENCER_MERCY.wrap(true, () -> WrenchableVanillaMod.playSound(level, customSound, realPos, source, volume, pitch));
            return true;
        }
        // Silence this (repeated) sound.
        return true;
    }

    private WrenchEntityLogic() {
    }
}
