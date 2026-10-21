package de.domenik_yt1.small_utilities;

import de.domenik_yt1.small_utilities.block.HarvesterBlock;
import de.domenik_yt1.small_utilities.block.HarvesterBlockEntity;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class SURegistry {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Small_utilities.MOD_ID, Registries.BLOCK);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Small_utilities.MOD_ID, Registries.ITEM);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Small_utilities.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<Block> HARVESTER = BLOCKS.register("harvester",
            () -> new HarvesterBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_WHITE)
                    .strength(2.0F)
                    .sound(SoundType.BAMBOO_WOOD)
                    .noOcclusion()
            ));


    public static final RegistrySupplier<Item> HARVESTER_ITEM = ITEMS.register("harvester",
            () -> new BlockItem(HARVESTER.get(), new Item.Properties()));

    public static final RegistrySupplier<BlockEntityType<HarvesterBlockEntity>> HARVESTER_BE =
            BLOCK_ENTITIES.register("harvester",
                    () -> BlockEntityType.Builder.of(HarvesterBlockEntity::new, HARVESTER.get()).build(null));

    private SURegistry() {}
}
