package wd40.vaakx.cog.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import wd40.lubricant.api.init.ClientInit;
import wd40.lubricant.api.client.renderer.entity.EntityRenderers;
import wd40.lubricant.api.client.gui.render.HudRenderers;
import wd40.lubricant.api.client.gui.screens.MenuRenderers;
import wd40.lubricant.api.client.renderer.particle.ParticleRenderers;
import wd40.lubricant.api.event.ClientEvent;
import wd40.vaakx.cog.Cog;
import wd40.vaakx.cog.common.entities.Entities;
import wd40.vaakx.cog.common.menus.Menus;
import wd40.vaakx.cog.common.particles.Particles;

/**
 * All of cog's client-side setup in one place. Mirrors {@link wd40.vaakx.cog.server.Server}
 * for the client side.
 *
 * <p>Loaded as a regular {@link wd40.lubricant.api.init.ClientInit} - listed in
 * {@code META-INF/services/wd40.lubricant.api.init.ClientInit}. Lubricant's
 * {@link EntityRenderers} facade short-circuits to no-op on dedicated server, so
 * this class is safe to load on any side; the actual renderer registration
 * only takes effect on the client.</p>
 */
public final class Client implements ClientInit {

    private static int tickCounter = 0;

    static {
        EntityRenderers.entity(Entities.SPINNING_COG, SpinningCogRenderer::new);
        // Reuse vanilla EndRodParticle.Provider for the gear_spark sprite particle.
        ParticleRenderers.register(Particles.GEAR_SPARK, EndRodParticle.Provider::new);

        // Smoke test: HUD layer drawn above the air-bar (NeoForge) / on top of HUD (Fabric).
        HudRenderers.registerAbove(
                ResourceLocation.fromNamespaceAndPath(Cog.ID, "tick_counter"),
                (graphics, delta) -> {
                    if (graphics.guiHeight() <= 0) return;
                    Component text = Component.literal("cog ticks: " + tickCounter)
                            .withStyle(ChatFormatting.GOLD);
                    graphics.drawString(
                            net.minecraft.client.Minecraft.getInstance().font,
                            text, 4, 4, 0xFFFFFF, true);
                },
                HudRenderers.Anchor.AIR_LEVEL);

        ClientEvent.TICK.register(client -> tickCounter++);

        MenuRenderers.register(Menus.COUNTER, CounterScreen::new);
    }

    public Client() {}
}
