<p align="center">
  <img src="common/src/main/resources/icon.png" alt="lubricant" width="160" />
</p>

# lubricant

A loader-abstraction layer for Minecraft 1.21.1 mods. Write your items, blocks,
events, and networking once - lubricant wires them through Fabric and NeoForge.

## Status
Active development on the `1.21.1` branch. No published release yet.

## Modules
- `common/` - public API (`wd40.lubricant.api.*`) and internal SPI
- `fabric/` - Fabric loader implementation
- `neoforge/` - NeoForge loader implementation
- `example/cog/` - reference mod demonstrating items, blocks, events, networking

## Build
    ./gradlew build

Produces four jars under `*/build/libs/`:
- `lubricant-fabric-1.0.0.jar`
- `lubricant-neoforge-1.0.0.jar`
- `cog-fabric-1.0.0.jar`
- `cog-neoforge-1.0.0.jar`

## Run dev clients
    ./gradlew :fabric:runClient
    ./gradlew :neoforge:runClient

Both load lubricant + the cog example mod. Try `/give @s cog:cog` in either.

## Bundle (single jar for both loaders)

`./gradlew bundle` produces one jar that loads on Fabric AND NeoForge:

    common/build/libs/lubricant-1.0.0.jar

Internals: a JIJ wrapper carrying `lubricant-fabric.jar` and `lubricant-neoforge.jar`
as nested jars. Each loader reads its own metadata and loads the matching nested
jar; the other is ignored. Convenience artifact for dev/testing where one mods
folder is shared between fabric and neoforge runs. Per-loader jars are still the
primary distribution.

## Publish to maven local

    ./gradlew publish

Pushes four artifacts to `~/.m2/repository/wd40/lubricant/`:
- `common:1.21.1-DEV` - API classes, compileOnly dep for downstream
- `fabric:1.21.1-DEV` - fabric production jar (intermediary-mapped)
- `neoforge:1.21.1-DEV` - neoforge production jar (mojmap)
- `lubricant:1.21.1-DEV` - the bundle

The `-DEV` suffix marks these as mutable dev artifacts (not maven SNAPSHOT
protocol, just a plain release-style version that overwrites in place).

## Consume from a downstream mod

Downstream mods (see `mods/_template/`) use `mavenLocal()` plus:

    configurations { lubricantBundle }

    dependencies {
        compileOnly("wd40.lubricant:common:${minecraft_version}-DEV") { changing = true }
        compileOnly("wd40.lubricant:fabric:${minecraft_version}-DEV") { changing = true }

        lubricantBundle("wd40.lubricant:lubricant:${minecraft_version}-DEV") {
            transitive = false
            changing   = true
        }
    }

    tasks.register('copyLubricantBundle', Copy) {
        from configurations.lubricantBundle
        into rootProject.file('runs/client/mods')
    }

    tasks.matching { it.name in ['runClient', 'runServer'] }.configureEach {
        dependsOn 'copyLubricantBundle'
    }

`changing = true` plus `cacheChangingModulesFor 0` (set on root) means each
downstream build re-resolves from `~/.m2/`, picking up the most recent
`./gradlew publish` automatically.

## Documentation
Reference + tutorial: see the [wiki](https://github.com/vaakx-dev/lubricant/wiki) (in progress).

## License
MIT. See [LICENSE](LICENSE).
