package wd40.lubricant.api.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import wd40.lubricant.internal.Services;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Registers {@link Block}s under one mod's namespace. {@link #register} also
 * registers a matching BlockItem; use {@link #registerNoItem} for blocks that
 * shouldn't appear in inventory (fluid placeholders, technical blocks).
 *
 * <p>Lubricant binds blocks before items so item factories can reference
 * registered blocks via {@code .get()}.</p>
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/Blocks">Blocks wiki</a></p>
 */
public interface BlockRegistry {

    static BlockRegistry create(String modId) {
        return Services.registry().createBlockRegistry(modId);
    }

    Supplier<Block> register(String path, Function<BlockBehaviour.Properties, Block> factory);

    Supplier<Block> registerNoItem(String path, Function<BlockBehaviour.Properties, Block> factory);
}
