package wd40.vaakx.lubricant.buildscript;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.nio.file.Files;
import java.nio.file.Path;

// Gradle task that produces a Mojang-named Minecraft client jar at the configured
// output location. Downloads + remap happens once per (mcVersion, gradle user home)
// and is shared across projects via the gradle user cache.

public abstract class SetupVanillaMinecraftTask extends DefaultTask {

    @Input
    public abstract Property<String> getMcVersion();

    @OutputFile
    public abstract RegularFileProperty getOutputJar();

    @TaskAction
    public void run() throws Exception {
        String version = getMcVersion().get();

        // Shared cache across all projects on this machine, keyed by MC version.
        Path cache = getProject().getGradle().getGradleUserHomeDir().toPath()
                .resolve("caches").resolve("lubricant-mc").resolve(version);
        Files.createDirectories(cache);

        // Download vanilla bits if not already cached + verified.
        VanillaDownloader.Result downloaded = new VanillaDownloader().fetch(version, cache);

        Path target = getOutputJar().get().getAsFile().toPath();

        // If a remapped jar already exists in cache and our target points to it (or matches),
        // we can skip the remap. Check by comparing modification times against inputs.
        Path cachedRemapped = cache.resolve("minecraft-mojmap.jar");
        if (Files.exists(cachedRemapped)
                && Files.getLastModifiedTime(cachedRemapped).toMillis()
                    > Files.getLastModifiedTime(downloaded.clientJar()).toMillis()) {
            getLogger().lifecycle("Lubricant: cached mojmap jar is up to date.");
        } else {
            getLogger().lifecycle("Lubricant: remapping {} -> Mojang names...", downloaded.clientJar().getFileName());
            new MojangMappingsRemapper().remap(downloaded.clientJar(), downloaded.mappings(), cachedRemapped);
        }

        // Copy/link the cached jar to the project-local output location declared as @OutputFile.
        // Gradle uses this for up-to-date checks against downstream tasks.
        Files.createDirectories(target.getParent());
        Files.copy(cachedRemapped, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        getLogger().lifecycle("Lubricant: vanilla MC {} ready at {}", version, target);
    }
}
