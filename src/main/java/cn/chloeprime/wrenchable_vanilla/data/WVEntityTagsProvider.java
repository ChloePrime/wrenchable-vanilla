package cn.chloeprime.wrenchable_vanilla.data;

import cn.chloeprime.wrenchable_vanilla.WrenchableVanillaMod;
import cn.chloeprime.wrenchable_vanilla.common.WrenchTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

class WVEntityTagsProvider extends EntityTypeTagsProvider {
    public static final TagKey<EntityType<?>> MINECARTS = TagKey.create(Registries.ENTITY_TYPE, WrenchableVanillaMod.rl("minecarts"));
    public static final TagKey<EntityType<?>> BOATS = TagKey.create(Registries.ENTITY_TYPE, WrenchableVanillaMod.rl("boats"));

    public WVEntityTagsProvider(
            PackOutput packOutput,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(packOutput, lookupProvider, WrenchableVanillaMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(@Nonnull HolderLookup.Provider lookupProvider) {
        tag(MINECARTS)
                .add(EntityType.MINECART)
                .add(EntityType.HOPPER_MINECART)
                .add(EntityType.CHEST_MINECART)
                .add(EntityType.FURNACE_MINECART)
                .add(EntityType.TNT_MINECART)
                .addOptionalTags(ctag("minecarts"));
        tag(BOATS)
                .add(EntityType.BOAT)
                .add(EntityType.CHEST_BOAT)
                .addOptionalTags(ctag("boats"));
        tag(WrenchTags.WRENCHABLE_ENTITY)
                .addTag(MINECARTS)
                .addTag(BOATS)
                .add(EntityType.ITEM_FRAME, EntityType.GLOW_ITEM_FRAME, EntityType.PAINTING)
                .add(EntityType.ARMOR_STAND)
                .add(EntityType.END_CRYSTAL);

        tag(WrenchTags.FORCE_PICK_ENTITY)
                .add(EntityType.END_CRYSTAL);
    }

    @SuppressWarnings("unchecked")
    private static TagKey<EntityType<?>>[] ctag(String... path) {
        var tags = new TagKey[2 * path.length];
        for (int i = 0; i < path.length; i++) {
            tags[2 * i] = BlockTags.create(WrenchableVanillaMod.rl("forge", path[i]));
            tags[2 * i + 1] = BlockTags.create(WrenchableVanillaMod.rl("c", path[i]));
        }
        return tags;
    }
}
