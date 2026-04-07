package wd40.lubricant.datagen;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Entry point for {@code ./gradlew runDatagen}. Boots a stripped JVM with vanilla
 * MC + lubricant common + the consumer mod on classpath, runs {@link Provider},
 * writes JSON straight to the output directory passed as {@code args[0]}.
 *
 * <p>No fabric-loader, no NeoForge runtime, no per-loader datagen wiring. The
 * generated tree is loader-agnostic - both Fabric and NeoForge load it from
 * {@code src/main/generated/resources/assets/...} as a normal resource srcDir.</p>
 */
public final class DatagenMain {

    public static void main(String[] args) throws Exception {
        // Tell lubricant facades (Events, Net, Stacks, etc.) that we're running the datagen
        // JVM - no loader module on classpath, so Services lookups would otherwise blow up
        // when consumer mods call Stacks.register / Events.X().subscribe / Net.toClient
        // from their static blocks. With the flag set, those calls become no-ops.
        System.setProperty("lubricant.datagen", "true");

        if (args.length < 1) {
            System.err.println("Usage: DatagenMain <output-resources-dir> [<hand-authored-resources-dir>]");
            System.exit(2);
        }
        Path outputRoot = Path.of(args[0]);
        Files.createDirectories(outputRoot);

        // Optional second arg points at the consumer mod's src/main/resources so we can
        // skip generating any file the modder already hand-authored. Without it, datagen
        // overwrites blindly (and Gradle resource processing may complain about duplicates).
        Path handAuthoredRoot = args.length >= 2 ? Path.of(args[1]) : null;

        PackOutput packOutput = new PackOutput(outputRoot);
        CachedOutput cache = (path, bytes, hash) -> {
            Files.createDirectories(path.getParent());
            Files.write(path, bytes);
        };

        new Provider(packOutput, handAuthoredRoot).run(cache).join();
        System.out.println("[lubricant datagen] wrote assets to " + outputRoot.toAbsolutePath());
    }

    private DatagenMain() {}
}
