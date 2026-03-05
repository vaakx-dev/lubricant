package wd40.lubricant.api;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

// Fires on right-click-with-item (server side on NeoForge, both sides on Fabric -
// check level.isClientSide if you only want one). Return InteractionResult.PASS to let
// other handlers and vanilla continue. Any other result short-circuits further handling.
@FunctionalInterface
public interface ItemUseListener {
    InteractionResult onUse(Player player, Level level, InteractionHand hand);
}
