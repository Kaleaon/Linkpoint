# UI Refactor Release Gates

This document defines the required pass criteria for each UI refactor release slice.

## Global required gates

Every release slice must pass the `ui-refactor` CI gate job with all of the checks below:

1. **Route coverage check**
   - Source of truth: `docs/ui-refactor/route-coverage.json`
   - Rule: `(covered_routes / total_routes) * 100` must be `>= minimum_percent`.
2. **Theme catalog validation**
   - Source of truth: `docs/ui-refactor/theme-catalog.json`
   - Rule: catalog must include at least one theme and each theme entry must include `name` and `status`.
3. **Compose screenshot / golden diffs**
   - Optional-by-configuration.
   - If `UI_REFACTOR_GOLDEN_DIFF_CMD` is configured in CI, it must execute successfully.

## Migration matrix requirements (A/B/C/D)

Source of truth: `docs/ui-refactor/migration-matrix.json`.

| Slice | Required completion | Gate behavior |
| --- | ---: | --- |
| A | 100% | Must be present as `slices.A.completion_percent` and meet or exceed required threshold for release. |
| B | 95% | Must be present as `slices.B.completion_percent` and meet or exceed required threshold for release. |
| C | 90% | Must be present as `slices.C.completion_percent` and meet or exceed required threshold for release. |
| D | 85% | Must be present as `slices.D.completion_percent` and meet or exceed required threshold for release. |

### Required pass criteria by release slice

- **Slice A release**: Global gates pass; migration matrix includes A/B/C/D percentages; slice A completion is at least 100%.
- **Slice B release**: Global gates pass; migration matrix includes A/B/C/D percentages; slice B completion is at least 95%.
- **Slice C release**: Global gates pass; migration matrix includes A/B/C/D percentages; slice C completion is at least 90%.
- **Slice D release**: Global gates pass; migration matrix includes A/B/C/D percentages; slice D completion is at least 85%.
