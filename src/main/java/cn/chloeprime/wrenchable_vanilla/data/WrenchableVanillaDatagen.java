package cn.chloeprime.wrenchable_vanilla.data;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class WrenchableVanillaDatagen {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        if (event.includeServer()) {
            var blockTagProvider = generator.addProvider(true, new WVBlockTagsProvider(
                    generator.getPackOutput(),
                    event.getLookupProvider(),
                    event.getExistingFileHelper()
            ));
            generator.addProvider(true, new WVItemTagsProvider(
                    generator.getPackOutput(),
                    event.getLookupProvider(),
                    blockTagProvider.contentsGetter(),
                    event.getExistingFileHelper()
            ));
            generator.addProvider(true, new WVEntityTagsProvider(
                    generator.getPackOutput(),
                    event.getLookupProvider(),
                    event.getExistingFileHelper()
            ));
        }
    }

}
