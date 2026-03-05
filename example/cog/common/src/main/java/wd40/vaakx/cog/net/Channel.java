package wd40.vaakx.cog.net;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import wd40.lubricant.api.Init;
import wd40.lubricant.api.events.Events;
import wd40.lubricant.api.net.Net;
import wd40.lubricant.api.net.S2CPayload;
import wd40.vaakx.cog.Cog;

/**
 * All of cog's networking: the payload type definitions plus the registration +
 * scheduling that wire them up. Holder pattern is the same as
 * {@link wd40.vaakx.cog.events.Listeners} - one file per behavior folder, named
 * for its role ({@code Channel}, because it holds cog's network channel).
 *
 * <h3>The vanilla payload triple</h3>
 *
 * Custom packets in MC 1.21+ are vanilla constructs, not loader-specific. Each one
 * needs three things:
 * <ul>
 *   <li>A {@link CustomPacketPayload} (typically a record) with a {@link CustomPacketPayload.Type}
 *       carrying its {@link ResourceLocation} id.</li>
 *   <li>A {@link StreamCodec} for serialization. Use {@code StreamCodec.composite}
 *       for records: pair each codec with its getter, end with the constructor.</li>
 *   <li>A {@code type()} method on the payload returning its Type.</li>
 * </ul>
 * Lubricant's API takes the Type + codec as separate parameters so the loader
 * can register them with its own networking system.
 *
 * <h3>S2CPayload vs C2SPayload</h3>
 *
 * {@link Net#toClient} returns an {@link S2CPayload} - it has {@code sendTo},
 * {@code sendToAll}, {@code sendToDimension}, {@code sendToTracking}, but NOT
 * {@code sendToServer}. {@link Net#toServer} returns a
 * {@link wd40.lubricant.api.net.C2SPayload} - it has only {@code sendToServer}.
 * The compiler refuses to call the wrong-direction method, catching direction
 * mismatch bugs before runtime.
 *
 * <h3>Periodic broadcasts</h3>
 *
 * The {@code static {}} block subscribes to {@link Events#serverTick} and uses
 * a modulo throttle to send once every N ticks (20 ticks = 1 second). This is
 * the standard pattern for any "push state to clients regularly" feature.
 *
 * <h3>Threading</h3>
 *
 * The handler lambda passed to {@link Net#toClient} runs on the main client thread,
 * so it's safe to touch the GUI, the world, the player, etc. directly. Same for
 * the {@link Net#toServer} handler on the server thread. Lubricant does the
 * {@code enqueueWork} hop on NeoForge internally to make this guarantee uniform.
 */
public final class Channel implements Init {

    /** A trivial S2C payload carrying just the server's tick count. */
    public record HelloPayload(int tickStamp) implements CustomPacketPayload {
        public static final Type<HelloPayload> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(Cog.ID, "hello"));

        public static final StreamCodec<RegistryFriendlyByteBuf, HelloPayload> CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.VAR_INT, HelloPayload::tickStamp,
                        HelloPayload::new);

        @Override public Type<HelloPayload> type() { return TYPE; }
    }

    /**
     * The S2C handle for HelloPayload. Registration happens at class-load time
     * (during {@code Bootstrap.loadAllInit}) - the loader has the codec installed
     * and the client receiver wired before any packet can fly.
     */
    public static final S2CPayload<HelloPayload> HELLO = Net.toClient(
            HelloPayload.TYPE, HelloPayload.CODEC,
            (payload, ctx) -> Cog.LOG.info("client received hello @ tick {}", payload.tickStamp()));

    static {
        // Broadcast to every connected player once per second.
        Events.serverTick().subscribe(server -> {
            int tick = server.getTickCount();
            if (tick % 20 != 0) return;
            HELLO.sendToAll(server, new HelloPayload(tick));
        });

        // Compile-error check: uncomment to verify the direction split works.
        // The line below should NOT compile - sendToServer doesn't exist on S2CPayload.
        // HELLO.sendToServer(new HelloPayload(0));
    }

    /** Public no-arg constructor required by ServiceLoader. Body intentionally empty. */
    public Channel() {}
}
