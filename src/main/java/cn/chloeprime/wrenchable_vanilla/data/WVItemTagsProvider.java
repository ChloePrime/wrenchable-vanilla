package cn.chloeprime.wrenchable_vanilla.data;

import cn.chloeprime.wrenchable_vanilla.WrenchableVanillaMod;
import cn.chloeprime.wrenchable_vanilla.common.WrenchTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class WVItemTagsProvider extends ItemTagsProvider {
    public WVItemTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagLookup<Block>> blockTags,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, blockTags, WrenchableVanillaMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(@Nonnull HolderLookup.Provider p_256380_) {
        tag(WrenchTags.WRENCHES).addOptionalTag(WrenchableVanillaMod.rl("forge", "tools/wrench"));
        tag(WrenchTags.WRENCHES).addOptionalTag(WrenchableVanillaMod.rl("c", "tools/wrench"));
    }
}
