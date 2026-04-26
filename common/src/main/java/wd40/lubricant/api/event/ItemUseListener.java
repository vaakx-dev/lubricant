package wd40.lubricant.api.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Right-click-with-item listener. Return {@link InteractionResult#PASS} to let
 * other handlers and vanilla continue; any other result short-circuits.
 *
 * <p>Fires server-side on NeoForge, both sides on Fabric - check
 * {@code level.isClientSide()} if you only want one.</p>
 */
@FunctionalInterface
public interface ItemUseListener {
    InteractionResult onUse(Player player, Level level, InteractionHand hand);
}
