# UI Refactor Module Map

> Scope: package-level modules inside the current `Linkpoint` Android app module.

| Module | Primary package(s) | Depends on | Owner | Review-required labels |
|---|---|---|---|---|
| `ui-theme` | `com.linkpoint.ui.theme` | _none_ | `@ui-foundation` | `area:ui`, `review:design-system` |
| `ui-common-components` | `com.linkpoint.ui.components`, `com.linkpoint.ui.common`, `com.linkpoint.ui.dialogs` | `ui-theme` | `@ui-foundation` | `area:ui`, `review:component-api` |
| `ui-navigation` | `com.linkpoint.ui.navigation` | `ui-theme`, `ui-common-components` | `@ui-platform` | `area:ui`, `review:navigation` |
| `ui/chat` | `com.linkpoint.ui.chat` | `ui-theme`, `ui-common-components`, `ui-navigation`, domain/service adapters | `@feature-chat` | `area:chat`, `review:ui-arch` |
| `ui/inventory` | `com.linkpoint.ui.inventory` | `ui-theme`, `ui-common-components`, `ui-navigation`, domain/service adapters | `@feature-inventory` | `area:inventory`, `review:ui-arch` |
| `ui/world` | `com.linkpoint.ui.world` | `ui-theme`, `ui-common-components`, `ui-navigation`, domain/service adapters | `@feature-world` | `area:world`, `review:ui-arch` |
| Other `ui/*` feature packages (`ui/avatar`, `ui/map`, `ui/people`, etc.) | `com.linkpoint.ui.<feature>` | `ui-theme`, `ui-common-components`, `ui-navigation`, domain/service adapters | Per-feature maintainers | `area:ui`, `review:ui-arch` |

## Boundary rules

1. **One-way direction:** `ui/*` packages may depend on domain/service adapter interfaces, but non-UI packages must not import `com.linkpoint.ui.*`.
2. **Runtime/protocol isolation:** `com.linkpoint.protocol`, `com.linkpoint.network`, and `com.linkpoint.render` are blocked from direct UI coupling except via explicit API/interface/adapter contracts.
3. **Feature encapsulation:** `ui/chat`, `ui/inventory`, `ui/world`, and other feature packages should not import each other directly; shared code must live in `ui-theme`, `ui-common-components`, or `ui-navigation`.

Implementation note: enforced by `verifyUiArchitectureBoundaries` in `build.gradle.kts`.
