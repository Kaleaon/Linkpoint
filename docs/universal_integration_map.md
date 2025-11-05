# Universal Coding Integration Map

## Summary

This document maps the major code assets, technology stacks, and integration touchpoints needed to build a platform-agnostic Second Life–compatible viewer ecosystem. It focuses on aligning Kotlin/Java (Android), Firestorm/RestrainedLove (C++/wxWidgets), LibreMetaverse (.NET/C#), and emerging targets such as Rust- or Godot-based clients. The goal is to make each component interoperable through shared protocols, modular service layers, and well-defined adapter interfaces.

## Platform & Language Landscape

| Platform / Viewer | Primary Languages | Build Targets | Key Interfaces & SDKs | Notes |
| --- | --- | --- | --- | --- |
| Kotlin Android Client (Lumiya, Linkpoint) | Kotlin, Java, JNI (C++) | Android (ARM/ARM64) | Android SDK, NDK, OpenGL ES/Vulkan, gRPC-lite | Mobile-first, already modularized into core, rendering, network layers |
| Firestorm Viewer | C++, XML UI, Python (build tools) | Windows, macOS, Linux (x86/x64) | LLMessageSystem, OpenGL, Havok/PhysX, FMOD Ex, wxWidgets | Canonical desktop Second Life viewer; RestrainedLove (RLV) is a behavioral fork |
| RestrainedLove Viewer | C++ (Firestorm fork) | Windows, macOS, Linux | Same as Firestorm plus RLV constraints API | Upstream Firestorm patches + RLV-specific capability flags |
| LibreMetaverse / OpenMetaverse | C#, .NET, Mono | Windows, Linux, macOS, Headless services | Caps/UDP simulators, LLSD, WebSockets, libcurl | Provides protocol stack for bots, tooling, or middleware |
| Second Life Grid Services | C++, Erlang, Python, Mono | Server-side simulators | LLUDP, Capabilities API, Agent Domain, Inventory API | Source of truth for protocols; requires compatibility layers |
| Rust Target (future) | Rust, FFI (C, C#) | Desktop, WASM, Server | Tokio, Bevy, WGPU, bindings to libremetaverse | Enables safety-focused core, modular rendering |
| Godot Target (future) | GDScript, C#, C++ modules | Desktop, Mobile, Web | Godot High-Level Multiplayer, GDNative, WGPU | Candidate for simplified 3D client or companion app |

## Integration Layers & Reusable Services

- **Protocol & Session Layer**: Normalize agent login, region handshake, capabilities discovery. Leverage LibreMetaverse as a reference model, expose consistent gRPC/REST endpoints, and wrap legacy LLMessage-based flows in adapters for Kotlin (Coroutine-based) and C++ (asio-based).
- **Scene Graph & State Replication**: Define a canonical entity/component schema in protobuf/FlatBuffers. Implement mappers in Kotlin (Jetpack Serialization), C++ (flatbuffer bindings), and C# (System.Text.Json) to keep region state synchronized across clients.
- **Rendering Pipeline**: Abstract core rendering interfaces (`Renderer`, `MaterialSystem`, `AvatarRig`). Map to OpenGL ES/Vulkan on Android (via Filament), OpenGL on Firestorm/RLV, and WGPU (Rust/Godot). Provide shader translation guidelines (GLSL ↔ SPIR-V ↔ WGSL).
- **UI & Interaction Layer**: Encourage MVVM pattern. For Firestorm/RLV, wrap legacy XML UI with command bus accessible via shared protocol. For Kotlin clients, use Jetpack Compose or XML bridging to ensure shared UX flows. For Godot, leverage signal bindings mirroring the command bus.
- **Asset & Inventory Services**: Centralize in microservice (possibly leveraging Rust for concurrency). Provide REST endpoints exposing inventory, textures, animations. Clients consume through platform-specific caches (Room DB for Android, SQLite/boost::filesystem for C++).
- **Telemetry & Diagnostics**: Unified logging schema (OpenTelemetry). Provide bindings for Kotlin (OTel SDK), C++ (otel-cpp), C# (OpenTelemetry-dotnet). Feed dashboards for latency, render performance, inventory sync health.

## Cross-Language Bridge Strategies

| Source Stack | Target Stack | Bridge Mechanism | Notes |
| --- | --- | --- | --- |
| Kotlin ↔ Java | Direct interop | Shared Gradle modules, Kotlin Multiplatform (expect/actual) | Kotlin should own new business logic; Java modules converted incrementally |
| Kotlin/Java ↔ C++ (Firestorm) | JNI, AIDL, gRPC | Encapsulate viewer core features as NDK libraries; expose command API to desktop clients | Useful for reusing mobile rendering or asset caches on desktop |
| C++ (Firestorm/RLV) ↔ C# (LibreMetaverse) | C API façade + P/Invoke | Export “ViewerCore” C interface; C# hosts can automate tests or provide middleware | Ensures protocols align with LibreMetaverse reference implementation |
| C# LibreMetaverse ↔ Rust | C FFI, gRPC, Cap’n Proto | Generate shared schema; Rust services (inventory, physics) integrate via async gRPC | Facilitates modular server-side or bot components |
| Rust Core ↔ Godot | GDNative/ GDExtension | Rust supplies scene data & physics; Godot handles UI/UX | Enables rapid prototyping for cross-platform viewers |

## Platform-Agnostic Second Life Viewer Blueprint

1. **Establish Canonical Domain Model**: Define avatars, regions, parcels, inventory, and chat as shared protobuf schemas with versioning rules.
2. **Protocol Adapters**: Build adapters for LLUDP/Capabilities that expose the canonical model as platform-neutral events (event bus + gRPC streaming). Reuse LibreMetaverse codegen where possible.
3. **Service Mesh**: Deploy lightweight Rust or C# services for authentication, inventory, messaging, and texture fetching. Provide REST + WebSocket endpoints for all clients.
4. **Rendering Backends**: Maintain interchangeable backends: Filament (Android), legacy OpenGL (Firestorm/RLV), WGPU (Rust desktop), and Godot renderer. Each backend consumes the canonical scene graph.
5. **UI Composition Layer**: Separate domain commands (teleport, inventory operations) from presentation. Kotlin Compose, Firestorm XML, and Godot scenes bind to the same command names and payloads.
6. **Extensibility & Mod Safety**: Standardize scripting hooks (e.g., Lua, JavaScript) with sandboxed capability flags, enabling RestrainedLove constraints or third-party accessories without diverging core code.
7. **Continuous Compliance**: Maintain automated compatibility tests against Second Life grid simulators (login, teleport, inventory, attachments) via LibreMetaverse-driven harnesses.

## Conversion & Modernization Opportunities

- **Java → Kotlin**: Prioritize utility modules, network layer, and UI controllers. Use Kotlin Multiplatform to share protocols with desktop targets in the future.
- **C++ → Rust**: Start with stateless services (asset fetchers, cache managers). Use `cxx` or `ffi-support` crates to integrate Rust modules back into Firestorm until full port is feasible.
- **C# → Rust**: For LibreMetaverse, prototype Rust-based simulators or agent services with bindings to existing bot APIs.
- **C++/Kotlin → Godot**: Translate core scene entities into Godot resources, leveraging `godot-rust` bindings. Ideal for lightweight companion viewer or VR front-ends.
- **Rendering Shader Conversions**: Maintain shader translation pipeline (GLSL ↔ SPIR-V ↔ WGSL) using SPIRV-Cross or Naga to keep render paths synchronized across engines.

## Code Module Catalog & Translation Layout

| Functional Module | Responsibilities | Legacy Implementations | Kotlin/Android Translation | C#/LibreMetaverse Alignment | Rust Target | Godot/Other Targets |
| --- | --- | --- | --- | --- | --- | --- |
| Protocol Gateway | Login, capabilities, region handshake | Firestorm `llmessage` stack, LibreMetaverse `AgentManager` | Coroutine-based client using Retrofit/ktor + protobuf | Reuse `CapsClient`, expose gRPC façade | Async gateway with `tonic` + `serde` | GDNative service, integrate via WebSocket |
| Network Transport | UDP messaging, interest management | C++ `LLMessageSystem`, Java `LLUDPClient` | Kotlin Multiplatform networking module + okio buffers | .NET `UDPPacketBuffer` | Rust `quinn`/`tokio` with ECS event queue | Godot high-level multiplayer bridge |
| Scene Graph | Region primitives, attachments, terrain | Firestorm `LLVOVolume`, Lumiya `SceneNode` | Shared `SceneGraph` Kotlin module (Flow-based updates) | `SimObject` wrappers | ECS (bevy specs) with FlatBuffer schema | Godot `Node3D` graph importer |
| Rendering Core | Materials, shaders, avatar rendering | Firestorm OpenGL pipeline, Lumiya Filament layer | Jetpack Compose + Filament renderer adapters | Hook via interop to provide test harness | WGPU-based renderer with `wgpu-rs` | Godot renderer scripts and shader porting |
| Asset & Inventory | Inventory, textures, animations | Firestorm `LLInventoryModel`, LibreMetaverse `InventoryManager` | Kotlin repository pattern + Room cache | Direct reuse; expose REST caching | Rust asset microservice with `reqwest`, `sled` | Godot resource loader via REST/gRPC |
| UI & Interaction | UI markup, command handling, input | Firestorm XML/UI widgets, Lumiya Activities | Compose screens bound to shared command bus | Avalonia/MAUI front-ends backed by shared commands | Tauri/Wry UI or egui front | Godot scenes with signal mapping |
| Scripting & Mods | LSL script execution, RLV constraints | Firestorm script engine, RLV module | Kotlin plugin sandbox using wasmtime | .NET scripting host, capability flags | Rust WASM host with capability checks | Godot GDScript sandbox connectors |
| Voice & Chat | Vivox/voice, text chat, IM logs | Firestorm `LLVoiceClient`, Lumiya chat stack | Kotlin service connecting to Vivox SDK | LibreMetaverse `VoiceGateway` integration | Rust voice relay using `janus` or `mediasoup` | Godot WebRTC or Vivox plugin |
| Telemetry & QA | Logging, metrics, replay harness | Firestorm `LLPerfStats`, Lumiya logs | Kotlin OTel exporter, logcat bridge | .NET OTel pipeline | Rust tracing + OpenTelemetry exporter | Godot analytics plugin |

### Translation Recipes

- **Schema First**: Maintain protobuf/FlatBuffer definitions per module. Generate Kotlin, C++, C#, and Rust bindings to eliminate hand-written translations.
- **Interface Contracts**: Promote language-neutral service interfaces (gRPC IDL or OpenAPI) so each client consumes or exposes the same capabilities.
- **Shared Configuration**: Centralize settings in TOML/YAML manifests; clients load and validate against schema to maintain parity.
- **Automated Diffing**: Use semantic diff tools (e.g., `kotlinx.serialization` schema diff, `clang` AST diff) to ensure translated modules remain behaviorally equivalent.
- **Documentation Synchronization**: Store module docs in Markdown with embedded code snippets per language, generated via literate tooling (MkDocs + `mdbook` for Rust).

## Lego-Brick Modularization Strategy

### Design Principles

- **Clear Boundaries**: Each module exposes a minimal public surface (`NetworkModule`, `SceneModule`, `RenderModule`) through language-agnostic interfaces.
- **Dependency Direction**: Enforce flow from outer UI shells inward to core services; core modules avoid platform-specific dependencies.
- **Hot-Swappable Components**: Build adapter registries so alternate implementations (e.g., Rust renderer vs. C++ OpenGL) can be swapped via configuration.
- **Versioned Contracts**: Tag every interface and schema with semantic versions; provide compatibility shims when incrementing.
- **Extensibility Hooks**: Offer plugin APIs (Lua/WASM) gated by capability flags to keep RestrainedLove constraints intact without forking.

### Modular Layout Blueprint

```
clients/
  android-kotlin/          # Compose UI + Filament renderer adapters
  desktop-firestorm/       # wxWidgets UI shell consuming shared modules
  desktop-rust/            # WGPU demo client (future)
shared/
  protocol/                # Protobuf/gRPC definitions, shared models
  services/                # Auth, inventory, messaging microservices (Rust/C#)
  scene/                   # Canonical scene graph builders and sync logic
  rendering/               # Shader packages, material definitions, tools
  scripting/               # Sandbox runtimes, capability descriptors
  telemetry/               # Logging schemas, dashboard configs
tooling/
  converters/              # Shader translation, asset pipeline scripts
  testing/                 # Grid compliance harness, replay tools
```

### Implementation Roadmap for Modularity

1. **Inventory the Codebase**: Tag existing files with module IDs. For Firestorm, map `ll*` files to proposed shared modules; for Kotlin, align packages.
2. **Define Interfaces**: Write interface contracts in the shared repository, generate stubs for each language, and integrate gradually (facade pattern).
3. **Extract Shared Libraries**: Carve out protocol and scene graph logic into language-appropriate libraries (KMP for Kotlin, static libs for C++, crates for Rust).
4. **Adapter Wrappers**: Build thin adapters that translate legacy calls into modular interfaces, allowing incremental migration without large rewrites.
5. **Automated Testing**: Create contract tests per module to validate any implementation (Kotlin, C++, Rust) adheres to expected behaviors.
6. **Continuous Packaging**: Use package managers (Gradle Maven artifacts, NuGet, Cargo crates, vcpkg) to distribute module builds; version lock in manifest.
7. **Governance**: Establish module maintainers, review checklists, and compatibility matrices to ensure bricks snap together without regressions.

### Independent Upgrade Pathways

- **Protocol Upgrades**: Deploy new capability endpoints in the shared service mesh; clients update generated stubs without touching core logic.
- **Rendering Experimentation**: Introduce new renderer modules behind feature flags, enabling A/B testing (e.g., Vulkan vs. OpenGL).
- **Feature Plugins**: Allow optional modules (VR support, accessibility overlays) to register via discovery endpoints; clients that opt in simply load the plugin brick.
- **Rollback Strategy**: Maintain compatibility bins (stable, beta, experimental) so modules can be promoted or rolled back independently.

## Additional Language & Platform Targets

- **Apple Ecosystem (iOS/iPadOS/macOS)**: Primary languages are Swift and Objective-C, with SwiftUI/AppKit for UI and Metal for rendering. Consider a Swift consumer of shared protocols, Metal-backed renderer brick, and Catalyst for iPad→macOS reuse.
- **Web Front-Ends**: TypeScript + WebAssembly for a browser-based viewer or dashboard (use Rust/WASM or AssemblyScript bindings for heavy logic).
- **Scripting & Automation**: Python for build/test tooling, Lua for in-world scripting experiments, JavaScript (Node.js) for companion services.
- **XR/VR Targets**: C#/Unity and C++/Unreal integration layers if VR viewers are planned; align with OpenXR bindings and reuse canonical scene graph.
- **Data/Analytics Pipelines**: SQL for telemetry warehousing, plus Scala/Kotlin (JVM) or Rust for real-time analytics microservices.

## Roadmap & Milestones

| Phase | Focus | Key Deliverables | Dependencies |
| --- | --- | --- | --- |
| Phase 0 (Weeks 0-2) | Discovery & Alignment | Finalize canonical schemas, audit existing code modules, set up shared repo structure | Access to Firestorm/RLV source, LibreMetaverse API docs |
| Phase 1 (Weeks 3-6) | Protocol Harmonization | Capabilities gateway service (Rust/C#), Kotlin coroutine client, C++ adapter stubs | Canonical schema, load-testing harness |
| Phase 2 (Weeks 7-12) | Rendering & UI Abstraction | Filament renderer upgrade, OpenGL adapter refactor, command bus interface across clients | Shared scene graph builder, shader pipeline |
| Phase 3 (Weeks 13-18) | Cross-Language Services | Inventory microservice, texture CDN cache, unified logging/telemetry | Service mesh infra, observability stack |
| Phase 4 (Weeks 19-26) | Experimental Targets | Rust desktop prototype (WGPU), Godot companion viewer, scripting sandbox | Stabilized APIs from previous phases |
| Phase 5 (Continuous) | Compliance & QA | Automated grid compatibility suite, cross-client regression dashboards, release train | CI/CD pipelines, integration testing farm |

## Operational Considerations

- **Repository Strategy**: Adopt a polyrepo with synchronized submodules per client, or a monorepo with language-specific workspaces (Gradle, CMake, Cargo, .NET). Provide automated dependency updates.
- **Documentation & Knowledge Base**: Maintain living ADRs (architecture decision records) for protocol changes, renderer upgrades, and interoperability guidelines.
- **Testing Matrix**: Automate tests across Android devices, Windows/macOS/Linux desktops, and headless LibreMetaverse simulators. Include VR/AR device smoke tests if Godot integration targets XR.
- **Governance & Contribution**: Establish code owners per layer, enforce linting/formatting per language, and document contribution paths for community-driven viewers (RestrainedLove, third-party forks).

This integration map should serve as the foundation for detailed implementation tickets, ensuring that legacy viewers, modern mobile clients, and new experimental stacks converge toward a single, platform-agnostic Second Life experience.
