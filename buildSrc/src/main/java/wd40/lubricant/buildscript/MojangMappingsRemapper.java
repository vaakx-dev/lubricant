package wd40.lubricant.buildscript;

import net.fabricmc.mappingio.MappingReader;
import net.fabricmc.mappingio.format.MappingFormat;
import net.fabricmc.mappingio.tree.MappingTree;
import net.fabricmc.mappingio.tree.MappingTreeView;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import net.fabricmc.tinyremapper.IMappingProvider;
import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// Remaps an obfuscated Minecraft client.jar to use Mojang names (mojmap), optionally
// emitting Parchment parameter names into the LocalVariableTable on the way.
//
// ProGuard files ship as "named -> obfuscated" so mapping-io reads them with
// src namespace "source" (mojang) and dst namespace "target" (obf). We remap target -> source.
//
// Parchment data is layered separately via ParchmentData (custom parser - mapping-io 0.7
// does not yet support the Parchment format).

public final class MojangMappingsRemapper {

    public void remap(Path inputObfJar, Path proguardMappings, Path parchmentJson, Path outputMojmapJar) throws IOException {
        MemoryMappingTree tree = new MemoryMappingTree();
        MappingReader.read(proguardMappings, MappingFormat.PROGUARD_FILE, tree);

        ParchmentData parchment = ParchmentData.parse(parchmentJson);

        IMappingProvider mappingProvider = adapter(tree, parchment, "target", "source");

        Files.createDirectories(outputMojmapJar.getParent());
        Files.deleteIfExists(outputMojmapJar);

        TinyRemapper remapper = TinyRemapper.newRemapper()
                .withMappings(mappingProvider)
                .renameInvalidLocals(true)
                .invalidLvNamePattern(java.util.regex.Pattern.compile("\\$\\$\\d+"))
                .inferNameFromSameLvIndex(true)
                .checkPackageAccess(false)
                .fixPackageAccess(false)
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

    private static IMappingProvider adapter(MappingTreeView tree, ParchmentData parchment, String sourceNs, String targetNs) {
        return acceptor -> {
            int srcId = resolveNs(tree, sourceNs);
            int dstId = resolveNs(tree, targetNs);

            for (MappingTree.ClassMapping cls : ((MappingTree) tree).getClasses()) {
                String src = nameAt(cls, srcId);
                String dst = nameAt(cls, dstId);
                if (src == null || dst == null) continue;
                acceptor.acceptClass(src, dst);

                for (MappingTree.MethodMapping m : cls.getMethods()) {
                    String mSrc = nameAt(m, srcId);
                    String mDst = nameAt(m, dstId);
                    String mDescObf = descAt(m, srcId);
                    String mDescMoj = descAt(m, dstId);
                    if (mSrc == null || mDst == null || mDescObf == null) continue;
                    IMappingProvider.Member member = new IMappingProvider.Member(src, mSrc, mDescObf);
                    acceptor.acceptMethod(member, mDst);

                    if (!parchment.isEmpty() && mDescMoj != null) {
                        java.util.List<ParchmentData.Param> ps = parchment.paramsFor(dst, mDst, mDescMoj);
                        if (ps != null) {
                            for (ParchmentData.Param p : ps) {
                                acceptor.acceptMethodArg(member, p.lvIndex(), p.name());
                            }
                        }
                    }
                }
                for (MappingTree.FieldMapping f : cls.getFields()) {
                    String fSrc = nameAt(f, srcId);
                    String fDst = nameAt(f, dstId);
                    String fDesc = descAt(f, srcId);
                    if (fSrc == null || fDst == null || fDesc == null) continue;
                    acceptor.acceptField(new IMappingProvider.Member(src, fSrc, fDesc), fDst);
                }
            }
        };
    }

    private static String nameAt(MappingTreeView.ElementMappingView e, int nsId) {
        return nsId == MappingTreeView.SRC_NAMESPACE_ID ? e.getSrcName() : e.getName(nsId);
    }

    private static String descAt(MappingTreeView.MemberMappingView m, int nsId) {
        return nsId == MappingTreeView.SRC_NAMESPACE_ID ? m.getSrcDesc() : m.getDesc(nsId);
    }

    private static int resolveNs(MappingTreeView tree, String name) {
        if (name.equals(tree.getSrcNamespace())) return MappingTreeView.SRC_NAMESPACE_ID;
        int dst = tree.getDstNamespaces().indexOf(name);
        if (dst < 0) {
            throw new IllegalStateException("Namespace not found in mapping tree: " + name
                    + " (src=" + tree.getSrcNamespace() + ", dst=" + tree.getDstNamespaces() + ")");
        }
        return dst;
    }
}
