package com.linkpoint.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.linkpoint.LinkpointApp

object Routes {
    const val LOGIN = "login"
    const val WORLD = "world"
    const val CHAT = "chat"
    const val INVENTORY = "inventory"
    const val FRIENDS = "friends"
    const val GROUPS = "groups"
    const val PROFILE = "profile/{userId}"
    const val MY_PROFILE = "profile/me"
    const val SETTINGS = "settings"
    const val MAP = "map"
    const val MINIMAP = "minimap"
    const val RADAR = "radar"
    const val SEARCH = "search"
    const val TELEPORT_HISTORY = "teleport_history"
    const val NEARBY_PEOPLE = "nearby_people"
    const val MY_AVATAR = "my_avatar"
    const val TOS = "tos"
    const val THEME_PICKER = "theme_picker"
    const val SLURL = "slurl/{slurl}"
    const val XR_WORLD = "xr_world"

    const val WALLET = "wallet"
    const val BUILD_TOOLS = "build_tools"
    const val NOTIFICATIONS_FEED = "notifications_feed"
    const val VOICE_DEEP = "voice_deep"
    const val PLACES_DETAIL = "places/{placeId}"
    const val PLACES_EVENTS = "places/events"
    const val PLACES_SEARCH = "places/search"
    const val ONBOARDING_WELCOME = "onboarding/welcome"
    const val ONBOARDING_PERMISSIONS = "onboarding/permissions"
    const val ONBOARDING_AVATAR = "onboarding/avatar"

    // Linkpoint 2.0 additions wired through LinkpointNavHost.
    const val IM_LIST = "im_list"
    const val OUTFIT_PICKER = "outfit_picker"
    const val OUTFIT_COMPOSER = "outfit_composer"
    const val GROUP_PROFILE = "group_profile/{groupId}"
    const val CAMERA_MODE = "camera_mode"
    const val GRAPHICS_SETTINGS = "settings/graphics"
    const val PRIVACY_SETTINGS = "settings/privacy"
    const val GRID_MANAGEMENT = "settings/grids"
    const val EMPTY_STATES_REF = "dev/empty_states"

    fun groupProfile(groupId: String) = "group_profile/$groupId"

    fun profile(userId: String) = "profile/$userId"
    fun slurl(slurl: String) = "slurl/$slurl"
    fun placeDetail(placeId: String) = "places/$placeId"
}

enum class MenuPlacement { BOTTOM_TAB, DRAWER, OVERFLOW, NONE }
enum class BackBehavior { DEFAULT, ROOT_LEVEL, ALWAYS_TO_WORLD }

data class RouteMetadata(
    val menuPlacement: MenuPlacement,
    val title: String,
    val subtitle: String? = null,
    val backBehavior: BackBehavior = BackBehavior.DEFAULT,
    val requiresAuth: Boolean = true,
)

data class LinkpointMenuDestination(val route: String, val label: String)
data class LinkpointDrawerSection(val title: String, val items: List<LinkpointMenuDestination>)

object LinkpointMenus {
    val bottomTabs = listOf(
        LinkpointMenuDestination(Routes.CHAT, "Chat"),
        LinkpointMenuDestination(Routes.WORLD, "World"),
        LinkpointMenuDestination(Routes.MAP, "Map"),
        LinkpointMenuDestination(Routes.INVENTORY, "Inventory"),
        LinkpointMenuDestination(Routes.MY_PROFILE, "Me"),
    )

