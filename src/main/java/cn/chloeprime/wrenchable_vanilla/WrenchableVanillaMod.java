package cn.chloeprime.wrenchable_vanilla;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

import java.util.Random;

@Mod(WrenchableVanillaMod.MOD_ID)
public class WrenchableVanillaMod {
    public static final String MOD_ID = "wrenchable_vanilla";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WrenchableVanillaMod(IEventBus ignored, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
    }

    public static ResourceLocation rl(String path) {
        return rl(MOD_ID, path);
    }

    public static ResourceLocation rl(String name, String path) {
        return ResourceLocation.fromNamespaceAndPath(name, path);
    }

    public static void playSound(
            Level level, Holder<SoundEvent> sound,
            Vec3 pos, SoundSource source,
            float volume, float pitch
    ) {
        var seed = SOUND_SEED_RNG.get().nextLong();
        level.playSeededSound(null, pos.x(), pos.y(), pos.z(), sound, source, volume, pitch, seed);
    }

    private static final ThreadLocal<Random> SOUND_SEED_RNG = ThreadLocal.withInitial(Random::new);
}
