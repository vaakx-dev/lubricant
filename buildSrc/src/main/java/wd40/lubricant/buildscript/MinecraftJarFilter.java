package wd40.lubricant.buildscript;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

// Filters Mojang's client.jar to drop entries that are useless on a compile classpath.
//
// What we drop:
//   - META-INF/MANIFEST.MF (3 MB of class digests, invalid after remap)
//   - META-INF/MOJANGCS.SF (3 MB of signed digests, invalid after remap)
//   - META-INF/MOJANGCS.RSA (signing cert, irrelevant)
//   - any other META-INF/* (no compiler need for them)
//
// What we keep:
//   - net/minecraft/**       Minecraft itself
//   - com/mojang/blaze3d/**  the rendering layer Mojang ships with MC (NOT a separate dep)
//   - assets/**, data/**     vanilla content (textures, recipes, advancements)
//   - pack.mcmeta            vanilla resource pack manifest
//   - version.json           MC version info
//   - any other top-level .class file or resource not in META-INF
//
// Note: MC's other bundled libs (authlib, datafixers, brigadier, etc.) are NOT inside
// client.jar - they're separate jars listed in Mojang's version manifest. So this
// filter only strips signing metadata, not any actual code.

public final class MinecraftJarFilter {

    public static void filter(Path inputJar, Path outputJar) throws IOException {
        Files.createDirectories(outputJar.getParent());
        Files.deleteIfExists(outputJar);

        try (InputStream in = Files.newInputStream(inputJar);
             ZipInputStream zin = new ZipInputStream(in);
             OutputStream out = Files.newOutputStream(outputJar);
             ZipOutputStream zout = new ZipOutputStream(out)) {

            byte[] buf = new byte[8192];
            ZipEntry entry;
            while ((entry = zin.getNextEntry()) != null) {
                if (shouldDrop(entry.getName())) continue;

                ZipEntry copy = new ZipEntry(entry.getName());
                copy.setTime(entry.getTime());
                zout.putNextEntry(copy);
                int n;
                while ((n = zin.read(buf)) > 0) zout.write(buf, 0, n);
                zout.closeEntry();
            }
        }
    }

    private static boolean shouldDrop(String name) {
        return name.startsWith("META-INF/");
    }

    private MinecraftJarFilter() {}
}
