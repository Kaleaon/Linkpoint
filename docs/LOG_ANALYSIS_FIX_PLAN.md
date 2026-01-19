# Linkpoint Debug Log Analysis & Fix Plan

> **Log File:** `linkpoint_log_2026-01-19_01-23-38.txt`  
> **Analysis Date:** January 19, 2026  
> **Reference:** Decompiled Lumiya viewer in `lumiya_decompiled_source/`

---

## Executive Summary

Analysis of the debug log from filebin.net reveals **4 critical issues** preventing the viewer from functioning:

| Issue | Severity | Impact |
|-------|----------|--------|
| RegionHandshake never received | 🔴 Critical | Region name "Unknown", world data won't load |
| Missing message handlers | 🔴 Critical | 396 handler misses, objects/terrain not processed |
| No SwapChain | 🔴 Critical | Rendering not visible |
| Packet ACK timing | 🟡 Medium | High latency, potential retries |

---

## Issue 1: RegionHandshake Never Received

### Log Evidence
```
Region Name: Unknown
⚠️ RegionHandshake never received - world data won't load!
```

### Root Cause Analysis
The simulator sends `RegionHandshake` (message ID `0xFFFF0094`) after `UseCircuitCode` is acknowledged. Either:
1. The message ID is not being recognized correctly
2. The handler is registered but never called
3. The handler fails silently during parsing

### Lumiya Reference (`SLAgentCircuit.java` lines 1334-1364)
```java
@Override
public void HandleRegionHandshake(RegionHandshake regionHandshake) {
    if (this.authReply.isTemporary) {
        return;
    }
    // 1. Send reply immediately
    RegionHandshakeReply regionHandshakeReply = new RegionHandshakeReply();
    regionHandshakeReply.AgentData_Field.AgentID = this.circuitInfo.agentID;
    regionHandshakeReply.AgentData_Field.SessionID = this.circuitInfo.sessionID;
    regionHandshakeReply.RegionInfo_Field.Flags = 0;
    
    // 2. Apply terrain data
    if (this.gridConn != null && this.gridConn.parcelInfo != null) {
        this.gridConn.parcelInfo.terrainData.ApplyRegionInfo(regionHandshake.RegionInfo_Field);
    }
    SendMessage(regionHandshakeReply);
    
    // 3. Extract region name from SimName bytes
    this.regionName = SLMessage.stringFromVariableOEM(regionHandshake.RegionInfo_Field.SimName);
    
    // 4. Store region ID
    if (regionHandshake.RegionInfo2_Field != null && regionHandshake.RegionInfo2_Field.RegionID != null) {
        this.regionID = regionHandshake.RegionInfo2_Field.RegionID;
    }
    
    // 5. Store estate manager flag
    this.isEstateManager = regionHandshake.RegionInfo_Field.IsEstateManager;
    
    // 6. Notify listeners
    if (this.eventBus != null) {
        this.eventBus.publish(new SLRegionInfoChangedEvent());
    }
}
```

### Recommended Fix
1. **Verify message ID decoding** - `0xFFFF0094` is decoded as a low-frequency message in Lumiya (4-byte header: `0xFFFF` prefix + 2-byte ID `0x0094`). Note: The actual ID on wire is `148` (0x94).
2. **Add debug logging** before handler registration to confirm correct ID mapping
3. **Check SimName parsing** - Lumiya uses `unpackVariable(byteBuffer, 1)` for variable-length string with 1-byte length prefix

---

## Issue 2: Missing Message Handlers (396 Misses)

### Log Evidence
```
Handler Misses: 396
No handler registered for message 11
No handler registered for message 65297
Registered Handlers: 11
```

### Missing Critical Handlers

| Message ID | Name | Purpose |
|------------|------|---------|
| `11` (0x0B) | LayerData | Terrain heightmap, wind, clouds |
| `65297` (0xFF11) | ObjectUpdateCompressed | Compressed object data |
| `0x0C` (12) | ObjectUpdate | Full object data (may be registered but not working) |

### Lumiya Reference for LayerData (`SLAgentCircuit.java` lines 1143-1149)
```java
@Override
public void HandleLayerData(LayerData layerData) {
    SLParcelInfo sLParcelInfo;
    // Type 76 (0x4C = 'L') = Land/terrain heightmap
    if (layerData.LayerID_Field.Type != 76 || (sLParcelInfo = this.gridConn.parcelInfo) == null) {
        return;
    }
    sLParcelInfo.terrainData.ProcessLayerData(layerData.LayerDataData_Field.Data);
}
```

### Lumiya Reference for ObjectUpdateCompressed (`SLAgentCircuit.java` lines 1246-1290)
```java
@Override
public void HandleObjectUpdateCompressed(ObjectUpdateCompressed objectUpdateCompressed) {
    SLParcelInfo sLParcelInfo = this.gridConn.parcelInfo;
    for (ObjectUpdateCompressed.ObjectData objectData : objectUpdateCompressed.ObjectData_Fields) {
        try {
            // Get LocalID from compressed data
            UUID uuid = sLParcelInfo.uuidsNearby.get(Integer.valueOf(SLObjectInfo.getLocalID(objectData)));
            SLObjectInfo sLObjectInfo = uuid != null ? sLParcelInfo.allObjectsNearby.get(uuid) : null;
            
            if (sLObjectInfo != null) {
                // Update existing object
                int previousParentID = sLObjectInfo.parentID;
                sLObjectInfo.ApplyObjectUpdate(objectData);
                sLParcelInfo.updateObjectParent(previousParentID, sLObjectInfo);
            } else {
                // Create new object
                sLObjectInfo = SLObjectInfo.create(objectData);
                sLParcelInfo.addObject(sLObjectInfo);
            }
            // Handle avatar-specific updates...
        } catch (UnsupportedObjectTypeException e) {
            // Skip unsupported object types
        }
    }
}
```

