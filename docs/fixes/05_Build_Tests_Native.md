# Sector 05 — Build System, Tests, Native Libraries, CI, i18n & Economy

**Findings:** 23 | **Critical:** 3 | **High:** 6 | **Medium:** 10 | **Low:** 4

Verified against source on 2026-09-17. Note: the `.github/workflows/build-linkpoint.yml`
now has **7** `continue-on-error: true` sites (up from 6 at the 2026-04-24
baseline) — see CI-001.

---

## Gradle Build System

### BUILD-SYS-001 | Parallel build, daemon, and caching all disabled

- **File:** `gradle.properties` (repo root)
- **Category:** broken-build / performance-degradation
- **Severity:** High

**What is broken:**
```properties
org.gradle.parallel=false
org.gradle.configureondemand=false
org.gradle.caching=false
org.gradle.daemon=false
```
Comments cite "resource conflicts" but the root cause is unresolved. Builds run
sequentially without daemon — 2–3× slower than necessary.

**Fix plan:**
1. Investigate resource conflicts: look for duplicate `outputDir` paths, shared
   file locks between modules, or `kapt` thread safety issues.
2. Re-enable `parallel=true`, `caching=true`, `daemon=true` one at a time,
   running full `./gradlew clean build` after each.

**Acceptance test:** `./gradlew clean build` completes without resource errors.

---

### BUILD-SYS-002 | KAPT incremental compilation disabled

- **File:** `gradle.properties` (repo root)
- **Category:** broken-build / performance-degradation
- **Severity:** Medium

**What is broken:**
```properties
kapt.incremental.apt=false
kapt.use.worker.api=false
```

**Fix plan:**
1. Set both to `true`.
2. Consider migrating from KAPT to KSP for Room (faster, incremental by default).

**Acceptance test:** `./gradlew :Linkpoint:kaptDebugKotlin` completes with incremental markers.

---

### BUILD-SYS-003 | Resource validation disabled globally

- **File:** `gradle.properties` (repo root)
- **Category:** broken-build / validation-skip
- **Severity:** Medium

**What is broken:**
`android.disableResourceValidation=true` suppresses ALL Android resource checks.

**Fix plan:**
1. Remove the flag. Fix the resource errors that surface.
2. Common root causes: duplicate `attr/fontStyle` between Material/AppCompat,
   conflicting drawable names from old Lumiya assets.

**Acceptance test:** `./gradlew :Linkpoint:lintDebug` passes without resource errors.

---

### BUILD-SYS-004 | Hilt DI plugin commented out

- **File:** `build.gradle` (repo root)
- **Category:** missing-gradle-dep / design-decision
- **Severity:** Low

**What is broken:**
Hilt plugin is commented with `// not currently used`.

**Fix plan:** Either remove the comment entirely, or implement Hilt DI starting
with `LinkpointApp` and manager singletons.

---

### BUILD-SYS-005 | LLSD library dependency commented out

- **File:** `Linkpoint/build.gradle.kts`
- **Category:** missing-gradle-dep / local-fallback
- **Severity:** Low

**What is broken:**
`lindenlab:llsd:1.0` is commented — project uses a local LLSD implementation
under `com.linkpoint.protocol.llsd`.

**Fix plan:** Verify local implementation against `github.com/Kaleaon/LLSD` test
suite; document version and maintenance status.

---

### BUILD-SYS-006 | XR/Cardboard libraries are placeholder comments

- **File:** `Linkpoint/build.gradle.kts`
- **Category:** missing-gradle-dep / future-feature
- **Severity:** Low

**Fix plan:** Uncomment when libraries reach stable release. Ties to RENDER-001/002.

---

## Native C++ / JNI Libraries

### NATIVE-001 | Zstd stub: KTX2 supercompression fails

- **File:** `Linkpoint/src/main/cpp/zstd/zstd.h`
- **Category:** broken-native-link / incomplete-codec
- **Severity:** High

**What is broken:**
Zstd header is a minimal stub — `ZSTD_decompress()` always returns an error.
KTX2 textures using Zstd supercompression cannot be decoded.

