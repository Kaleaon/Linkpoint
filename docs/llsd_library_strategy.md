# LLSD Cross-Language Library Strategy

Linden Lab Structured Data (LLSD) (https://wiki.secondlife.com/wiki/LLSD) remains central to Second Life protocol messaging. Standardizing LLSD encoders/decoders across languages provides consistent behavior and simplifies interoperability. This document evaluates the benefits of per-language LLSD libraries and outlines implementation plans leveraging existing assets such as the `LLSD-Kotlin` repository.

## Why Per-Language LLSD Libraries Help

- **Consistency**: Ensures identical serialization/deserialization semantics across Kotlin, C++, C#, Rust, Swift, TypeScript, etc., reducing protocol drift.
- **Testing**: Enables cross-language contract tests where the same LLSD fixture is parsed/serialized by each library and compared byte-for-byte.
- **Performance Optimization**: Allows language-specific tuning (e.g., Kotlin coroutines, Rust zero-copy parsers) while preserving canonical behavior.
- **Developer Productivity**: Provides idiomatic APIs (data classes in Kotlin, structs in Rust, Codable in Swift) that integrate naturally with platform frameworks.
- **Legacy Interop**: Many Firestorm/Second Life APIs still emit LLSD XML/Binary; solid libraries ease migration to newer formats like JSON or protobuf.

## Existing Assets

- **LLSD-Kotlin**: Kotlin multiplatform implementation supporting XML/JSON/Notation serialization. Can serve as baseline for JVM/Android and reference spec.
- **OpenMetaverse LLSD (C#)**: Established library used by LibreMetaverse; review for modernization and alignment with canonical schema tests.
- **Firestorm LLSD (C++)**: Legacy implementation within viewer; extract into standalone module and add unit tests.
- **Third-Party Libraries**: Check for Rust or TypeScript community libraries to assess reuse potential.

## Implementation Plan per Language

| Language | Approach | Notes |
| --- | --- | --- |
| Kotlin | Adopt `LLSD-Kotlin`, convert to Kotlin Multiplatform module, ensure native targets for desktop/shared clients. | Add coroutine-friendly streaming parser, integrate with Compose data models. |
| C++ | Extract Firestorm LLSD code into standalone library (`llsd-cpp`), wrap with modern API, add unit/integration tests. | Provide C API for interoperability and Rust FFI. |
| C# | Fork/modernize OpenMetaverse LLSD into `LLSD.DotNet` with Span<T> optimizations, netstandard support. | Publish NuGet package. |
| Rust | Implement `llsd-rs` with Serde support, zero-copy parsing (borrowed data). Provide conversions to/from `serde_json::Value`. | Use QuickCheck/proptest for fuzz testing. |
| Swift | Build `LLSDKit` using Codable, supporting XML and binary encodings. | Provide bridging converters from Kotlin shared models. |
| TypeScript | Implement LLSD parser in Deno/Node for web tooling, optionally compile to WebAssembly via Rust library. | Use for web dashboards and automated tests. |

## Shared Test Suite

- Maintain canonical LLSD fixtures (XML, Binary, JSON Notation) in `tests/llsd-fixtures`.
- Provide language-agnostic test harness (e.g., Python script) that runs each library against fixtures and compares canonical JSON representation.
- Integrate into CI pipelines as part of cross-language contract checks.

## Repository Strategy

- Host per-language LLSD libraries in mono-repo under `shared/llsd/<language>` or as separate repos with synchronized releases.
- Publish packages to respective registries (Maven, NuGet, crates.io, SwiftPM, npm) with consistent versioning (e.g., `llsd-1.x`).
- Document API usage and migration guides in each repo plus central docs.

## Next Steps

1. Audit LLSD-Kotlin repository to confirm feature coverage (binary/XML/notation) and licensing compatibility.
2. Create design ADR for LLSD standardization, referencing this strategy.
3. Set up shared fixture repo and initial CI harness.
4. Schedule extraction/refactor of Firestorm C++ LLSD module and C# modernization.
5. Kick off Rust/Swift/TypeScript library prototypes following the plan above.

## Actionable Rollout Plan

| Workstream | Tasks | Owners (Proposed) | Deliverables | Target Timeline |
| --- | --- | --- | --- | --- |
| Governance | Draft LLSD standardization ADR; define versioning & API guidelines | Architecture WG | ADR-LLSD-001, contribution checklist | Week 1 |
| Kotlin (LLSD-Kotlin) | Fork repo, migrate to KMP, add coroutine streaming parser, publish Maven artifact | Android Platform team | `llsd-kotlin` 1.0.0, docs, CI pipeline | Weeks 1-3 |
| C++ (`llsd-cpp`) | Extract Firestorm LLSD code, modernize API, add unit tests, package with CMake | Desktop Viewer team | Standalone library, pkg scripts, docs | Weeks 2-5 |
| C# (`LLSD.DotNet`) | Port OpenMetaverse LLSD to netstandard2.1+, optimize spans, publish NuGet | Services team | NuGet package, integration tests | Weeks 2-4 |
| Rust (`llsd-rs`) | Implement Serde-based parser, fuzz tests, publish crates.io | Services/Infra team | `llsd-rs` crate, docs, CI | Weeks 3-6 |
| Swift (`LLSDKit`) | Create Codable-based library, integrate with SwiftPM, add tests | Apple client team | Swift package, DocC docs, CI | Weeks 4-6 |
| TypeScript (`llsd-ts`) | Build parser or wrap WebAssembly, provide npm package | Web Tooling team | npm package, typings, tests | Weeks 4-6 |
| Tooling & Scripts | Generate Python/Lua/Node helpers from canonical fixtures | Tooling guild | Utility modules, automation scripts | Weeks 5-7 |
| Fixtures & CI | Build shared fixture repo, create CLI runner invoking each library, integrate into CI nightly | QA Automation | `llsd-fixtures` repo, CI dashboards | Weeks 2-6 |

## Audit Checklist

1. **LLSD-Kotlin**
   - Review supported encodings (XML, Binary, Notation) and ensure parity with Linden Lab spec.
   - Verify Kotlin Multiplatform targets (JVM, Android, native) and update Gradle config accordingly.
   - Assess performance benchmarks vs. legacy Java implementation and document findings.
2. **Firestorm C++ LLSD**
   - Locate LLSD classes (`llsd.cpp`, `llsdserialize.cpp`, etc.) and map dependencies.
   - Identify divergent behavior (e.g., date parsing) and create issues for alignment.
   - Plan extraction path into new library with minimal viewer build disruption.
3. **OpenMetaverse LLSD (C#)**
   - Audit API surface, determine compatibility with .NET 8.
   - Measure serialization/deserialization speed; identify optimization targets (Span<T>, pooling).
   - Confirm licensing and contribution process for modernization.

## Shared Fixture & CI Plan

- **Fixture Repository Structure**
  - `fixtures/xml/*.llsd` – curated XML messages (agent update, inventory, teleport).
  - `fixtures/binary/*.llsd` – binary encoded equivalents.
  - `fixtures/notation/*.llsd` – LLSD notation inputs.
  - `expected/json/*.json` – canonical normalized JSON output for comparison.
- **Test Harness**
  - Provide CLI (`llsd-verify`) implemented in Python or Rust that executes each language library via subprocess or bindings, comparing outputs to expected JSON.
  - Integrate diff reporting and optional property-based fuzz inputs.
- **CI Integration**
  - Add nightly workflow that pulls latest versions of each LLSD library, runs fixture tests, reports to dashboard.
  - Gate releases of LLSD libraries on passing shared fixture tests.
  - Track coverage metrics to ensure new fixtures are added for emerging protocol fields.
