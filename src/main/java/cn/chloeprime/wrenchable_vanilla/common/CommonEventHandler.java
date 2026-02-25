package cn.chloeprime.wrenchable_vanilla.common;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommonEventHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        // Preconditions
        if (event.getResult() == Event.Result.DENY) {
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
        onPlayerRightClickEntity(event, event.getTarget());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        onPlayerRightClickEntity(event, event.getTarget());
    }

    public static void onPlayerRightClickEntity(PlayerInteractEvent event, Entity target) {
        // Preconditions
        if (target.isRemoved()) {
            return;
        }
        if (event.getResult() == Event.Result.DENY) {
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
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    private CommonEventHandler() {
    }
}
