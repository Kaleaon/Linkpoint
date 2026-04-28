package com.linkpoint.ui.linkpoint2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.linkpoint.LinkpointApp
import com.linkpoint.ui.avatar.AvatarAppearance
import com.linkpoint.ui.avatar.MyAvatarScreen
import com.linkpoint.ui.chat.ChatScreen
import com.linkpoint.ui.common.UiLoadState
import com.linkpoint.ui.friends.FriendsScreen
import com.linkpoint.ui.groups.GroupData
import com.linkpoint.ui.groups.GroupsScreen
import com.linkpoint.ui.inventory.InventoryScreen
import com.linkpoint.ui.map.MapScreen
import com.linkpoint.ui.minimap.MinimapScreen
import com.linkpoint.ui.people.NearbyPeopleScreen
import com.linkpoint.ui.profile.ProfileData
import com.linkpoint.ui.profile.ProfileScreen
import com.linkpoint.ui.search.ComposeSearchResult
import com.linkpoint.ui.search.SearchScreen
import com.linkpoint.ui.settings.SettingsScreen
import com.linkpoint.ui.settings.SettingsState
import com.linkpoint.ui.teleport.TeleportHistoryScreen
import com.linkpoint.ui.linkpoint2.screens.BuildToolsScreen
import com.linkpoint.ui.linkpoint2.screens.CameraScreen
import com.linkpoint.ui.linkpoint2.screens.EmptyStatesReferenceScreen
import com.linkpoint.ui.linkpoint2.screens.EventsScreen
import com.linkpoint.ui.linkpoint2.screens.GraphicsSettingsScreen
import com.linkpoint.ui.linkpoint2.screens.GridManagementScreen
import com.linkpoint.ui.linkpoint2.screens.GroupProfileScreen
import com.linkpoint.ui.linkpoint2.screens.IMListScreen
import com.linkpoint.ui.linkpoint2.screens.NotificationsScreen
import com.linkpoint.ui.linkpoint2.screens.OnboardingAvatarScreen
import com.linkpoint.ui.linkpoint2.screens.OnboardingPermissionsScreen
import com.linkpoint.ui.linkpoint2.screens.OnboardingWelcomeScreen
import com.linkpoint.ui.linkpoint2.screens.OutfitComposerScreen
import com.linkpoint.ui.linkpoint2.screens.OutfitPickerScreen
import com.linkpoint.ui.linkpoint2.screens.PlaceDetailScreen
import com.linkpoint.ui.linkpoint2.screens.PlacesScreen
import com.linkpoint.ui.linkpoint2.screens.PrivacySettingsScreen
import com.linkpoint.ui.linkpoint2.screens.VoiceDeepScreen
import com.linkpoint.ui.linkpoint2.screens.WalletScreen
import com.linkpoint.ui.login.L2LoginRoute
import com.linkpoint.ui.navigation.Routes
import com.linkpoint.ui.navigation.navigateBack
import com.linkpoint.ui.navigation.navigateTo
import com.linkpoint.ui.slurl.L2SlurlRoute
import com.linkpoint.ui.theme.ThemeManager
import com.linkpoint.ui.theme.ThemePickerScreen
import com.linkpoint.ui.tos.L2TosRoute
import com.linkpoint.ui.world.L2WorldRoute
import com.linkpoint.ui.xr.L2XrWorldRoute

/**
 * Renders the Linkpoint 2.0 Compose screen for the given navigation route.
 * Returns true if the route is owned by the L2 graph; the caller should fall
 * back to legacy activity handling otherwise.
 *
 * State sources are deliberately abstracted here — the NavHost wires each
 * screen to a [LinkpointApp] manager when one exists, otherwise to
 * [L2Demo] sample data. As ViewModels land, replace the demo wiring per route.
 */
