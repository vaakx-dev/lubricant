# Mojang client.jar contents and our filter

What's actually inside Mojang's client.jar for MC 1.21.1:

| Path | Size | Notes |
|---|---|---|
| `net/minecraft/**` | ~17 MB | Minecraft itself |
| `com/mojang/blaze3d/**` | ~0.5 MB | Mojang's rendering layer; ships WITH MC, not as a separate dep |
| `assets/**` | ~3 MB | Vanilla resource pack content |
| `data/**` | ~2 MB | Vanilla data pack content |
| `META-INF/MANIFEST.MF` | 3.18 MB | Per-class digest entries used for jar signing |
| `META-INF/MOJANGCS.SF` | 3.18 MB | Signed digest list (same content, signed) |
| `META-INF/MOJANGCS.RSA` | ~5 KB | Signing certificate |
| `pack.mcmeta`, `version.json` | tiny | Misc metadata |

**Surprise:** the bundled libraries some old guides talk about (authlib, datafixers, brigadier, slf4j, etc.) are NOT inside client.jar in MC 1.21.1. They're separate jars listed in Mojang's launcher manifest and downloaded alongside.

So there's nothing useful to strip code-wise. Only `META-INF/*` is dead weight.

## What we strip

`MinecraftJarFilter.filter()` drops `META-INF/*` entirely:
- The MANIFEST.MF and MOJANGCS.SF signing data is invalid the moment TinyRemapper rewrites a single class
- A compile-time MC jar doesn't need a manifest

Result: ~6 MB shaved off the input to TinyRemapper. The final mojmap jar drops from ~33 MB to ~30 MB.

## What we keep

Everything else: `net/minecraft/**`, `com/mojang/blaze3d/**`, `assets/**`, `data/**`, `pack.mcmeta`. All of these are referenced by MC code or used by mod authors.

## When to revisit

- If a future MC version starts bundling more libs in client.jar, audit `com/**` paths to decide what to keep.
- If we want to slim the dev jar further (~5 MB more), we could strip `assets/**` and `data/**` since the compiler doesn't need them - but loom-equivalent dev runs DO need them for in-IDE testing, so keep them on for now.
