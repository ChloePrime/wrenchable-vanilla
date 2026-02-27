package cn.chloeprime.wrenchable_vanilla.client;

import cn.chloeprime.wrenchable_vanilla.CommonConfig;
import cn.chloeprime.wrenchable_vanilla.WrenchableVanillaMod;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Optional;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientConfig {
    public static Optional<Holder<SoundEvent>> getBreakBlockSound() {
        if (breakBlockSound == null) {
            CommonConfig.loadSoundEvent(WRENCH_BREAK_BLOCK_SOUND, value -> breakBlockSound = value);
        }
        return CommonConfig.decodeSoundEvent(breakBlockSound);
    }

    public static boolean hideBreakParticle() {
        return hideBreakParticle;
    }


    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static final ModConfigSpec.ConfigValue<String> WRENCH_BREAK_BLOCK_SOUND = BUILDER
            .comment("""
                    ID of sound played when block is wrenched.
                    Set to empty to let the original break sound play.""")
            .define("wrench_break_block_sound", "minecraft:entity.item_frame.remove_item", CommonConfig::validateNullableResourceLocation);

    static final ModConfigSpec.BooleanValue HIDE_BREAK_PARTICLE = BUILDER
            .comment("Hide block break particle when block is wrenched")
            .define("hide_break_particle", true);

    static final ModConfigSpec SPEC = BUILDER.build();


    static volatile ResourceKey<SoundEvent> breakBlockSound;
    static volatile boolean hideBreakParticle;


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (event instanceof ModConfigEvent.Unloading) {
            return;
        }
        if (!WrenchableVanillaMod.MOD_ID.equals(event.getConfig().getModId())) {
            return;
        }
        breakBlockSound = null;
        hideBreakParticle = HIDE_BREAK_PARTICLE.get();
    }

    private ClientConfig() {
    }
}
