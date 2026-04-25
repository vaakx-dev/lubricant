package wd40.vaakx.cog.common.tabs;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import wd40.lubricant.api.Init;
import wd40.lubricant.api.registry.CreativeTabRegistry;
import wd40.vaakx.cog.Cog;
import wd40.vaakx.cog.common.blocks.Blocks;
import wd40.vaakx.cog.common.items.Items;

import java.util.function.Supplier;

/**
 * Smoke test for {@link CreativeTabRegistry}. One tab containing cog's two items.
 * Tab title is a literal Component to avoid a lang dependency for the smoke test.
 */
public final class Tabs implements Init {

    public static final CreativeTabRegistry TABS = CreativeTabRegistry.create(Cog.ID);

    /** Resource id: {@code cog:cog}. */
    public static final Supplier<CreativeModeTab> COG_TAB = TABS.register("cog", builder -> builder
            .title(Component.literal("Cog"))
            .icon(() -> new ItemStack(Items.COG))
            .displayItems((params, output) -> {
                for (var entry : Items.ITEMS.entries()) {
                    output.accept(entry.bound());
                }
                for (var entry : Blocks.BLOCKS.entries()) {
                    if (Blocks.BLOCKS.isNoItem(entry.path())) continue;
                    output.accept(entry.bound());
                }
            }));

    public Tabs() {}
}
