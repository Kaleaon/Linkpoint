## Android App Variants

This repository now contains three fully separated Android builds. Each variant lives in
its own Gradle module or included build so they can evolve independently without breaking
each other.

| Variant | Location | Description | Build command |
| --- | --- | --- | --- |
| Modern Lumiya | `app` | Kotlin-first rebuild that keeps the login prototype and graphics probes minimal for stability. | `./gradlew :app:assembleDebug` |
| Legacy Lumiya | `lumiya` | Original Java/Kotlin implementation with all historical assets and UI flows preserved for reference. | `./gradlew :lumiya:assembleDebug` |
| Linkpoint | `Linkpoint` (composite build) | Material 3 successor with Filament rendering and WebRTC integrations. | `./gradlew :Linkpoint:assembleDebug` |

### Working with the modules

- `app` is now a clean Android app with its own manifest, resources, and crash-safe Kotlin code.
- `lumiya` retains the original sources, assets, and native scaffolding for anyone who needs to
  debug legacy behaviour.
- `Linkpoint` remains an independent build, but it is wired in via `includeBuild` so its tasks
  can be invoked from the root Gradle wrapper.

Use `./gradlew tasks` to verify that all modules are visible, or run the build commands above to
generate APKs for each variant.
