package wd40.lubricant.buildscript;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

// Fetches the Parchment release zip from ParchmentMC's maven and extracts parchment.json.
// Parchment is a community-maintained mapping overlay providing parameter names + javadoc
// on top of Mojang's class/method/field names.
//
// Coords:  https://maven.parchmentmc.org/org/parchmentmc/data/parchment-<MC_VERSION>/<PARCHMENT_VERSION>/parchment-<MC_VERSION>-<PARCHMENT_VERSION>.zip

public final class ParchmentDownloader {

    private static final String BASE = "https://maven.parchmentmc.org/org/parchmentmc/data";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public Path fetch(String mcVersion, String parchmentVersion, Path cacheDir) throws IOException, InterruptedException {
        Files.createDirectories(cacheDir);
        Path jsonOut = cacheDir.resolve("parchment-" + mcVersion + "-" + parchmentVersion + ".json");
        if (Files.exists(jsonOut)) return jsonOut;

        String url = BASE
                + "/parchment-" + mcVersion
                + "/" + parchmentVersion
                + "/parchment-" + mcVersion + "-" + parchmentVersion + ".zip";

        Path zipTmp = cacheDir.resolve("parchment-" + mcVersion + "-" + parchmentVersion + ".zip");
        HttpResponse<Path> r = http.send(
                HttpRequest.newBuilder(URI.create(url)).GET().build(),
                HttpResponse.BodyHandlers.ofFile(zipTmp));
        if (r.statusCode() != 200) {
            throw new IOException("HTTP " + r.statusCode() + " downloading " + url);
        }

        try (InputStream in = Files.newInputStream(zipTmp);
             ZipInputStream zin = new ZipInputStream(in)) {
            ZipEntry entry;
            while ((entry = zin.getNextEntry()) != null) {
                if ("parchment.json".equals(entry.getName())) {
                    Files.copy(zin, jsonOut, StandardCopyOption.REPLACE_EXISTING);
                    return jsonOut;
                }
            }
        } finally {
            Files.deleteIfExists(zipTmp);
        }
        throw new IOException("parchment.json not found in " + url);
    }
}
