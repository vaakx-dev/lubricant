package wd40.lubricant.buildscript;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// Gradle task that produces a Mojang-named (and optionally Parchment-overlaid,
// access-widened) Minecraft client jar at the configured output location.
//
// Caching strategy:
//   ~/.gradle/caches/lubricant-mc/<mc>/client.jar              (Mojang download, sha1-verified)
//   ~/.gradle/caches/lubricant-mc/<mc>/client_mappings.txt     (Mojang download, sha1-verified)
//   ~/.gradle/caches/lubricant-mc/<mc>/parchment-<v>.json      (ParchmentMC download)
//   ~/.gradle/caches/lubricant-mc/<mc>/minecraft-mojmap-p<v>.jar (remapped, parchment-overlaid)
//   <project>/build/minecraft/...-mojmap.jar                   (above + AW applied; @OutputFile)
//
// AW is applied per build into the project-local file because the AW list is per-project.

public abstract class SetupVanillaMinecraftTask extends DefaultTask {

    @Input
    public abstract Property<String> getMcVersion();

    @Input @Optional
    public abstract Property<String> getParchmentVersion();

    @Input @Optional
    public abstract Property<String> getParchmentMcVersion();

    @InputFiles @Optional @PathSensitive(PathSensitivity.RELATIVE)
    public abstract ConfigurableFileCollection getAccessWideners();

    @OutputFile
    public abstract RegularFileProperty getOutputJar();

    @TaskAction
    public void run() throws Exception {
        String mc = getMcVersion().get();

        Path cache = getProject().getGradle().getGradleUserHomeDir().toPath()
                .resolve("caches").resolve("lubricant-mc").resolve(mc);
        Files.createDirectories(cache);

        // 1. Vanilla.
        VanillaDownloader.Result vanilla = new VanillaDownloader().fetch(mc, cache);

        // 2. Parchment (optional).
        String pv = getParchmentVersion().getOrNull();
        String pmc = getParchmentMcVersion().getOrElse(mc);
        Path parchmentJson = null;
        if (pv != null && !pv.isBlank()) {
            getLogger().lifecycle("Lubricant: fetching Parchment {}-{}", pmc, pv);
            parchmentJson = new ParchmentDownloader().fetch(pmc, pv, cache);
        }

        // 3. Remap obf -> mojang(+parchment), cached per (mc, parchment version).
        String mojmapName = pv != null && !pv.isBlank()
                ? "minecraft-mojmap-parchment-" + pmc + "-" + pv + ".jar"
                : "minecraft-mojmap.jar";
        Path cachedMojmap = cache.resolve(mojmapName);

        boolean mojmapStale = !Files.exists(cachedMojmap)
                || Files.getLastModifiedTime(cachedMojmap).toMillis()
                    < Files.getLastModifiedTime(vanilla.clientJar()).toMillis()
                || (parchmentJson != null
                    && Files.getLastModifiedTime(cachedMojmap).toMillis()
                        < Files.getLastModifiedTime(parchmentJson).toMillis());

        if (mojmapStale) {
            getLogger().lifecycle("Lubricant: remapping {} -> Mojang names{}",
                    vanilla.clientJar().getFileName(),
                    parchmentJson != null ? " + Parchment" : "");
            new MojangMappingsRemapper().remap(vanilla.clientJar(), vanilla.mappings(), parchmentJson, cachedMojmap);
        } else {
            getLogger().lifecycle("Lubricant: cached mojmap jar is up to date.");
        }

        // 4. Apply access wideners (if any) to a project-local copy.
        Path target = getOutputJar().get().getAsFile().toPath();
        Files.createDirectories(target.getParent());
        List<Path> awFiles = getAccessWideners().getFiles().stream()
                .map(java.io.File::toPath)
                .toList();
        new AccessWidenerApplier().apply(cachedMojmap, awFiles, target);

        getLogger().lifecycle("Lubricant: vanilla MC {} ready at {}{}{}",
                mc, target,
                pv != null ? " [parchment " + pv + "]" : "",
                !awFiles.isEmpty() ? " [aw: " + awFiles.size() + " file(s)]" : "");
    }
}