@Composable
fun Linkpoint2RouteHost(
    route: String,
    entry: NavBackStackEntry,
    navController: NavHostController,
    modifier: Modifier = Modifier,
): Boolean {
    val app = LinkpointApp.getInstanceOrNull()
    val back: () -> Unit = { navController.navigateBack() }

    // Strip arg suffixes for matching ("group_profile/123" -> "group_profile")
    val baseRoute = route.substringBeforeLast('/').takeIf { it.isNotEmpty() } ?: route

    when {
        route == Routes.LOGIN -> L2LoginRoute(
            onLoginSuccess = {
                navController.navigate(Routes.WORLD) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                    launchSingleTop = true
                }
            },
            onOpenSettings = { navController.navigateTo(Routes.SETTINGS) },
            modifier = modifier,
        )
        route == Routes.WORLD -> L2WorldRoute(
            onOpenChat = { navController.navigateTo(Routes.CHAT) },
            onOpenMinimap = { navController.navigateTo(Routes.MINIMAP) },
            onOpenInventory = { navController.navigateTo(Routes.INVENTORY) },
            onOpenMap = { navController.navigateTo(Routes.MAP) },
            modifier = modifier,
        )
        route == Routes.XR_WORLD -> L2XrWorldRoute(
            onBack = back,
            modifier = modifier,
        )
        route == Routes.TOS -> L2TosRoute(
            onAccepted = { navController.navigateTo(Routes.LOGIN) },
            onDeclined = back,
            modifier = modifier,
        )
        route == Routes.THEME_PICKER -> {
            val context = androidx.compose.ui.platform.LocalContext.current
            val themeManager = remember(context) { ThemeManager.getInstance(context) }
            ThemePickerScreen(
                themeManager = themeManager,
                onNavigateBack = back,
            )
        }
        route.startsWith("slurl/") -> L2SlurlRoute(
            slurl = entry.arguments?.getString("slurl").orEmpty(),
            onDismiss = back,
            onLogin = { navController.navigateTo(Routes.LOGIN) },
            modifier = modifier,
        )
        route == Routes.ONBOARDING_WELCOME -> OnboardingWelcomeScreen(
            onBegin = { navController.navigateTo(Routes.ONBOARDING_AVATAR) },
            onIHaveAnAccount = { navController.navigateTo(Routes.LOGIN) },
            modifier = modifier,
        )
        route == Routes.ONBOARDING_AVATAR -> OnboardingAvatarScreen(
            onContinue = { navController.navigateTo(Routes.ONBOARDING_PERMISSIONS) },
            onUploadPhoto = { /* handled via host activity */ },
            modifier = modifier,
        )
        route == Routes.ONBOARDING_PERMISSIONS -> OnboardingPermissionsScreen(
            onAllow = { /* delegate to PermissionManager via host */ },
            onSkip = { /* host */ },
            onEnterWorld = { navController.navigateTo(Routes.LOGIN) },
            modifier = modifier,
        )
        route == Routes.IM_LIST -> IMListScreen(
            conversations = L2Demo.Conversations,
            onBack = back,
            onOpenConversation = { navController.navigateTo(Routes.CHAT) },
            onCompose = { navController.navigateTo(Routes.CHAT) },
            modifier = modifier,
        )
        route == Routes.CHAT -> ChatScreen(
            messages = L2Demo.ChatThread,
            currentAvatarName = app?.sessionManager?.getAvatarName() ?: "You",
            threadAvatarName = "Echo Nightshade",
            threadOnline = true,
            threadLocation = "Crystal Coast (186, 92, 24)",
            uiLoadState = UiLoadState.Content,
            onRetry = {},
            onSendMessage = { _, _ -> },
            onNavigateBack = back,
            modifier = modifier,
        )
        route == Routes.FRIENDS -> FriendsScreen(
            friends = L2Demo.Friends,
            uiLoadState = UiLoadState.Content,
            onRetry = {},
            onNavigateBack = back,
            onOpenIM = { navController.navigateTo(Routes.CHAT) },
            onTeleportTo = {},
            onViewProfile = { friend -> navController.navigateTo(Routes.profile(friend.id.toString())) },
            onRemoveFriend = {},
            onAddFriend = {},
            modifier = modifier,
        )
        route == Routes.WALLET -> WalletScreen(
            balanceLinden = 14_320,
            usdEquivalent = 56.45,
            weeklyIn = 2_180,
            weeklyOut = 1_499,
            transactions = L2Demo.WalletTransactions,
            onBack = back,
            onSend = {},
            onRequest = {},
            onBuy = {},
            onRefresh = {},
            modifier = modifier,
        )
        route == Routes.BUILD_TOOLS -> BuildToolsScreen(
            onClose = back,
            onApply = { _, _ -> },
            modifier = modifier,
        )
        route == Routes.VOICE_DEEP -> VoiceDeepScreen(
            selfName = app?.sessionManager?.getAvatarName() ?: "You",
            participants = L2Demo.Voices,
            onBack = back,
            onTogglePtt = {},
            onMuteParticipant = {},
            modifier = modifier,
        )
        route == Routes.PLACES_SEARCH -> PlacesScreen(
            places = L2Demo.Places,
            onBack = back,
            onSelect = { p -> navController.navigateTo(Routes.placeDetail(p.id)) },
            modifier = modifier,
        )
        baseRoute == "places" && route != Routes.PLACES_EVENTS && route != Routes.PLACES_SEARCH -> {
            val placeId = entry.arguments?.getString("placeId")
            val place = L2Demo.Places.firstOrNull { it.id == placeId } ?: L2Demo.Places.first()
            PlaceDetailScreen(
                place = place,
                description = "A beautiful seaside region with bonfires, mesh terrain and a small live-music venue.",
                whoIsHere = listOf("Echo", "Aria", "Mira", "Voss"),
                onBack = back,
                onTeleport = {},
                onSave = {},
                onShare = {},
                modifier = modifier,
            )
        }
        route == Routes.PLACES_EVENTS -> EventsScreen(
            events = L2Demo.Events,
            onBack = back,
            onSelect = { /* open detail */ },
            modifier = modifier,
        )
        route == Routes.OUTFIT_PICKER -> OutfitPickerScreen(
            outfits = L2Demo.Outfits,
            onBack = back,
            onWear = {},
            onEdit = { navController.navigateTo(Routes.OUTFIT_COMPOSER) },
            modifier = modifier,
        )
        route == Routes.OUTFIT_COMPOSER -> OutfitComposerScreen(
            avatarName = app?.sessionManager?.getAvatarName() ?: "You",
            slots = L2Demo.OutfitSlots,
            onBack = back,
            onSave = {},
            onWear = {},
            onTap = {},
            modifier = modifier,
        )
        baseRoute == "group_profile" -> {
            val groupId = entry.arguments?.getString("groupId") ?: "tinkerers"
            GroupProfileScreen(
                groupTag = groupId.take(4).uppercase(),
                groupName = "Tinkerers Guild",
                description = "A community for builders and scripters.",
                membersOnline = 12,
                membersTotal = 87,
                members = L2Demo.GroupMembers,
                role = "Member",
                onBack = back,
                modifier = modifier,
            )
        }
        route == Routes.NOTIFICATIONS_FEED -> {
            var items by remember { mutableStateOf(L2Demo.Notifications) }
            NotificationsScreen(
                items = items,
                onBack = back,
                onClearAll = { items = emptyList() },
                onAccept = { item -> items = items.filterNot { it.id == item.id } },
                onDecline = { item -> items = items.filterNot { it.id == item.id } },
                onTap = {},
                modifier = modifier,
            )
        }
        route == Routes.CAMERA_MODE -> CameraScreen(
            onClose = back,
            onShutter = {},
            onSnapToFriends = {},
            onFlip = {},
            modifier = modifier,
        )
        route == Routes.GRAPHICS_SETTINGS -> {
            var state by remember { mutableStateOf(L2Demo.DefaultGraphics) }
            GraphicsSettingsScreen(
                initial = state,
                onBack = back,
                onChange = { state = it },
                modifier = modifier,
            )
        }
        route == Routes.PRIVACY_SETTINGS -> {
            var state by remember { mutableStateOf(L2Demo.DefaultPrivacy) }
            PrivacySettingsScreen(
                initial = state,
                blockedUsers = L2Demo.BlockedUsers,
                onBack = back,
                onUnblock = {},
                onChange = { state = it },
                modifier = modifier,
            )
        }
        route == Routes.GRID_MANAGEMENT -> GridManagementScreen(
            grids = L2Demo.Grids,
            onBack = back,
            onAdd = {},
            onEdit = {},
            onDelete = {},
            modifier = modifier,
        )
        route == Routes.EMPTY_STATES_REF -> EmptyStatesReferenceScreen(onBack = back, modifier = modifier)
        route == Routes.INVENTORY -> InventoryScreen(
            items = emptyList(),
            breadcrumb = listOf("My Inventory"),
            uiLoadState = UiLoadState.Empty(
                title = "Inventory loading",
                message = "Connect to a region to see your items.",
            ),
            onRetry = {},
            onItemClick = {},
            onNavigateBack = back,
            onNavigateUp = {},
            modifier = modifier,
        )
        route == Routes.MAP -> MapScreen(
            currentRegion = app?.sessionManager?.currentRegion?.value?.name ?: "Unknown region",
            currentPosition = androidx.compose.ui.geometry.Offset(128f, 128f),
            uiLoadState = UiLoadState.Content,
            onRetry = {},
            onNavigateBack = back,
            onTeleportTo = {},
            onTeleportHome = {},
            onSearch = {},
            modifier = modifier,
        )
        route == Routes.GROUPS -> GroupsScreen(
            myGroups = emptyList<GroupData>(),
            searchResults = emptyList(),
            onNavigateBack = back,
            onOpenGroupChat = { navController.navigateTo(Routes.CHAT) },
            onViewGroupInfo = { g -> navController.navigateTo(Routes.groupProfile(g.id.toString())) },
            onSetActiveGroup = {},
            onLeaveGroup = {},
            onJoinGroup = {},
            onSearch = {},
            modifier = modifier,
        )
        route.startsWith("profile/") -> {
            val userId = entry.arguments?.getString("userId") ?: "me"
            ProfileScreen(
                profile = ProfileData(
                    id = java.util.UUID.randomUUID(),
                    displayName = if (route == Routes.MY_PROFILE) {
                        app?.sessionManager?.getAvatarName() ?: "You"
                    } else "Resident",
                    username = "$userId.resident",
                    aboutText = "A Linkpoint 2.0 resident.",
                    isOwnProfile = userId == "me",
                    isOnline = true,
                ),
                onNavigateBack = back,
                onSendIM = { navController.navigateTo(Routes.CHAT) },
                onAddFriend = {},
                onTeleportTo = {},
                onEditProfile = { navController.navigateTo(Routes.MY_AVATAR) },
                onOpenWeb = {},
                modifier = modifier,
            )
        }
        route == Routes.MY_AVATAR -> {
            var appearance by remember { mutableStateOf(AvatarAppearance()) }
            MyAvatarScreen(
                appearance = appearance,
                onAppearanceChange = { appearance = it },
                onRebakeTextures = {},
                onOpenAppearanceEditor = { navController.navigateTo(Routes.OUTFIT_COMPOSER) },
                onNavigateBack = back,
                modifier = modifier,
            )
        }
        route == Routes.SEARCH -> SearchScreen(
            results = emptyList<ComposeSearchResult>(),
            uiLoadState = UiLoadState.Content,
            onRetry = {},
            onSearch = { _, _ -> },
            onResultClick = {},
            onNavigateBack = back,
            modifier = modifier,
        )
        route == Routes.NEARBY_PEOPLE -> {
            var filter by remember { mutableStateOf(com.linkpoint.ui.people.NearbyPeopleFilter.ALL) }
            NearbyPeopleScreen(
                people = emptyList(),
                selectedFilter = filter,
                onFilterChange = { filter = it },
                isLoading = false,
                emptyMessage = "Other residents in this region will appear here.",
                onRefresh = {},
                onSendIM = { navController.navigateTo(Routes.CHAT) },
                onAddFriend = {},
                onViewProfile = { person ->
                    navController.navigateTo(Routes.profile(person.id.toString()))
                },
                onNavigateBack = back,
                modifier = modifier,
            )
        }
        route == Routes.TELEPORT_HISTORY -> TeleportHistoryScreen(
            history = emptyList(),
            onTeleportTo = {},
            onDeleteEntry = {},
            onClearHistory = {},
            onNavigateBack = back,
            modifier = modifier,
        )
        route == Routes.MINIMAP -> MinimapScreen(
            regionName = app?.sessionManager?.currentRegion?.value?.name ?: "Unknown",
            avatarPosition = androidx.compose.ui.geometry.Offset(128f, 128f),
            avatarHeading = 0f,
            markers = emptyList(),
            onNavigateBack = back,
            onOpenWorldMap = { navController.navigateTo(Routes.MAP) },
            onMarkerTapped = {},
            modifier = modifier,
        )
        route == Routes.SETTINGS -> {
            var s by remember { mutableStateOf(SettingsState()) }
            SettingsScreen(
                settings = s,
                onSettingsChange = { s = it },
                onNavigateBack = back,
                onOpenThemePicker = { navController.navigateTo(Routes.THEME_PICKER) },
                onOpenAccount = { navController.navigateTo(Routes.MY_PROFILE) },
                onOpenPrivacy = { navController.navigateTo(Routes.PRIVACY_SETTINGS) },
                onOpenAbout = {},
                onOpenLayoutEditor = {},
                modifier = modifier,
            )
        }
        else -> return false
    }
    return true
}