**Fix plan:**
1. Add the full Zstandard library (`github.com/facebook/zstd`, BSD license) to
   the CMake build.
2. Replace stub header with real `zstd.h`.

**Acceptance test:** Decode a Zstd-compressed KTX2 texture in a native unit test.

---

### NATIVE-002 | Vivox JNI: all functions are stubs

- **File:** `Linkpoint/src/main/cpp/jni/vivox_stubs.cpp`
- **Category:** broken-native-link / incomplete-voice
- **Severity:** Critical
- **Cross-ref:** RENDER-008 in `02_Rendering_and_Graphics.md`.

---

### NATIVE-003 | VxAudio / SndFile / oRTP: all stubs

- **File:** `Linkpoint/src/main/cpp/jni/audio_stubs.cpp`
- **Category:** broken-native-link / incomplete-audio
- **Severity:** Critical
- **Cross-ref:** RENDER-004 through RENDER-007.

---

### NATIVE-004 | Basis transcoder stub

- **File:** `Linkpoint/src/main/cpp/jni/basis_transcoder_stub.cpp`
- **Category:** broken-native-link / incomplete-texture-codec
- **Severity:** High
- **Cross-ref:** RENDER-009.

---

### NATIVE-005 | RawBuffer stub: no native optimisation

- **File:** `Linkpoint/src/main/cpp/jni/rawbuf_stub.cpp`
- **Category:** broken-native-link / incomplete-buffers
- **Severity:** Low

**What is broken:**
`DirectByteBuffer` is a thin Java `ByteBuffer` wrapper.

**Fix plan:** Acceptable for now. If profiling shows buffer copies as a hotspot,
integrate the full rawbuf native library.

---

### NATIVE-006 | Basis Universal upstream TODOs

- **File:** `Linkpoint/src/main/cpp/basis_universal/basisu_transcoder.cpp`
- **Category:** incomplete-dependency / upstream-TODO
- **Severity:** Medium

**Fix plan:** Update to latest Basis Universal release. Accept known limitations
for this release.

---

## CI / CD

### CI-001 | Seven CI steps mask failures

- **File:** `.github/workflows/build-linkpoint.yml`
- **Lines:** 130, 136, 140, 150, 178, 199, 380 (verified 2026-09-17)
- **Category:** broken-CI / test-failure-masked
- **Severity:** Critical

**What is broken:**
`continue-on-error: true` on seven steps. Broken APKs, failing tests, and lint
violations all pass CI silently. The count has increased from 6 (2026-04-24) to
7 in the intervening months.

**Fix plan:**
1. Audit each of the 7 lines. For build/compile/test/lint steps, remove
   `continue-on-error` — failures must block PR merges.
2. Keep `continue-on-error` only on artifact upload steps (non-critical if
   build itself succeeded).
3. Reference: GitHub Actions best practices for required checks.

**Acceptance test:** Push a commit with a deliberate compile error; verify CI
fails and PR is blocked.

---

### CI-002 | Fuzz test may fail silently

- **File:** `.github/workflows/build-linkpoint.yml`
- **Category:** test-failure-masked
- **Severity:** Medium

**What is broken:**
LLSD parser fuzz test runs after a unit-test step whose failures may be masked.

**Fix plan:** Once CI-001 is fixed, this becomes moot.

---

## Test Suite

### TEST-001 | ConnectionDiagnosticTest uses all-synthetic data

- **File:** `Linkpoint/src/test/kotlin/com/linkpoint/connection/ConnectionDiagnosticTest.kt`
- **Category:** test-fixture-incomplete
- **Severity:** Medium

**What is broken:**
Tests create synthetic packets with zeroed UUIDs and fake circuit codes.

**Fix plan:**
1. Add binary packet fixtures captured from a real SL login session.
2. Verify parser output against known-good values.

---

### TEST-002 | AvatarParsingBenchmark is a test with no assertions

- **File:** `Linkpoint/src/test/kotlin/com/linkpoint/avatar/AvatarParsingBenchmark.kt`
- **Category:** test-classification
- **Severity:** Medium

**Fix plan:** Add correctness assertions alongside timing output, or move to a
dedicated benchmarking task.

