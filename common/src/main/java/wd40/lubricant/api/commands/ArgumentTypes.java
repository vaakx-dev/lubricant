package wd40.lubricant.api.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import wd40.lubricant.internal.Datagen;

import java.util.ArrayList;
import java.util.List;

/**
 * Registers custom Brigadier {@link ArgumentType}s with the
 * {@code COMMAND_ARGUMENT_TYPE} registry and the class-to-info map vanilla
 * uses to sync the type to clients. The loader entry drains every
 * {@link ArgumentTypes#ALL} registry at the right phase.
 *
 * <pre>{@code
 * private static final ArgumentTypes ARG_TYPES = ArgumentTypes.create("mymod");
 *
 * static {
 *     ARG_TYPES.register("cog_count", CogCountArgument.class,
 *             SingletonArgumentInfo.contextFree(CogCountArgument::cogCount));
 * }
 * }</pre>
 *
 * <p>Use the registered {@link ArgumentType} like any other in command
 * declarations: {@code argument("amount", CogCountArgument.cogCount())}. The
 * sync layer handles the rest.</p>
 *
 * <p><a href="https://github.com/vaakx-dev/lubricant/wiki/ArgumentTypes">ArgumentTypes wiki</a></p>
 */
public final class ArgumentTypes {

    public static final List<ArgumentTypes> ALL = new ArrayList<>();

    public record Entry<A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>>(
            String path,
            Class<? extends A> argClass,
            ArgumentTypeInfo<A, T> info) {}

    private final String modId;
    private final List<Entry<?, ?>> entries = new ArrayList<>();

    private ArgumentTypes(String modId) { this.modId = modId; }

    public static ArgumentTypes create(String modId) {
        ArgumentTypes registry = new ArgumentTypes(modId);
        if (!Datagen.IS_DATAGEN) ALL.add(registry);
        return registry;
    }

    public String modId() { return modId; }
    public List<Entry<?, ?>> entries() { return entries; }

    public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void register(
            String path, Class<? extends A> argClass, ArgumentTypeInfo<A, T> info) {
        if (Datagen.IS_DATAGEN) return;
        entries.add(new Entry<>(path, argClass, info));
    }
}
