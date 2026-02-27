package cn.chloeprime.wrenchable_vanilla.client;

import cn.chloeprime.wrenchable_vanilla.WrenchableVanillaMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(value = WrenchableVanillaMod.MOD_ID, dist = Dist.CLIENT)
public class WrenchableVanillaModClient {
    public WrenchableVanillaModClient(IEventBus ignored, ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }
}
