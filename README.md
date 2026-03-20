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

## Documentation
Reference + tutorial: see the [wiki](https://github.com/vaakxxx/lubricant/wiki) (in progress).

## License
MIT. See [LICENSE](LICENSE).
