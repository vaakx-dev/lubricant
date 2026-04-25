package wd40.lubricant.api.server.net.handlers;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/** {@link ServerHandler} context. {@link #player()} is the sending player - no cast needed. */
public interface ServerContext {
    MinecraftServer server();
    ServerPlayer player();
}
