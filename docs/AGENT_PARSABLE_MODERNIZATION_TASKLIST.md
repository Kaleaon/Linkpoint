# Linkpoint Modernization Tasklist (Agent-Parseable)

> Purpose: Single-source task document consolidating protocol, LLSD, connectivity, rendering, and conformance work into a machine-parseable checklist for autonomous agents.
>
> Scope: `Linkpoint` repository modernization with explicit upstream parity references.
>
> Status model: `todo | in_progress | blocked | done`.

## Parse Contract

Agents should parse each task block using the following fields:

- `task_id`: stable identifier
- `title`: short work item name
- `priority`: `P0 | P1 | P2`
- `status`: `todo | in_progress | blocked | done`
- `labels`: array of tags
- `depends_on`: array of `task_id`
- `owner`: optional text
- `repo_paths`: local files/folders to modify
- `external_refs`: upstream references (GitHub repos/files)
- `deliverables`: required artifacts
- `acceptance_criteria`: verifiable completion checks

## Canonical Upstream References

- Second Life master template (source of truth):
  - https://github.com/secondlife/master-message-template
  - https://raw.githubusercontent.com/secondlife/master-message-template/master/message_template.msg
  - https://raw.githubusercontent.com/secondlife/master-message-template/master/message_template.msg.sha1
- Second Life viewer behavior reference:
  - https://github.com/secondlife/viewer
  - https://github.com/secondlife/viewer/blob/main/scripts/messages/message_template.msg
- Firestorm viewer parity reference:
  - https://github.com/FirestormViewer/phoenix-firestorm
  - https://github.com/FirestormViewer/phoenix-firestorm/blob/master/scripts/messages/message_template.msg
- Lumiya-Redux protocol modernization reference:
  - https://github.com/Kaleaon/Lumiya-Redux
  - https://github.com/Kaleaon/Lumiya-Redux/tree/main/tools/protocol
  - https://github.com/Kaleaon/Lumiya-Redux/tree/main/recovered/reference

## Label Taxonomy

- `PROTO-TEMPLATE`
- `PROTO-UDP`
- `PROTO-CAPS`
- `PROTO-LLSD-XML`
- `PROTO-LLSD-BINARY`
- `PROTO-LLSD-NOTATION`
- `PROTO-CONFORMANCE`
- `RUNTIME-CONNECTIVITY`
- `RUNTIME-WORLD`
- `RUNTIME-RENDER`
- `ANDROID`
- `TESTING`
- `OBSERVABILITY`
- `DOCUMENTATION`

---

## Task Blocks

### task_id: LP-P0-001
- title: Pin SL master message template SHA and gate CI on drift
- priority: P0
- status: done
- labels: [PROTO-TEMPLATE, PROTO-CONFORMANCE]
- depends_on: []
- owner: claude/plan-modernization-fixes-8NRUg
- repo_paths:
  - Linkpoint/tools/protocol/sync_master_template.sh
  - Linkpoint/tools/protocol/message_template.msg.sha1
  - Linkpoint/tools/protocol/message_template_mismatches.txt
  - .github/workflows/protocol-conformance.yml
  - Linkpoint/src/test/resources/protocol/message_template.msg
- external_refs:
  - https://github.com/secondlife/master-message-template
  - https://raw.githubusercontent.com/secondlife/master-message-template/master/message_template.msg
  - https://raw.githubusercontent.com/secondlife/master-message-template/master/message_template.msg.sha1
- deliverables:
  - script to sync template + SHA
  - CI job that fails on unapproved mismatch drift
  - explicit mismatch allowlist file
- acceptance_criteria:
  - CI fails when local template diverges without mismatch update
  - CI passes when sync + allowlist updates are intentional and committed together

### task_id: LP-P0-002
- title: Unified protocol conformance runner
- priority: P0
- status: in_progress
- labels: [PROTO-CONFORMANCE]
- depends_on: [LP-P0-001]
- owner: claude/plan-modernization-fixes-8NRUg
- repo_paths:
  - Linkpoint/tools/protocol/run_conformance.sh
  - Linkpoint/tools/protocol/verify_message_template_conformance.py
  - Linkpoint/tools/protocol/verify_protocol_docs.py
  - Linkpoint/tools/protocol/verify_llsd_conformance.py
  - Linkpoint/tools/protocol/verify_model_mapping_conformance.py
  - .github/workflows/protocol-conformance.yml
- external_refs:
  - https://github.com/Kaleaon/Lumiya-Redux/tree/main/tools/protocol
