package wd40.vaakx.cog.events;

import wd40.lubricant.api.Init;
import wd40.lubricant.api.events.Events;
import wd40.vaakx.cog.Cog;

/**
 * All of cog's event subscriptions. Same lifecycle as
 * {@link wd40.vaakx.cog.items.Items} - see Items.java for the full explanation.
 *
 * <p>Subscriptions live in {@code static {}} instead of static fields because
 * {@code Events.X().subscribe(...)} doesn't return a value worth storing. Both
 * styles run during {@code <clinit>}.</p>
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/Events">Events wiki</a></p>
 */
public final class Listeners implements Init {

    static {
        Events.serverStart().subscribe(server ->
                Cog.LOG.info("server starting: {}", server.getServerVersion()));

        Events.playerJoin().subscribe(player ->
                Cog.LOG.info("player joined: {}", player.getScoreboardName()));

        Events.playerLeave().subscribe(player ->
                Cog.LOG.info("player left: {}", player.getScoreboardName()));
    }

    public Listeners() {}
}
