package wd40.vaakx.cog.data;

import com.mojang.serialization.Codec;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import wd40.lubricant.api.Init;
import wd40.lubricant.api.data.Key;
import wd40.lubricant.api.data.Keys;
import wd40.lubricant.api.data.Stacks;
import wd40.lubricant.api.events.Events;
import wd40.vaakx.cog.Cog;
import wd40.vaakx.cog.items.Items;

/**
 * Demo of the unified attached-data API. Declares one item-attached key
 * ({@link #CHARGE}) and wires it to itemUse: every right-click on a greased_cog
 * increments the count, persisted via vanilla's component machinery on 1.21
 * and via NBT under {@code cog:charge} on 1.20 (when that branch lands).
 *
 * <p>Loaded as a regular {@link Init} - listed in
 * {@code META-INF/services/wd40.lubricant.api.Init} - so its static block runs
 * during {@code Bootstrap.loadAllInit()} early in mod boot.</p>
 */
public final class Attachments implements Init {

    private static final Keys KEYS = Keys.create(Cog.ID);

    /** Click-counter on a greased_cog ItemStack. Persists across logout/world reload. */
    public static final Key<Integer> CHARGE = KEYS.of("charge", Codec.INT, 0);

    static {
        Stacks.register(CHARGE);

        Events.itemUse().subscribe((player, level, hand) -> {
            // itemUse fires on both client and server (loader event model).
            // Mutate state only on the server; vanilla syncs the new component back to the client.
            if (level.isClientSide()) return InteractionResult.PASS;
            ItemStack held = player.getItemInHand(hand);
            if (held.is(Items.GREASED_COG.get())) {
                int next = Stacks.get(held, CHARGE) + 1;
                Stacks.set(held, CHARGE, next);
                Cog.LOG.info("greased_cog charge -> {}", next);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });
    }

    public Attachments() {}
}
