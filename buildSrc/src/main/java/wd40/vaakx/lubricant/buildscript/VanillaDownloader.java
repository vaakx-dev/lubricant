package wd40.vaakx.lubricant.buildscript;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;

// Downloads vanilla Minecraft jar + Mojang's official mappings, with SHA1 verification
// and on-disk caching keyed by content. Idempotent: re-running with a populated cache
// is just a few stat() calls.

public final class VanillaDownloader {

    private static final String LAUNCHER_MANIFEST_URL =
            "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();
    private final Gson gson = new Gson();

    public Result fetch(String mcVersion, Path cacheDir) throws IOException, InterruptedException {
        Files.createDirectories(cacheDir);

        // 1. Top-level manifest -> find the entry for our MC version.
        MojangManifest.VersionList list = getJson(LAUNCHER_MANIFEST_URL, MojangManifest.VersionList.class);
        MojangManifest.VersionEntry entry = list.versions().stream()
                .filter(v -> mcVersion.equals(v.id()))
                .findFirst()
                .orElseThrow(() -> new IOException("Minecraft version not in manifest: " + mcVersion));

        // 2. Per-version manifest -> client.jar URL + mappings URL.
        MojangManifest.VersionDetail detail = getJson(entry.url(), MojangManifest.VersionDetail.class);
        if (detail.downloads().client() == null || detail.downloads().client_mappings() == null) {
            throw new IOException("Version " + mcVersion + " has no client or mappings download");
        }

        // 3. Download both with SHA1 verification + caching.
        Path clientJar = cacheDir.resolve("client.jar");
        Path mappings  = cacheDir.resolve("client_mappings.txt");
        downloadIfMissing(detail.downloads().client(), clientJar);
        downloadIfMissing(detail.downloads().client_mappings(), mappings);

        return new Result(clientJar, mappings);
    }

    public record Result(Path clientJar, Path mappings) {}

    private <T> T getJson(String url, Class<T> type) throws IOException, InterruptedException {
        HttpResponse<InputStream> r = http.send(
                HttpRequest.newBuilder(URI.create(url)).GET().build(),
                HttpResponse.BodyHandlers.ofInputStream());
        if (r.statusCode() != 200) {
            throw new IOException("HTTP " + r.statusCode() + " from " + url);
        }
        try (InputStream in = r.body();
             var reader = new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, type);
        }
    }

    private void downloadIfMissing(MojangManifest.Download d, Path dest) throws IOException, InterruptedException {
        if (Files.exists(dest) && sha1(dest).equalsIgnoreCase(d.sha1())) {
            return;
        }
        HttpResponse<Path> r = http.send(
                HttpRequest.newBuilder(URI.create(d.url())).GET().build(),
                HttpResponse.BodyHandlers.ofFile(dest));
        if (r.statusCode() != 200) {
            throw new IOException("HTTP " + r.statusCode() + " downloading " + d.url());
        }
        String got = sha1(dest);
        if (!got.equalsIgnoreCase(d.sha1())) {
            Files.deleteIfExists(dest);
            throw new IOException("SHA1 mismatch for " + d.url() + ": expected " + d.sha1() + " got " + got);
        }
    }

    private static String sha1(Path file) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            try (InputStream in = Files.newInputStream(file)) {
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) > 0) md.update(buf, 0, n);
            }
            return HexFormat.of().formatHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 not available in this JVM", e);
        }
    }
}
