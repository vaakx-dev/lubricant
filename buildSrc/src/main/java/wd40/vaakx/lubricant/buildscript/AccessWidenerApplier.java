package wd40.vaakx.lubricant.buildscript;

import net.fabricmc.accesswidener.AccessWidener;
import net.fabricmc.accesswidener.AccessWidenerClassVisitor;
import net.fabricmc.accesswidener.AccessWidenerReader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

// Reads .accesswidener files and rewrites a jar so widened classes/fields/methods are
// accessible to the compiler. Uses Fabric's access-widener library (the canonical reader)
// + plain ASM to walk + transform classes inside the jar in place.
//
// Operates on a *copy* of the input - the caller passes input + output paths.

public final class AccessWidenerApplier {

    public void apply(Path inputJar, List<Path> awFiles, Path outputJar) throws IOException {
        if (awFiles.isEmpty()) {
            // No-op. Just copy.
            Files.copy(inputJar, outputJar, StandardCopyOption.REPLACE_EXISTING);
            return;
        }

        // Read all AW files into a single AccessWidener.
        AccessWidener aw = new AccessWidener();
        AccessWidenerReader reader = new AccessWidenerReader(aw);
        for (Path awFile : awFiles) {
            try (InputStream in = Files.newInputStream(awFile);
                 var br = new java.io.BufferedReader(new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8))) {
                reader.read(br);
            }
        }

        Files.copy(inputJar, outputJar, StandardCopyOption.REPLACE_EXISTING);

        try (FileSystem fs = FileSystems.newFileSystem(outputJar, Map.of())) {
            for (Path root : fs.getRootDirectories()) {
                try (Stream<Path> walk = Files.walk(root)) {
                    walk.filter(p -> p.toString().endsWith(".class"))
                        .forEach(p -> rewriteClass(p, aw));
                }
            }
        }
    }

    private static void rewriteClass(Path classFile, AccessWidener aw) {
        try {
            byte[] in = Files.readAllBytes(classFile);
            ClassReader cr = new ClassReader(in);
            ClassWriter cw = new ClassWriter(0);
            cr.accept(AccessWidenerClassVisitor.createClassVisitor(
                    org.objectweb.asm.Opcodes.ASM9, cw, aw), 0);
            try (OutputStream out = Files.newOutputStream(classFile)) {
                out.write(cw.toByteArray());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to rewrite " + classFile, e);
        }
    }
}
