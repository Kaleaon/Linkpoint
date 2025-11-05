# Skeleton Repository Templates

This guide captures starter layouts for each platform in the universal Second Life viewer initiative. Each skeleton keeps module boundaries consistent with the integration map and includes essential build tooling hooks. Copy the appropriate template when bootstrapping a new repo or submodule.

## Kotlin / Java (Android & Multiplatform)

```
android-client/
  build.gradle.kts
  settings.gradle.kts
  gradle.properties
  app/
    src/
      main/
        AndroidManifest.xml
        java/
        kotlin/
      androidTest/
      test/
    build.gradle.kts
  core-protocol/
    src/commonMain/kotlin/
    src/androidMain/kotlin/
    src/desktopMain/kotlin/
    build.gradle.kts
  render-filament/
    src/main/kotlin/
    src/main/cpp/
    CMakeLists.txt
  integration-tests/
    src/androidTest/kotlin/
  scripts/
    lint.sh
    build_matrix.yml
```

- **Primary tooling**: Gradle (KTS), Detekt, Ktlint, Dokka.
- **CI hook**: GitHub Actions workflow calling `./gradlew lint test assemble`.

## C++ (Firestorm / RestrainedLove Desktop)

```
desktop-firestorm/
  CMakeLists.txt
  cmake/
    toolchains/
  src/
    viewer/
    protocol/
    plugins/
  include/
  tests/
    unit/
    integration/
  thirdparty/
  scripts/
    configure.sh
    build_debug.sh
    lint.sh
  docs/
    ARCHITECTURE.md
```

- **Primary tooling**: CMake presets, clang-format, clang-tidy, vcpkg for dependencies.
- **CI hook**: matrix builds for Windows/macOS/Linux using Ninja + ccache.

## C# / .NET (LibreMetaverse Services)

```
libremetaverse-services/
  src/
    LibMetaverse.Protocol/
    LibMetaverse.Services.Auth/
    LibMetaverse.Services.Inventory/
    LibMetaverse.Client/
  tests/
    LibMetaverse.Protocol.Tests/
    LibMetaverse.Services.Tests/
  proto/
  tools/
    codegen/
  docs/
  Directory.Build.props
  Directory.Build.targets
  global.json
```

- **Primary tooling**: dotnet CLI, Paket/NuGet, StyleCop, Sonar analyzers.
- **CI hook**: GitHub Actions invoking `dotnet test`, docker-compose integration tests.

## Rust (Services & Viewer Prototype)

```
rust-metaverse/
  Cargo.toml
  Cargo.lock
  crates/
    metaverse-protocol/
    service-auth/
    service-inventory/
    service-telemetry/
    viewer-wgpu/
    ffi-bridge/
  proto/
  scripts/
    fmt.sh
    lint.sh
    coverage.sh
  benches/
  docs/
    ARCHITECTURE.md
```

- **Primary tooling**: Cargo workspaces, rustfmt, Clippy, cargo-udeps.
- **CI hook**: GitHub Actions `cargo fmt --check`, `cargo clippy`, `cargo test`, `cargo bench` (nightly).

## Swift / Objective-C (Apple Catalyst + iOS)

```
apple-viewer/
  Package.swift
  Sources/
    MetaverseProtocolKit/
    ViewerUI/
    MetalRenderer/
  Projects/
    ViewerApp.xcodeproj
  Tests/
    MetaverseProtocolKitTests/
    ViewerUITests/
  Resources/
    Shaders.metal
  scripts/
    ci_build.sh
    lint.sh
```

- **Primary tooling**: Swift Package Manager, Xcode schemes, SwiftLint, Fastlane.
- **CI hook**: Xcode Cloud or GitHub Actions macOS runners executing `xcodebuild` + SwiftLint.

## TypeScript / WebAssembly (Web Viewer)

```
web-viewer/
  package.json
  pnpm-lock.yaml
  tsconfig.json
  src/
    app/
    sdk/
  public/
  wasm/
    rust/
      Cargo.toml
      src/
    build.sh
  tests/
    e2e/
    unit/
  scripts/
    lint.mjs
    generate-sdk.mjs
```

- **Primary tooling**: pnpm, Vite, ESLint, Prettier, Vitest, Playwright.
- **CI hook**: GitHub Actions with Node + wasm-pack steps.

## Python / Lua / Node.js (Tooling & Automation)

```
tooling-suite/
  python/
    pyproject.toml
    src/
    tests/
    scripts/
  lua/
    modules/
    tests/
  node/
    package.json
    src/
    tests/
  docker/
    docker-compose.yml
  docs/
```

- **Primary tooling**: poetry or hatch for Python, busted for Lua tests, Jest for Node.js.
- **CI hook**: Composite workflow running lint/test for each sublanguage.

## Unity (C#) XR Client

```
unity-client/
  Assets/
    Scripts/
      Networking/
      Rendering/
      UI/
    Shaders/
    Plugins/
      Grpc/
  ProjectSettings/
  Packages/
  Tests/
    EditMode/
    PlayMode/
  Tools/
    BuildPipeline/
```

- **Primary tooling**: Unity 2022+, OpenXR plugin, Mirror/Photon, Unity Test Runner.
- **CI hook**: Unity Builder GitHub Action for headless playmode tests.

## Unreal Engine (C++) XR Client

```
unreal-client/
  MetaverseViewer.uproject
  Source/
    MetaverseViewer/
      MetaverseViewer.Build.cs
      Public/
      Private/
  Plugins/
    MetaverseProtocol/
  Config/
  Content/
  Scripts/
    Build.ps1
    Build.sh
```

- **Primary tooling**: Unreal 5, OpenXR, Unreal Build Tool, clang-format.
- **CI hook**: Unreal Engine Automation Tool, per-platform package jobs.

## Data & Analytics Stack

```
telemetry-stack/
  docker-compose.yml
  services/
    ingestion-rust/
    processing-kotlin/
    warehouse-sql/
  schemas/
  dashboards/
    grafana/
  tests/
  scripts/
    load-test.sh
```

- **Primary tooling**: Docker Compose, Rust/Kotlin microservices, dbt/SQL for warehousing, Grafana dashboards.
- **CI hook**: Integration workflow running unit tests, schema validation, and load-testing smoke suite.

These templates can be converted into actual repositories or submodules. Adjust names and dependent packages to match organizational standards.
