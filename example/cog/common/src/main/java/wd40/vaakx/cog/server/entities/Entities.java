package wd40.vaakx.cog.server.entities;

import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import wd40.lubricant.api.Init;
import wd40.lubricant.api.data.Key;
import wd40.lubricant.api.data.Keys;
import wd40.lubricant.api.registry.EntityRegistry;
import wd40.vaakx.cog.Cog;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Cog's {@link EntityType} registrations + the entity-attached data {@link Key}s.
 * Behavior subscribers (e.g. playerJoin OWNER capture) live in
 * {@link wd40.vaakx.cog.server.Server}; renderer registration lives in
 * {@link wd40.vaakx.cog.client.Client}.
 *
 * <p>Note the simple-name collision with {@code wd40.lubricant.api.data.Entities}
 * (the lubricant attached-data facade) - this file fully-qualifies that facade
 * inside the static block.</p>
 */
public final class Entities implements Init {

    public static final EntityRegistry TYPES = EntityRegistry.create(Cog.ID);

    /** Invisible no-op entity. Summon with {@code /summon cog:spinning_cog}. */
    public static final Supplier<EntityType<SpinningCog>> SPINNING_COG = TYPES.register("spinning_cog",
            EntityType.Builder.<SpinningCog>of(SpinningCog::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f));

    private static final Keys KEYS = Keys.create(Cog.ID);

    /** Player UUID, captured on first join and re-logged on every subsequent join. */
    public static final Key<UUID> OWNER = KEYS.of("owner", UUIDUtil.CODEC);

    static {
        wd40.lubricant.api.data.Entities.register(OWNER);
    }

    public Entities() {}
}
