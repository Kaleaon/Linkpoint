# Firestorm / Second Life VR UI Research

This note summarizes known Oculus/VR user interface adaptations drawn from the Firestorm Viewer VR Mod and related Second Life experiments. The goal is to inform future cross-platform UI refactors so that VR workflows align with the universal integration map.

## Sources & Branches

- **Firestorm VR Mod** (community branch maintained by Peter Kappler): integrates OpenVR/Oculus support into Firestorm. Public builds accompany documentation and source diffs.
- **Second Life Lab Experiments**: Linden Lab prototyped Oculus Rift support circa 2014 using a dedicated viewer fork with stereoscopic rendering and VR-friendly HUDs. Although discontinued, artifacts describe UI constraints.
- **Third-Party Mods**: Projects such as Alchemy VR and CtrlAltStudio viewer (historical) provide additional references for VR HUD layout and interaction schemes.

## VR UI Characteristics Observed

| Area | Firestorm VR Mod Behavior | Implications |
| --- | --- | --- |
| **Rendering** | Uses OpenVR (SteamVR) APIs for stereoscopic rendering, applying barrel distortion shaders. UI rendered to texture overlays per eye. | Rendering layer must support offscreen UI rendering and compositing into VR space. |
| **Head Tracking** | Couples avatar camera to headset orientation; provides toggle to decouple pitch/roll. | Cross-platform command bus needs VR pose events and avatar camera synchronization hooks. |
| **Menus & HUDs** | Legacy 2D UI rendered as floating curved panels anchored to headset; VR Mod positions toolbar and chat windows as overlays with adjustable distance. | Requires UI abstraction capable of presenting panels in 3D space; Compose/SwiftUI should emit layout metadata consumed by VR renderer. |
| **Input** | Supports mouse/keyboard plus VR controllers via OpenVR action mappings. Simple raycast pointer interacts with existing UI sensations. | Input model should accept ray pointer, controller buttons, and gesture events mapped to standard viewer commands. |
| **Teleport / Movement** | Adds quick-turn and teleport gestures triggered by controller inputs. | Command set must include VR-specific locomotion commands while retaining legacy keyboard controls. |
| **Performance Tools** | VR Mod exposes FOV, supersampling, and performance overlays accessible via HUD. | Telemetry system should stream VR performance metrics across platforms. |

## Architecture Notes

- **UI Rendering Path**: Firestorm VR Mod renders the existing wxWidgets UI to an offscreen framebuffer, then maps it onto curved quads inside the VR scene. The viewer maintains separate render targets for world and UI layers.
- **Scene Integration**: A dedicated VR camera controller maintains headset pose, while the avatar and world camera remain synchronized to avoid motion sickness.
- **Configuration**: VR-specific settings stored under `fs_vr_*` keys (e.g., inter-pupillary distance, HUD distance). These can be modeled as capability flags in the modular architecture.
- **OpenXR Migration Opportunity**: Current mod relies on OpenVR; migrating to OpenXR would standardize API usage across Oculus, SteamVR, and Windows MR devices.

## Gaps to Address

- **UI Scaling & Responsiveness**: Legacy UI was designed for flat screens, resulting in suboptimal readability in VR. Need auto-scaling and reflow rules when presenting in 3D space.
- **Text Input**: VR keyboard integration is limited; consistent virtual keyboard or voice dictation support is needed across platforms.
- **Swift/Apple VR**: No official Apple VR branch exists; spatial computing (visionOS) requires declarative UI (SwiftUI) with spatial anchors.
- **Android XR**: Quest Android-based builds rely on OpenXR; translation from Java UI to VR overlay must be explored.

## Recommendations

1. **Abstract VR Presentation Layer**: Extend UI module contracts to emit semantic layout metadata (panel type, interaction mode). VR renderer consumes metadata to place panels.
2. **Command Bus Extensions**: Add VR-specific commands/events (`ToggleVR`, `Recenter`, `Teleport`, `SnapTurn`). Ensure compatibility with existing desktop/mobile controls.
3. **Input Adapters**: Define interface for VR controller input; map to Firestorm interaction model and Compose/SwiftUI actions.
4. **OpenXR First**: Implement OpenXR rendering backend to replace platform-specific APIs, ensuring portability to future devices.
5. **Shared HUD Components**: Create reusable HUD widgets (chat, minimap, inventory) with layout descriptors for 2D and 3D contexts.
6. **Testing Strategy**: Add automated scene playback in VR (headless) plus manual QA protocols for comfort metrics (motion sickness, latency).

## Next Steps

- Acquire latest Firestorm VR Mod source/diff and catalogue UI-related changes.
- Prototype layout metadata export from Kotlin Compose UI and SwiftUI for VR consumption.
- Design OpenXR-based renderer module (C++/Rust) integrating with shared scene graph.
- Document VR-specific UX guidelines (font sizes, panel distance) in UI standards.
