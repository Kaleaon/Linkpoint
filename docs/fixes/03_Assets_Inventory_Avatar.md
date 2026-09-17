# Sector 03 — Assets, Inventory & Avatar

**Findings:** 19 | **Critical:** 5 | **High:** 9 | **Medium:** 4 | **Low:** 1

Verified against source at `Linkpoint/src/main/java/com/linkpoint/` on 2026-09-17.

---

## ASSET-001 | JPEG2000 decoder unavailable — placeholder textures

- **File:** `Linkpoint/src/main/java/com/linkpoint/assets/JPEG2000Decoder.kt`
- **Line:** ~87
- **Category:** placeholder-return / missing-decoder
- **Severity:** High

**What is broken:**
When neither the native decoder nor the JP2ForAndroid fallback is loaded, the
decoder logs `"JPEG2000 decoding unavailable. Second Life textures may use
placeholders."` and returns `null`. All in-world textures silently fail.

**Fix plan:**
1. Implement JNI bindings to OpenJPEG (source already at `src/main/cpp/`).
2. As a safety net, add a pure-Kotlin minimal J2K header-only decoder that can
   at least report image dimensions for placeholder sizing.
3. Reference: SL viewer `indra/llimagej2c/llimagej2c.cpp`.

**Acceptance test:** `./gradlew :Linkpoint:testDebugUnitTest --tests "*JPEG2000*"`

---

## ASSET-002 | JPEG2000 fallback chain only recognises JPEG, not J2K

- **File:** `Linkpoint/src/main/java/com/linkpoint/assets/JPEG2000Decoder.kt`
- **Lines:** ~109–137 (decode), ~188–197 (`decodeFallback`)
- **Category:** missing-decoder / incomplete-fallback-chain
- **Severity:** Critical

**What is broken:**
`decodeFallback()` checks for JPEG magic bytes (`0xFF 0xD8`) only. Actual JPEG2000
codestreams (`0xFF 0x4F`) and JP2 file boxes (`0x00 0x00 0x00 0x0C`) are not
recognised. If native + reflection decoders both fail, true J2K images silently
return `null`.

**Fix plan:**
1. Add J2K magic-byte detection in `decodeFallback()`.
2. Attempt a header-only parse (dimensions + component count) even if full
   decode is unavailable.
3. Reference: ISO/IEC 15444-1 Section 4.3.1 codestream header.

**Acceptance test:** Feed a real J2K byte array through `decodeFallback` and
verify non-null.

---

## ASSET-003 | JP2 box parser: no 64-bit box length support

- **File:** `Linkpoint/src/main/java/com/linkpoint/assets/JPEG2000Decoder.kt`
- **Lines:** ~200–245 (`parseJ2KHeader`)
- **Category:** incomplete-parser
- **Severity:** Medium

**What is broken:**
When `boxLen == 1` (JPEG2000 signal for a 64-bit extended length), the parser
does not read the following 8-byte XL field. Instead it advances by 1 byte,
producing garbage parse state.

**Fix plan:**
1. When `boxLen == 1`, read the next 8 bytes as the 64-bit box length.
2. Raise or remove the 65536-byte scan limit.
3. Reference: ISO/IEC 15444-1:2016 Section 4.3.6.

**Acceptance test:** Unit test with a JP2 file using 64-bit box lengths.

---

## ASSET-004 | OutfitManager fallback returns empty textures/params

- **File:** `Linkpoint/src/main/java/com/linkpoint/inventory/OutfitManager.kt`
- **Lines:** ~210–236 (`fallbackWearableData`)
- **Category:** typed-fallback-not-wired
- **Severity:** High

**What is broken:**
When wearable asset fetch fails, `fallbackWearableData()` returns
`WearableData(textures = emptyMap(), params = emptyMap())`. Avatar appears as
cloud/Ruth with no skin texture; appearance sliders reset to defaults.

**Fix plan:**
1. Populate fallback textures with SL system-default UUIDs (Ruth skin
   `5748decc-f629-461c-9a36-a35a221fe21f`, default shape, default eyes).
