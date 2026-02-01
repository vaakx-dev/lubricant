package wd40.vaakx.lubricant.buildscript;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Parses parchment.json into a fast lookup table for parameter names.
//
// Parchment shape (only the bits we use):
// {
//   "classes": [
//     {
//       "name": "net/minecraft/Foo",
//       "methods": [
//         {
//           "name": "bar",
//           "descriptor": "(II)V",
//           "parameters": [ { "index": 1, "name": "x" }, { "index": 2, "name": "y" } ]
//         }
//       ]
//     }
//   ]
// }
//
// Names + descriptors are in MOJANG namespace.

public final class ParchmentData {

    public record MethodKey(String mojangClass, String mojangMethod, String mojangDesc) {}
    public record Param(int lvIndex, String name) {}

    private final Map<MethodKey, List<Param>> params;

    private ParchmentData(Map<MethodKey, List<Param>> params) {
        this.params = params;
    }

    public List<Param> paramsFor(String mojangClass, String mojangMethod, String mojangDesc) {
        return params.get(new MethodKey(mojangClass, mojangMethod, mojangDesc));
    }

    public boolean isEmpty() {
        return params.isEmpty();
    }

    public static ParchmentData parse(Path parchmentJson) throws IOException {
        if (parchmentJson == null) return new ParchmentData(Map.of());
        try (Reader r = Files.newBufferedReader(parchmentJson, StandardCharsets.UTF_8)) {
            JsonRoot root = new Gson().fromJson(r, JsonRoot.class);
            Map<MethodKey, List<Param>> map = new HashMap<>();
            if (root != null && root.classes != null) {
                for (JsonClass c : root.classes) {
                    if (c.methods == null) continue;
                    for (JsonMethod m : c.methods) {
                        if (m.parameters == null || m.parameters.isEmpty()) continue;
                        List<Param> ps = m.parameters.stream()
                                .filter(p -> p.name != null && !p.name.isBlank())
                                .map(p -> new Param(p.index, p.name))
                                .toList();
                        if (!ps.isEmpty()) {
                            map.put(new MethodKey(c.name, m.name, m.descriptor), ps);
                        }
                    }
                }
            }
            return new ParchmentData(map);
        }
    }

    // Gson DTOs - nested for locality. Field names match the JSON keys.
    private static final class JsonRoot { List<JsonClass> classes; }
    private static final class JsonClass { String name; List<JsonMethod> methods; }
    private static final class JsonMethod { String name; String descriptor; List<JsonParam> parameters; }
    private static final class JsonParam { int index; String name; }
}
