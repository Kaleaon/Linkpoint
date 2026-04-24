# Broken Code Recovery Tracking Protocol (2026-04-24)

## Tracking Artifact

Use `docs/broken_code_tracking_board_2026-04-24.csv` as the canonical implementation tracker keyed by `path` for all records where `status == BROKEN` in `docs/broken_code_inventory_2026-04-24.csv`.

## Mandatory Tracking Fields

Every row in the tracking board must preserve and/or populate the following required fields:

- `path` (primary key)
- `task_group`
- `assignee_team`
- `state`
- `pr_link`
- `verification_status`

## Pre-assignment Rules

The board is pre-assigned using these deterministic rules:

- `decompilation-stub|unsupported-operation` -> **Android Core Recovery Team**
- `unsupported-operation` -> **Platform Stability Team**
- `todo-call` -> **Tooling & Developer Experience Team**

## PR Referencing Requirement (Mandatory)

Each implementation PR must explicitly list the exact `path` value(s) from `docs/broken_code_inventory_2026-04-24.csv` that the PR resolves. Use a dedicated PR section titled `Resolved inventory paths` and include one path per line.

## Done Criteria

This initiative is complete only when all of the following are true:

1. All **179** `BROKEN` rows are mapped in the tracking board.
2. Every mapped row has an implementation linked through `pr_link`.
3. Every implementation has completed code review.
4. Verification confirms each path is no longer `BROKEN` in a refreshed inventory export.
5. The refreshed inventory shows zero remaining `BROKEN` statuses for those 179 paths.
