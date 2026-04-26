package wd40.lubricant.api.event;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import wd40.lubricant.api.event.Event;
import wd40.lubricant.internal.Datagen;
import wd40.lubricant.internal.Services;

import java.util.function.Consumer;

/**
 * Command-system events. {@link #REGISTER} fires on every {@code /reload};
 * keep registration logic idempotent.
 */
public final class CommandEvent {

    private CommandEvent() {}

    public static final Event<Consumer<CommandDispatcher<CommandSourceStack>>> REGISTER =
            Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().commands();
}
