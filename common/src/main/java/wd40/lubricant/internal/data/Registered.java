package wd40.lubricant.internal.data;

import net.minecraft.resources.ResourceLocation;
import wd40.lubricant.api.data.DataKey;

import java.util.HashMap;
import java.util.Map;

/**
 * Cross-facade dedup table. Each {@link DataKey} can register on at most one of
 * StackData, BlockEntityData, EntityData, Worlds. Registering the same key on two
 * facades is almost always a modder mistake (the loader-side AttachmentType
 * registry forbids duplicate ids anyway); we throw early with a clearer
 * message instead of letting vanilla blow up later.
 *
 * <p>Modders who want similar data on multiple holder kinds should declare
 * separate keys with different paths.</p>
 *
 * <p>Threading: registration runs on the main mod-init thread (or NeoForge
 * mod-bus thread for queue-and-drain). Concurrent registration is not
 * expected; the table is unsynchronized.</p>
 */
public final class Registered {

    /** Names of the public facades that can claim a DataKey. Used in error messages. */
    public enum Facade { STACKS, BLOCK_ENTITIES, ENTITIES, WORLDS }

    private static final Map<ResourceLocation, Facade> CLAIMED = new HashMap<>();

    /**
     * Claim {@code key} for {@code facade}. Throws if the key is already claimed
     * by a different facade.
     */
    public static void claim(DataKey<?> key, Facade facade) {
        Facade existing = CLAIMED.putIfAbsent(key.id(), facade);
        if (existing != null && existing != facade) {
            throw new IllegalStateException(
                    "Key " + key.id() + " is already registered on " + existing
                    + ", cannot also register on " + facade
                    + " - declare a separate Key with a different path for cross-facade data");
        }
    }

    private Registered() {}
}
