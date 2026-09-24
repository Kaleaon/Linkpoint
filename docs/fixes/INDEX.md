# Linkpoint — Remaining Broken-Code Audit & Fix Roadmap

**Audit date:** 2026-09-17
**Branch:** `claude/fix-broken-code-audit-AkDJR`
**Scope:** Full repository — `Linkpoint/src/**` plus the outer repo root (build,
CI, tooling, native code).
**Method:** Five parallel static-analysis passes (protocol, render, assets,
UI/voice/XR, build/tests/native) cross-verified against source on the branch.
Findings from the earlier `docs/BROKEN_CODE_AUDIT_2026-04-24.md` heuristic sweep
have been re-verified against current source; items already resolved by merged
PRs #559–#575 (protocol UDP fixes, renderer ctx init, rendering/UDP errors,
stub-screen fixes) are marked `[RESOLVED]` and dropped from the active list.

This directory supersedes the ad-hoc earlier notes
([`Broken_Code_Analysis_and_Fixes.md`](../Broken_Code_Analysis_and_Fixes.md),
[`FIXES_AND_STATUS.md`](../FIXES_AND_STATUS.md),
[`LOG_ANALYSIS_FIX_PLAN.md`](../LOG_ANALYSIS_FIX_PLAN.md),
[`comprehensive_fixes_complete.md`](../archive/project-reports/comprehensive_fixes_complete.md),
[`linkpoint_issues_analysis.md`](../reports/analysis/linkpoint_issues_analysis.md),
[`linkpoint_null_safety_analysis.md`](../reports/analysis/linkpoint_null_safety_analysis.md),
and [`BROKEN_CODE_AUDIT_2026-04-24.md`](../BROKEN_CODE_AUDIT_2026-04-24.md))
with a single, freshly verified picture of what remains broken or incomplete.

## How this audit is organised

Each sector has its own fix document. Every document uses the same row format so
the findings can be mechanically merged back into `docs/MASTER_TRACKING.md`:

| Column | Meaning |
|---|---|
| **ID** | Stable identifier (e.g. `PROTO-001`). Stable across re-runs. |
| **File / Line(s)** | Source reference verified against the working tree on this branch. |
| **Category** | stub / placeholder-return / missing-parser / broken-logic / race-condition / null-safety / TODO / missing-native-link / etc. |
| **What is broken** | One-paragraph explanation. |
| **Fix plan** | Step-by-step fix with references to upstream SL viewer (`github.com/secondlife/viewer`), Lumiya Redux (`github.com/Kaleaon/Lumiya-Redux`), and the LLSD library (`github.com/Kaleaon/LLSD`). |
| **Acceptance test** | Gradle / test command that must pass after the fix. |

The raw machine-readable merged list lives in [`findings.json`](findings.json).

## Sector documents

| # | Document | Findings | Summary |
|---|---|---:|---|
| 01 | [`01_Protocol_and_Networking.md`](01_Protocol_and_Networking.md) | 20 | 279 message-template parity gaps, only 23 parsers registered vs 568 IDs, LLSD silent-failures, IM session sync race, capability event handlers, terrain packet truncation, agent identity lifecycle. |
| 02 | [`02_Rendering_and_Graphics.md`](02_Rendering_and_Graphics.md) | 19 | XR session stubs, HUD texture placeholders, prim geometry limited to boxes, hard-coded avatar skin tone, water mesh unfinished, shader compile errors silently swallowed, Basis transcoder stub, OpenJPEG placeholder, audio-native JNI stubs. |
| 03 | [`03_Assets_Inventory_Avatar.md`](03_Assets_Inventory_Avatar.md) | 19 | JPEG2000 fallback chain, wearable fetcher not wired, outfit fallback returns empty, baked-texture MIME mismatch, animation constraints skipped, sound spatial lookup missing, notecard/script upload state handling, mesh header parser, landmark UDP fallback, media ICY metadata. |
| 04 | [`04_UI_Voice_XR_Build.md`](04_UI_Voice_XR_Build.md) | 16 | OpenXR/AndroidXR stubs, BuildActivity placeholder, BuildTools create/duplicate/distribute no-ops, Voice gain not applied, RLV force-teleport/attach/detach/remove stubs, RLV info queries empty, FCM token upload stub. Voice SDP create/handle now RESOLVED. |
| 05 | [`05_Build_Tests_Native.md`](05_Build_Tests_Native.md) | 23 | Gradle parallelism/daemon disabled, KAPT incremental off, resource validation off, seven CI steps mask failures via `continue-on-error`, JNI Vivox/VxAudio/SndFile/oRTP/Basis/RawBuf all stubs, Zstd KTX2 stub, i18n only 3 locales, EconomyManager lacks buy-currency endpoint, test-classification hygiene. |

## Severity at a glance

| Severity | Protocol | Render | Assets | UI/XR | Build/Tests | **Total** |
|---|---:|---:|---:|---:|---:|---:|
| **Critical** | 4 | 5 | 5 | 3 | 3 | **20** |
| **High** | 8 | 7 | 9 | 7 | 6 | **37** |
| **Medium** | 6 | 6 | 4 | 5 | 10 | **31** |
| **Low** | 2 | 1 | 1 | 1 | 4 | **9** |
| **Per-sector total** | **20** | **19** | **19** | **16** | **23** | **97** |

## Reference sources

1. **Second Life viewer (C++ reference)** — `github.com/secondlife/viewer`
   - `indra/llmessage/` — UDP circuit, message template, packet ack
   - `indra/newview/` — scene graph, capability handlers, HUD, voice client
   - `indra/llprimitive/llwearable.*` — wearable parse behavior
   - `indra/llimagej2c/` — JPEG2000 decode path
   - `indra/llcharacter/llkeyframemotion.cpp` — animation constraints
   - `indra/newview/lltoolbrush.*`, `llselectmgr.*` — build/edit tools
2. **Lumiya Redux (legacy Java viewer)** — `github.com/Kaleaon/Lumiya-Redux`
   - Android-specific render/voice/inventory flows Linkpoint is replacing
3. **LLSD reference** — `github.com/Kaleaon/LLSD`
   - Binary, XML, and Notation parser/writer spec

## Execution order

The five fix docs are independent but share an implicit dependency order to
avoid rework:

1. **Build / Tests / Native (doc 05)** — re-enable parallel builds, stop
   masking CI failures; without this, later fixes can ship silently broken.
2. **Protocol (doc 01)** — parser coverage and the IM race are the foundation
   every other sector depends on.
3. **Assets (doc 03)** — JPEG2000 + wearable + bake pipeline must work before
   render parity matters.
4. **Render (doc 02)** — HUD/world/avatar geometry fixes need assets producing
   real data.
5. **UI / Voice / XR (doc 04)** — user-facing features sit on top of all of the
   above.

## Relationship to `docs/MASTER_TRACKING.md`

The existing tracker (`MASTER_TRACKING.md` / `MASTER_TRACKING.json`) covers only
the modernization-phase acceptance criteria (`MP-00x`) and the four parity-debt
entries (`PD-001`…`PD-004`). This audit is additive: every `PROTO-###`,
`RENDER-###`, `ASSET-###`, `UI-###`, `BUILD-###`, `NATIVE-###`, `CI-###`,
`TEST-###`, `DIAG-###`, `I18N-###`, `ECON-###` ID here should be promoted into
the master tracker as it is scheduled, not duplicated.
