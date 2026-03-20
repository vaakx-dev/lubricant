package wd40.lubricant.api.net.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

/**
 * Context passed to a {@link ClientHandler}. Always invoked on the main client
 * thread.
 *
 * <p>{@link #player()} returns a {@link LocalPlayer} directly - no cast from
 * generic {@code Player}.</p>
 */
public interface ClientContext {

    /** The {@link Minecraft} instance for this client. */
    Minecraft client();

    /** The local player who received this payload. */
    LocalPlayer player();
}
