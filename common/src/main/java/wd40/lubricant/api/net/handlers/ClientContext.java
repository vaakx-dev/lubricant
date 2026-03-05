package wd40.lubricant.api.net.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

// Context passed to a ClientHandler when a server-bound payload arrives. Always
// invoked on the main client thread - safe to touch the world, GUI, etc.
public interface ClientContext {
    Minecraft client();
    LocalPlayer player();
}
