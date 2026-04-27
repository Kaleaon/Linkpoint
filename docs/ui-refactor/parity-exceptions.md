# UI Refactor Known Parity Exceptions

Use this register for temporary parity gaps accepted during rollout. Every exception must include an owner and an expiry date; expired items must be either fixed or re-approved.

## Exception log

| ID | Feature area | Flag state affected | Behavior difference | User impact | Owner | Introduced | Expiry date | Tracking issue | Mitigation / rollout guard | Status |
|---|---|---|---|---|---|---|---|---|---|---|
| EX-001 | _TBD_ | _legacy_on / legacy_off / both_ | _Describe exact parity mismatch_ | _Low/Med/High + details_ | _Team/Person_ | 2026-04-27 | 2026-06-30 | _Issue/Link_ | _Feature gate, copy tweak, fallback, etc._ | Open |

## Rules

- Expiry date is mandatory and must be an absolute date in `YYYY-MM-DD` format.
- Default maximum lifetime is **60 days** from introduced date unless explicitly approved by product + engineering.
- High-impact exceptions require:
  - active mitigation,
  - release-note callout,
  - weekly review until resolved.

## Review cadence

- **Weekly QA/Eng review:** validate each open exception is still reproducible and correctly scoped.
- **Release readiness gate:** no expired exceptions may remain open at release cut.

## Closure criteria

An exception can be closed when all are true:

1. Fix is merged and validated under both flag states.
2. Related instrumentation confirms expected behavior in smoke flows.
3. Entry status updated to `Closed` with closure date and validating build/version.
