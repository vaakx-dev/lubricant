package wd40.lubricant.buildscript;

import java.util.List;

// Records mirroring the JSON shape of Mojang's launcher manifest.
// Only the fields we use are declared - Gson tolerates extras.

public final class MojangManifest {

    public record VersionList(List<VersionEntry> versions) {}

    public record VersionEntry(
            String id,        // e.g. "1.21.1"
            String type,      // "release", "snapshot", etc.
            String url,       // per-version manifest URL
            String sha1
    ) {}

    public record VersionDetail(Downloads downloads) {}

    public record Downloads(
            Download client,
            Download client_mappings
    ) {}

    public record Download(String url, String sha1, long size) {}

    private MojangManifest() {}
}
