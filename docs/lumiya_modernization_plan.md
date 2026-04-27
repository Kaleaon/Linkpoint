# Lumiya Modernization RFC

- **Status**: Draft
- **Authors**: Linkpoint maintainers
- **Last updated**: 2026-04-26
- **Target area**: Protocol/runtime extraction from decompiled Lumiya Java into maintainable Kotlin-first modules

## 1) Context and Problem Statement

Linkpoint currently blends actively executed runtime paths with decompiled/reference code recovered from Lumiya artifacts. This has accelerated parity work, but it now creates ambiguity in ownership, testing confidence, and modernization velocity.

This RFC defines a six-phase execution model that:

1. Makes runtime-vs-reference boundaries explicit.
2. Expands protocol compatibility confidence from spot checks to matrix coverage.
3. Introduces interface-first seams before behavior replacement.
4. Replaces high-risk behaviors incrementally with measurable safeguards.
5. Enforces performance budgets in CI.
6. Ends with a decompiled tree that is reference-only and never runtime-coupled.

## 2) Goals

- Maintain protocol/render behavior parity where required while migrating implementation ownership to Kotlin modules.
- Reduce blast radius by introducing strict interfaces before internals are replaced.
- Increase confidence through repeatable golden fixtures and performance regression gates.
- Establish provenance boundaries so decompiled code is auditable but non-executable.

## 3) Non-Goals

- A full rewrite of all graphics/protocol systems in one cycle.
- Immediate deletion of decompiled assets before parity evidence exists.
- Changing externally observable wire protocol behavior during extraction phases.

## 4) Workstream Scope

Primary workstreams:

- Message parser and pack/unpack behavior.
- LLSD map/array/streaming decode/encode safety.
- Renderer pass orchestration and draw-list update logic.
- GL resource lifecycle ownership and teardown discipline.
- Avatar pipeline ownership boundaries.

## 5) Six-Phase Plan

### Phase 1 — Inventory & ownership

Deliverables:

- Runtime ownership inventory across parser, renderer, GL resource management, and avatar pipeline.
- Classification tags for each code path:
  - `ACTIVE_RUNTIME`
  - `REFERENCE_ONLY_DECOMPILED`
  - `MIXED/TRANSITIONAL`
- Ownership map naming module owners + review gates.

Exit criteria:

- Every touched package has explicit runtime/reference classification.
- No unknown ownership hotspots remain in critical paths.

### Phase 2 — Golden compatibility harness

Deliverables:

- Expansion of parity coverage beyond `WireFormatParityTest` and `LumiyaReferencePackers`.
- Message matrix fixture suite covering:
  - object update slices
  - teleport/session slices
  - inventory slices
  - LLSD payload fixtures (valid + malformed + boundary)
- Golden comparison harness with deterministic mismatch reports.

Exit criteria:

- Matrix suite runs in CI and reports stable pass/fail diffs.
- Golden fixture update process documented and review-protected.

### Phase 3 — Interface-first extraction

Deliverables:

- Stable Kotlin interfaces for:
  - parser pack/unpack
  - render pass graph
  - GL resource lifecycle
- Adapters that allow legacy/decompiled implementations to sit behind interfaces without direct app-layer coupling.
- Contract tests for each interface.

Exit criteria:

- Runtime depends on interfaces, not concrete decompiled classes.
- Decompiled Java is retained only as offline oracle implementations.

### Phase 4 — Incremental replacements

Priority replacement targets (highest risk first):

1. message-ID decode behavior
2. variable field handling
3. LLSD map/array streaming safety
4. draw-list update logic

Deliverables:

- Kotlin replacements landed one behavior slice at a time.
- Each replacement gated by golden parity tests + contract tests.
- Rollback switch or compatibility fallback for each slice until confidence threshold is met.

Exit criteria:

- No critical behavior slice in this priority list relies on runtime decompiled execution.
- Regression windows are bounded by feature flags and test evidence.

### Phase 5 — Performance gates

Deliverables:

- CI-enforced budgets for:
  - frame-time p95
  - allocations/frame
  - packet parse throughput
- Baseline capture job + trend report artifact.
- Regression policy: fail CI when exceeding approved thresholds.

Exit criteria:

- Budgets are versioned and visible.
- No unapproved performance regression can merge.

### Phase 6 — Deprecation endpoint

Deliverables:

- Decompiled tree marked read-only reference in docs and repository policy.
- Runtime coupling removed (build/runtime imports blocked).
- Provenance boundaries documented:
  - origin of decompiled artifacts
  - allowed usage (reference/oracle only)
  - prohibited usage (runtime coupling)

Exit criteria:

- Build fails if runtime references decompiled tree.
- Documentation clearly states legal/technical provenance boundary.

## 6) Risks and Mitigations

- **Risk**: hidden behavior dependencies in decompiled paths.
  - **Mitigation**: phase-gated extraction with golden harness and rollback switches.
- **Risk**: fixture drift causes false confidence.
  - **Mitigation**: controlled fixture update workflow and mandatory reviewers.
- **Risk**: performance regressions during replacement.
  - **Mitigation**: hard CI budgets with explicit waivers only.

## 7) Decision Log and Governance

- Any interface contract change requires:
  - contract test update
  - migration note
  - ownership approval
- Any golden fixture update requires:
  - diff rationale
  - affected matrix slice labels
  - reviewer sign-off

## 8) Success Metrics

- 100% critical-path ownership classification (Phase 1).
- Message matrix coverage for object/teleport/inventory + LLSD boundary fixtures (Phase 2).
- Runtime interfaces adopted across parser/render/GL lifecycle (Phase 3).
- High-risk behavior replacements fully Kotlin-owned (Phase 4).
- CI budget gates active and enforced (Phase 5).
- Zero runtime imports from decompiled tree (Phase 6).
