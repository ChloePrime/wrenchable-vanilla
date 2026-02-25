package cn.chloeprime.wrenchable_vanilla.common;

import cn.chloeprime.wrenchable_vanilla.util.ContextStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public record WrenchContext(
        Type type,
        Level level,
        BlockPos pos,
        LivingEntity user,
        Player userPlayer,
        Entity entityTarget,
        WrenchMode mode,
        AtomicBoolean soundPlayed
) {
    public WrenchContext(
            Type type,
            Level level,
            BlockPos pos,
            LivingEntity user,
            Player userPlayer,
            Entity entityTarget,
            WrenchMode mode
    ) {
        this(type, level, pos, user, userPlayer, entityTarget, mode, new AtomicBoolean());
    }

    public enum Type {
        BLOCK,
        ENTITY,
    }

    private static final ContextStack<WrenchContext> CONTEXT_STACK = new ContextStack<>();

    public static Optional<WrenchContext> current() {
        return CONTEXT_STACK.current();
    }

    public static void wrap(LivingEntity user, BlockPos pos, WrenchMode mode, Runnable code) {
        var userPlayer = user instanceof Player player ? player : null;
        CONTEXT_STACK.wrap(new WrenchContext(Type.BLOCK, user.level(), pos, user, userPlayer, null, mode), code);
    }

    public static void wrap(LivingEntity user, Entity target, WrenchMode mode, Runnable code) {
        var userPlayer = user instanceof Player player ? player : null;
        CONTEXT_STACK.wrap(new WrenchContext(Type.ENTITY, user.level(), target.blockPosition(), user, userPlayer, target, mode), code);
    }
}
