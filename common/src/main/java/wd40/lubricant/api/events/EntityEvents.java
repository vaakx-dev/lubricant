package wd40.lubricant.api.events;

import wd40.lubricant.core.Datagen;
import wd40.lubricant.core.Services;

/**
 * Entity interaction events. {@link #INTERACT} fires on right-click against an
 * entity; return {@code InteractionResult.PASS} to let other handlers and
 * vanilla continue, any other result short-circuits.
 *
 * <p>Fires server-side on NeoForge, both sides on Fabric - check
 * {@code player.level().isClientSide()} if you only want one.</p>
 */
public final class EntityEvents {

    private EntityEvents() {}

    public static final Event<EntityInteractListener> INTERACT =
            Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().entityInteract();
}
