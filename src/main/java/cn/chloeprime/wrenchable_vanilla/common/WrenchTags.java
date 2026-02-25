package cn.chloeprime.wrenchable_vanilla.common;

import cn.chloeprime.wrenchable_vanilla.WrenchableVanillaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class WrenchTags {
    public static final TagKey<Block> WRENCHABLE = BlockTags.create(WrenchableVanillaMod.rl("wrenchable"));
    public static final TagKey<Item> WRENCHES = ItemTags.create(WrenchableVanillaMod.rl("wrenches"));
    public static final TagKey<Block> FORCE_PICK = BlockTags.create(WrenchableVanillaMod.rl("force_pick"));

    public static final TagKey<EntityType<?>> WRENCHABLE_ENTITY = TagKey.create(Registries.ENTITY_TYPE, WrenchableVanillaMod.rl("wrenchable"));
    public static final TagKey<EntityType<?>> FORCE_PICK_ENTITY = TagKey.create(Registries.ENTITY_TYPE, WrenchableVanillaMod.rl("force_pick"));

    private WrenchTags() {
    }
}
