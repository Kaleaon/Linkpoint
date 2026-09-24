# Using Lumiya-Redux while building Linkpoint Next

Lumiya-Redux is now a buildable, source-complete implementation that can be
used as an executable compatibility oracle for the active React/Rust viewer.
It is not the architecture to merge wholesale: its Android UI and Java runtime
do not replace Linkpoint's Rust core or React interface. Its verified protocol
implementation, fixtures, recovery checks, and observable behavior can,
however, materially accelerate Linkpoint Next.

The review behind this guide used Lumiya-Redux commit
`4bf5ca476c79eeb583f18e9960df2ea2b422b0f5`. Re-check the upstream revision
before relying on paths or conclusions below. At that revision,
`./gradlew :app:assembleDebug`, the protocol conformance suite, and source/APK
verification pass; all application classes compile from source, with zero
classes supplied from the original-bytecode fallback. The verifier reports
zero damaged classes and zero unresolved references, while separately tracking
reviewed and toolchain differences. That is strong recovery evidence, but not
a claim that every source method is byte-for-byte identical or that every live
grid feature has received a new end-to-end test.

## Ways Linkpoint can use it

Use Lumiya-Redux in four increasingly invasive ways:

| Mode | What Linkpoint gains | Requirement |
| --- | --- | --- |
| Executable oracle | Compare login, packet, state, lifecycle, and UI outcomes against a working Android viewer | Pin the commit and record device/grid conditions |
| Fixture producer | Convert observed messages and edge cases into synthetic Rust golden tests | Remove all resident/session data and document provenance |
| Tooling donor | Adapt conformance ledgers, template checks, queue tests, and differential-verification ideas | Keep the adapted tool deterministic and attribute it |
| Source donor | Port a small algorithm or data table instead of rediscovering it | Complete the source-review gate below and preserve notices |

The first three modes should begin immediately. Direct source reuse is also
possible, but it is a deliberate per-file decision rather than blanket
permission inferred from a repository-level license file. Before adapting
source, record the file and commit, identify its copyright/provenance (including
bundled third-party or recovered APK origins), confirm license compatibility
and notice obligations, and obtain maintainer approval. If that review is not
clear, use the executable behavior to write an independent Rust test and
implementation instead.

Do not add Lumiya-Redux as a production Java dependency. Running it as a test
oracle preserves a clean runtime boundary; porting selected protocol behavior
into the owning Rust crate avoids carrying Android contexts, thread models,
database objects, or renderer assumptions into Linkpoint Next.

## Ideas worth carrying forward

Lumiya-Redux's most useful contribution is its method of working:

1. **Preserve behavior before replacing it.** Characterize a seam with a
   sanitized fixture or state-machine test before changing its implementation.
2. **Use conformance ledgers.** Tie protocol fields to the canonical Second
   Life message template or viewer source, and record intentional differences
   instead of silently normalizing them.
3. **Deliver narrow vertical slices.** Change one boundary at a time and keep
   the application buildable. Do not combine protocol, renderer, and UI
   migrations in one change.
4. **Do not remove a fallback without runtime evidence.** A replacement needs
   fixture coverage and, when it involves a platform or network, a recorded
   integration result.
5. **Use differential tests.** Feed the same synthetic input to Lumiya-Redux
   and Rust, normalize nondeterministic values, and compare outputs or state.
6. **Make phase exits measurable.** A scaffold or compiling type is not feature
   parity. Acceptance should describe an observable outcome, its test, and its
   evidence.

These principles complement Linkpoint's architecture: Rust owns protocol and
authoritative viewer state, while React consumes typed projections through
`ViewerClient`.

## Source hierarchy

Use references in this order for protocol-facing work:

1. The open-source Second Life viewer, master message template, and published
   protocol documentation are normative.
2. Lumiya-Redux and Linkpoint's preserved Lumiya material provide evidence of
   mobile behavior and edge cases.
3. Existing Linkpoint implementations show current behavior but are not a
   protocol specification.

When the sources disagree, preserve the evidence in a test or task note and do
not guess. Reuse source only through the documented provenance/license gate;
otherwise independently implement the observed behavior. Do not commit
credentials, session identifiers, resident data, or raw packet captures.

## Establish the oracle baseline

Before deriving a Linkpoint slice, check out the pinned Lumiya-Redux revision
in a separate workspace and record these upstream results:

```bash
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
tools/protocol/run_conformance.sh
tools/verify/verify_against_apk.sh /path/to/Lumiya_3.4.2.apk
```

The APK is not a Linkpoint dependency and must not be committed here. The last
command is optional for contributors who lawfully possess the original APK;
published upstream verification may be cited otherwise. Record tool versions,
the Lumiya-Redux commit, and the resulting report in the parity task.

For protocol work, create a small adapter or test runner in Lumiya-Redux that
accepts synthetic hex/JSON input and emits normalized JSON. Run the equivalent
Rust fixture locally against that output. Commit only the synthetic input,
normalized expected result, provenance manifest, and Linkpoint test—not the
APK, personal traffic, or an unreviewed source dump. This produces durable
value from the recovered implementation even when no Java is ported.

## One-slice workflow

Use this sequence for each capability, beginning with login/circuit bring-up,
then event queue and capabilities, chat, inventory, and scene updates.

### 1. State one observable behavior

Write a small task statement, for example:

> When an inbound reliable packet is accepted, queue its sequence number for
> acknowledgement; when the ACK list exceeds the packet budget, split it
> without reordering entries.

Name the source revision and reference paths used to discover that behavior.
Keep the statement independent of Java, TypeScript, or Rust implementation
details.

### 2. Capture safe evidence

Prefer hand-built fixtures containing fictional UUIDs and content. If a real
session is needed to learn the shape, reduce it to the smallest synthetic case
before committing it. A useful fixture records:

