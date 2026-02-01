# Bundled Mojang dependencies in client.jar

The MC client.jar Mojang ships contains a few non-Minecraft classes alongside `net/minecraft/*`:

- `com/mojang/authlib/...` - account/profile classes
- `com/mojang/blocklist/...` - the public-suffix blocklist
- `com/mojang/datafixers/...` - data-fixer infrastructure
- A handful of vendored utility classes

These get carried through our remap pipeline unchanged (only `net/minecraft/*` is in the Mojang mappings file). They're harmless on the compile classpath but pollute auto-completion.

## When you'd want to strip them

- IDE auto-complete shows `com.mojang.authlib.GameProfile` when you didn't ask for it
- The mojmap jar is ~30 MB; stripping cuts a few MB
- A future Mojang change bundles something that conflicts with a real dep

## How to strip (when needed)

In `MojangMappingsRemapper.remap()`, before passing the input jar to `TinyRemapper.readInputs(...)`, filter the input. Either:

1. **Pre-filter the input jar.** Walk the input jar, copy only entries matching `^net/minecraft/.*\.class$|^[^/]+\.class$|^pack\.mcmeta$|^assets/.*|^data/.*` to a temp jar, feed that to TinyRemapper.
2. **Filter via TinyRemapper input tag.** TinyRemapper supports input tags + `IMappingProvider` filtering, but pre-filtering is simpler.

Likely an extra ~30 lines in `buildSrc/src/main/java/wd40/vaakx/lubricant/buildscript/`. Add a `MinecraftJarFilter.java` step between download and remap.

## Why this isn't done yet

It's purely cosmetic until something concrete breaks. Mojang's bundled set is small and stable. fabric-loom doesn't strip them either. Defer until needed.
