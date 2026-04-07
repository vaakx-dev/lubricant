package wd40.lubricant.api.events;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * Right-click-on-entity listener. Return {@link InteractionResult#PASS} to let
 * other handlers and vanilla continue; any other result short-circuits.
 *
 * <p>Fires server-side on NeoForge, both sides on Fabric - check
 * {@code player.level().isClientSide()} if you only want one.</p>
 */
@FunctionalInterface
public interface EntityInteractListener {
    InteractionResult onInteract(Player player, Entity target, InteractionHand hand);
}
