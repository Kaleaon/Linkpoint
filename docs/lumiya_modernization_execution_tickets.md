# Lumiya Modernization Execution Tickets

Status model: `todo | in_progress | blocked | done`
Priority model: `P0 | P1 | P2`

---

## Phase 1 — Inventory & ownership

### ticket_id: LMP-P1-001
- title: Classify runtime vs decompiled reference paths across parser/renderer/GL/avatar
- phase: 1
- priority: P0
- status: todo
- labels: [RUNTIME-WORLD, RUNTIME-RENDER, PROTO-UDP, DOCUMENTATION]
- depends_on: []
- repo_paths:
  - src/main/java/
  - secondlife_decompiled/
  - lumiya_decompiled_source/
  - docs/
- deliverables:
  - package-level inventory table with `ACTIVE_RUNTIME`, `REFERENCE_ONLY_DECOMPILED`, `MIXED/TRANSITIONAL`
  - owner assignment per critical module
- acceptance_criteria:
  - all parser/renderer/GL/avatar packages are classified
  - owner field present for each critical path

### ticket_id: LMP-P1-002
- title: Add ownership review gates for critical runtime modules
- phase: 1
- priority: P1
- status: todo
- labels: [DOCUMENTATION, TESTING]
- depends_on: [LMP-P1-001]
- repo_paths:
  - .github/
  - docs/
- deliverables:
  - CODEOWNERS or equivalent review policy for critical module directories
  - documented escalation path for unowned modules
- acceptance_criteria:
  - protected review boundaries exist for all P0 modules

---

## Phase 2 — Golden compatibility harness

### ticket_id: LMP-P2-001
- title: Expand parity harness into message matrix for object/teleport/inventory slices
- phase: 2
- priority: P0
- status: todo
- labels: [PROTO-CONFORMANCE, TESTING]
- depends_on: [LMP-P1-001]
- repo_paths:
  - src/test/
  - src/test/resources/
  - docs/lumiya-parity/
- deliverables:
  - matrix test runner extending `WireFormatParityTest` and `LumiyaReferencePackers`
  - fixture index with per-slice labels
- acceptance_criteria:
  - matrix covers object, teleport/session, and inventory slices
  - failures report exact slice + fixture ID

### ticket_id: LMP-P2-002
- title: Add LLSD payload fixture corpus including malformed and boundary cases
- phase: 2
- priority: P0
- status: todo
- labels: [PROTO-LLSD-XML, PROTO-LLSD-BINARY, TESTING]
- depends_on: [LMP-P2-001]
- repo_paths:
  - src/test/resources/protocol/llsd/
  - src/test/
- deliverables:
  - valid/invalid/boundary LLSD fixtures for maps/arrays/streaming payloads
  - parser error snapshot assertions
- acceptance_criteria:
  - malformed payload classes each have at least one deterministic fixture
  - CI captures fixture mismatch reports as artifacts

---

## Phase 3 — Interface-first extraction

### ticket_id: LMP-P3-001
- title: Define ParserCodec Kotlin interface (pack/unpack contract)
- phase: 3
- priority: P0
- status: todo
- labels: [PROTO-UDP, DOCUMENTATION, TESTING]
- depends_on: [LMP-P2-001]
- repo_paths:
  - src/main/java/
  - src/test/
- deliverables:
  - stable interface + contract tests for pack/unpack semantics
  - adapter shim for existing implementation
- acceptance_criteria:
  - app/runtime call sites use interface abstraction
  - contract tests run against at least two implementations

### ticket_id: LMP-P3-002
- title: Define RenderPassGraph and GLResourceLifecycle Kotlin interfaces
- phase: 3
- priority: P0
- status: todo
- labels: [RUNTIME-RENDER, TESTING]
- depends_on: [LMP-P2-001]
- repo_paths:
  - src/main/java/com/linkpoint/render/
  - src/test/
- deliverables:
  - render pass graph interface
  - GL resource create/reuse/dispose lifecycle interface
  - contract tests for frame-to-frame stability and teardown correctness
- acceptance_criteria:
  - runtime render pipeline depends only on interfaces
  - teardown path validated in automated tests

### ticket_id: LMP-P3-003
- title: Enforce decompiled Java as offline oracle only
- phase: 3
- priority: P1
- status: todo
- labels: [DOCUMENTATION, PROTO-CONFORMANCE]
- depends_on: [LMP-P3-001, LMP-P3-002]
- repo_paths:
  - docs/
  - build scripts
- deliverables:
  - policy doc + build check preventing direct runtime coupling
