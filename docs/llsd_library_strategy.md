# LLSD Cross-Language Library Strategy

Linden Lab Structured Data (LLSD) remains central to Second Life protocol messaging. Standardizing LLSD encoders/decoders across languages provides consistent behavior and simplifies interoperability. This document evaluates the benefits of per-language LLSD libraries and outlines implementation plans leveraging existing assets such as the `LLSD-Kotlin` repository.

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