- deliverables:
  - one command to run all protocol conformance checks
  - CI artifact publication under build/reports/protocol/
- acceptance_criteria:
  - local run and CI run produce deterministic pass/fail output
  - all conformance outputs are archived as workflow artifacts

### task_id: LP-P0-003
- title: Close critical declared-only message gaps
- priority: P0
- status: todo
- labels: [PROTO-UDP]
- depends_on: [LP-P0-001]
- owner: unassigned
- repo_paths:
  - src/main/java/com/linkpoint/protocol/messages/MessageTemplateCatalog.kt
  - src/main/java/com/linkpoint/protocol/messages/
  - src/test/
- external_refs:
  - https://github.com/secondlife/master-message-template
  - https://github.com/secondlife/viewer
  - https://github.com/FirestormViewer/phoenix-firestorm
- deliverables:
  - prioritized critical message queue (inventory, IM/chat/group, teleport/session)
  - parser + writer + handler + integration test for each critical message
- acceptance_criteria:
  - critical declared-only count reaches zero for P0 families
  - no classification regressions in conformance tests

### task_id: LP-P0-004
- title: LLSD schema contract registry for capabilities and endpoints
- priority: P0
- status: todo
- labels: [PROTO-CAPS, PROTO-LLSD-XML, PROTO-LLSD-BINARY]
- depends_on: [LP-P0-002]
- owner: unassigned
- repo_paths:
  - src/main/java/com/linkpoint/capabilities/
  - src/main/java/com/linkpoint/protocol/llsd/
  - src/test/resources/protocol/llsd/
  - docs/capabilities/
- external_refs:
  - https://wiki.secondlife.com/wiki/LLSD
  - https://github.com/secondlife/viewer
  - https://github.com/Kaleaon/Lumiya-Redux
- deliverables:
  - schema table mapping endpoint => request/response LLSD types + content-type
  - runtime validation hooks with labeled error paths
  - fixture suite for malformed/empty/wrong-type/oversized LLSD
- acceptance_criteria:
  - seed capability path validated as LLSD array request + LLSD map response
  - LLSD parse failures include schema ID and endpoint label

### task_id: LP-P0-005
- title: Receive-idle watchdog and reconnect ladder
- priority: P0
- status: todo
- labels: [RUNTIME-CONNECTIVITY, PROTO-UDP]
- depends_on: [LP-P0-003]
- owner: unassigned
- repo_paths:
  - src/main/java/com/linkpoint/protocol/circuit/
  - src/main/java/com/linkpoint/network/
  - src/test/
  - docs/lumiya-parity/
- external_refs:
  - https://github.com/Kaleaon/Lumiya-Redux
  - https://github.com/secondlife/viewer
- deliverables:
  - receive-idle watchdog
  - ping reliability enforcement
  - 3-strike disconnect + reconnect ladder
  - reconnection telemetry events
- acceptance_criteria:
  - idle silence scenarios recover automatically in harness tests
  - retry/ACK queues drain back to baseline after reconnect

### task_id: LP-P0-006
- title: Android background survival policy for connectivity
- priority: P0
- status: todo
- labels: [RUNTIME-CONNECTIVITY, ANDROID]
- depends_on: [LP-P0-005]
- owner: unassigned
- repo_paths:
  - src/main/java/com/linkpoint/
  - src/main/AndroidManifest.xml
  - docs/
- external_refs:
  - https://developer.android.com/develop/background-work/services/fgs
  - https://developer.android.com/reference/android/os/PowerManager.WakeLock
- deliverables:
  - foreground service and wake-lock policy implementation
  - lifecycle-safe connectivity behavior across pause/stop/resume
- acceptance_criteria:
  - no disconnect caused solely by app lifecycle transitions
  - Android 14+ foreground service type requirements satisfied

### task_id: LP-P0-007
- title: Deterministic packet->scene->render replay harness
- priority: P0
- status: todo
- labels: [RUNTIME-WORLD, RUNTIME-RENDER, TESTING]
- depends_on: [LP-P0-003, LP-P0-005]
- owner: unassigned
- repo_paths:
  - src/test/
  - src/main/java/com/linkpoint/render/
  - src/main/java/com/linkpoint/world/
  - docs/reports/
- external_refs:
  - https://github.com/secondlife/viewer
  - https://github.com/FirestormViewer/phoenix-firestorm
- deliverables:
  - replay runner with stage counters:
    - packet received
    - parsed
    - manager applied
    - scene inserted
    - renderer submitted
