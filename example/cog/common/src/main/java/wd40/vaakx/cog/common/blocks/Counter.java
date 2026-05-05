package wd40.vaakx.cog.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.server.level.ServerLevel;
import wd40.lubricant.api.block.entity.BlockEntityData;
import wd40.vaakx.cog.Cog;
import wd40.vaakx.cog.common.config.Configs;
import wd40.vaakx.cog.common.menus.CounterMenu;
import wd40.vaakx.cog.common.saveddata.Worlds;

import static wd40.vaakx.cog.common.blockentities.BlockEntities.COUNTER_CLICKS;

/**
 * A toy block that increments a counter on every right-click. The count is
 * stored on a {@link CounterBE} via the lubricant {@link BlockEntityData} data
 * API, persisted across world reloads.
 *
 * <p>Demo of: custom block + custom BlockEntity registered through lubricant,
 * mutating per-BE attached data. Sneak-right-click opens a menu via
 * {@code MenuRenderers}.</p>
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
        if (player.isShiftKeyDown()) {
            if (level.isClientSide()) return InteractionResult.SUCCESS;
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("Counter @ " + pos.toShortString());
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player p) {
                        return new CounterMenu(containerId, inv);
                    }
                });
            }
            return InteractionResult.SUCCESS;
        }
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof CounterBE counter)) return InteractionResult.PASS;
        int step = Configs.COG.get().counterStep;
        int next = BlockEntityData.get(counter, COUNTER_CLICKS) + step;
        BlockEntityData.set(counter, COUNTER_CLICKS, next);
        ServerLevel serverLevel = (ServerLevel) level;
        Worlds.COUNTER_TOTAL.update(serverLevel, total -> total + step);
        Cog.LOG.info("counter @ {} -> {} (world total {}, step {})", pos, next, Worlds.COUNTER_TOTAL.get(serverLevel), step);
        return InteractionResult.SUCCESS;
    }
}
