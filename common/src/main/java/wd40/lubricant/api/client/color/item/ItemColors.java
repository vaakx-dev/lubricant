package wd40.lubricant.api.client.color.item;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.Item;
import wd40.lubricant.internal.Datagen;
import wd40.lubricant.internal.Services;
import wd40.lubricant.internal.rendering.RendererHelper;

import java.util.function.Supplier;

/**
 * Registers item-tint handlers. Each handler receives the {@link ItemColor}
 * for an item, called per tint index per render. Layers in the item model
 * with a {@code tintindex} pick up the handler's return value (ARGB int -
 * top byte alpha, low 24 bits RGB).
 *
 * <pre>{@code
 * ItemColors.register(Items.MY_ITEM, (stack, tintIndex) -> {
 *     if (tintIndex != 0) return -1;  // -1 = no tint
 *     int charge = StackData.get(stack, MyKeys.CHARGE);
 *     return 0xFF000000 | (charge * 12 << 16) | ((255 - charge * 12) << 8);
 * });
 * }</pre>
 *
 * <p>Client-only - no-ops on dedicated server / datagen. Call from a
 * {@link wd40.lubricant.api.init.ClientInit} static block.</p>
 */
public final class ItemColors {

    private ItemColors() {}

    public static void register(Supplier<? extends Item> item, ItemColor handler) {
        if (Datagen.IS_DATAGEN) return;
        RendererHelper helper = Services.renderers();
        if (helper == null) return;
        helper.itemColor(item, handler);
    }
}
