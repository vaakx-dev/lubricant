package wd40.lubricant.api.inventory;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import wd40.lubricant.api.registry.RegistrySupplier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Registers {@link MenuType}s under one mod's namespace. Same shape as
 * {@code ItemRegistry} - plain data, deferred construction in the loader's
 * binding phase.
 *
 * <p>{@link Factory} is the constructor reference your menu class exposes.
 * The loader passes the synced container id and the player's {@link Inventory}.</p>
 *
 * <pre>{@code
 * public static final MenuRegistry MENUS = MenuRegistry.create(MyMod.ID);
 * public static final RegistrySupplier<MenuType<MyMenu>> MY_MENU =
 *         MENUS.register("my_menu", MyMenu::new);
 * }</pre>
 */
public final class MenuRegistry {

    public static final List<MenuRegistry> ALL = new CopyOnWriteArrayList<>();

    private final String modId;
    private final List<Entry<?>> entries = new ArrayList<>();

    @FunctionalInterface
    public interface Factory<T extends AbstractContainerMenu> {
        T create(int containerId, Inventory inventory);
    }

    public static final class Entry<T extends AbstractContainerMenu> implements RegistrySupplier<MenuType<T>> {
        private final String path;
        private final ResourceLocation id;
        private final Factory<T> factory;
        private MenuType<T> bound;

        Entry(String path, ResourceLocation id, Factory<T> factory) {
            this.path = path;
            this.id = id;
            this.factory = factory;
        }

        public String path() { return path; }
        public Factory<T> factory() { return factory; }

        public void setBound(MenuType<T> type) { this.bound = type; }

        @Override public MenuType<T> get() { return bound; }
        @Override public ResourceLocation id() { return id; }
    }

    public static MenuRegistry create(String modId) {
        MenuRegistry registry = new MenuRegistry(modId);
        ALL.add(registry);
        return registry;
    }

    private MenuRegistry(String modId) {
        this.modId = modId;
    }

    public <T extends AbstractContainerMenu> RegistrySupplier<MenuType<T>> register(String path, Factory<T> factory) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, path);
        Entry<T> entry = new Entry<>(path, id, factory);
        entries.add(entry);
        return entry;
    }

    public String modId() {
        return modId;
    }

    public List<Entry<?>> entries() {
        return Collections.unmodifiableList(entries);
    }
}
