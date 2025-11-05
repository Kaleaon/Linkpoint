# Swift UI Playbook for Second Life Clients

This playbook outlines how to build and maintain Swift-based user interfaces that align with the universal integration architecture. It covers module structure, data bridges from the legacy Java/Kotlin stack, and multiple interface profiles tailored to different Second Life usage modes (mobile touch, desktop Catalyst, spatial VR, accessibility).

## 1. Module Architecture

```
MetaverseSwift/
  Package.swift
  Sources/
    MetaverseProtocolKit/    # Generated protobuf + state models (shared with Kotlin)
    ViewerUI/                # SwiftUI views, navigation, state reducers
    MetalRenderer/           # Metal-based rendering bridge consuming shared scene graph
    SharedComponents/        # Reusable UI/HUD pieces (chat, inventory, minimap)
  Tests/
    ViewerUITests/
    ProtocolKitTests/
```

- **MetaverseProtocolKit**: Wraps protobuf-generated models (`AgentState`, `InventoryEntry`) and provides Combine publishers for updates from the command bus.
- **ViewerUI**: Implements MVU-style reducers (inspired by Redux) so UI logic matches Kotlin Compose flows. Expose `ViewerApp` entry point and profile-specific scenes.
- **MetalRenderer**: Hosts WGPU/Metal renderer; provides `MetalViewRepresentable` components for integration into SwiftUI layouts.
- **SharedComponents**: Contains cross-profile building blocks such as `ChatPanel`, `InventoryGrid`, `QuickActionsBar`.

## 2. Data & Command Bridge

- **Shared Schemas**: Consume the canonical protobuf/FlatBuffer schemas; generated Swift code is stored in `MetaverseProtocolKit`. Use Swift Package Manager plugins to regenerate code as part of the build.
- **Command Bus**: Provide `CommandDispatch` protocol mirroring Kotlin/Java implementation. SwiftUI views invoke commands via environment objects.
- **State Synchronization**: Use Combine pipelines receiving updates from shared services (gRPC streaming). Convert to `@Published` properties or `State`/`StateObject` for SwiftUI consumption.

## 3. UI Profiles

### 3.1 iPhone (Touch-First)

- **Navigation**: `NavigationStack` with tab-based entry points (Chat, People, World, Inventory).
- **Layout**: Single-column scroll views with context panels using `Sheet` and `BottomSheet` components.
- **Interaction**: Touch gestures, haptics, simplified HUD overlays for quick actions.

```swift
struct PhoneRootView: View {
    @State private var selectedTab: Tab = .chat

    var body: some View {
        TabView(selection: $selectedTab) {
            ChatFeedView()
                .tabItem { Label("Chat", systemImage: "text.bubble") }
                .tag(Tab.chat)

            WorldView()
                .tabItem { Label("World", systemImage: "globe") }
                .tag(Tab.world)

            InventoryView()
                .tabItem { Label("Inventory", systemImage: "tray.full") }
                .tag(Tab.inventory)
        }
    }
}
```

### 3.2 iPad (Multitasking)

- **Navigation**: `NavigationSplitView` with persistent sidebar + detail panes. Support Stage Manager and external keyboard.
- **Layout**: Multi-pane inventory and chat sidebars; floating HUD for region controls using `popover`.
- **Interaction**: Pointer interactions, keyboard shortcuts, Apple Pencil gestures for building tools.

### 3.3 Mac Catalyst Desktop

- **Navigation**: Window-based UI with toolbar, inspector panels, resizable HUD dock.
- **Layout**: Leverage `SplitView` and `InspectorGroup` to emulate desktop viewers. Provide menu commands mapping to shared command bus.
- **Integration**: Share Compose-equivalent command names so features remain consistent with Android desktop builds.

### 3.4 visionOS / Spatial (VR/AR)

- **Navigation**: Spatial tab bar or volumetric windows anchored in 3D space.
- **Layout**: Use `ImmersiveSpace` for world rendering; overlay `ImmersiveView` panels for chat/inventory at adjustable distances.
- **Interaction**: Air gestures and voice. UI components supply `SpatialTapAction` and `RealityKit` anchors.
- **Renderer**: Integrate OpenXR-compatible pipeline via Metal; reuse layout metadata schema (see `mobile_ui_translation_guide.md` section 7).

### 3.5 Accessibility Profile

- **Features**: Dynamic Type scaling, VoiceOver descriptions, high-contrast themes.
- **Implementation**: Provide `AccessibilityOptions` environment to toggle UI adjustments globally. Ensure components have `accessibilityLabel`, `accessibilityValue`.

## 4. Styling & Theming

- Define shared theme tokens (colors, typography, spacing) in `SharedComponents/Theme.swift`; ensure alignment with Android Compose theme.
- Support user customization by exposing theme overrides via profile settings, synced through the command bus.

## 5. Testing & Automation

- **Snapshot Tests**: Use `pointfreeco/swift-snapshot-testing` with profile-specific reference images (phone, tablet, spatial).
- **UITests**: Xcode UI tests covering key flows (login, chat, teleport) per profile.
- **CI Tasks**: Extend `swift-build.yml` to build, test, and archive frameworks for iOS, iPadOS, macOS, and visionOS.

## 6. Integration Steps

1. Generate Swift protobuf bindings from canonical schema and commit to `MetaverseProtocolKit`.
2. Implement command bus bridge using shared gRPC/OpenAPI endpoints.
3. Build profile-specific root views (`PhoneRootView`, `TabletRootView`, `CatalystApp`, `SpatialViewer`).
4. Create shared HUD component library with layout metadata for VR.
5. Hook Swift packages into the automation manifest (`build-manifest.yml`) and configure CI targets.

## 7. References

- `docs/mobile_ui_translation_guide.md` – covers Java→Kotlin→SwiftUI translation patterns.
- `docs/vr_ui_research.md` – VR-specific HUD behaviors and layout metadata recommendations.
- `docs/skeleton_repo_templates.md` – Swift/Metal project skeleton and tooling.
