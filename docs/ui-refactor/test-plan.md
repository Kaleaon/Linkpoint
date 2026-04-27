# UI Refactor Migration Smoke Test Plan

## Scope
This smoke plan verifies Compose routing, theme integrity, built-in theme compatibility, and screenshot stability after UI refactor milestones.

## Preconditions
- Build debug + androidTest artifacts for Linkpoint module.
- Emulator/device available (API 34+ recommended).
- Theme assets and built-ins bundled in app.

## Smoke Suite

### 1) Compose navigation routing
- Bottom tab routing: verify selecting each bottom tab updates active route state and visible destination content.
- Drawer section routing: verify selecting drawer sections updates active destination and closes drawer.

### 2) Theme correctness
- Primary controls: verify Material primary controls (button/FAB) use active theme `colorPrimary`.
- Spot-check linkpoint, viewer-inspired, and cleverferret family themes.

### 3) Theme catalog consistency
- IDs are globally unique.
- Every declared family has at least one entry.
- Lookups return expected family and theme by ID.

### 4) Built-in compatibility
- `linkpoint_default` resolves via built-in lookup.
- No duplicate IDs in merged built-in/catalog list.

### 5) Screenshot/golden guardrail
Capture representative screens under three theme families:
- Login
- Chat
- Friends
- Settings
- Teleport History

For each screen:
- capture snapshot hash for all 3 families,
- ensure expected themed visual variation,
- archive hashes as migration baseline for future regressions.

## Execution order
1. Unit tests (`ThemeCatalog*`, `BuiltInThemesCompatibility*`).
2. Instrumented Compose UI routing/theme tests.
3. Instrumented screenshot/golden tests.

## Pass criteria
- All unit tests pass.
- All Compose routing/theme tests pass.
- Screenshot/golden tests produce 5x3 snapshots with expected per-screen variation.

## Follow-up on failures
- Routing failure: inspect test harness nav wiring and destination state updates.
- Theme failure: inspect `LinkpointTheme`, `ThemeColors`, and built-in pack values.
- Golden failure: validate intentional UI change; if intentional, regenerate and approve new baselines.
