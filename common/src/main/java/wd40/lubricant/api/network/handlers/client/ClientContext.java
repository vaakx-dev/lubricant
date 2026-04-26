package wd40.lubricant.api.network.handlers.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

/** {@link ClientHandler} context. {@link #player()} is the local player - no cast needed. */
public interface ClientContext {
    Minecraft client();
    LocalPlayer player();
}
