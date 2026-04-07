package wd40.vaakx.cog.server.blockentities;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.entity.BlockEntityType;
import wd40.lubricant.api.Init;
import wd40.lubricant.api.data.Key;
import wd40.lubricant.api.data.Keys;
import wd40.lubricant.api.registry.BlockEntityRegistry;
import wd40.vaakx.cog.Cog;
import wd40.vaakx.cog.server.blocks.Blocks;
import wd40.vaakx.cog.server.blocks.CounterBE;

import java.util.function.Supplier;

/**
 * All of cog's {@link BlockEntityType}s, plus block-entity-attached data.
 * Same lifecycle as {@link wd40.vaakx.cog.server.items.Items}.
 *
 * <p>Note the simple-name collision with {@code wd40.lubricant.api.data.BlockEntities}
 * (the lubricant attached-data facade). They're different concepts in different
 * packages; this file uses a fully-qualified reference when calling the facade.</p>
 */
public final class BlockEntities implements Init {

    public static final BlockEntityRegistry TYPES = BlockEntityRegistry.create(Cog.ID);

    public static final Supplier<BlockEntityType<CounterBE>> COUNTER =
            TYPES.register("counter", CounterBE::new, Blocks.COUNTER);

    private static final Keys KEYS = Keys.create(Cog.ID);

    /** Click count on a Counter block, stored on its {@link CounterBE} via lubricant attached data. */
    public static final Key<Integer> COUNTER_CLICKS = KEYS.of("counter_clicks", Codec.INT, 0);

    static {
        wd40.lubricant.api.data.BlockEntities.register(COUNTER_CLICKS);
    }

    public BlockEntities() {}
}
