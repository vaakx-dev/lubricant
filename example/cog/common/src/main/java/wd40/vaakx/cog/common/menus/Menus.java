package wd40.vaakx.cog.common.menus;

import net.minecraft.world.inventory.MenuType;
import wd40.lubricant.api.init.CommonInit;
import wd40.lubricant.api.inventory.MenuRegistry;
import wd40.lubricant.api.registry.RegistrySupplier;
import wd40.vaakx.cog.Cog;

public final class Menus implements CommonInit {

    public static final MenuRegistry MENUS = MenuRegistry.create(Cog.ID);

    public static final RegistrySupplier<MenuType<CounterMenu>> COUNTER =
            MENUS.register("counter", CounterMenu::new);

    public Menus() {}
}
