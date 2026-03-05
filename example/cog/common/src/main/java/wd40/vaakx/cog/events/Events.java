package wd40.vaakx.cog.events;

import wd40.lubricant.api.Init;
import wd40.vaakx.cog.Cog;

// Demonstrates lubricant's Events API. Subscriptions register inside the static
// initializer, which runs when ServiceLoader instantiates this class during
// Bootstrap.loadAllInit() - i.e. during the loader's startup phase, before any of
// these events have a chance to fire.
//
// The simple-name clash with wd40.lubricant.api.Events is harmless because we
// reference the lubricant class by FQN below.
public final class Events implements Init {

    static {
        wd40.lubricant.api.Events.serverStart().subscribe(server ->
                Cog.LOG.info("server starting: {}", server.getServerVersion()));

        wd40.lubricant.api.Events.playerJoin().subscribe(player ->
                Cog.LOG.info("player joined: {}", player.getScoreboardName()));

        wd40.lubricant.api.Events.playerLeave().subscribe(player ->
                Cog.LOG.info("player left: {}", player.getScoreboardName()));
    }

    public Events() {}
}
