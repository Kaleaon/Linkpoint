## LibreMetaverse Reference

- **Source:** Cloned locally from `https://github.com/cinderblocks/libremetaverse`
- **Path:** `Libremetaverse/`

The repository contains the canonical C# implementation of the Second Life/OpenSimulator protocol stack used by modern viewers. Use it as the authoritative reference when porting features (network messaging, asset system, appearance baking) into the Kotlin codebase. Compare packet structures and helper utilities (e.g. message template parsing, LLSD handling) against this source in addition to Linden Lab’s C++ viewer.
