package wd40.lubricant.api.net.handlers;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

// Context passed to a ServerHandler when a client-bound payload arrives. Always
// invoked on the main server thread - safe to touch the world, mutate entities, etc.
public interface ServerContext {
    MinecraftServer server();
    ServerPlayer player();
}