    val drawerSections = listOf(
        LinkpointDrawerSection(
            title = "Explore",
            items = listOf(
                LinkpointMenuDestination(Routes.MAP, "Map"),
                LinkpointMenuDestination(Routes.PLACES_SEARCH, "Places"),
                LinkpointMenuDestination(Routes.PLACES_EVENTS, "Events"),
                LinkpointMenuDestination(Routes.SEARCH, "Search"),
            ),
        ),
        LinkpointDrawerSection(
            title = "Social",
            items = listOf(
                LinkpointMenuDestination(Routes.FRIENDS, "Friends"),
                LinkpointMenuDestination(Routes.IM_LIST, "Messages"),
                LinkpointMenuDestination(Routes.GROUPS, "Groups"),
                LinkpointMenuDestination(Routes.VOICE_DEEP, "Voice"),
            ),
        ),
        LinkpointDrawerSection(
            title = "Tools",
            items = listOf(
                LinkpointMenuDestination(Routes.BUILD_TOOLS, "Build tools"),
                LinkpointMenuDestination(Routes.OUTFIT_PICKER, "Outfits"),
                LinkpointMenuDestination(Routes.OUTFIT_COMPOSER, "Outfit composer"),
                LinkpointMenuDestination(Routes.CAMERA_MODE, "Camera"),
                LinkpointMenuDestination(Routes.NOTIFICATIONS_FEED, "Notifications"),
            ),
        ),
        LinkpointDrawerSection(
            title = "Account",
            items = listOf(
                LinkpointMenuDestination(Routes.MY_PROFILE, "My profile"),
                LinkpointMenuDestination(Routes.MY_AVATAR, "My avatar"),
                LinkpointMenuDestination(Routes.WALLET, "Wallet"),
                LinkpointMenuDestination(Routes.SETTINGS, "Settings"),
                LinkpointMenuDestination(Routes.GRAPHICS_SETTINGS, "Graphics"),
                LinkpointMenuDestination(Routes.PRIVACY_SETTINGS, "Privacy"),
                LinkpointMenuDestination(Routes.GRID_MANAGEMENT, "Grids"),
                LinkpointMenuDestination(Routes.THEME_PICKER, "Theme"),
            ),
        ),
    )
}

val routeMetadata: Map<String, RouteMetadata> = mapOf(
    Routes.LOGIN to RouteMetadata(MenuPlacement.NONE, "Sign in", requiresAuth = false),
    Routes.WORLD to RouteMetadata(MenuPlacement.BOTTOM_TAB, "World", "3D session", BackBehavior.ROOT_LEVEL),
    Routes.CHAT to RouteMetadata(MenuPlacement.BOTTOM_TAB, "Chat", "Conversations"),
    Routes.INVENTORY to RouteMetadata(MenuPlacement.BOTTOM_TAB, "Inventory", "Items and outfits"),
    Routes.FRIENDS to RouteMetadata(MenuPlacement.BOTTOM_TAB, "Friends", "People list"),
    Routes.GROUPS to RouteMetadata(MenuPlacement.DRAWER, "Groups"),
    Routes.PROFILE to RouteMetadata(MenuPlacement.NONE, "Resident profile", backBehavior = BackBehavior.ALWAYS_TO_WORLD),
    Routes.MY_PROFILE to RouteMetadata(MenuPlacement.DRAWER, "My profile"),
    Routes.SETTINGS to RouteMetadata(MenuPlacement.DRAWER, "Settings"),
    Routes.MAP to RouteMetadata(MenuPlacement.DRAWER, "Map"),
    Routes.MINIMAP to RouteMetadata(MenuPlacement.OVERFLOW, "Minimap"),
    Routes.RADAR to RouteMetadata(MenuPlacement.OVERFLOW, "Radar"),
    Routes.SEARCH to RouteMetadata(MenuPlacement.DRAWER, "Search"),
    Routes.TELEPORT_HISTORY to RouteMetadata(MenuPlacement.OVERFLOW, "Teleport history"),
    Routes.NEARBY_PEOPLE to RouteMetadata(MenuPlacement.OVERFLOW, "Nearby"),
    Routes.MY_AVATAR to RouteMetadata(MenuPlacement.OVERFLOW, "My avatar"),
    Routes.TOS to RouteMetadata(MenuPlacement.NONE, "Terms of service", requiresAuth = false),
    Routes.THEME_PICKER to RouteMetadata(MenuPlacement.OVERFLOW, "Theme picker"),
    Routes.SLURL to RouteMetadata(MenuPlacement.NONE, "SLURL handoff", requiresAuth = false),
    Routes.XR_WORLD to RouteMetadata(MenuPlacement.OVERFLOW, "XR world"),
    Routes.WALLET to RouteMetadata(MenuPlacement.DRAWER, "Wallet", "L$ balance and transactions"),
    Routes.BUILD_TOOLS to RouteMetadata(MenuPlacement.DRAWER, "Build tools"),
    Routes.NOTIFICATIONS_FEED to RouteMetadata(MenuPlacement.DRAWER, "Notifications", "Messages and system notices"),
    Routes.VOICE_DEEP to RouteMetadata(MenuPlacement.DRAWER, "Voice", "Channel and diagnostics"),
    Routes.PLACES_DETAIL to RouteMetadata(MenuPlacement.NONE, "Place details", backBehavior = BackBehavior.ALWAYS_TO_WORLD),
    Routes.PLACES_EVENTS to RouteMetadata(MenuPlacement.DRAWER, "Events", "Places and happenings"),
    Routes.PLACES_SEARCH to RouteMetadata(MenuPlacement.DRAWER, "Places", "Discover regions"),
    Routes.ONBOARDING_WELCOME to RouteMetadata(MenuPlacement.NONE, "Welcome", requiresAuth = false),
    Routes.ONBOARDING_PERMISSIONS to RouteMetadata(MenuPlacement.NONE, "Permissions", requiresAuth = false),
    Routes.ONBOARDING_AVATAR to RouteMetadata(MenuPlacement.NONE, "Starter avatar", requiresAuth = false),
    Routes.IM_LIST to RouteMetadata(MenuPlacement.OVERFLOW, "Messages", "All conversations"),
    Routes.OUTFIT_PICKER to RouteMetadata(MenuPlacement.OVERFLOW, "Outfits", "Saved looks"),
    Routes.OUTFIT_COMPOSER to RouteMetadata(MenuPlacement.OVERFLOW, "Outfit composer", "Mix and match wearables"),
    Routes.GROUP_PROFILE to RouteMetadata(MenuPlacement.NONE, "Group", backBehavior = BackBehavior.DEFAULT),
    Routes.CAMERA_MODE to RouteMetadata(MenuPlacement.NONE, "Camera", backBehavior = BackBehavior.ALWAYS_TO_WORLD),
    Routes.GRAPHICS_SETTINGS to RouteMetadata(MenuPlacement.OVERFLOW, "Graphics", "Quality & performance"),
    Routes.PRIVACY_SETTINGS to RouteMetadata(MenuPlacement.OVERFLOW, "Privacy", "Visibility & blocking"),
    Routes.GRID_MANAGEMENT to RouteMetadata(MenuPlacement.OVERFLOW, "Grids", "Configured worlds"),
    Routes.EMPTY_STATES_REF to RouteMetadata(MenuPlacement.NONE, "Empty states", "Reference"),
)

