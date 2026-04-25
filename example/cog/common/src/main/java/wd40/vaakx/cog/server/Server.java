package wd40.vaakx.cog.server;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import wd40.lubricant.api.Init;
import wd40.lubricant.api.data.EntityData;
import wd40.lubricant.api.data.StackData;
import wd40.lubricant.api.events.ItemEvents;
import wd40.lubricant.api.events.PlayerEvents;
import wd40.lubricant.api.events.ServerEvents;
import wd40.vaakx.cog.Cog;
import wd40.vaakx.cog.common.entities.Entities;
import wd40.vaakx.cog.common.items.Items;
import wd40.vaakx.cog.common.net.Channel;
import wd40.vaakx.cog.common.particles.Particles;
import wd40.vaakx.cog.common.sounds.Sounds;
import wd40.vaakx.cog.server.net.HelloPayload;

import java.util.UUID;

/**
 * All of cog's server-side behavior in one place. Other Init classes
 * ({@link Items}, {@link Entities}, etc., living in {@code common/}) only
 * <i>declare</i> things (registry entries, data keys); behavior - event
 * subscribers, listener bodies - lives here.
 *
 * <p>Conceptually server-only even though Lubricant's event facades are
 * side-aware: the subscriber bodies all use {@code ServerPlayer} /
 * {@code MinecraftServer} types or guard with {@code !level.isClientSide()}.
 * Co-locating them makes "what does cog do at runtime on the server" a
 * one-file question.</p>
 */
public final class Server implements Init {

    static {
        ServerEvents.START.register(server ->
                Cog.LOG.info("server starting: {}", server.getServerVersion()));

        ServerEvents.STOP.register(server ->
                Cog.LOG.info("server stopping"));

        PlayerEvents.JOIN.register(player -> {
            Cog.LOG.info("player joined: {}", player.getScoreboardName());

            UUID stored = EntityData.get(player, Entities.OWNER);
            if (stored == null) {
                EntityData.set(player, Entities.OWNER, player.getUUID());
                Cog.LOG.info("first join, OWNER set to {}", player.getUUID());
            } else {
                Cog.LOG.info("welcome back, OWNER={}", stored);
            }
        });

        PlayerEvents.LEAVE.register(player ->
                Cog.LOG.info("player left: {}", player.getScoreboardName()));

        ItemEvents.USE.register((player, level, hand) -> {
            // ItemEvents.USE fires on both sides; mutate only on server. Vanilla syncs the new
            // component back to the client.
            if (level.isClientSide()) return InteractionResult.PASS;
            ItemStack held = player.getItemInHand(hand);
            if (held.is(Items.GREASED_COG)) {
                int next = StackData.get(held, Items.CHARGE) + 1;
                StackData.set(held, Items.CHARGE, next);
                Cog.LOG.info("greased_cog charge -> {}", next);
                playFeedback(player, level);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });

        ServerEvents.TICK.register(server -> {
            int tick = server.getTickCount();
            if (tick % 20 != 0) return;
            Channel.HELLO.sendToAll(server, new HelloPayload(tick));
        });
    }

    private static void playFeedback(Player player, Level level) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                Sounds.GEAR_CLICK.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
        if (level instanceof ServerLevel server) {
            server.sendParticles(Particles.GEAR_SPARK.get(),
                    player.getX(), player.getY() + 1.2, player.getZ(),
                    8, 0.2, 0.2, 0.2, 0.05);
        }
    }

    public Server() {}
}
