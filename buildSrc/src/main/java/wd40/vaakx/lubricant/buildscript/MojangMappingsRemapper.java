package wd40.vaakx.lubricant.buildscript;

import net.fabricmc.mappingio.MappingReader;
import net.fabricmc.mappingio.format.MappingFormat;
import net.fabricmc.mappingio.tree.MappingTree;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import net.fabricmc.tinyremapper.IMappingProvider;
import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// Remaps an obfuscated Minecraft client.jar to use Mojang names (mojmap),
// using Mojang's official ProGuard mappings file as the source of truth.
//
// ProGuard files ship as "named -> obfuscated", so we read them, then tell
// the remapper to go from "official" (obf) -> "named" (mojang).

public final class MojangMappingsRemapper {

    public void remap(Path inputObfJar, Path proguardMappings, Path outputMojmapJar) throws IOException {
        // Parse the ProGuard mappings into an in-memory tree.
        // ProGuard format: left side = mojang names, right side = obfuscated names.
        // mapping-io reads this with src namespace "source" (mojang) and dst "target" (obf).
        MemoryMappingTree tree = new MemoryMappingTree();
        MappingReader.read(proguardMappings, MappingFormat.PROGUARD_FILE, tree);

        // We want to remap an obfuscated jar -> mojang names, so go target -> source.
        IMappingProvider mappingProvider = adapter(tree, "target", "source");

        Files.createDirectories(outputMojmapJar.getParent());
        Files.deleteIfExists(outputMojmapJar);

        TinyRemapper remapper = TinyRemapper.newRemapper()
                .withMappings(mappingProvider)
                .renameInvalidLocals(false)
                .rebuildSourceFilenames(true)
                .build();

        try (OutputConsumerPath out = new OutputConsumerPath.Builder(outputMojmapJar).build()) {
            out.addNonClassFiles(inputObfJar);
            remapper.readInputs(inputObfJar);
            remapper.apply(out);
        } finally {
            remapper.finish();
        }
    }

    // Build a TinyRemapper IMappingProvider from a MappingTree, going from
    // sourceNamespace -> targetNamespace. Walks classes/methods/fields and emits
    // every binding to the remapper's MappingAcceptor.
    private static IMappingProvider adapter(MappingTree tree, String sourceNs, String targetNs) {
        return acceptor -> {
            int srcId = resolveNs(tree, sourceNs);
            int dstId = resolveNs(tree, targetNs);

            for (MappingTree.ClassMapping cls : tree.getClasses()) {
                String src = cls.getName(srcId);
                String dst = cls.getName(dstId);
                if (src == null || dst == null) continue;
                acceptor.acceptClass(src, dst);

                for (MappingTree.MethodMapping m : cls.getMethods()) {
                    String mSrc = m.getName(srcId);
                    String mDst = m.getName(dstId);
                    String mDesc = m.getDesc(srcId);
                    if (mSrc == null || mDst == null || mDesc == null) continue;
                    acceptor.acceptMethod(new IMappingProvider.Member(src, mSrc, mDesc), mDst);
                }
                for (MappingTree.FieldMapping f : cls.getFields()) {
                    String fSrc = f.getName(srcId);
                    String fDst = f.getName(dstId);
                    String fDesc = f.getDesc(srcId);
                    if (fSrc == null || fDst == null || fDesc == null) continue;
                    acceptor.acceptField(new IMappingProvider.Member(src, fSrc, fDesc), fDst);
                }
            }
        };
    }

    private static int resolveNs(MappingTree tree, String name) {
        if (name.equals(tree.getSrcNamespace())) return MappingTree.SRC_NAMESPACE_ID;
        int dst = tree.getDstNamespaces().indexOf(name);
        if (dst < 0) {
            throw new IllegalStateException("Namespace not found in mapping tree: " + name
                    + " (src=" + tree.getSrcNamespace() + ", dst=" + tree.getDstNamespaces() + ")");
        }
        return dst;
    }
}
