package wd40.lubricant.api.event;

import wd40.lubricant.api.event.Event;
import wd40.lubricant.internal.Datagen;
import wd40.lubricant.internal.Services;

/**
 * Item interaction events. {@link #USE} fires on right-click with an item;
 * return {@code InteractionResult.PASS} to let other handlers and vanilla
 * continue, any other result short-circuits.
 *
 * <p>Fires server-side on NeoForge, both sides on Fabric - check
 * {@code level.isClientSide()} if you only want one.</p>
 */
public final class ItemEvent {

    private ItemEvent() {}

    public static final Event<ItemUseListener> USE =
            Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().itemUse();
}
