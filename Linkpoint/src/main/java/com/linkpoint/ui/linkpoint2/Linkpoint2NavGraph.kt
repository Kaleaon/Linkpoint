package com.linkpoint.ui.linkpoint2

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.linkpoint.ui.chat.L2ChatRoute
import com.linkpoint.ui.linkpoint2.routes.L2EventsRoute
import com.linkpoint.ui.linkpoint2.routes.L2FriendsRoute
import com.linkpoint.ui.linkpoint2.routes.L2GraphicsSettingsRoute
import com.linkpoint.ui.linkpoint2.routes.L2GridManagementRoute
import com.linkpoint.ui.linkpoint2.routes.L2GroupProfileRoute
import com.linkpoint.ui.linkpoint2.routes.L2GroupsRoute
import com.linkpoint.ui.linkpoint2.routes.L2InventoryRoute
import com.linkpoint.ui.linkpoint2.routes.L2MapRoute
import com.linkpoint.ui.linkpoint2.routes.L2MinimapRoute
import com.linkpoint.ui.linkpoint2.routes.L2NearbyPeopleRoute
import com.linkpoint.ui.linkpoint2.routes.L2NotificationsRoute
import com.linkpoint.ui.linkpoint2.routes.L2OutfitComposerRoute
import com.linkpoint.ui.linkpoint2.routes.L2OutfitPickerRoute
import com.linkpoint.ui.linkpoint2.routes.L2PlaceDetailRoute
import com.linkpoint.ui.linkpoint2.routes.L2PlacesRoute
import com.linkpoint.ui.linkpoint2.routes.L2PrivacySettingsRoute
import com.linkpoint.ui.linkpoint2.routes.L2ProfileRoute
import com.linkpoint.ui.linkpoint2.routes.L2SearchRoute
import com.linkpoint.ui.linkpoint2.routes.L2TeleportHistoryRoute
import com.linkpoint.ui.linkpoint2.routes.L2VoiceDeepRoute
import com.linkpoint.ui.linkpoint2.routes.L2WalletRoute
import com.linkpoint.ui.linkpoint2.screens.BuildToolsScreen
import com.linkpoint.ui.linkpoint2.screens.CameraScreen
import com.linkpoint.ui.linkpoint2.screens.EmptyStatesReferenceScreen
import com.linkpoint.ui.linkpoint2.screens.L2IMListRoute
import com.linkpoint.ui.linkpoint2.screens.OnboardingAvatarScreen
import com.linkpoint.ui.linkpoint2.screens.OnboardingPermissionsScreen
import com.linkpoint.ui.linkpoint2.screens.OnboardingWelcomeScreen
import com.linkpoint.ui.login.L2LoginRoute
import com.linkpoint.ui.navigation.Routes
import com.linkpoint.ui.navigation.navigateBack
import com.linkpoint.ui.navigation.navigateTo
import com.linkpoint.ui.settings.SettingsRepository
import com.linkpoint.ui.settings.SettingsScreen
import com.linkpoint.ui.settings.SettingsState
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
 * Each post-login surface is wired to a real [LinkpointApp] manager via the
 * `L2*Route` composables in [com.linkpoint.ui.linkpoint2.routes]. When a
 * manager isn't initialised yet (pre-login) the route renders an honest
 * empty state — never fabricated data. The previous version of this file
 * passed `L2Demo.*` placeholders into every screen, which made the UI look
 * populated with sample residents while every action was a silent no-op
 * (chat, IM, friends, inventory, groups, wallet, teleport history, etc).
 * That is no longer the case here.
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
        route == Routes.ONBOARDING_PERMISSIONS -> {
            // Map PermissionRequest IDs to Android Manifest permissions.
            val permissionMap = mapOf(
                "mic" to Manifest.permission.RECORD_AUDIO,
                "loc" to Manifest.permission.ACCESS_FINE_LOCATION,
                "cam" to Manifest.permission.CAMERA,
            ) + if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                mapOf("notif" to Manifest.permission.POST_NOTIFICATIONS)
            } else emptyMap()

            // Track which permission is pending so the result handler can log it.
            var pendingPermission by remember { mutableStateOf<String?>(null) }
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->
                android.util.Log.d(
                    "OnboardingPermissions",
                    "Permission ${pendingPermission ?: "unknown"} granted=$granted",
                )
                if (!granted) pendingPermission = null
            }

            OnboardingPermissionsScreen(
                onAllow = { req ->
                    val androidPerm = permissionMap[req.id]
                    if (androidPerm != null) {
                        pendingPermission = androidPerm
                        launcher.launch(androidPerm)
                    }
                },
                onSkip = { /* user opted out; continue without this permission */ },
                onEnterWorld = { navController.navigateTo(Routes.LOGIN) },
                modifier = modifier,
            )
        }
        route == Routes.IM_LIST -> L2IMListRoute(
            onBack = back,
            onOpenConversation = { navController.navigateTo(Routes.CHAT) },
            onCompose = { navController.navigateTo(Routes.CHAT) },
            modifier = modifier,
        )
        route == Routes.CHAT -> L2ChatRoute(
            onNavigateBack = back,
            modifier = modifier,
        )
        route == Routes.FRIENDS -> L2FriendsRoute(
            onNavigateBack = back,
            onOpenIM = { _ -> navController.navigateTo(Routes.CHAT) },
            onViewProfile = { friend -> navController.navigateTo(Routes.profile(friend.id.toString())) },
            modifier = modifier,
        )
        route == Routes.WALLET -> L2WalletRoute(
            onBack = back,
            modifier = modifier,
        )
        route == Routes.BUILD_TOOLS -> BuildToolsScreen(
            onClose = back,
            // Build tools require a full ObjectEdit + selection pipeline
            // which is not yet implemented; tap is intentionally inert
            // until ObjectManager.editProperties is wired through.
            onApply = { _, _ -> },
            modifier = modifier,
        )
        route == Routes.VOICE_DEEP -> L2VoiceDeepRoute(
            onBack = back,
            modifier = modifier,
        )
        route == Routes.PLACES_SEARCH -> L2PlacesRoute(
            onBack = back,
            onSelect = { p -> navController.navigateTo(Routes.placeDetail(p.id)) },
            modifier = modifier,
        )
        baseRoute == "places" && route != Routes.PLACES_EVENTS && route != Routes.PLACES_SEARCH -> {
            L2PlaceDetailRoute(
                placeId = entry.arguments?.getString("placeId"),
                onBack = back,
                modifier = modifier,
            )
        }
        route == Routes.PLACES_EVENTS -> L2EventsRoute(
            onBack = back,
            modifier = modifier,
        )
        route == Routes.OUTFIT_PICKER -> L2OutfitPickerRoute(
            onBack = back,
            onEdit = { navController.navigateTo(Routes.OUTFIT_COMPOSER) },
            modifier = modifier,
        )
        route == Routes.OUTFIT_COMPOSER -> L2OutfitComposerRoute(
            onBack = back,
            modifier = modifier,
        )
        baseRoute == "group_profile" -> L2GroupProfileRoute(
            groupId = entry.arguments?.getString("groupId").orEmpty(),
            onNavigateBack = back,
            modifier = modifier,
        )
        route == Routes.NOTIFICATIONS_FEED -> L2NotificationsRoute(
            onBack = back,
            modifier = modifier,
        )
        route == Routes.CAMERA_MODE -> CameraScreen(
            onClose = back,
            // Snapshot capture path (SnapshotManager.capture) isn't wired
            // to the Compose surface yet; shutter is currently a no-op.
            onShutter = {},
            onSnapToFriends = {},
            onFlip = {},
            modifier = modifier,
        )
        route == Routes.GRAPHICS_SETTINGS -> L2GraphicsSettingsRoute(
            onBack = back,
            modifier = modifier,
        )
        route == Routes.PRIVACY_SETTINGS -> L2PrivacySettingsRoute(
            onBack = back,
            modifier = modifier,
        )
        route == Routes.GRID_MANAGEMENT -> L2GridManagementRoute(
            onBack = back,
            modifier = modifier,
        )
        route == Routes.EMPTY_STATES_REF -> EmptyStatesReferenceScreen(onBack = back, modifier = modifier)
        route == Routes.INVENTORY -> L2InventoryRoute(
            onNavigateBack = back,
            modifier = modifier,
        )
        route == Routes.MAP -> L2MapRoute(
            onNavigateBack = back,
            modifier = modifier,
        )
        route == Routes.GROUPS -> L2GroupsRoute(
            onNavigateBack = back,
            onOpenGroupChat = { _ -> navController.navigateTo(Routes.CHAT) },
            onViewGroupInfo = { g -> navController.navigateTo(Routes.groupProfile(g.id.toString())) },
            modifier = modifier,
        )
        route.startsWith("profile/") -> {
            val userId = entry.arguments?.getString("userId") ?: "me"
            L2ProfileRoute(
                userId = userId,
                isMe = userId == "me" || route == Routes.MY_PROFILE,
                onNavigateBack = back,
                onSendIM = { navController.navigateTo(Routes.CHAT) },
                onTeleportToMe = { /* requires friend-tracking; not yet wired here */ },
                onEditMyProfile = { navController.navigateTo(Routes.MY_AVATAR) },
                modifier = modifier,
            )
        }
        route == Routes.MY_AVATAR -> {
            // AvatarAppearance is currently a UI-only model; appearance state
            // round-trips through avatarManager but the editor surface only
            // controls visual params locally until AppearanceManager is
            // wired into the Compose layer.
            var appearance by remember { mutableStateOf(AvatarAppearance()) }
            MyAvatarScreen(
                appearance = appearance,
                onAppearanceChange = { appearance = it },
                onRebakeTextures = {
                    if (app != null) {
                        runCatching { app.appearanceManager.requestRebake() }
                    }
                },
                onOpenAppearanceEditor = { navController.navigateTo(Routes.OUTFIT_COMPOSER) },
                onNavigateBack = back,
                modifier = modifier,
            )
        }
        route == Routes.SEARCH -> L2SearchRoute(
            onNavigateBack = back,
            modifier = modifier,
        )
        route == Routes.NEARBY_PEOPLE -> L2NearbyPeopleRoute(
            onNavigateBack = back,
            onSendIM = { _ -> navController.navigateTo(Routes.CHAT) },
            onViewProfile = { person ->
                navController.navigateTo(Routes.profile(person.id.toString()))
            },
            modifier = modifier,
        )
        route == Routes.TELEPORT_HISTORY -> L2TeleportHistoryRoute(
            onNavigateBack = back,
            modifier = modifier,
        )
        route == Routes.MINIMAP -> L2MinimapRoute(
            onNavigateBack = back,
            onOpenWorldMap = { navController.navigateTo(Routes.MAP) },
            modifier = modifier,
        )
        route == Routes.SETTINGS -> {
            val context = androidx.compose.ui.platform.LocalContext.current
            val settingsRepo = remember(context) { SettingsRepository.getInstance(context) }
            var s by remember(settingsRepo) { mutableStateOf(settingsRepo.load()) }
            SettingsScreen(
                settings = s,
                onSettingsChange = {
                    s = it
                    settingsRepo.save(it)
                },
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
