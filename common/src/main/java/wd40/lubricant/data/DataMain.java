package wd40.lubricant.data;

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
public final class DataMain {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: DataMain <output-resources-dir>");
            System.exit(2);
        }
        Path outputRoot = Path.of(args[0]);
        Files.createDirectories(outputRoot);

        PackOutput packOutput = new PackOutput(outputRoot);
        CachedOutput cache = (path, bytes, hash) -> {
            Files.createDirectories(path.getParent());
            Files.write(path, bytes);
        };

        new Provider(packOutput).run(cache).join();
        System.out.println("[lubricant datagen] wrote assets to " + outputRoot.toAbsolutePath());
    }

    private DataMain() {}
}
