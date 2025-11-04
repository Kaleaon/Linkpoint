## Bare-Bones Mobile Viewer Plan

### Current Foundation
- **Protocol:** Kotlin port covers packet transport (`SLCircuitNew`, `SLPacket`) with template-backed decoding to catch unimplemented messages.
- **Authentication:** XML-RPC login (`SLAuth`) + session persistence (`SessionManager`).
- **Rendering:** Filament integration present; PBR-ready shaders available via Google’s mobile library.
- **Reference Repos:** Firestorm (C++), Second Life viewer, LibreMetaverse (C#) linked locally for parity checks.

### Remaining Minimum Viable Work
1. **Template Message Coverage**
   - Complete template-driven field decoding (LLSD, estate data, etc.).
   - Emit Kotlin message wrappers only for high-frequency packets (agent/object updates).
   - **ETA:** 1 week (parallel with protocol validation).

2. **Network Validation Loop**
   - Reconcile message handling against LibreMetaverse unit tests (login handshake, agent circuit).
   - Implement ACK + resend metrics to ensure stable mobile networking.
   - **ETA:** 1 week following template completion.

3. **Asset + Texture Path**
   - Port LibreMetaverse asset fetch + cache logic (textures, meshes) into Kotlin coroutines.
   - Hook OpenJPEG/OpenXR paths to Filament texture upload (ensure ETC2/ASTC fallback for Android).
   - **ETA:** 2 weeks; can begin once network validation is underway.

4. **Renderer Bring-Up (Mobile)**
   - Integrate Filament’s mobile PBR pipeline with decoded scene graph.
   - Implement minimal scene: skybox, terrain patch, avatar mesh with single texture.
   - Ensure GLES3 feature gating for lower-end devices.
   - **ETA:** 2–3 weeks, overlapping with asset ingestion.

5. **UI & Inputs**
   - Compose-based login shell, simple world HUD (FPS, chat feed).
   - Touch controls (virtual joystick + camera orbit) using existing gesture utilities.
   - **ETA:** 1 week after renderer displays first frame.

### Timeline Snapshot (Assuming 1-engineer focus)
- **Week 1:** Finish template decoder, start protocol validation.
- **Week 2:** Protocol tests complete; asset pipeline port begins.
- **Weeks 3–4:** Asset fetch + Filament integration (first lit scene on device at end of Week 4).
- **Week 5:** UI shell and controls; prepare internal alpha build.

### Deliverable Definition (Bare-Bones)
- Login to Agni grid, load starter region, render terrain + own avatar + nearby chat text.
- Basic movement & camera; no inventory, IM, or advanced UI.
- Asset fetch limited to needed textures/meshes (no baking, deferred features stubbed).

### Risks & Mitigations
- **Protocol Drift:** Continual comparison to Firestorm/LibreMetaverse; template fallback ensures packets still surface data for debugging.
- **Mobile Performance:** Keep Filament usage lean (deferred shading off, use ASTC/ETC2), leverage Google PBR library.
- **OpenJPEG Integration:** Validate JNI bridge performance early; consider transcoding to KTX2 for GPU upload.

### Immediate Next Steps
1. Extend template decoder to cover LLSD/map variable types.
2. Reconcile StartPingCheck/AgentUpdate handling with LibreMetaverse.
3. Draft Kotlin asset fetch service mirroring LibreMetaverse `AssetManager`.
