## GlobalOptions Kotlin Rewrite

- **Source references**
  - `reference/procyon/Lumiya_3.4.2/com/lumiyaviewer/lumiya/GlobalOptions.java`
  - `reference/jadx/Lumiya_3.4.2/sources/com/lumiyaviewer/lumiya/GlobalOptions.java`
  - Upstream cache-handling behaviour compared with current Second Life viewer (see `SecondLife/indra/newview/llappviewer.cpp` cache init).

- **Key decisions**
  - Re-implemented as a Kotlin `object` maintaining parity with the singleton Java pattern.
  - Preserved Guava `ImmutableList` usage for cache directory tracking to avoid accidental mutation.
  - Normalised preference parsing, using Kotlin `toIntOrNull()` / `toFloatOrNull()` for exception-free parsing.
  - Cache directory update now logs via `Debug.Printf` and safely handles `.nomedia` creation; failures report through `Debug.Warning`.
  - Voice enablement honours original behaviour but guards availability with `VoicePluginServiceConnection.isPluginSupported()`.

- **Modernisations**
  - Removed manual null checks in favour of Kotlin null-safety.
  - Replaced magic numbers with expressive `when` blocks.
  - Eliminated deprecated `SharedPreferences.Editor.commit()` usage except where immediate persistence is required (system defaults bootstrap).

- **Follow-up**
  - Revisit texture download heuristics once the updated renderer pipeline from Firestorm is ported.
  - Consider migrating cache management to the new asset system once rendering module is rebuilt.
