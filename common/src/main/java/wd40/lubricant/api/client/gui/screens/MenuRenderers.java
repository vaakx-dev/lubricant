package wd40.lubricant.api.client.gui.screens;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import wd40.lubricant.internal.Datagen;
import wd40.lubricant.internal.Services;
import wd40.lubricant.internal.rendering.RendererHelper;

import java.util.function.Supplier;

/**
 * Client-side screen registration for {@link MenuType}s. Same short-circuit
 * contract as the other client facades.
 *
 * <pre>{@code
 * MenuRenderers.register(Menus.MY_MENU, MyScreen::new);
 * }</pre>
 */
public final class MenuRenderers {

    private MenuRenderers() {}

    public static <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void register(
            Supplier<? extends MenuType<? extends M>> type,
            MenuScreens.ScreenConstructor<M, S> screen) {
        if (Datagen.IS_DATAGEN) return;
        RendererHelper helper = Services.renderers();
        if (helper == null) return;
        helper.menuScreen(type, screen);
    }
}