fun NavHostController.navigateTo(route: String) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
    }
}

fun NavHostController.navigateBack() {
    popBackStack()
}

fun resolveRouteMetadata(route: String?): RouteMetadata {
    val normalizedRoute = route ?: Routes.WORLD
    return routeMetadata[normalizedRoute]
        ?: when {
            normalizedRoute.startsWith("profile/") -> routeMetadata.getValue(Routes.PROFILE)
            normalizedRoute.startsWith("slurl/") -> routeMetadata.getValue(Routes.SLURL)
            normalizedRoute.startsWith("group_profile/") -> routeMetadata.getValue(Routes.GROUP_PROFILE)
            normalizedRoute.startsWith("places/") -> routeMetadata.getValue(Routes.PLACES_DETAIL)
            else -> routeMetadata.getValue(Routes.WORLD)
        }
}

@Composable
fun LinkpointNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.LOGIN
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val metadata = resolveRouteMetadata(currentRoute)

    LinkpointAppShell(
        navController = navController,
        currentRoute = currentRoute,
        title = metadata.title,
        subtitle = metadata.subtitle,
        bottomTabs = LinkpointMenus.bottomTabs,
        drawerSections = LinkpointMenus.drawerSections
    ) { paddingModifier ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = paddingModifier
        ) {
            routeMetadata.forEach { (routePattern, _) ->
                composable(routePattern) { entry ->
                    val resolved = resolveRouteMetadata(entry.destination.route)
                    GuardedRoute(
                        navController = navController,
                        route = routePattern,
                        entry = entry,
                        metadata = resolved,
                    )
                }
            }
        }
    }
}

@Composable
private fun GuardedRoute(
    navController: NavHostController,
    route: String,
    entry: androidx.navigation.NavBackStackEntry,
    metadata: RouteMetadata
) {
    val isAuthenticated = LinkpointApp.getInstanceOrNull()?.sessionManager?.isConnected() == true

    if (metadata.requiresAuth && !isAuthenticated) {
        LaunchedEffect(route) {
            navController.navigateTo(Routes.LOGIN)
        }
        return
    }

    val handled = com.linkpoint.ui.linkpoint2.Linkpoint2RouteHost(
        route = route,
        entry = entry,
        navController = navController,
    )
    if (handled) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "${metadata.title} is unavailable.",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
