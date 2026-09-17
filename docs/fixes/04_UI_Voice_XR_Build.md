# Sector 04 — UI, Voice, XR & Build Tools

**Findings:** 16 | **Critical:** 3 | **High:** 7 | **Medium:** 5 | **Low:** 1

Verified against source on 2026-09-17.

**Note:** Two prior findings from the 2026-04-24 audit are now `[RESOLVED]`:

- `[RESOLVED] VOICE-001 VoiceSession.createOffer` — now uses
  `createOfferSuspend()` in `VoiceManager.kt` line ~881.
- `[RESOLVED] VOICE-002 VoiceSession.handleOffer` — SDP round-trip now wired
  via `session.handleOffer(offer)` at `VoiceManager.kt:426`.

---

## XR-001 | OpenXR session stub

- **File:** `Linkpoint/src/main/java/com/linkpoint/xr/XRManager.kt`
- **Lines:** ~363–372
- **Category:** stub / not-implemented
- **Severity:** Critical
- **Cross-ref:** RENDER-001 in `02_Rendering_and_Graphics.md`

---

## XR-002 | Android XR session stub

- **File:** `Linkpoint/src/main/java/com/linkpoint/xr/XRManager.kt`
- **Lines:** ~375–385
- **Category:** stub / not-implemented
- **Severity:** Critical
- **Cross-ref:** RENDER-002 in `02_Rendering_and_Graphics.md`

---

## BUILD-001 | BuildActivity is a placeholder

- **File:** `Linkpoint/src/main/java/com/linkpoint/ui/build/BuildActivity.kt`
- **Lines:** 9–18
- **Category:** placeholder / not-yet-implemented
- **Severity:** Critical

**What is broken:**
Docstring says "This is a placeholder that will be expanded with: object
manipulation tools (move, rotate, scale), object properties editing,
linking/unlinking, texture application, script editing". The activity contains
only `onCreate()` boilerplate.

**Fix plan:**
1. Implement a Compose-based build panel with tool palette (move/rotate/scale
   mode selector), properties sheet (name, description, permissions), and
   texture picker.
2. Wire toolbar buttons to `BuildTools` methods.
3. Reference: SL viewer `indra/newview/llfloatertools.cpp`; Lumiya Redux
   `BuildActivity.java`.

**Acceptance test:** Manual — open build mode, select a prim, move it.

---

## BUILD-002 | BuildTools.finishBuild() create call commented out

- **File:** `Linkpoint/src/main/java/com/linkpoint/objects/BuildTools.kt`
- **Lines:** ~98–116
- **Category:** incomplete-implementation
- **Severity:** High

**What is broken:**
```kotlin
// Send create request
// objectManager.createPrim(params)  // <-- COMMENTED OUT
return params
```

**Fix plan:**
1. Uncomment `objectManager.createPrim(params)`.
2. Verify `ObjectManager.createPrim()` sends the `ObjectAdd` UDP message.
3. Reference: SL viewer `indra/newview/llviewerobject.cpp:sendObjectAdd()`.

**Acceptance test:** Manual — rez a box in-world.

---

## BUILD-003 | BuildTools.duplicateSelection() sends nothing

- **File:** `Linkpoint/src/main/java/com/linkpoint/objects/BuildTools.kt`
- **Lines:** ~158–166
- **Category:** incomplete-implementation
- **Severity:** High

**What is broken:**
```kotlin
for (localId in selected) {
    val obj = objectManager.getObject(localId) ?: continue
    // Would send ObjectDuplicate message
}
```

**Fix plan:**
1. Implement `ObjectDuplicate` UDP message per `message_template.msg`.
2. Send one per selected object with the offset vector.
3. Reference: SL viewer `indra/newview/llselectmgr.cpp:sendDuplicate()`.

**Acceptance test:** Select a prim and duplicate it; verify copy appears.

---

## BUILD-004 | BuildTools.distributeSelection() does not update positions

- **File:** `Linkpoint/src/main/java/com/linkpoint/objects/BuildTools.kt`
- **Lines:** ~197–215
- **Category:** incomplete-implementation
- **Severity:** High

**What is broken:**
Positions are calculated in the loop but never sent to the sim.

**Fix plan:**
Call `objectManager.updateObjectPosition(obj.localId, newPos)` inside the loop.

**Acceptance test:** Select 3+ prims; distribute along X; verify even spacing.

---

## VOICE-003 | setOutputGain() only toggles audio on/off

