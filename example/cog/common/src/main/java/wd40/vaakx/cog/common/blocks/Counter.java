package wd40.vaakx.cog.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import wd40.lubricant.api.block.entity.BlockEntityData;
import wd40.vaakx.cog.Cog;

import static wd40.vaakx.cog.common.blockentities.BlockEntities.COUNTER_CLICKS;

/**
 * A toy block that increments a counter on every right-click. The count is
 * stored on a {@link CounterBE} via the lubricant {@link BlockEntityData} data
 * API, persisted across world reloads.
 *
 * <p>Demo of: custom block + custom BlockEntity registered through lubricant,
 * mutating per-BE attached data.</p>
 */
public final class Counter extends Block implements EntityBlock {

    public Counter(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CounterBE(pos, state);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof CounterBE counter)) return InteractionResult.PASS;
        int next = BlockEntityData.get(counter, COUNTER_CLICKS) + 1;
        BlockEntityData.set(counter, COUNTER_CLICKS, next);
        Cog.LOG.info("counter @ {} -> {}", pos, next);
        return InteractionResult.SUCCESS;
    }
}
