package cn.chloeprime.wrenchable_vanilla;

import cn.chloeprime.wrenchable_vanilla.common.WrenchMode;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class CommonConfig {
    public static WrenchMode getBlockMode() {
        return Optional.ofNullable(blockMode)
                .or(() -> Optional.of(BLOCK_MODE.get()).map(name -> blockMode = WrenchMode.valueOf(name)))
                .orElse(WrenchMode.BREAK);
    }

    public static WrenchMode getEntityMode() {
        return Optional.ofNullable(entityMode)
                .or(() -> Optional.of(ENTITY_MODE.get()).map(name -> entityMode = WrenchMode.valueOf(name)))
                .orElse(WrenchMode.BREAK);
    }

    public static Optional<Holder<SoundEvent>> getBreakEntitySound() {
        if (breakEntitySound == null) {
            loadSoundEvent(WRENCH_BREAK_ENTITY_SOUND, value -> breakEntitySound = value);
        }
        return CommonConfig.decodeSoundEvent(breakEntitySound);
    }


    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.ConfigValue<String> BLOCK_MODE = BUILDER
            .push("implementation")
            .comment("""
                    Wrench mode on blocks.
                    - BREAK: break the block and get its drop.
                    - PICK_AND_SAVE: works like shift + middle click in creative mode.""")
            .define("block_mode", WrenchMode.BREAK.name(), enumValidator(WrenchMode.class));

    private static final ModConfigSpec.ConfigValue<String> ENTITY_MODE = BUILDER
            .comment("""
                    Wrench mode on entities.
                    - BREAK: Kill the entity and get its drop.
                    - PICK_AND_SAVE: works like middle click in creative mode.""")
            .define("entity_mode", WrenchMode.BREAK.name(), enumValidator(WrenchMode.class));

    static final ModConfigSpec.ConfigValue<String> WRENCH_BREAK_ENTITY_SOUND = BUILDER
            .pop()
            .push("sound")
            .comment("""
                    ID of sound played when entity is wrenched.
                    Set to empty to let the original break sound play.""")
            .define("wrench_break_entity_sound", "minecraft:entity.item_frame.remove_item", CommonConfig::validateNullableResourceLocation);

    static final ModConfigSpec SPEC = BUILDER.build();


    private static volatile WrenchMode blockMode;
    private static volatile WrenchMode entityMode;
    static volatile ResourceKey<SoundEvent> breakEntitySound;


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (event instanceof ModConfigEvent.Unloading) {
            return;
        }
        if (!WrenchableVanillaMod.MOD_ID.equals(event.getConfig().getModId())) {
            return;
        }
        breakEntitySound = null;
        blockMode = null;
        entityMode = null;
    }

    public static boolean validateNullableResourceLocation(Object configValue) {
        if (!(configValue instanceof String str)) {
            return false;
        }
        if (str.isEmpty()) {
            return true;
        }
        return ResourceLocation.tryParse(str) != null;
    }

    public static <E extends Enum<E>> Predicate<Object> enumValidator(Class<E> enumType) {
        return configValue -> {
            try {
                Enum.valueOf(enumType, String.valueOf(configValue));
                return true;
            } catch (IllegalArgumentException ex) {
                return false;
            }
        };
    }

    public static void loadSoundEvent(Supplier<String> configValue, Consumer<@Nullable ResourceKey<SoundEvent>> setter) {
        var idStr = configValue.get();
        if (idStr.isEmpty()) {
            setter.accept(null);
        } else {
            var id = ResourceLocation.tryParse(idStr);
            if (id != null) {
                setter.accept(ResourceKey.create(Registries.SOUND_EVENT, id));
            }
        }
    }

    public static Optional<Holder<SoundEvent>> decodeSoundEvent(@Nullable ResourceKey<SoundEvent> id) {
        if (id == null) {
            return Optional.empty();
        }
        return BuiltInRegistries.SOUND_EVENT.getHolder(id)
                .map(ref -> (Holder<SoundEvent>) ref)
                .or(() -> Optional.of(Holder.direct(SoundEvent.createVariableRangeEvent(id.location()))));
    }

    private CommonConfig() {
    }
}
