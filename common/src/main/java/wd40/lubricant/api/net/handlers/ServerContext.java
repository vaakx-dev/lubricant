package wd40.lubricant.api.net.handlers;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/**
 * Context passed to a {@link ServerHandler}. Always invoked on the main server
 * thread.
 *
 * <p>{@link #player()} returns a {@link ServerPlayer} directly - no cast from
 * generic {@code Player}.</p>
 */
public interface ServerContext {

    /** The server instance handling this connection. */
    MinecraftServer server();

    /** The player who sent this payload. */
    ServerPlayer player();
}
