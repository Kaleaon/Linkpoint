# UI Refactor Parity QA Checklist

This checklist verifies behavioral parity between the legacy UI shell (`legacy_ui_enabled=true`) and the refactored UI shell (`legacy_ui_enabled=false`).

## Test matrix

| Area | Legacy ON | Legacy OFF | Notes |
|---|---|---|---|
| Login | ☐ | ☐ | Include invalid credential and reconnect paths. |
| Chat send/retry | ☐ | ☐ | Include transient network failure + retry success. |
| Inventory browse/search | ☐ | ☐ | Include empty query and no-results state. |
| Profile/Friends/Groups navigation | ☐ | ☐ | Verify deep links + back-stack behavior. |
| Settings/theme apply | ☐ | ☐ | Verify immediate apply + persistence after relaunch. |
| Shell nav instrumentation smoke | ☐ | ☐ | Tab/route transition telemetry emitted. |
| Critical dialogs/modals instrumentation smoke | ☐ | ☐ | Open/close/confirm telemetry emitted. |

## Pre-conditions

- Build variant includes both shell implementations behind `legacy_ui_enabled` flag.
- Test account has:
  - At least one friend.
  - Membership in at least one group.
  - Inventory folders/items and searchable names.
- Instrumentation sink/log capture is enabled for QA builds.

---

## Feature parity scenarios by flag state

> Execute every scenario twice:
> 1. `legacy_ui_enabled=true`
> 2. `legacy_ui_enabled=false`

### 1) Login

#### Scenario L1: Successful login
- Launch app from clean state.
- Enter valid credentials and submit.
- **Expected parity:**
  - Loading/progress state appears.
  - User reaches home shell.
  - Session/account summary matches expected avatar/account.

#### Scenario L2: Invalid credentials
- Enter invalid password and submit.
- **Expected parity:**
  - Authentication error is shown with actionable copy.
  - Login form remains interactive after error.
  - No partial shell initialization occurs.

#### Scenario L3: Reconnect after network interruption
- Start login, briefly disable network, then restore.
- **Expected parity:**
  - Error/retry state is shown.
  - Retry path succeeds without requiring app restart.

### 2) Chat send/retry

#### Scenario C1: Send message success
- Open a direct chat thread and send message `Parity ping`.
- **Expected parity:**
  - Message appears in pending/sending state then sent/confirmed state.
  - Timestamp and sender styling are consistent with platform conventions.

#### Scenario C2: Send failure then retry
- Disable network, send message, then restore network and tap retry.
- **Expected parity:**
  - Failed-state indicator appears.
  - Retry action is available and functional.
  - Final delivered message appears once, no duplicates.

#### Scenario C3: Thread continuity after navigation
- Send or retry a message, navigate away, then return.
- **Expected parity:**
  - Composer draft/state is preserved per product spec.
  - Message list order and statuses are unchanged.

### 3) Inventory browse/search

#### Scenario I1: Browse folders and items
- Open inventory root and drill into nested folders.
- **Expected parity:**
  - Hierarchy/order matches backend data.
  - Folder/item taps open correct detail/action menus.

#### Scenario I2: Search positive query
- Search for an existing item name fragment.
- **Expected parity:**
  - Results include expected item(s).
  - Selecting a result opens correct item context.

#### Scenario I3: Search empty/no results
- Run an empty query and then a random no-hit query.
- **Expected parity:**
  - Empty query behavior matches product spec (recent/all/none).
  - No-results state copy and CTA behavior are equivalent.

### 4) Profile / Friends / Groups navigation

#### Scenario N1: Profile route
- Open profile from shell, then open a linked section (e.g., picks/about).
- **Expected parity:**
  - Content loads correctly.
  - Back navigation returns to prior screen without stack corruption.

#### Scenario N2: Friends list and profile handoff
- Open friends list, select a friend, open friend profile, go back.
- **Expected parity:**
  - Friend presence/status is visible.
  - Back-stack order is preserved.

#### Scenario N3: Groups list and group detail
- Open groups list, select a group, open members/notices tab if available.
- **Expected parity:**
  - Group data renders completely.
  - Tab/section switching does not reset unexpectedly.

### 5) Settings / Theme apply

#### Scenario S1: Theme toggle apply
- Open settings, switch Light ↔ Dark (or supported themes).
- **Expected parity:**
  - Theme applies immediately to current shell/components.
  - No unreadable text or contrast regressions in primary surfaces.

#### Scenario S2: Persistence after relaunch
- Change theme, kill app, relaunch.
- **Expected parity:**
  - Previously selected theme persists.
  - Settings screen reflects stored value.

#### Scenario S3: Theme + navigation interaction
- Apply theme and navigate across at least 3 top-level destinations.
- **Expected parity:**
  - No flashing/recomposition artifacts beyond acceptable threshold.
  - Themed system bars/chrome remain consistent.

---

## Instrumentation smoke tests

### A) Shell navigation telemetry

Run for both flag states.

#### Scenario T1: Top-level tab transitions
- Navigate across all primary shell destinations (e.g., Home → Chat → Inventory → Settings).
- **Expected events (minimum):**
  - `shell_nav_open` for initial route.
  - `shell_nav_select` (or equivalent) for each transition.
  - Route identifier and source route metadata are populated.

#### Scenario T2: Deep-link + back
- Open a deep link (e.g., profile or chat thread), then back out to shell root.
- **Expected events (minimum):**
  - `shell_nav_deeplink_open`.
  - `shell_nav_back` with from/to route metadata.

### B) Critical dialogs/modals telemetry

Run for both flag states.

#### Scenario T3: Confirmation dialog lifecycle
- Trigger a destructive/critical confirm dialog (e.g., sign out, leave group, delete item in QA-safe context).
- Cancel once, then reopen and confirm.
- **Expected events (minimum):**
  - `dialog_open` with dialog identifier.
  - `dialog_cancel` and `dialog_confirm` on respective actions.
  - `dialog_close` on dismissal path.

#### Scenario T4: Error modal lifecycle
- Force a recoverable error modal (e.g., network/API timeout in QA environment).
- Dismiss and retry action where available.
- **Expected events (minimum):**
  - `modal_open` with error code/category.
  - `modal_action` for retry/dismiss.
  - Retry result event (`modal_retry_success` or failure equivalent).

## Sign-off checklist

- [ ] All scenarios passed with `legacy_ui_enabled=true`.
- [ ] All scenarios passed with `legacy_ui_enabled=false`.
- [ ] No untracked parity regressions remain.
- [ ] Known exceptions are recorded in `parity-exceptions.md` with owner + expiry date.
- [ ] QA report links attached in release ticket.
