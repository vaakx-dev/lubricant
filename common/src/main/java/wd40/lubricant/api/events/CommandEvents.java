package wd40.lubricant.api.events;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import wd40.lubricant.core.Datagen;
import wd40.lubricant.core.Services;

import java.util.function.Consumer;

/**
 * Command-system events. {@link #REGISTER} fires on every {@code /reload};
 * keep registration logic idempotent.
 */
public final class CommandEvents {

    private CommandEvents() {}

    public static final Event<Consumer<CommandDispatcher<CommandSourceStack>>> REGISTER =
            Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().commands();
}
