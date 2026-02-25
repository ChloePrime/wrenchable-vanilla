package cn.chloeprime.wrenchable_vanilla.client;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class WrenchableVanillaModClient {
    @SuppressWarnings("removal")
    public WrenchableVanillaModClient() {
        var loadContext = ModLoadingContext.get();
        loadContext.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }
}
