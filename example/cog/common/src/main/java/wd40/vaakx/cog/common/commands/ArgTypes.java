package wd40.vaakx.cog.common.commands;

import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import wd40.lubricant.api.commands.ArgumentTypes;
import wd40.lubricant.api.init.CommonInit;
import wd40.vaakx.cog.Cog;

/**
 * Registers cog's custom argument types. Same lifecycle as
 * {@link wd40.vaakx.cog.common.items.Items} - see Items.java.
 */
public final class ArgTypes implements CommonInit {

    public static final ArgumentTypes ARG_TYPES = ArgumentTypes.create(Cog.ID);

    static {
        ARG_TYPES.register("cog_count", CogCountArgument.class,
                SingletonArgumentInfo.contextFree(CogCountArgument::cogCount));
    }

    public ArgTypes() {}
}
