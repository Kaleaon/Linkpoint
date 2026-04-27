# Screen Migration Matrix

_Last updated: 2026-04-27._

| Legacy entry point | Compose destination wiring | Feature flag key | Current default path | Bridge status |
|---|---|---|---|---|
| `LoginActivity` | `LoginActivity` can be launched via `LegacyFeatureBridge.loginIntent(...)` with route args (`RouteArgs.Login`) and compose toggle extra | `ui_migration_compose_login` | Legacy XML login layout | Bridged (launch-time decision) |
| `ChatActivity` | `ChatActivity` reads `RouteArgs.EXTRA_USE_COMPOSE` and renders `ChatScreen` when enabled; route args normalized via `RouteArgs.Chat` | `ui_migration_compose_chat` | Legacy RecyclerView chat UI | Bridged (launch-time decision) |
| `InventoryActivity` | `InventoryActivity` reads `RouteArgs.EXTRA_USE_COMPOSE` and renders `InventoryScreen` when enabled | `ui_migration_compose_inventory` | Legacy RecyclerView inventory UI | Bridged (launch-time decision) |
| `SettingsActivity` | `SettingsActivity` reads `RouteArgs.EXTRA_USE_COMPOSE` and renders `SettingsScreen` when enabled | `ui_migration_compose_settings` | Legacy preference-fragment UI | Bridged (launch-time decision) |

## Fallback tracking

- Launchers now route through `LegacyFeatureBridge`, which selects compose/fallback using `UiMigrationFlagsProvider`.
- Route argument conversion for launch intents is centralized in `Linkpoint/src/main/java/com/linkpoint/ui/navigation/RouteArgs.kt`.
- Back navigation parity is handled by `finishOrPopBackStack(...)`, mapping legacy `finish()` semantics to `navController.popBackStack()` when a Compose nav controller is present.
