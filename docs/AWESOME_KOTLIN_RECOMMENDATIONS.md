# Awesome Kotlin Recommendations for Linkpoint Issues

This note reviews candidates from [mcxiaoke/awesome-kotlin](https://github.com/mcxiaoke/awesome-kotlin) against Linkpoint's current blockers in [`FIXES_AND_STATUS.md`](FIXES_AND_STATUS.md).

## Scope and Selection Criteria

Current high-priority Linkpoint problems:
1. RegionHandshake state not propagating (`Unknown` region name)
2. Object/avatar updates not reaching visible scene
3. Swap chain lifecycle instability
4. ACK retries under high latency

We selected only items that can realistically reduce diagnosis time or prevent regressions in those areas.

---

## High-Value Additions (Recommended)

### 1) Timber (logging)
- **Awesome Kotlin entry:** `timber`
- **Why it helps Linkpoint:** Current issues are pipeline/ordering issues (packet received → parsed → manager applied → renderer submitted). Timber can standardize taggable structured logs around each stage and reduce noisy `Log.d` sprawl.
- **Suggested rollout:**
  - Add `Timber.DebugTree` for debug builds.
  - Introduce domain tags (`Net/Ack`, `Net/Handshake`, `World/ObjectFlow`, `Render/Swapchain`).
  - Replace ad-hoc logs in region/object/render critical paths first.

### 2) LeakCanary (memory/lifecycle diagnostics)
- **Awesome Kotlin entry:** `leakcanary`
- **Why it helps Linkpoint:** Swap chain and rendering failures are often coupled with stale Surface/Activity/resource lifecycle state. LeakCanary helps catch retained renderer/surface references after lifecycle changes.
- **Suggested rollout:**
  - Debug-only dependency.
  - Test activity pause/resume, rotation, and reconnect cycles.
  - Use retained object traces to verify `RenderManager` teardown symmetry.

### 3) Detekt + KtLint (static guardrails)
- **Awesome Kotlin entries:** `detekt`, `ktlint`
- **Why it helps Linkpoint:** Linkpoint is heavily protocol-oriented Kotlin; regressions often come from subtle unsafe/complex paths. Static checks can enforce complexity limits and style consistency in parser/manager logic.
- **Suggested rollout:**
  - Enable on CI with incremental baseline.
  - Prioritize rules for cyclomatic complexity, long methods, magic numbers in protocol constants, and potential nullability hazards.

### 4) MockK (protocol + manager unit tests)
- **Awesome Kotlin entry:** `mockk`
- **Why it helps Linkpoint:** RegionHandshake/object-flow bugs are ideal for deterministic parser and manager tests with mocked dependencies (connection state, object manager, renderer queue).
- **Suggested rollout:**
  - Add focused tests for `RegionHandshake` name extraction and state propagation.
  - Add tests for object/avatar update routing to scene queues.
  - Add ACK retry logic tests with virtual time and mocked transport.

---

## Medium-Value Additions (Phase 2)

### 5) Kaspresso / Kakao (UI integration tests)
- **Awesome Kotlin entries:** `Kaspresso`, `Kakao`
- **Why it helps Linkpoint:** Once world loading path is stable, UI automation can verify end-to-end connection UX (login → region shown → world visible indicators).
- **Note:** Better after core render/object-flow stabilizes.

### 6) StateMachine (connection state modeling)
- **Awesome Kotlin entry:** `StateMachine`
- **Why it helps Linkpoint:** ACK sequencing and handshake gating are stateful. A formal state machine can prevent invalid transitions (e.g., sending movement completion before circuit ACK).
- **Note:** Useful if connection logic continues to grow; likely a targeted refactor.

---

## Low Priority / Not Recommended Right Now

- **Alternative networking stacks (Fuel/Ktor)**: Linkpoint already relies on OkHttp/gRPC and protocol-specific UDP flows; migration cost is high with limited direct payoff.
- **UI component libraries**: not related to the current blockers.
- **Database/ORM libraries**: no evidence they address immediate runtime failures.

---

## Practical Adoption Plan

1. **Week 1:** Add Timber + LeakCanary (debug only), instrument packet→scene→render checkpoints.
2. **Week 1-2:** Add Detekt/KtLint CI jobs with baseline; enforce on touched files.
3. **Week 2:** Add MockK tests for RegionHandshake + ObjectUpdate + ACK retry behavior.
4. **Week 3+:** Evaluate StateMachine only if ACK/order bugs remain recurring.

## Expected Impact

- Faster diagnosis of where entity flow breaks.
- Earlier detection of lifecycle leaks causing rendering instability.
- Fewer regressions in protocol parsing and connection sequencing.
- Better confidence when shipping fixes to login/world-loading code paths.