- acceptance_criteria:
  - region handshake completes with valid region metadata
  - object count > 0 and avatar count > 0 in replay tests
  - swapchain/frame submission success asserted

### task_id: LP-P1-008
- title: Expand LLSD corpus and fuzz-style resilience tests
- priority: P1
- status: todo
- labels: [PROTO-LLSD-XML, PROTO-LLSD-BINARY, PROTO-LLSD-NOTATION, TESTING]
- depends_on: [LP-P0-004]
- owner: unassigned
- repo_paths:
  - src/test/resources/protocol/llsd/
  - src/test/
- external_refs:
  - https://wiki.secondlife.com/wiki/LLSD
  - https://github.com/Kaleaon/Lumiya-Redux
- deliverables:
  - scalar and nested LLSD fixture corpus
  - malformed/truncated/overflow inputs
  - round-trip semantic validation for valid fixtures
- acceptance_criteria:
  - parser handles malformed inputs safely without crashes
  - valid fixtures pass semantic round-trip checks

### task_id: LP-P1-009
- title: Capability telemetry with LLSD schema labels
- priority: P1
- status: todo
- labels: [PROTO-CAPS, OBSERVABILITY]
- depends_on: [LP-P0-004]
- owner: unassigned
- repo_paths:
  - src/main/java/com/linkpoint/capabilities/
  - src/main/java/com/linkpoint/logging/
  - docs/
- external_refs:
  - https://github.com/secondlife/viewer
- deliverables:
  - endpoint + schema ID tagged logs/metrics
  - grouped LLSD failure analytics by schema and content type
- acceptance_criteria:
  - operational logs identify failing capability schema in one lookup
  - metrics can break down success/failure per endpoint/schema

### task_id: LP-P1-010
- title: Publish protocol truth map
- priority: P1
- status: todo
- labels: [PROTO-CONFORMANCE, DOCUMENTATION]
- depends_on: [LP-P0-001, LP-P0-002, LP-P0-004]
- owner: unassigned
- repo_paths:
  - docs/
  - docs/reports/
- external_refs:
  - https://github.com/secondlife/master-message-template
  - https://github.com/secondlife/viewer
  - https://github.com/FirestormViewer/phoenix-firestorm
  - https://github.com/Kaleaon/Lumiya-Redux
- deliverables:
  - protocol surface map linking source -> implementation -> tests -> deviations
- acceptance_criteria:
  - each protocol/capability/LLSD surface has explicit source-of-truth and owner

### task_id: LP-P2-011
- title: Complete medium-utility message families
- priority: P2
- status: todo
- labels: [PROTO-UDP]
- depends_on: [LP-P0-003]
- owner: unassigned
- repo_paths:
  - src/main/java/com/linkpoint/protocol/messages/
  - src/test/
- external_refs:
  - https://github.com/secondlife/master-message-template
  - https://github.com/FirestormViewer/phoenix-firestorm
- deliverables:
  - implemented parser/writer/handler/tests for profile/search/map/economy utility families
- acceptance_criteria:
  - medium-priority declared-only backlog reduced to agreed threshold
  - no regressions in P0 critical flows

### task_id: LP-P2-012
- title: Deprecated and historical message handling policy
- priority: P2
- status: todo
- labels: [PROTO-TEMPLATE, DOCUMENTATION]
- depends_on: [LP-P0-001, LP-P1-010]
- owner: unassigned
- repo_paths:
  - src/main/java/com/linkpoint/protocol/messages/MessageTemplateCatalog.kt
  - docs/
  - src/test/
- external_refs:
  - https://github.com/secondlife/master-message-template
  - https://github.com/secondlife/viewer
- deliverables:
  - policy categories: retained-for-compat | ignored | removed
  - conformance checks enforcing policy classification
- acceptance_criteria:
  - deprecated support status is explicit in catalog + reports
  - no ambiguous deprecated handling in tests

---

## Milestones

- Milestone 1 (Core stabilization): LP-P0-001 .. LP-P0-007
- Milestone 2 (Conformance + observability): LP-P1-008 .. LP-P1-010
- Milestone 3 (Long-tail parity): LP-P2-011 .. LP-P2-012

## Agent Execution Notes

- Always run `LP-P0-001` and `LP-P0-002` before bulk protocol implementation tasks.
- Prefer upstream SL master template for protocol contract disputes.
- Use Firestorm as behavior tie-breaker when SL references are ambiguous.
- Use Lumiya-Redux tools as workflow blueprint for conformance automation.

