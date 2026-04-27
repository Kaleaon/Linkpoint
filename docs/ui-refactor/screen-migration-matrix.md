# Screen Migration Matrix

This matrix tracks handoff from legacy `ui/*` implementations to feature-oriented composables.

## Status legend
- `NOT_STARTED`: No destination scaffold exists.
- `STUBBED`: Destination composable stub exists.
- `IN_PROGRESS`: Migration started in a PR.
- `DONE`: Legacy handoff complete and verified.

## Cluster execution order
1. Auth + World shell
2. Chat/IM
3. Friends/Groups/Profile
4. Inventory/Outfits
5. Map/Places/Events
6. Wallet/Build/Settings/Notifications

## Handoff screens

| Cluster | Existing file(s) | Target composable file | Required states | Owner | Date | Status |
|---|---|---|---|---|---|---|
| Auth + World shell | `Linkpoint/src/main/java/com/linkpoint/ui/login/LoginScreen.kt`, `.../LoginActivity.kt` | `Linkpoint/src/main/java/com/linkpoint/feature/auth/AuthGatewayScreen.kt` | Loading, Error, EmptyGridList, Authenticated | @unassigned | 2026-04-27 | STUBBED |
| Auth + World shell | `Linkpoint/src/main/java/com/linkpoint/ui/login/StartLocationDialog.kt` | `Linkpoint/src/main/java/com/linkpoint/feature/onboarding/OnboardingEntryScreen.kt` | Loading, Error, EmptyStartLocations, Ready | @unassigned | 2026-04-27 | STUBBED |
| Auth + World shell | `Linkpoint/src/main/java/com/linkpoint/ui/world/WorldViewActivity.kt`, `.../navigation/Navigation.kt` | `Linkpoint/src/main/java/com/linkpoint/feature/world/WorldShellScreen.kt` | LoadingRegion, Error, EmptySession, Connected | @unassigned | 2026-04-27 | STUBBED |
| Chat/IM | `Linkpoint/src/main/java/com/linkpoint/ui/chat/ChatScreen.kt`, `.../ChatActivity.kt` | `Linkpoint/src/main/java/com/linkpoint/feature/communication/ChatImScreen.kt` | Loading, Error, EmptyConversations, Connected | @unassigned | 2026-04-27 | STUBBED |
| Friends/Groups/Profile | `Linkpoint/src/main/java/com/linkpoint/ui/friends/FriendsScreen.kt`, `.../FriendsActivity.kt`, `.../FriendsListFragment.kt` | `Linkpoint/src/main/java/com/linkpoint/feature/social/FriendsGroupsProfileScreen.kt` | Loading, Error, EmptyFriends, Ready | @unassigned | 2026-04-27 | STUBBED |
| Friends/Groups/Profile | `Linkpoint/src/main/java/com/linkpoint/ui/groups/GroupsScreen.kt`, `.../GroupsActivity.kt` | `Linkpoint/src/main/java/com/linkpoint/feature/social/FriendsGroupsProfileScreen.kt` | Loading, Error, EmptyGroups, Ready | @unassigned | 2026-04-27 | STUBBED |
| Friends/Groups/Profile | `Linkpoint/src/main/java/com/linkpoint/ui/profile/ProfileScreen.kt`, `.../ProfileActivity.kt` | `Linkpoint/src/main/java/com/linkpoint/feature/social/FriendsGroupsProfileScreen.kt` | Loading, Error, EmptyProfile, Ready | @unassigned | 2026-04-27 | STUBBED |
| Friends/Groups/Profile | `Linkpoint/src/main/java/com/linkpoint/ui/people/NearbyPeopleScreen.kt`, `.../NearbyPeopleActivity.kt`, `.../NearbyPeopleFragment.kt` | `Linkpoint/src/main/java/com/linkpoint/feature/social/FriendsGroupsProfileScreen.kt` | Loading, Error, EmptyNearby, Ready | @unassigned | 2026-04-27 | STUBBED |
| Inventory/Outfits | `Linkpoint/src/main/java/com/linkpoint/ui/inventory/InventoryScreen.kt`, `.../InventoryActivity.kt`, `.../InventoryFragment.kt` | `Linkpoint/src/main/java/com/linkpoint/feature/inventory/InventoryOutfitsScreen.kt` | Loading, Error, EmptyInventory, Ready | @unassigned | 2026-04-27 | STUBBED |
| Inventory/Outfits | `Linkpoint/src/main/java/com/linkpoint/ui/avatar/MyAvatarScreen.kt`, `.../MyAvatarActivity.kt`, `.../AppearanceEditorFragment.kt` | `Linkpoint/src/main/java/com/linkpoint/feature/inventory/InventoryOutfitsScreen.kt` | Loading, Error, EmptyWearables, Ready | @unassigned | 2026-04-27 | STUBBED |
| Inventory/Outfits | `Linkpoint/src/main/java/com/linkpoint/ui/notecard/NotecardEditorScreen.kt`, `.../NotecardEditorActivity.kt` | `Linkpoint/src/main/java/com/linkpoint/feature/inventory/InventoryOutfitsScreen.kt` | Loading, Error, EmptyDocument, Edited | @unassigned | 2026-04-27 | STUBBED |
| Inventory/Outfits | `Linkpoint/src/main/java/com/linkpoint/ui/scripts/ScriptEditorScreen.kt`, `.../ScriptEditorActivity.kt` | `Linkpoint/src/main/java/com/linkpoint/feature/inventory/InventoryOutfitsScreen.kt` | Loading, Error, EmptyScript, Edited | @unassigned | 2026-04-27 | STUBBED |
| Map/Places/Events | `Linkpoint/src/main/java/com/linkpoint/ui/map/MapScreen.kt`, `.../MapActivity.kt` | `Linkpoint/src/main/java/com/linkpoint/feature/discovery/MapPlacesEventsScreen.kt` | Loading, Error, EmptyRegionResults, Ready | @unassigned | 2026-04-27 | STUBBED |
| Map/Places/Events | `Linkpoint/src/main/java/com/linkpoint/ui/search/SearchScreen.kt`, `.../SearchActivity.kt` | `Linkpoint/src/main/java/com/linkpoint/feature/discovery/MapPlacesEventsScreen.kt` | Loading, Error, EmptySearchResults, Ready | @unassigned | 2026-04-27 | STUBBED |
| Map/Places/Events | `Linkpoint/src/main/java/com/linkpoint/ui/slurl/SLURLScreen.kt`, `.../SLURLActivity.kt` | `Linkpoint/src/main/java/com/linkpoint/feature/discovery/MapPlacesEventsScreen.kt` | Loading, Error, InvalidLocation, Ready | @unassigned | 2026-04-27 | STUBBED |
| Map/Places/Events | `Linkpoint/src/main/java/com/linkpoint/ui/teleport/TeleportHistoryScreen.kt`, `.../TeleportHistoryActivity.kt` | `Linkpoint/src/main/java/com/linkpoint/feature/discovery/MapPlacesEventsScreen.kt` | Loading, Error, EmptyHistory, Ready | @unassigned | 2026-04-27 | STUBBED |
| Map/Places/Events | `Linkpoint/src/main/java/com/linkpoint/ui/minimap/MinimapScreen.kt`, `.../MinimapActivity.kt` | `Linkpoint/src/main/java/com/linkpoint/feature/discovery/MapPlacesEventsScreen.kt` | Loading, Error, EmptySurroundings, Ready | @unassigned | 2026-04-27 | STUBBED |
| Map/Places/Events | `Linkpoint/src/main/java/com/linkpoint/ui/radar/RadarCompose.kt`, `.../RadarActivity.kt`, `.../RadarView.kt` | `Linkpoint/src/main/java/com/linkpoint/feature/discovery/MapPlacesEventsScreen.kt` | Loading, Error, EmptyNearbyAgents, Ready | @unassigned | 2026-04-27 | STUBBED |
| Wallet/Build/Settings/Notifications | `Linkpoint/src/main/java/com/linkpoint/ui/settings/SettingsScreen.kt`, `.../SettingsActivity.kt` | `Linkpoint/src/main/java/com/linkpoint/feature/system/WalletBuildSettingsNotificationsScreen.kt` | Loading, Error, EmptySettings, Ready | @unassigned | 2026-04-27 | STUBBED |
| Wallet/Build/Settings/Notifications | `Linkpoint/src/main/java/com/linkpoint/ui/build/` (legacy layout: `Linkpoint/src/main/res/layout/activity_build.xml`) | `Linkpoint/src/main/java/com/linkpoint/feature/system/WalletBuildSettingsNotificationsScreen.kt` | Loading, Error, EmptySelection, Ready | @unassigned | 2026-04-27 | STUBBED |
| Wallet/Build/Settings/Notifications | `Linkpoint/src/main/java/com/linkpoint/economy/EconomyManager.kt` (wallet entrypoint), `Linkpoint/src/main/res/layout/activity_world_view.xml` actions | `Linkpoint/src/main/java/com/linkpoint/feature/system/WalletBuildSettingsNotificationsScreen.kt` | Loading, Error, EmptyTransactions, Ready | @unassigned | 2026-04-27 | STUBBED |
| Wallet/Build/Settings/Notifications | `Linkpoint/src/main/java/com/linkpoint/notifications/NotificationManager.kt` | `Linkpoint/src/main/java/com/linkpoint/feature/system/WalletBuildSettingsNotificationsScreen.kt` | Loading, Error, EmptyNotifications, Ready | @unassigned | 2026-04-27 | STUBBED |
| Wallet/Build/Settings/Notifications | `Linkpoint/src/main/java/com/linkpoint/ui/tos/TosScreen.kt`, `.../TosActivity.kt` | `Linkpoint/src/main/java/com/linkpoint/feature/system/WalletBuildSettingsNotificationsScreen.kt` | Loading, Error, EmptyDocument, Accepted | @unassigned | 2026-04-27 | STUBBED |
| Wallet/Build/Settings/Notifications | `Linkpoint/src/main/java/com/linkpoint/ui/xr/XRWorldActivity.kt` | `Linkpoint/src/main/java/com/linkpoint/feature/system/WalletBuildSettingsNotificationsScreen.kt` | Loading, Error, UnsupportedDevice, Ready | @unassigned | 2026-04-27 | STUBBED |

## PR requirement
Every PR that migrates a listed screen **must** update at least one corresponding matrix row (`Owner`, `Date`, and `Status`).
