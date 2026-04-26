package wd40.lubricant.api.event;

import wd40.lubricant.api.event.Event;
import wd40.lubricant.internal.Datagen;
import wd40.lubricant.internal.Services;

/**
 * Entity interaction events. {@link #INTERACT} fires on right-click against an
 * entity; return {@code InteractionResult.PASS} to let other handlers and
 * vanilla continue, any other result short-circuits.
 *
 * <p>Fires server-side on NeoForge, both sides on Fabric - check
 * {@code player.level().isClientSide()} if you only want one.</p>
 */
public final class EntityEvent {

    private EntityEvent() {}

    public static final Event<EntityInteractListener> INTERACT =
            Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().entityInteract();
}