- **File:** `Linkpoint/src/main/java/com/linkpoint/voice/VoiceManager.kt`
- **Lines:** ~640–654
- **Category:** incomplete-implementation
- **Severity:** Medium

**What is broken:**
```kotlin
track.setEnabled(gain > 0f)  // Only on/off, not 0-2x multiplier
```

**Fix plan:**
1. Implement a custom `AudioProcessor` that scales audio frame samples by the
   gain multiplier.
2. Register the processor with the WebRTC `AudioSource`.

**Acceptance test:** Set gain to 0.5; verify audio is at half volume.

---

## VOICE-004 | setInputGain() stores value but never applies it

- **File:** `Linkpoint/src/main/java/com/linkpoint/voice/VoiceManager.kt`
- **Lines:** ~217–220
- **Category:** stub
- **Severity:** Medium

**What is broken:**
```kotlin
fun setInputGain(gain: Float) {
    inputGain = gain.coerceIn(0f, 2f)
    // Apply to audio source
}
```

**Fix plan:**
Apply gain via a custom `AudioProcessor` on the `audioSource`.

**Acceptance test:** Set input gain to 0; verify outbound audio is silent.

---

## RLV-001 | forceTeleport() logs but does nothing

- **File:** `Linkpoint/src/main/java/com/linkpoint/rlv/RLVController.kt`
- **Lines:** ~284–288
- **Category:** stub
- **Severity:** High

**What is broken:**
```kotlin
private fun forceTeleport(coords: String?): RLVResult {
    Log.d(TAG, "Force teleport to: $coords")
    return RLVResult.Success  // Returns success but never teleports
}
```

**Fix plan:**
1. Parse `coords` as `"regionname/x/y/z"`.
2. Call `TeleportManager.teleportToLocation(regionName, x, y, z)`.
3. Reference: RLVa spec `@tpto:regionname/x/y/z=force`.

**Acceptance test:** Trigger `@tpto` RLV command; verify teleport.

---

## RLV-002 | forceAttach() logs but does nothing

- **File:** `Linkpoint/src/main/java/com/linkpoint/rlv/RLVController.kt`
- **Lines:** ~290–293
- **Category:** stub
- **Severity:** High

**Fix plan:**
Parse target as inventory item UUID; call `AvatarManager.attachItem(itemId, attachPoint)`.
Reference: RLVa spec `@attach[:attachpt]=force`.

---

## RLV-003 | forceDetach() logs but does nothing

- **File:** `Linkpoint/src/main/java/com/linkpoint/rlv/RLVController.kt`
- **Lines:** ~295–298
- **Category:** stub
- **Severity:** High

**Fix plan:**
Parse target UUID; call `AvatarManager.detachItem()`.

---

## RLV-004 | forceRemoveOutfit() logs but does nothing

- **File:** `Linkpoint/src/main/java/com/linkpoint/rlv/RLVController.kt`
- **Lines:** ~300–303
- **Category:** stub
- **Severity:** High

**Fix plan:**
Parse layer name; call `OutfitManager.removeWearablesByLayer()`.

---

## RLV-005 | getOutfitInfo() returns empty string

- **File:** `Linkpoint/src/main/java/com/linkpoint/rlv/RLVController.kt`
- **Lines:** ~307–310
- **Category:** incomplete / query-stub
- **Severity:** Medium

**Fix plan:**
1. Query `OutfitManager.getWornWearables()`.
2. Format as `"layer_name:item_id,..."` per RLVa spec.

---

## RLV-006 | getAttachInfo() returns empty string

- **File:** `Linkpoint/src/main/java/com/linkpoint/rlv/RLVController.kt`
- **Lines:** ~312–315
- **Category:** incomplete / query-stub
- **Severity:** Medium

**Fix plan:**
Query `AvatarManager.getAttachments()`; format per RLVa spec.

---

## PUSH-001 | FCM onNewToken() does not upload token

- **File:** `Linkpoint/src/main/java/com/linkpoint/push/LinkpointFirebaseMessagingService.kt`
- **Lines:** ~29–32
- **Category:** stub / future-work
- **Severity:** Medium

**What is broken:**
```kotlin
override fun onNewToken(token: String) {
    Log.i(TAG, "FCM token refreshed (${token.length} chars)")
    // Future: upload token to grid service / self-host relay endpoint.
}
```

**Fix plan:**
1. Implement token upload via a grid-specific registration endpoint or
   self-hosted relay.
2. Re-upload on every token refresh and on login.

**Acceptance test:** Verify token upload request in network log after login.
