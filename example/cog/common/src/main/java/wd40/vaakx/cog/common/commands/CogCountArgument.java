package wd40.vaakx.cog.common.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.network.chat.Component;

/**
 * Tiny custom Brigadier argument: an integer in {@code [1, 100]}. Demo for the
 * lubricant {@link wd40.lubricant.api.commands.ArgumentTypes} surface.
 */
public final class CogCountArgument implements ArgumentType<Integer> {

    private static final SimpleCommandExceptionType OUT_OF_RANGE =
            new SimpleCommandExceptionType(Component.literal("cog count must be 1-100"));

    private CogCountArgument() {}

    public static CogCountArgument cogCount() { return new CogCountArgument(); }

    public static int getCogCount(CommandContext<?> context, String name) {
        return context.getArgument(name, Integer.class);
    }

    @Override
    public Integer parse(StringReader reader) throws CommandSyntaxException {
        int value = reader.readInt();
        if (value < 1 || value > 100) throw OUT_OF_RANGE.create();
        return value;
    }
}