### Recommended Fixes
1. **Add LayerData handler** - Register for message ID `11` (high-frequency, single byte)
2. **Investigate ObjectUpdateCompressed ID mismatch** - Log shows `65297` (0xFF11) but standard SL protocol uses `13` for high-frequency ObjectUpdateCompressed. This suggests either:
   - Linkpoint is using `0xFF` + `0x11` (medium-frequency encoding) incorrectly
   - The simulator is sending a different message type
3. **Check message ID decoding logic** - Review `extractMessageId()` in `UDPConnectionFixed.kt` for proper high/medium/low frequency handling

---

## Issue 3: No SwapChain

### Log Evidence
```
SwapChain: ✗
⚠️ NO SWAP CHAIN - Rendering not visible!
```

### Root Cause
The SwapChain must be created when the Surface is available. This happens via Filament's `UiHelper` callback system.

### Linkpoint RenderManager Pattern (Kotlin)
```kotlin
// SwapChain should be created in surface callback (from Linkpoint's RenderManager.kt)
uiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK).apply {
    renderCallback = object : UiHelper.RendererCallback {
        override fun onNativeWindowChanged(surface: Surface) {
            swapChain?.let { engine?.destroySwapChain(it) }
            swapChain = engine?.createSwapChain(surface)  // ← Creation point
            attachDisplayHelper()
        }
        override fun onDetachedFromSurface() {
            swapChain?.let { engine?.destroySwapChain(it) }
            swapChain = null
        }
    }
    attachTo(surfaceView)
}
```

Note: Lumiya used OpenGL ES directly. Linkpoint uses Google Filament for modern 3D rendering.
The SwapChain pattern above is Filament-specific and already exists in `RenderManager.kt`.

### Recommended Fix
1. **Verify SurfaceView lifecycle** - Is the surface created before render loop starts?
2. **Add logging** - Log when `onNativeWindowChanged` is called
3. **Check activity lifecycle** - Ensure render components survive configuration changes

---

## Issue 4: Invalid Agent Position

### Log Evidence
```
Position: (1.4224355E9, -1.3995137E21, -1.8634906E-32)
```

### Analysis
These are invalid floating-point values indicating:
- Uninitialized memory
- Byte order mismatch in position parsing
- Position not updated from `AgentMovementComplete` or `ObjectUpdate`

### Lumiya Reference for Position Updates
```java
// In HandleAgentMovementComplete:
this.modules.avatarControl.setAgentPosition(agentMovementComplete.Data_Field.Position, null);

// In processMyAvatarUpdate:
this.modules.avatarControl.setAgentPosition(sLObjectAvatarInfo.getAbsolutePosition(), 
    sLObjectAvatarInfo.getObjectCoords().get(2));
```

### Recommended Fix
1. **Check `AgentMovementComplete` handler** - Verify position extraction
2. **Check byte order** - Position vectors use LITTLE_ENDIAN
3. **Initialize defaults** - Set position to (128, 128, 25) as fallback

---

## Implementation Priority

### Phase 1: Message Routing (Day 1)
- [ ] Fix message ID decoding for `0xFFFF0094` (RegionHandshake)
- [ ] Add handler for `LayerData` (ID 11)
- [ ] Verify `ObjectUpdateCompressed` registration (ID 13)

### Phase 2: Handler Implementation (Day 2)
- [ ] Implement `SimName` extraction in RegionHandshake handler
- [ ] Implement LayerData terrain processing (Type 76 only)
- [ ] Wire ObjectUpdate to ObjectManager

### Phase 3: Rendering (Day 3)
- [ ] Debug SwapChain creation lifecycle
- [ ] Add logging to `onNativeWindowChanged`
- [ ] Verify Surface availability before render loop

### Phase 4: Validation (Day 4)
- [ ] Test full login flow
- [ ] Verify region name displayed
- [ ] Check object count in debug report
- [ ] Confirm 3D rendering visible

---

## Testing Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Install and run
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.linkpoint/.LinkpointActivity

# View logs
adb logcat | grep -E "(UDP|Region|Object|SwapChain|Render)"
```

---

## Reference Files

| Component | Linkpoint | Lumiya Decompiled |
|-----------|-----------|-------------------|
| Message IDs | `MessageIds.kt` | `SLMessage.java` |
| Handler Base | `MessageRouter.kt` | `SLMessageHandler.java` |
| Main Circuit | `LinkpointApp.kt` | `SLAgentCircuit.java` |
| Message Parser | `MessageParser.kt` | Per-message classes |
| Region Handshake | `MessageParser.parseRegionHandshake()` | `RegionHandshake.java` |
| Object Update | `MessageParser.parseObjectUpdate()` | `ObjectUpdate.java` |
| Layer Data | (not implemented) | `LayerData.java` |

---

## Success Criteria

After fixes are applied, the debug log should show:
- ✅ `Region Name: <actual_region_name>`
- ✅ `Total Objects in Scene: > 0`
- ✅ `SwapChain: ✓`
- ✅ `Position: (128.x, 128.x, xx.x)` (valid coordinates)
- ✅ `Handler Misses: < 50` (some misses acceptable)