- the source/specification and revision;
- the wire encoding and field order that matter;
- expected output or state transition;
- redactions or substitutions made; and
- an explicit size/time bound for parser inputs.

When practical, replay the fixture through the pinned Lumiya-Redux build and
store its normalized result as the expected output. Strip timestamps, random
identifiers, network endpoints, and ordering that the protocol does not
guarantee.

### 3. Put the behavior in the owning Rust crate

| Behavior | Owner |
| --- | --- |
| Login, capabilities, UDP framing, reliability | `crates/linkpoint-protocol` |
| Session lifecycle and command orchestration | `crates/linkpoint-core` |
| Asset requests, scheduling, decode boundaries | `crates/linkpoint-assets` |
| Normalized entities, transforms, scene deltas | `crates/linkpoint-scene` |
| Memory/disk cache policy | `crates/linkpoint-cache` |
| Native command/event adaptation only | `crates/linkpoint-tauri` |

Parsing must be bounded before it is connected to a live transport. Add a
golden or property test in the same slice; parser changes should also add a fuzz
target when the fuzz harness is introduced.

If directly adapting an algorithm, keep the original license header, add the
source repository/path/commit and a modification notice, update the project's
third-party notices as required, and make the Rust API fit this ownership table
rather than retaining the Java class boundary.

### 4. Expose the smallest stable contract

Only user-observable commands, events, and snapshots cross into
`packages/viewer-types`. Do not expose packet layouts, capability response
internals, Tauri APIs, or Rust storage models. Regenerate the TypeScript
contract from Rust and let its drift check prove both sides agree.

### 5. Project state rather than duplicating authority

Update `packages/viewer-store` only when the new event changes visible state.
The store should deterministically project events and clear session-owned data
on logout/disconnect. It must not recreate protocol rules already owned by
Rust.

### 6. Build the React behavior against a test client

Screens in `apps/linkpoint` should use `ViewerClient`, never native globals or
simulator packets. First test loading, success, empty, error, reconnect, and
logout behavior with `MockViewerClient`; then run the same contract through the
Tauri adapter. Renderer changes consume normalized scene data through
`packages/renderer` and stay separate from protocol decoding changes.

### 7. Record evidence before declaring parity

Every completed slice should link:

- focused Rust and TypeScript tests;
- the sanitized fixture and its provenance;
- the relevant `npm run check:*` result;
- a desktop integration result when native transport is involved; and
- a screenshot only when the visible React UI changed.

Use the terms **foundation**, **fixture-verified**, **integration-verified**,
and **device-verified** precisely. Only the last two demonstrate a live viewer
path; unit tests alone do not.

## Recommended next slices

The following order applies Lumiya-Redux's conformance-first approach to the
current Linkpoint gaps while respecting the dependency graph.

### Slice A — message-template ledger and golden codecs

1. Pin the canonical message-template revision used by tests.
2. Inventory each message as `supported`, `declared-only`, or `deprecated`.
3. Implement the minimum codecs for circuit activation and region handshake.
4. Add golden tests for block order, field order, variable-length bounds,
   zerocoding, and appended ACKs.

**Exit:** no implemented codec lacks provenance and a bounded golden fixture.

This is the best first use of the completed recovery: reuse or adapt the
Lumiya-Redux conformance tooling after the source-review gate, then compare its
message encoders/decoders with Rust over a synthetic corpus. The source-complete
Java build can expose cases that documentation alone does not describe.

### Slice B — native transport and circuit state machine

1. Implement the native HTTP and UDP adapters behind existing transport
   traits; keep platform details out of protocol crates.
2. Drive login redirect, circuit activation, handshake, timeout, logout, and
   reconnect through `linkpoint-core`.
3. Emit redacted diagnostic state through the typed event boundary.

**Exit:** a sanitized integration harness reaches `Live`, proves retry limits,
and tears down all session state deterministically.

### Slice C — capabilities and event queue lifecycle

1. Centralize capability names and response schemas.
2. Test monotonic event acknowledgements, duplicate delivery, cancellation,
   malformed payloads, region crossing, and old-region cleanup.
3. Expose domain events rather than raw LLSD to React.

**Exit:** event queue reconnect and region replacement pass a timed integration
test with no stale events reaching the store.

### Slice D — chat as the first UI vertical

1. Route local chat send/receive through Rust and the generated contract.
2. Project pending, delivered, failed, disconnected, and reconnected states.
3. Exercise the React screen entirely with the mock client before native
   integration.

**Exit:** fixture, contract, store, and component tests cover one complete
command-to-event path, followed by a redacted live-grid validation record.

### Slice E — scene feed before renderer expansion

1. Decode a minimal object-update corpus in Rust.
2. Normalize identity, transform, material, and entity kind in
   `linkpoint-scene`.
3. Apply deterministic add/update/remove deltas in `packages/renderer`.

**Exit:** replaying the same corpus produces the same scene snapshot, and the
renderer never receives simulator packet structures.

## Pull-request checklist

- [ ] The PR names one capability and one architecture seam.
- [ ] Normative and behavioral references include repository paths and commits.
- [ ] Directly adapted code passed provenance/license review and retains all
      required attribution and modification notices.
- [ ] Fixtures are synthetic/sanitized and have explicit input bounds.
- [ ] Rust remains authoritative; React uses only `ViewerClient` contracts.
- [ ] Protocol decoding and renderer/UI work are separate unless the change is
      a deliberately small end-to-end slice.
- [ ] Focused tests and the appropriate full workspace checks pass.
- [ ] The progress page uses an evidence level rather than claiming parity from
      scaffolding.
- [ ] A rollback path or retained fallback is identified for native changes.
