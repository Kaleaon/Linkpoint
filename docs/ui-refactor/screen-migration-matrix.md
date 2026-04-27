# Screen Migration Matrix

This matrix tracks activity-to-destination handoff work for the UI cluster refactor.

## Priority Order

1. **Phase 1 (first):** auth + world shell + chat + inventory + settings
2. **Phase 2 (next):** social/discovery/system advanced surfaces

## Matrix

| Handoff screen name | Current implementation file | Target destination | Status |
| --- | --- | --- | --- |
| Login | `ui/login/LoginScreen.kt` | `ui/auth/LoginDestination` | in progress |
| Start Location | `ui/login/StartLocationDialog.kt` | `ui/onboarding/StartLocationDestination` | not started |
| World Shell | `ui/world/WorldViewActivity.kt` | `ui/world/WorldShellDestination` | in progress |
| Chat | `ui/chat/ChatScreen.kt` | `ui/communication/ChatDestination` | in progress |
| Inventory | `ui/inventory/InventoryScreen.kt` | `ui/inventory/InventoryDestination` | in progress |
| Settings | `ui/settings/SettingsScreen.kt` | `ui/system/SettingsDestination` | in progress |
| Friends | `ui/friends/FriendsScreen.kt` | `ui/social/FriendsDestination` | not started |
| Groups | `ui/groups/GroupsScreen.kt` | `ui/social/GroupsDestination` | not started |
| Profile | `ui/profile/ProfileScreen.kt` | `ui/social/ProfileDestination` | not started |
| My Profile | `ui/profile/ProfileScreen.kt` | `ui/social/MyProfileDestination` | not started |
| Nearby People | `ui/people/NearbyPeopleScreen.kt` | `ui/social/NearbyPeopleDestination` | not started |
| My Avatar | `ui/avatar/MyAvatarScreen.kt` | `ui/social/MyAvatarDestination` | not started |
| Search | `ui/search/SearchScreen.kt` | `ui/discovery/SearchDestination` | not started |
| Teleport History | `ui/teleport/TeleportHistoryScreen.kt` | `ui/discovery/TeleportHistoryDestination` | not started |
| SLURL | `ui/slurl/SLURLScreen.kt` | `ui/discovery/SlurlDestination` | not started |
| Map | `ui/map/MapScreen.kt` | `ui/world/MapDestination` | not started |
| Minimap | `ui/minimap/MinimapScreen.kt` | `ui/world/MinimapDestination` | not started |
| XR World | `ui/xr/XRWorldActivity.kt` | `ui/world/XrWorldDestination` | not started |
| Terms of Service | `ui/tos/TosScreen.kt` | `ui/system/TosDestination` | not started |
| Theme Picker | `ui/theme/ThemePickerScreen.kt` | `ui/system/ThemePickerDestination` | not started |
