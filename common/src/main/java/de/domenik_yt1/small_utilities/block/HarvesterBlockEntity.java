package de.domenik_yt1.small_utilities.block;

import de.domenik_yt1.small_utilities.SURegistry;
import de.domenik_yt1.small_utilities.Small_utilities;
import de.domenik_yt1.small_utilities.config.SmallUtilitiesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.logging.Logger;

public class HarvesterBlockEntity extends BlockEntity {

    public static final int RANGE = 4;


    private static final int SCAN_MIN_DY = 1;

    private static final int SCAN_MAX_DY = 3;

    private ItemStack hoe = ItemStack.EMPTY;
    private int timer = 0;


    public HarvesterBlockEntity(BlockPos pos, BlockState state) {
        super(SURegistry.HARVESTER_BE.get(), pos, state);
    }


    public ItemStack getHoe() {
        return hoe;
    }


    public void setHoe(ItemStack stack) {
        this.hoe = stack;
        setChanged();
    }

    public ItemStack takeHoe() {
        ItemStack out = hoe;
        hoe = ItemStack.EMPTY;
        setChanged();
        return out;
    }



    private static final org.slf4j.Logger LOG = com.mojang.logging.LogUtils.getLogger();

    public static void serverTick(Level level, BlockPos pos, BlockState state, HarvesterBlockEntity be) {
        if (++be.timer < SmallUtilitiesConfig.checkIntervalTicks()) return;
        be.timer = 0;
        boolean powered = level.hasNeighborSignal(pos);
        LOG.info("[Harvester {}] Intervall erreicht | Redstone={} | Hacke={}", pos, powered, be.hoe);
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (!powered) return;
        be.harvest(serverLevel, pos);
    }

    private void harvest(ServerLevel level, BlockPos pos) {
        Container output = HopperBlockEntity.getContainerAt(level, pos.above());
        if (output == null) return;
        if (!ensureHoe(level, pos)) return;

        outer:
        for (int dy = SCAN_MIN_DY; dy <= SCAN_MAX_DY; dy++) {
            for (int dx = -RANGE; dx <= RANGE; dx++) {
                for (int dz = -RANGE; dz <= RANGE; dz++) {
                    if (dx == 0 && dz == 0) continue; // Säule über dem Harvester (Kiste)
                    BlockPos target = pos.offset(dx, dy, dz);
                    if (!level.isLoaded(target)) continue;

                    BlockState targetState = level.getBlockState(target);
                    List<ItemStack> drops = CropHarvesting.tryHarvest(level, target, targetState, hoe);
                    if (drops == null) continue;

                    boolean full = false;
                    for (ItemStack drop : drops) {
                        ItemStack rest = HopperBlockEntity.addItem(null, output, drop, Direction.DOWN);
                        if (!rest.isEmpty()) {
                            Block.popResource(level, target, rest);
                            full = true;
                        }
                    }

                    damageHoe(level, pos);
                    if (full) break outer;
                    if (!ensureHoe(level, pos)) break outer;
                }
            }
        }
        setChanged();
    }



    private void damageHoe(ServerLevel level, BlockPos pos) {
        if (!hoe.isDamageableItem()) return;
        hoe.setDamageValue(hoe.getDamageValue() + 1);
        if (hoe.getDamageValue() >= hoe.getMaxDamage()) {
            level.playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 0.8F, 0.8F);
            hoe = ItemStack.EMPTY;
        }
    }

    private boolean ensureHoe(ServerLevel level, BlockPos pos) {
        if (!hoe.isEmpty()) return true;
        Container below = HopperBlockEntity.getContainerAt(level, pos.below());
        if (below == null) return false;

        int[] slots;
        WorldlyContainer worldly = below instanceof WorldlyContainer w ? w : null;
        if (worldly != null) {
            slots = worldly.getSlotsForFace(Direction.UP);
        } else {
            slots = new int[below.getContainerSize()];
            for (int i = 0; i < slots.length; i++) slots[i] = i;
        }
        for (int slot : slots) {
            ItemStack in = below.getItem(slot);
            if (in.isEmpty() || !in.is(ItemTags.HOES)) continue;
            if (worldly != null && !worldly.canTakeItemThroughFace(slot, in, Direction.UP)) continue;
            hoe = below.removeItem(slot, 1);
            below.setChanged();
            setChanged();
            return true;
        }
        return false;
    }



    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!hoe.isEmpty()) tag.put("Hoe", hoe.save(registries));
        tag.putInt("Timer", timer);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        hoe = tag.contains("Hoe")
                ? ItemStack.parse(registries, tag.getCompound("Hoe")).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;
        timer = tag.getInt("Timer");
    }

}