2. Populate fallback params with SL-default appearance parameter ranges.
3. Reference: SL viewer `indra/newview/llappearancemgr.cpp`.

**Acceptance test:** `./gradlew :Linkpoint:testDebugUnitTest --tests "*OutfitManager*"`

---

## ASSET-005 | Wearable asset fetcher not injected (nullable)

- **File:** `Linkpoint/src/main/java/com/linkpoint/inventory/OutfitManager.kt`
- **Lines:** 33 (declaration), ~160 (use)
- **Category:** missing-dependency-wiring
- **Severity:** Critical

**What is broken:**
```kotlin
private val wearableAssetFetcher: (suspend (InventoryItem) -> ByteArray?)? = null
```
Defaults to `null`. If not injected, EVERY wearable load immediately fails with
`MISSING_FETCHER`. No fallback to AssetCache or TransferManager.

**Fix plan:**
1. Wire an actual fetcher at construction — use `TransferManager` (UDP transfer)
   or `CapabilityManager` (HTTP `GetAsset` cap).
2. Add `AssetCache.get()` as a secondary lookup before network fetch.

**Acceptance test:** `./gradlew :Linkpoint:testDebugUnitTest --tests "*OutfitManager*"`

---

## ASSET-006 | Wearable parser fails entirely on any malformed line

- **File:** `Linkpoint/src/main/java/com/linkpoint/inventory/OutfitManager.kt`
- **Lines:** ~181–196
- **Category:** incomplete-parser / missing-recovery
- **Severity:** High

**What is broken:**
If `WearableAssetParser.parseWithDiagnostics()` encounters a single bad line,
the entire parse returns `null`. No partial data is recovered.

**Fix plan:**
1. Implement a lenient parse mode that skips invalid lines and returns partial
   `WearableData`.
2. Log skipped lines with line number and raw content.
3. Reference: SL viewer `indra/llprimitive/llwearable.cpp:importFile()`.

**Acceptance test:** Feed a wearable file with one corrupt line; verify partial
data is returned.

---

## ASSET-007 | Baked texture upload: MIME says J2C but body is PNG

- **File:** `Linkpoint/src/main/java/com/linkpoint/avatar/AvatarBaker.kt`
- **Lines:** ~366 (MIME `"image/x-j2c"`), ~369 (`Bitmap.CompressFormat.PNG`)
- **Category:** missing-encoder / MIME-mismatch
- **Severity:** Critical

**What is broken:**
```kotlin
mimeType = "image/x-j2c"
...
bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream)
```
The request declares JPEG2000 while the body is PNG. The sim may reject the
upload or store corrupted data.

**Fix plan:**
1. **Option A (correct):** Implement JPEG2000 encoding via OpenJPEG JNI; keep
   MIME `image/x-j2c`.
2. **Option B (pragmatic):** Change MIME to `image/png` until J2K encoder is
   available.

**Acceptance test:** Bake an outfit and verify the sim accepts the upload.

---

## ASSET-008 | Animation constraint parsing stubbed

- **File:** `Linkpoint/src/main/java/com/linkpoint/assets/AnimationManager.kt`
- **Lines:** ~177–189
- **Category:** stub / incomplete-parser
- **Severity:** Medium

**What is broken:**
```kotlin
// Skip constraint data for now
if (buffer.remaining() >= chainLen * 24) {
    buffer.position(buffer.position() + chainLen * 24)
}
```
Constraint section is skipped. IK chain animations (reaching, pointing, spine
bends) lose their constraint behavior.

**Fix plan:**
1. Parse constraint fields: type (byte), target joint (string), source offset
   (3×float), target offset (3×float), ease-in/out (2×float).
2. Populate `AnimationConstraint` objects.
3. Reference: SL viewer `indra/llcharacter/llkeyframemotion.cpp:deserialize()`.

**Acceptance test:** Load an animation with constraints; verify non-empty list.

