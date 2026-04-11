package wd40.vaakx.cog.server.blockentities;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.entity.BlockEntityType;
import wd40.lubricant.api.Init;
import wd40.lubricant.api.data.DataKey;
import wd40.lubricant.api.data.DataKeys;
import wd40.lubricant.api.registry.BlockEntityRegistry;
import wd40.vaakx.cog.Cog;
import wd40.vaakx.cog.server.blocks.Blocks;
import wd40.vaakx.cog.server.blocks.CounterBE;

import java.util.function.Supplier;

/**
 * All of cog's {@link BlockEntityType}s, plus block-entity-attached data.
 * Same lifecycle as {@link wd40.vaakx.cog.server.items.Items}.
 */
public final class BlockEntities implements Init {

    public static final BlockEntityRegistry TYPES = BlockEntityRegistry.create(Cog.ID);

    public static final Supplier<BlockEntityType<CounterBE>> COUNTER =
            TYPES.register("counter", CounterBE::new, Blocks.COUNTER);

    private static final DataKeys KEYS = DataKeys.create(Cog.ID);

    /** Click count on a Counter block, stored on its {@link CounterBE} via lubricant attached data. */
    public static final DataKey<Integer> COUNTER_CLICKS = KEYS.blockEntity("counter_clicks", Codec.INT, 0);

    public BlockEntities() {}
}
