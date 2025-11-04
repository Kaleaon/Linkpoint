## Lumiya Kotlin Rebuild Roadmap

### Context
- Original Lumiya Java client (circa 2014) diverged from current Second Life viewer.
- Recent pulls of `SecondLife/` and `Firestorm/` trees contain modern graphics (PBR), rendering, and protocol work we need to leverage.
- Kotlin port in `app/` and `Linkpoint/` came from partial decompiles and machine conversion; large portions are syntactically invalid.
- APK artifacts (`Lumiya_3.4.2.apk`, plugins) can serve as authoritative behaviour references when Java/C++ differ.

### Guiding Principles
1. **Authoritative source order**: New SL/Firestorm → Legacy Lumiya Java/bytecode → Damaged Kotlin.
2. **Incremental replacement**: keep the project buildable by gating unfinished Kotlin behind source-set excludes.
3. **Document continuously**: capture decisions, mappings, and deltas between old/new implementations.
4. **Prefer composition**: introduce clean Kotlin modules rather than patching decompiled fragments.

### Phase 0 – Workspace Stabilization
- [ ] Create `reference/java-legacy` module from the decompiled APK Java sources (compile to ensure fidelity).
- [ ] Lock down current Kotlin modules by excluding broken packages; build should fail only on missing implementations, not syntax.
- [ ] Catalogue critical subsystems (auth, inventory, rendering, chat, voice) and map them to C++/C#/Java source locations.

### Phase 1 – Architecture Blueprint
- [ ] Draft high-level architecture doc comparing SL viewer subsystems to Lumiya components.
- [ ] Identify reusable native components (Filament/PBR, protocol stacks) and plan integration boundaries.
- [ ] Decide module layout (e.g., `core-runtime`, `protocol`, `render`, `ui-classic`, `ui-compose`).

### Phase 2 – Core Runtime Migration
- [ ] Port `GlobalOptions`, logging, configuration, and cache management from validated Java/SL sources to idiomatic Kotlin.
- [ ] Rebuild connection/auth pipeline referencing new SL/Firestorm netcode.
- [ ] Replace or stub GreenDAO/legacy persistence with modern storage (Room/SQLDelight) as required.

### Phase 3 – Rendering Modernization
- [ ] Align Filament integration with current SL PBR pipeline (check `Firestorm/` PBR code).
- [ ] Re-implement resource caches (textures, meshes) and shader management in Kotlin, bridging to Filament/GL.
- [ ] Document differences between legacy renderer and new implementation.

### Phase 4 – Protocol & Feature Layers
- [ ] Reintroduce inventory, chat, voice, and sync systems using updated protocol definitions.
- [ ] Validate behaviour against APK and SL/Firestorm references.
- [ ] Replace temporary stubs with production-ready Kotlin.

### Phase 5 – UI Revival
- [ ] Determine Compose vs classic view strategy for each screen.
- [ ] Migrate high-value flows (login, inventory, chat, world view) with modern UI patterns, hooking into rebuilt runtime.
- [ ] Ensure accessibility/theme parity with modern Android guidelines.

### Phase 6 – QA & Tooling
- [ ] Establish unit/integration test suites against protocol and rendering layers.
- [ ] Automate APK builds, lint, and static analysis.
- [ ] Prepare migration guides and release notes.

### Documentation & Tracking
- Maintain this roadmap; update checkboxes as milestones complete.
- For each subsystem, create Markdown notes (e.g., `docs/runtime/`, `docs/render/`) summarizing reference sources, key design decisions, and Kotlin implementation status.
- Record any divergence from upstream SL/Firestorm behaviour with rationale.

### Immediate Next Actions
1. Stand up the Java reference module and ensure it compiles.
2. Freeze broken Kotlin by adjusting source-set excludes so the Gradle build reflects actual TODOs.
3. Begin Kotlin translation of `GlobalOptions` and configuration services using the reference Java + upstream improvements.