---

## ASSET-009 | Animation load: no transfer fallback on cache miss

- **File:** `Linkpoint/src/main/java/com/linkpoint/assets/AnimationManager.kt`
- **Lines:** ~79–86
- **Category:** missing-fallback / missing-retry
- **Severity:** Medium

**What is broken:**
On cache miss, `getAnimation()` increments `loadFailures` and returns `null`.
No attempt to fetch via `TransferManager` or queue for retry.

**Fix plan:**
1. Inject `TransferManager`; on cache miss, issue
   `requestAssetTransfer(animId, AssetType.ANIMATION)`.
2. Add a retry queue similar to `TextureManager`.

**Acceptance test:** Clear cache and verify animation still plays after fetch.

---

## ASSET-010 | Attached sound plays at listener, not object position

- **File:** `Linkpoint/src/main/java/com/linkpoint/assets/SoundManager.kt`
- **Lines:** ~211–218
- **Category:** incomplete-implementation
- **Severity:** Medium

**What is broken:**
```kotlin
fun playAttachedSound(soundId: UUID, objectId: UUID, ownerId: UUID, gain: Float) {
    // For now, play at listener position
    playSound(soundId, listenerPosition, gain, loop = false)
}
```
`objectId` is received but never used. Spatial audio is broken.

**Fix plan:**
1. Inject `ObjectManager`; look up object position by `objectId`.
2. Pass object position to `playSound()`.
3. Fall back to listener position if object is not in the local scene.

**Acceptance test:** Play a sound on a distant object; verify direction in stereo.

---

## ASSET-011 | NotecardManager: TransferManager nullable → immediate fail

- **File:** `Linkpoint/src/main/java/com/linkpoint/inventory/notecard/NotecardManager.kt`
- **Lines:** 41 (declaration), ~84–87 (use)
- **Category:** missing-dependency-wiring
- **Severity:** High

**What is broken:**
If `transferManager` is null, notecard load fails immediately with
`"Transfer manager unavailable"` — no queue, no retry, no AssetCache fallback.

**Fix plan:**
1. Make `TransferManager` a required constructor parameter.
2. Add `AssetCache.get()` lookup before requesting a network transfer.

**Acceptance test:** Open a notecard with a cold cache; verify content loads.

---

## ASSET-012 | Notecard upload: no polling for "processing" state

- **File:** `Linkpoint/src/main/java/com/linkpoint/inventory/notecard/NotecardManager.kt`
- **Lines:** ~500–503
- **Category:** incomplete-error-handling
- **Severity:** High

**What is broken:**
Upload response only checks for `"complete"`. A `"processing"` state is treated
as failure. The `errors` field is read as a string but may be an LLSD array.

**Fix plan:**
1. Poll every 500ms (up to 10 retries) when state is `"processing"`.
2. Parse `errors` as an LLSD array.
3. Distinguish retryable from permanent failures.

**Acceptance test:** Mock a "processing" → "complete" sequence; verify save succeeds.

---

## ASSET-013 | ScriptManager: no polling for "compiling" state

- **File:** `Linkpoint/src/main/java/com/linkpoint/assets/ScriptManager.kt`
- **Lines:** ~236–243, ~287–314 (`buildScriptSaveResult`)
- **Category:** incomplete-error-handling
- **Severity:** High

**What is broken:**
`buildScriptSaveResult()` treats any state other than `"complete"` as an error.
A `"compiling"` state is reported as a save failure. `response.getString("state")`
may return null (NPE risk on `.equals()`).

**Fix plan:**
1. Add null-safe state check.
2. Poll for `"compiling"` state (500ms × 20 retries, scripts can take 10s).
3. Reference: SL viewer `indra/newview/llscripteditor.cpp:handleSaveComplete()`.

**Acceptance test:** Save a script that takes >1s to compile; verify success.

---

## ASSET-014 | MeshManager: header-end scan uses single-byte match

