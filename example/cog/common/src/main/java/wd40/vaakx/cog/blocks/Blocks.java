package wd40.vaakx.cog.blocks;

import net.minecraft.world.level.block.Block;
import wd40.lubricant.api.BlockRegistry;
import wd40.lubricant.api.Init;
import wd40.vaakx.cog.CogMod;

import java.util.function.Supplier;

/**
 * All of cog's blocks (and their auto-generated BlockItems).
 *
 * Same lifecycle as {@link wd40.vaakx.cog.items.Items}: implements {@link Init},
 * declared in the META-INF services file, force-loaded by lubricant during
 * loader startup. Static fields run at that moment, queueing block (+ optional
 * BlockItem) registrations.
 *
 * <h3>Two register variants</h3>
 *
 * {@code register} - registers the block AND a BlockItem at the same id. This
 * is what most blocks want: the block exists in the world, and {@code /give} or
 * the creative inventory can hand out an item that places it.
 *
 * {@code registerNoItem} - registers ONLY the block. Use when:
 *   - the block is the secondary half of a multi-block (head of bed, top half
 *     of a tall plant, upper of a door). The "main" block has the BlockItem;
 *     placing it spawns the secondary block via vanilla logic.
 *   - the block is unobtainable by design (vanilla's end_portal, nether_portal,
 *     piston_head, command_block-without-creative). Worldgen / commands /
 *     other blocks place it; you never carry it in inventory.
 *
 * <h3>BlockBehaviour.Properties</h3>
 *
 * The factory receives a fresh Properties. Common builder methods:
 *   - {@code strength(hardness, resistance)} - mining time + blast resistance
 *   - {@code destroyTime(t)} - hardness only
 *   - {@code requiresCorrectToolForDrops()} - won't drop without proper pickaxe
 *   - {@code mapColor(MapColor)} - color shown on maps
 *   - {@code sound(SoundType)} - footstep / break / place sounds
 */
public final class Blocks implements Init {

    /** Per-mod block-registry handle. */
    public static final BlockRegistry BLOCKS = BlockRegistry.create(CogMod.ID);

    /**
     * Standard block: registers both the block and a BlockItem at {@code cog:gear}.
     * Players can {@code /give cog:gear} or pick it from the creative tab and place it.
     */
    public static final Supplier<Block> GEAR = BLOCKS.register("gear",
            props -> new Block(props.strength(2.0f).destroyTime(2.0f)));

    /**
     * No-item block. {@code cog:gear_head} exists in the BLOCK registry but is
     * NOT in the ITEM registry. {@code /give @s cog:gear_head} returns
     * "Unknown item"; {@code /setblock ~ ~ ~ cog:gear_head} works.
     *
     * In a real mod this would be paired with custom placement logic - for
     * example, GEAR's place handler would also place GEAR_HEAD on top, and
     * breaking either would break both. The example skips that to keep the
     * demo focused on the registry call.
     */
    public static final Supplier<Block> GEAR_HEAD = BLOCKS.registerNoItem("gear_head",
            props -> new Block(props.strength(50.0f).destroyTime(50.0f)));

    /** Public no-arg constructor required by ServiceLoader. */
    public Blocks() {}
}
