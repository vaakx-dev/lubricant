package wd40.lubricant.api.events;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Listener shape for {@link Events#itemUse()}. Fires on right-click-with-item.
 *
 * <p>Return {@link InteractionResult#PASS} to let other handlers and vanilla
 * continue. Any other result short-circuits further handling and applies that
 * result to the interaction.</p>
 *
 * <p>Fires server-side on NeoForge and on both sides on Fabric. If you only
 * want one side, check {@code level.isClientSide()} and return {@code PASS}
 * on the side you don't care about.</p>
 */
@FunctionalInterface
public interface ItemUseListener {
    InteractionResult onUse(Player player, Level level, InteractionHand hand);
}
