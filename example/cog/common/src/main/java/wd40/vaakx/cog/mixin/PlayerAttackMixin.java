package wd40.vaakx.cog.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wd40.vaakx.cog.Cog;

/**
 * Smoke test that mixin works alongside lubricant. Logs every server-side
 * Player.attack call. Cog itself doesn't need this; it's here so cog runs prove
 * mixin loading on both loaders.
 *
 * <p>Mixin lives in {@code cog/common/} (loader-agnostic - target is vanilla
 * {@code Player}). Loader configs both reference {@code cog.mixins.json}.</p>
 */
@Mixin(Player.class)
public abstract class PlayerAttackMixin {

    @Inject(method = "attack", at = @At("HEAD"))
    private void cog$logAttack(Entity target, CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (!self.level().isClientSide()) {
            Cog.LOG.info("[mixin] attack: {} -> {}", self.getName().getString(), target);
        }
    }
}
