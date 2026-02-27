package cn.chloeprime.wrenchable_vanilla.data;

import cn.chloeprime.wrenchable_vanilla.WrenchableVanillaMod;
import cn.chloeprime.wrenchable_vanilla.common.WrenchTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

class WVBlockTagsProvider extends BlockTagsProvider {
    public static final TagKey<Block> WRENCHABLE_VANILLA_TAGS = BlockTags.create(WrenchableVanillaMod.rl("wrenchable/tags/vanilla"));
    public static final TagKey<Block> WRENCHABLE_FORGE_TAGS = BlockTags.create(WrenchableVanillaMod.rl("wrenchable/tags/forge"));
    public static final TagKey<Block> WRENCHABLE_VANILLA = BlockTags.create(WrenchableVanillaMod.rl("wrenchable/blocks/vanilla"));
    public static final TagKey<Block> SKULLS = BlockTags.create(WrenchableVanillaMod.rl("skulls"));

    public WVBlockTagsProvider(
            PackOutput packOutput,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(packOutput, lookupProvider, WrenchableVanillaMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(@Nonnull HolderLookup.Provider lookupProvider) {
        tag(WrenchTags.WRENCHABLE)
                .addTag(WRENCHABLE_VANILLA_TAGS)
                .addTag(WRENCHABLE_FORGE_TAGS)
                .addTag(WRENCHABLE_VANILLA);
        // Vanilla Tags
        tag(WRENCHABLE_VANILLA_TAGS)
                .addTag(BlockTags.BUTTONS)
                .addTag(BlockTags.PRESSURE_PLATES)
                // .addTag(BlockTags.DOORS)
                .addTag(BlockTags.ANVIL)
                .addTag(BlockTags.RAILS)
                .addTag(BlockTags.TRAPDOORS)
                .addTag(BlockTags.FENCES)
                .addTag(BlockTags.CANDLES)
                .addTag(BlockTags.FLOWER_POTS)
                .addTag(BlockTags.ALL_SIGNS)
                .addTag(BlockTags.SHULKER_BOXES)
                .addTag(BlockTags.FENCE_GATES)
                .addTag(BlockTags.CAULDRONS);
        // Forge Tags
        tag(WRENCHABLE_FORGE_TAGS)
                .addOptionalTags(ctag(Tags.Blocks.BARRELS.location().getPath()))
                .addOptionalTags(ctag(Tags.Blocks.BOOKSHELVES.location().getPath()))
                .addOptionalTags(ctag(Tags.Blocks.CHESTS.location().getPath()))
                .addOptionalTags(ctag(Tags.Blocks.FENCES.location().getPath()))
                .addOptionalTags(ctag(Tags.Blocks.FENCE_GATES.location().getPath()));
        // Vanilla Functional Blocks
        tag(WRENCHABLE_VANILLA)
                .add(Blocks.TORCH, Blocks.SOUL_TORCH, Blocks.REDSTONE_TORCH)
                .add(Blocks.WALL_TORCH, Blocks.SOUL_WALL_TORCH, Blocks.REDSTONE_WALL_TORCH)
                .add(Blocks.LANTERN, Blocks.SOUL_LANTERN, Blocks.END_ROD, Blocks.REDSTONE_LAMP)
                .add(Blocks.CRAFTING_TABLE, Blocks.STONECUTTER)
                .add(Blocks.CARTOGRAPHY_TABLE, Blocks.FLETCHING_TABLE, Blocks.SMITHING_TABLE, Blocks.GRINDSTONE, Blocks.LOOM)
                .add(Blocks.FURNACE, Blocks.SMOKER, Blocks.BLAST_FURNACE)
                .add(Blocks.COMPOSTER, Blocks.NOTE_BLOCK, Blocks.JUKEBOX)
                .add(Blocks.ENCHANTING_TABLE, Blocks.BREWING_STAND, Blocks.CAULDRON, Blocks.BELL)
                .add(Blocks.BEACON, Blocks.CONDUIT, Blocks.LODESTONE)
                .add(Blocks.LADDER, Blocks.SCAFFOLDING)
                .add(Blocks.BEEHIVE)
                .add(Blocks.LIGHTNING_ROD, Blocks.FLOWER_POT, Blocks.DECORATED_POT)
                .add(Blocks.BOOKSHELF, Blocks.CHISELED_BOOKSHELF, Blocks.LECTERN)
                .addTag(SKULLS);
        // Vanilla Redstone Blocks
        tag(WRENCHABLE_VANILLA)
                .add(Blocks.REDSTONE_WIRE).addTag(Tags.Blocks.STORAGE_BLOCKS_REDSTONE)
                .add(Blocks.REPEATER, Blocks.COMPARATOR)
                .add(Blocks.TARGET)
                .add(Blocks.LEVER, Blocks.TRIPWIRE_HOOK, Blocks.TRIPWIRE)
                .add(Blocks.DAYLIGHT_DETECTOR)
                .add(Blocks.PISTON, Blocks.STICKY_PISTON)
                .add(Blocks.SLIME_BLOCK, Blocks.HONEY_BLOCK)
                .add(Blocks.DISPENSER, Blocks.DROPPER, Blocks.HOPPER, Blocks.OBSERVER)
                .add(Blocks.TNT);
        // Skulls
        tag(WrenchTags.FORCE_PICK).addTag(SKULLS);
        tag(SKULLS)
                .add(Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL)
                .add(Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD)
                .add(Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD)
                .add(Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD)
                .add(Blocks.PIGLIN_HEAD, Blocks.PIGLIN_WALL_HEAD)
                .add(Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD)
                .addOptionalTags(ctag("skulls"));
    }

    @SuppressWarnings("unchecked")
    private static TagKey<Block>[] ctag(String... path) {
        var tags = new TagKey[2 * path.length];
        for (int i = 0; i < path.length; i++) {
            tags[2 * i] = BlockTags.create(WrenchableVanillaMod.rl("neoforge", path[i]));
            tags[2 * i + 1] = BlockTags.create(WrenchableVanillaMod.rl("c", path[i]));
        }
        return tags;
    }
}