- acceptance_criteria:
  - runtime source set has no direct dependency on decompiled tree

---

## Phase 4 — Incremental replacements

### ticket_id: LMP-P4-001
- title: Replace message-ID decode behavior with Kotlin implementation
- phase: 4
- priority: P0
- status: todo
- labels: [PROTO-UDP, TESTING]
- depends_on: [LMP-P3-001]
- repo_paths:
  - src/main/java/com/linkpoint/protocol/
  - src/test/
- deliverables:
  - Kotlin decoder + parity tests + fallback flag
- acceptance_criteria:
  - parity matrix unchanged on message-ID slice

### ticket_id: LMP-P4-002
- title: Replace variable field parsing/writing behavior
- phase: 4
- priority: P0
- status: todo
- labels: [PROTO-UDP, TESTING]
- depends_on: [LMP-P4-001]
- repo_paths:
  - src/main/java/com/linkpoint/protocol/
  - src/test/
- deliverables:
  - Kotlin variable-field codec
  - boundary tests for length-prefix and truncation handling
- acceptance_criteria:
  - variable-field fuzz corpus passes without crashes

### ticket_id: LMP-P4-003
- title: Replace LLSD map/array streaming safety logic
- phase: 4
- priority: P0
- status: todo
- labels: [PROTO-LLSD-XML, PROTO-LLSD-BINARY, TESTING]
- depends_on: [LMP-P2-002, LMP-P3-001]
- repo_paths:
  - src/main/java/com/linkpoint/protocol/llsd/
  - src/test/
- deliverables:
  - Kotlin streaming parser/writer safety guards
  - malformed input resilience tests
- acceptance_criteria:
  - no OOM/unbounded read behavior on adversarial fixtures

### ticket_id: LMP-P4-004
- title: Replace draw-list update logic in render loop
- phase: 4
- priority: P0
- status: todo
- labels: [RUNTIME-RENDER, TESTING]
- depends_on: [LMP-P3-002]
- repo_paths:
  - src/main/java/com/linkpoint/render/
  - src/test/
- deliverables:
  - Kotlin draw-list updater with deterministic ordering rules
  - replay tests proving parity
- acceptance_criteria:
  - frame replay outputs remain deterministic under same input trace

---

## Phase 5 — Performance gates

### ticket_id: LMP-P5-001
- title: Add CI benchmark for frame-time p95 and allocations/frame
- phase: 5
- priority: P0
- status: todo
- labels: [RUNTIME-RENDER, OBSERVABILITY, TESTING]
- depends_on: [LMP-P4-004]
- repo_paths:
  - .github/workflows/
  - src/test/
  - docs/reports/
- deliverables:
  - benchmark job publishing frame-time and allocation metrics
  - threshold config checked into repo
- acceptance_criteria:
  - CI fails when p95 frame-time or allocations/frame exceed budget

### ticket_id: LMP-P5-002
- title: Add packet parse throughput budget and regression gate
- phase: 5
- priority: P0
- status: todo
- labels: [PROTO-UDP, OBSERVABILITY, TESTING]
- depends_on: [LMP-P4-002, LMP-P4-003]
- repo_paths:
  - src/test/
  - .github/workflows/
  - docs/reports/
- deliverables:
  - parse throughput benchmark with repeatable dataset
  - budget regression gate with waiver mechanism
- acceptance_criteria:
  - merge blocked on unapproved throughput regression

---

## Phase 6 — Deprecation endpoint

### ticket_id: LMP-P6-001
- title: Mark decompiled tree read-only and enforce provenance boundaries
- phase: 6
- priority: P0
- status: todo
- labels: [DOCUMENTATION, PROTO-CONFORMANCE]
- depends_on: [LMP-P5-001, LMP-P5-002]
- repo_paths:
  - docs/
  - secondlife_decompiled/
  - lumiya_decompiled_source/
  - build scripts
- deliverables:
  - provenance document defining allowed/prohibited usage
  - build-time checks blocking runtime imports from decompiled tree
- acceptance_criteria:
  - runtime coupling to decompiled modules is impossible by default
  - policy is discoverable from top-level docs

### ticket_id: LMP-P6-002
- title: Final runtime decoupling audit and sign-off
- phase: 6
- priority: P1
- status: todo
- labels: [DOCUMENTATION, TESTING]
- depends_on: [LMP-P6-001]
- repo_paths:
  - docs/
  - src/main/java/
- deliverables:
  - signed audit report of remaining references
  - closure checklist for modernization endpoint
- acceptance_criteria:
  - zero runtime references to decompiled tree in source and build graph