---

### TEST-003 | WorldMapBenchmark misplaced in test tree

- **File:** `Linkpoint/src/test/kotlin/com/linkpoint/world/WorldMapBenchmark.kt`
- **Category:** test-classification
- **Severity:** Medium

**Fix plan:** Move to a `tools/benchmarks` module, or add `@Test` methods.

---

### TEST-004 | Message parser conformance fixtures sparse

- **File:** `Linkpoint/src/test/resources/fixtures/messages/parser_conformance_fixtures.json`
- **Category:** test-fixture-incomplete
- **Severity:** High

**What is broken:**
Only 4 fixture entries exist for a protocol with 568+ message IDs.

**Fix plan:**
1. Expand fixtures to cover every message ID that has a registered parser
   (currently 23).
2. Add negative fixtures (truncated, corrupt, oversized).

**Acceptance test:** `./gradlew :Linkpoint:testDebugUnitTest --tests "*MessageParserConformanceFixture*"`

---

## Diagnostics

### DIAG-001 | ScenePopulationDiagnostics: counters not exposed

- **File:** `Linkpoint/src/main/java/com/linkpoint/diagnostics/ScenePopulationDiagnostics.kt`
- **Category:** incomplete-diagnostics
- **Severity:** Medium

**Fix plan:**
1. Expose counters via `StateFlow` for the diagnostics/debug UI panel.
2. Optionally emit to an analytics sink.

---

## Internationalization (i18n)

### I18N-001 | Only 3 locales supported

- **File:** `Linkpoint/src/main/java/com/linkpoint/i18n/LocalizationManager.kt`
- **Category:** incomplete-i18n
- **Severity:** Medium

**What is broken:**
`SUPPORTED_LOCALES` contains only `en`, `es`, `fr`.

**Fix plan:**
1. Add locale entries for `de`, `ja`, `pt`, `ru`, `zh`, `ko`, `it`, `nl`, `pl`,
   `tr` at minimum.
2. Create corresponding JSON files in `src/main/assets/localization/`.

---

### I18N-002 | Locale key coverage unknown

- **File:** `Linkpoint/src/main/assets/localization/`
- **Category:** incomplete-i18n
- **Severity:** Medium

**Fix plan:**
Add a unit test that loads all locale files and asserts key-set equality.

**Acceptance test:** `./gradlew :Linkpoint:testDebugUnitTest --tests "*Localization*"`

---

### I18N-003 | No plural forms support

- **File:** `Linkpoint/src/main/java/com/linkpoint/i18n/LocalizationManager.kt`
- **Category:** incomplete-i18n
- **Severity:** Medium

**Fix plan:**
1. Integrate `android.icu.text.MessageFormat` or `com.ibm.icu` for ICU `PluralRules`.
2. Add plural-form syntax to locale JSON files.

---

## Economy

### ECON-001 | EconomyManager: balance not exposed to UI

- **File:** `Linkpoint/src/main/java/com/linkpoint/economy/EconomyManager.kt`
- **Category:** incomplete-economy
- **Severity:** High

**Fix plan:**
1. Expose `balance: StateFlow<Int>` for toolbar display.
2. Persist transaction history with Room.

**Acceptance test:** Verify L$ balance appears in UI after login.

---

### ECON-002 | Transaction types: incomplete handler coverage

- **File:** `Linkpoint/src/main/java/com/linkpoint/economy/EconomyManager.kt`
- **Category:** incomplete-economy
- **Severity:** Medium

**Fix plan:**
Add a `transactionTypeName(type: Int): String` mapping for all 30+ types.

---

### ECON-003 | No buy-currency endpoint

- **File:** `Linkpoint/src/main/java/com/linkpoint/economy/EconomyManager.kt`
- **Category:** missing-economy-endpoint
- **Severity:** High

**Fix plan:**
1. Add `buyCurrency(amount: Int)` that opens the grid's buy-currency web page.
2. Reference: SL viewer `indra/newview/llbuycurrencyhtml.cpp`.

**Acceptance test:** Tap "Buy L$" button; verify web flow opens.
