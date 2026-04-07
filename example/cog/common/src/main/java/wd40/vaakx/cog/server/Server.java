package wd40.vaakx.cog.server;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import wd40.lubricant.api.Init;
import wd40.lubricant.api.data.Entities;
import wd40.lubricant.api.data.Stacks;
import wd40.lubricant.api.events.Events;
import wd40.vaakx.cog.Cog;
import wd40.vaakx.cog.server.items.Items;
import wd40.vaakx.cog.server.net.Channel;

import java.util.UUID;

/**
 * All of cog's server-side behavior in one place. Other Init classes
 * ({@link Items}, {@link wd40.vaakx.cog.server.entities.Entities}, etc.) only
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
        Events.serverStart().subscribe(server ->
                Cog.LOG.info("server starting: {}", server.getServerVersion()));

        Events.serverStop().subscribe(server ->
                Cog.LOG.info("server stopping"));

        Events.playerJoin().subscribe(player -> {
            Cog.LOG.info("player joined: {}", player.getScoreboardName());

            UUID stored = Entities.get(player, wd40.vaakx.cog.server.entities.Entities.OWNER);
            if (stored == null) {
                Entities.set(player, wd40.vaakx.cog.server.entities.Entities.OWNER, player.getUUID());
                Cog.LOG.info("first join, OWNER set to {}", player.getUUID());
            } else {
                Cog.LOG.info("welcome back, OWNER={}", stored);
            }
        });

        Events.playerLeave().subscribe(player ->
                Cog.LOG.info("player left: {}", player.getScoreboardName()));

        Events.itemUse().subscribe((player, level, hand) -> {
            // itemUse fires on both sides; mutate only on server. Vanilla syncs the new
            // component back to the client.
            if (level.isClientSide()) return InteractionResult.PASS;
            ItemStack held = player.getItemInHand(hand);
            if (held.is(Items.GREASED_COG.get())) {
                int next = Stacks.get(held, Items.CHARGE) + 1;
                Stacks.set(held, Items.CHARGE, next);
                Cog.LOG.info("greased_cog charge -> {}", next);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });

        Events.serverTick().subscribe(server -> {
            int tick = server.getTickCount();
            if (tick % 20 != 0) return;
            Channel.HELLO.sendToAll(server, new Channel.HelloPayload(tick));
        });
    }

    public Server() {}
}
