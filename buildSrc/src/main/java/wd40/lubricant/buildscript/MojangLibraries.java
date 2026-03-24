package wd40.lubricant.buildscript;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Resolves the Maven coordinates of every library Mojang's launcher pulls
 * alongside the vanilla MC jar for a given MC version. Used to build the
 * datagen runtime classpath without hand-pinning versions.
 *
 * <p>Caches the resolved list in {@code <cacheDir>/libraries-<mcVersion>.txt}.
 * Skips libraries with classifiers (natives) and rule-restricted libs that
 * don't allow the host OS - those aren't needed for headless datagen.</p>
 */
public final class MojangLibraries {

    private static final String LAUNCHER_MANIFEST_URL =
            "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json";

    public static List<String> resolve(String mcVersion, Path cacheDir) {
        try {
            Files.createDirectories(cacheDir);
            Path cached = cacheDir.resolve("libraries-" + mcVersion + ".txt");
            if (Files.exists(cached)) {
                return Files.readAllLines(cached, StandardCharsets.UTF_8);
            }

            HttpClient http = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(15))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
            Gson gson = new Gson();

            MojangManifest.VersionList list = getJson(http, gson, LAUNCHER_MANIFEST_URL, MojangManifest.VersionList.class);
            MojangManifest.VersionEntry entry = list.versions().stream()
                    .filter(v -> mcVersion.equals(v.id()))
                    .findFirst()
                    .orElseThrow(() -> new IOException("MC version not in manifest: " + mcVersion));

            MojangManifest.VersionDetail detail = getJson(http, gson, entry.url(), MojangManifest.VersionDetail.class);

            String hostOs = detectOs();
            List<String> coords = new ArrayList<>();
            for (MojangManifest.Library lib : detail.libraries()) {
                if (lib.downloads() == null || lib.downloads().artifact() == null) continue;
                if (!rulesAllow(lib.rules(), hostOs)) continue;
                if (isNative(lib.name())) continue;
                coords.add(lib.name());
            }

            Files.write(cached, coords, StandardCharsets.UTF_8);
            return coords;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to resolve MC libraries for " + mcVersion, e);
        }
    }

    private static <T> T getJson(HttpClient http, Gson gson, String url, Class<T> type)
            throws IOException, InterruptedException {
        HttpResponse<InputStream> r = http.send(
                HttpRequest.newBuilder(URI.create(url)).GET().build(),
                HttpResponse.BodyHandlers.ofInputStream());
        if (r.statusCode() != 200) throw new IOException("HTTP " + r.statusCode() + " from " + url);
        try (InputStream in = r.body();
             var reader = new java.io.InputStreamReader(in, StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, type);
        }
    }

    private static boolean rulesAllow(List<MojangManifest.Rule> rules, String hostOs) {
        if (rules == null || rules.isEmpty()) return true;
        boolean allowed = false;
        for (MojangManifest.Rule rule : rules) {
            boolean matches = rule.os() == null || matchesOs(rule.os(), hostOs);
            if (!matches) continue;
            allowed = "allow".equals(rule.action());
        }
        return allowed;
    }

    private static boolean matchesOs(MojangManifest.OsConstraint os, String hostOs) {
        if (os.name() != null && !os.name().equals(hostOs)) return false;
        return true;
    }

    private static String detectOs() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) return "windows";
        if (os.contains("mac")) return "osx";
        return "linux";
    }

    private static final Pattern NATIVE_COORD = Pattern.compile(".*:natives-.*");

    private static boolean isNative(String mavenCoord) {
        return NATIVE_COORD.matcher(mavenCoord).matches();
    }

    private MojangLibraries() {}
}
