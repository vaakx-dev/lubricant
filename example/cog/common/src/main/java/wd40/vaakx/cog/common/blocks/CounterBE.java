package wd40.vaakx.cog.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import wd40.vaakx.cog.common.blockentities.BlockEntities;

/**
 * Empty BlockEntity. All state lives in lubricant attached data
 * ({@link BlockEntities#COUNTER_CLICKS}); this class just exists to satisfy
 * the {@code EntityBlock#newBlockEntity} contract and to give the
 * BlockEntityType something to instantiate.
 */
public final class CounterBE extends BlockEntity {

    public CounterBE(BlockPos pos, BlockState state) {
        super(BlockEntities.COUNTER.get(), pos, state);
    }
}
