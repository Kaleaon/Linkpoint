# legacy/

Everything here is **not** part of the active Linkpoint Next application
(`apps/linkpoint`, `packages/`, `crates/`). It is kept for migration reference
and the compatibility Android build:

- `Linkpoint/`, `LLSD-KOTLIN/`, `src/`, `app/`, Gradle files: Kotlin/Android client
- `Gauss/`, `vendor/`, `include/`, `tests/`, `CMakeLists.txt`: supporting/native code
- `lumiya_*`, `secondlife_decompiled/`, `disassembled-apps/`, `apk_analysis/`, `android-sdk/`: reverse-engineering evidence
- `design/`, `file_bundle/`, `kotlin-translations/`, `ktheme-pr/`, Python texture/inventory prototypes: historical inputs

Build the Android client with `cd legacy && ./gradlew :Linkpoint:assembleDebug`.
