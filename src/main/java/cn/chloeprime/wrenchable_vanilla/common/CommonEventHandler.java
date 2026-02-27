package cn.chloeprime.wrenchable_vanilla.common;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.function.Consumer;

@EventBusSubscriber
public class CommonEventHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        // Preconditions
        if (event.getCancellationResult() != InteractionResult.PASS) {
            return;
        }
        var user = event.getEntity();
        if (!user.isShiftKeyDown() || !WrenchBlockLogic.isWrench(event.getItemStack())) {
            return;
        }

        // Check tag and permission
        var hit = event.getHitVec();
        var state = user.level().getBlockState(hit.getBlockPos());
        if (!WrenchBlockLogic.isWrenchable(state)) {
            return;
        }
        if (!WrenchBlockLogic.checkBlockPermission(user, hit.getBlockPos())) {
            return;
        }

        // Wrench!
        var result = WrenchBlockLogic.wrenchImpl(user, event.getHand(), hit);
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerRightClickEntity(PlayerInteractEvent.EntityInteractSpecific event) {
        onPlayerRightClickEntity(event, event.getTarget(), event::setCanceled, event::setCancellationResult);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        onPlayerRightClickEntity(event, event.getTarget(), event::setCanceled, event::setCancellationResult);
    }

    public static void onPlayerRightClickEntity(PlayerInteractEvent event, Entity target,
                                                BooleanConsumer canceller, Consumer<InteractionResult> reasonSetter
    ) {
        // Preconditions
        if (target.isRemoved()) {
            return;
        }
        var user = event.getEntity();
        if (!user.isShiftKeyDown() || !WrenchEntityLogic.isWrench(event.getItemStack())) {
            return;
        }

        // Check tag and permission
        if (!WrenchEntityLogic.isWrenchable(target)) {
            return;
        }
        if (!WrenchEntityLogic.checkEntityPermission(user, target)) {
            return;
        }

        // Wrench!
        var result = WrenchEntityLogic.wrenchImpl(user, event.getHand(), target);
        if (result != InteractionResult.PASS ) {
            canceller.accept(true);
            reasonSetter.accept(result);
        }
    }

    private CommonEventHandler() {
    }
}
