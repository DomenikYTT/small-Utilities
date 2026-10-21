package de.domenik_yt1.small_utilities.block;

import de.domenik_yt1.small_utilities.Small_utilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CropHarvesting {


    @Nullable
    public static List<ItemStack> tryHarvest(ServerLevel level, BlockPos pos, BlockState state, ItemStack hoe) {
        Block block = state.getBlock();


        if (block instanceof CropBlock crop) {
            if (!crop.isMaxAge(state)) return null;

            List<ItemStack> drops = Block.getDrops(state, level, pos, null, null, hoe);
            level.setBlock(pos, crop.getStateForAge(0), Block.UPDATE_ALL);
            cropSound(level, pos);
            return drops;
        }


        if (block instanceof NetherWartBlock) {
            if (state.getValue(NetherWartBlock.AGE) < NetherWartBlock.MAX_AGE) return null;
            List<ItemStack> drops = Block.getDrops(state, level, pos, null, null, hoe);
            level.setBlock(pos, state.setValue(NetherWartBlock.AGE, 0), Block.UPDATE_ALL);
            cropSound(level, pos);
            return drops;
        }

        if (block instanceof CocoaBlock) {
            if (state.getValue(CocoaBlock.AGE) < CocoaBlock.MAX_AGE) return null;
            List<ItemStack> drops = Block.getDrops(state, level, pos, null, null, hoe);
            level.setBlock(pos, state.setValue(CocoaBlock.AGE, 0), Block.UPDATE_ALL);
            cropSound(level, pos);
            return drops;
        }

        if (block instanceof SweetBerryBushBlock) {
            if (state.getValue(SweetBerryBushBlock.AGE) < SweetBerryBushBlock.MAX_AGE) return null;
            List<ItemStack> drops = Block.getDrops(state, level, pos, null, null, hoe);
            level.setBlock(pos, state.setValue(SweetBerryBushBlock.AGE, 1), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 1.0F);
            return drops;
        }

        if ((state.is(Blocks.MELON) || state.is(Blocks.PUMPKIN)) && hasAttachedStem(level, pos)) {
            return breakBlock(level, pos, state, hoe);
        }

        if ((state.is(Blocks.SUGAR_CANE) || state.is(Blocks.CACTUS))
                && level.getBlockState(pos.below()).is(block)) {
            return breakBlock(level, pos, state, hoe);
        }

        if (state.is(HARVESTABLE)) {
            return harvestByAge(level, pos, state, hoe);
        }

        return null;
    }


    private static boolean hasAttachedStem(ServerLevel level, BlockPos pos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState s = level.getBlockState(pos.relative(direction));

            if (s.getBlock() instanceof AttachedStemBlock && s.getValue(AttachedStemBlock.FACING) == direction.getOpposite()) {
                return true;
            }
        }
        return false;
    }

    private static List<ItemStack> breakBlock(ServerLevel level, BlockPos pos, BlockState state, ItemStack hoe) {
        List<ItemStack> drops = Block.getDrops(state, level, pos, null, null, hoe);
        level.levelEvent(2001, pos, Block.getId(state));
        level.setBlock(pos, state.getFluidState().createLegacyBlock(), Block.UPDATE_ALL);
        return drops;
    }

    private static void cropSound(ServerLevel level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);

    }

    public static final TagKey<Block> HARVESTABLE = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(Small_utilities.MOD_ID, "harvestable")
    );


    @Nullable
    private static List<ItemStack> harvestByAge(ServerLevel level, BlockPos pos, BlockState state, ItemStack hoe) {
        IntegerProperty ageProp = null;

        for (Property<?> p : state.getProperties()) {
            if (p instanceof IntegerProperty ip && ip.getName().equals("age")) {
                ageProp = ip;
                break;
            }
        }

        if (ageProp == null) return null;

        int max = Collections.max(ageProp.getPossibleValues());
        int min = Collections.min(ageProp.getPossibleValues());
        if (state.getValue(ageProp) < max) return null;

        List<ItemStack> drops = Block.getDrops(state, level, pos, null, null, hoe);
        level.setBlock(pos, state.setValue(ageProp, max), Block.UPDATE_ALL);
        cropSound(level, pos);
        return drops;
    }

    private CropHarvesting() {}
}