- **File:** `Linkpoint/src/main/java/com/linkpoint/assets/MeshManager.kt`
- **Lines:** ~184–192 (`findHeaderEnd`)
- **Category:** incomplete-parser
- **Severity:** High

**What is broken:**
Scans for a single `}` byte to find LLSD header end. Nested LLSD maps contain
`}` inside the header, causing premature termination.

**Fix plan:**
1. Use `LLSDParser.parseBinary()` to consume the header, then check
   `stream.available()` for the mesh data offset.
2. If a byte-scan is kept, implement brace-depth tracking.

**Acceptance test:** Load a mesh with nested LLSD header; verify correct parse.

---

## ASSET-015 | MeshManager: LOD fallback only tries one level

- **File:** `Linkpoint/src/main/java/com/linkpoint/assets/MeshManager.kt`
- **Lines:** ~145–158
- **Category:** incomplete-fallback-chain
- **Severity:** High

**What is broken:**
LOD fallback: requested → `"high_lod"` → fail. Should cascade through all LOD
levels before giving up.

**Fix plan:**
1. Implement cascading fallback: requested → HIGH → MEDIUM → LOW → LOWEST.
2. Only return null if no LOD level exists.

**Acceptance test:** Remove `medium_lod` from a mesh header; verify `low_lod` is used.

---

## ASSET-016 | MeshManager: decompression buffer size unbounded

- **File:** `Linkpoint/src/main/java/com/linkpoint/assets/MeshManager.kt`
- **Lines:** ~194–209
- **Category:** missing-bounds-checking / DoS-vector
- **Severity:** Critical

**What is broken:**
```kotlin
val buffer = ByteArray(data.size * 10)
```
A 10MB compressed payload allocates a 100MB buffer. No maximum decompression
limit. A malicious mesh asset could trigger OOM.

**Fix plan:**
1. Use streaming decompression with a 64KB chunk buffer.
2. Cap total decompressed output at 256MB.
3. Add `count == 0 && !inflater.finished()` bailout for stalled inflaters.

**Acceptance test:** Feed a truncated compressed payload; verify clean failure.

---

## ASSET-017 | LandmarkManager: UDP fallback is a no-op

- **File:** `Linkpoint/src/main/java/com/linkpoint/inventory/LandmarkManager.kt`
- **Lines:** ~224–232 (`createLandmarkViaUDP`)
- **Category:** stub
- **Severity:** Medium

**What is broken:**
```kotlin
private suspend fun createLandmarkViaUDP(...): UUID? {
    // This would use CreateInventoryItem UDP message
    return null
}
```

**Fix plan:**
1. Implement `CreateInventoryItem` UDP message per `message_template.msg`.
2. Reference: SL viewer `indra/newview/llinventorymodel.cpp`.

**Acceptance test:** Create a landmark with the HTTP cap disabled; verify UDP success.

---

## ASSET-018 | TextureManager: misleading "fallback asset server" log

- **File:** `Linkpoint/src/main/java/com/linkpoint/assets/TextureManager.kt`
- **Line:** ~602
- **Category:** misleading-comment
- **Severity:** Low

**What is broken:**
Log says `"using fallback asset server"` but no fallback server exists.

**Fix plan:** Change log message to `"GetTexture cap unavailable — queued for retry"`.

---

## ASSET-019 | MediaManager: ICY stream metadata not extracted

- **File:** `Linkpoint/src/main/java/com/linkpoint/media/MediaManager.kt`
- **Lines:** ~142–154
- **Category:** incomplete-implementation
- **Severity:** Medium

**What is broken:**
`setOnInfoListener` receives metadata callbacks but does not extract ICY stream
title/artist. `_streamTitle` and `_streamArtist` StateFlows are never updated.

**Fix plan:**
1. Extract ICY metadata from `MediaPlayer.onInfo()` or the HTTP response
   `Icy-MetaData` header.
2. Update `_streamTitle` / `_streamArtist` flows.

**Acceptance test:** Play a Shoutcast stream; verify title appears in UI.
