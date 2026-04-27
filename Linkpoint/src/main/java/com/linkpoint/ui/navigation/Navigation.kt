package com.linkpoint.ui.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.linkpoint.LinkpointApp
import com.linkpoint.ui.avatar.MyAvatarActivity
import com.linkpoint.ui.build.BuildActivity
import com.linkpoint.ui.chat.ChatActivity
import com.linkpoint.ui.friends.FriendsActivity
import com.linkpoint.ui.groups.GroupsActivity
import com.linkpoint.ui.inventory.InventoryActivity
import com.linkpoint.ui.login.LoginActivity
import com.linkpoint.ui.map.MapActivity
import com.linkpoint.ui.minimap.MinimapActivity
import com.linkpoint.ui.people.NearbyPeopleActivity
import com.linkpoint.ui.profile.ProfileActivity
import com.linkpoint.ui.search.SearchActivity
import com.linkpoint.ui.settings.SettingsActivity
import com.linkpoint.ui.teleport.TeleportHistoryActivity
import com.linkpoint.ui.tos.TosActivity
import com.linkpoint.ui.world.WorldViewActivity
import com.linkpoint.ui.xr.XRWorldActivity
import kotlin.reflect.KClass

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
    const val ONBOARDING_WELCOME = "onboarding/welcome"
    const val ONBOARDING_PERMISSIONS = "onboarding/permissions"
    const val ONBOARDING_AVATAR = "onboarding/avatar"

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
    val legacyActivity: KClass<out Activity>? = null
)

data class LinkpointMenuDestination(val route: String, val label: String)
data class LinkpointDrawerSection(val title: String, val items: List<LinkpointMenuDestination>)

object LinkpointMenus {
    val bottomTabs = listOf(
        LinkpointMenuDestination(Routes.WORLD, "World"),
        LinkpointMenuDestination(Routes.CHAT, "Chat"),
        LinkpointMenuDestination(Routes.INVENTORY, "Inventory"),
        LinkpointMenuDestination(Routes.FRIENDS, "Friends")
    )

    val drawerSections = listOf(
        LinkpointDrawerSection(
            title = "Explore",
            items = listOf(
                LinkpointMenuDestination(Routes.MAP, "Map"),
                LinkpointMenuDestination(Routes.SEARCH, "Search"),
                LinkpointMenuDestination(Routes.PLACES_EVENTS, "Events")
            )
        ),
        LinkpointDrawerSection(
            title = "Tools",
            items = listOf(
                LinkpointMenuDestination(Routes.BUILD_TOOLS, "Build Tools"),
                LinkpointMenuDestination(Routes.NOTIFICATIONS_FEED, "Notifications"),
                LinkpointMenuDestination(Routes.VOICE_DEEP, "Voice")
            )
        ),
        LinkpointDrawerSection(
            title = "Account",
            items = listOf(
                LinkpointMenuDestination(Routes.MY_PROFILE, "My Profile"),
                LinkpointMenuDestination(Routes.WALLET, "Wallet"),
                LinkpointMenuDestination(Routes.SETTINGS, "Settings")
            )
        )
    )
}

val routeMetadata: Map<String, RouteMetadata> = mapOf(
    Routes.LOGIN to RouteMetadata(MenuPlacement.NONE, "Sign in", requiresAuth = false, legacyActivity = LoginActivity::class),
    Routes.WORLD to RouteMetadata(MenuPlacement.BOTTOM_TAB, "World", "3D session", BackBehavior.ROOT_LEVEL, legacyActivity = WorldViewActivity::class),
    Routes.CHAT to RouteMetadata(MenuPlacement.BOTTOM_TAB, "Chat", "Conversations", legacyActivity = ChatActivity::class),
    Routes.INVENTORY to RouteMetadata(MenuPlacement.BOTTOM_TAB, "Inventory", "Items and outfits", legacyActivity = InventoryActivity::class),
    Routes.FRIENDS to RouteMetadata(MenuPlacement.BOTTOM_TAB, "Friends", "People list", legacyActivity = FriendsActivity::class),
    Routes.GROUPS to RouteMetadata(MenuPlacement.DRAWER, "Groups", legacyActivity = GroupsActivity::class),
    Routes.PROFILE to RouteMetadata(MenuPlacement.NONE, "Resident profile", backBehavior = BackBehavior.ALWAYS_TO_WORLD, legacyActivity = ProfileActivity::class),
    Routes.MY_PROFILE to RouteMetadata(MenuPlacement.DRAWER, "My profile", legacyActivity = ProfileActivity::class),
    Routes.SETTINGS to RouteMetadata(MenuPlacement.DRAWER, "Settings", legacyActivity = SettingsActivity::class),
    Routes.MAP to RouteMetadata(MenuPlacement.DRAWER, "Map", legacyActivity = MapActivity::class),
    Routes.MINIMAP to RouteMetadata(MenuPlacement.OVERFLOW, "Minimap", legacyActivity = MinimapActivity::class),
    Routes.SEARCH to RouteMetadata(MenuPlacement.DRAWER, "Search", legacyActivity = SearchActivity::class),
    Routes.TELEPORT_HISTORY to RouteMetadata(MenuPlacement.OVERFLOW, "Teleport history", legacyActivity = TeleportHistoryActivity::class),
    Routes.NEARBY_PEOPLE to RouteMetadata(MenuPlacement.OVERFLOW, "Nearby", legacyActivity = NearbyPeopleActivity::class),
    Routes.MY_AVATAR to RouteMetadata(MenuPlacement.OVERFLOW, "My avatar", legacyActivity = MyAvatarActivity::class),
    Routes.TOS to RouteMetadata(MenuPlacement.NONE, "Terms of service", requiresAuth = false, legacyActivity = TosActivity::class),
    Routes.THEME_PICKER to RouteMetadata(MenuPlacement.OVERFLOW, "Theme picker"),
    Routes.SLURL to RouteMetadata(MenuPlacement.NONE, "SLURL handoff"),
    Routes.XR_WORLD to RouteMetadata(MenuPlacement.OVERFLOW, "XR world", legacyActivity = XRWorldActivity::class),
    Routes.WALLET to RouteMetadata(MenuPlacement.DRAWER, "Wallet", "L$ balance and transactions"),
    Routes.BUILD_TOOLS to RouteMetadata(MenuPlacement.DRAWER, "Build tools", legacyActivity = BuildActivity::class),
    Routes.NOTIFICATIONS_FEED to RouteMetadata(MenuPlacement.DRAWER, "Notifications", "Messages and system notices"),
    Routes.VOICE_DEEP to RouteMetadata(MenuPlacement.DRAWER, "Voice", "Channel and diagnostics"),
    Routes.PLACES_DETAIL to RouteMetadata(MenuPlacement.NONE, "Place details", backBehavior = BackBehavior.ALWAYS_TO_WORLD),
    Routes.PLACES_EVENTS to RouteMetadata(MenuPlacement.DRAWER, "Events", "Places and happenings"),
    Routes.ONBOARDING_WELCOME to RouteMetadata(MenuPlacement.NONE, "Welcome", requiresAuth = false),
    Routes.ONBOARDING_PERMISSIONS to RouteMetadata(MenuPlacement.NONE, "Permissions", requiresAuth = false),
    Routes.ONBOARDING_AVATAR to RouteMetadata(MenuPlacement.NONE, "Starter avatar", requiresAuth = false)
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
                    GuardedRoute(navController = navController, route = routePattern, metadata = resolved)
                }
            }
        }
    }
}

@Composable
private fun GuardedRoute(
    navController: NavHostController,
    route: String,
    metadata: RouteMetadata
) {
    val isAuthenticated = LinkpointApp.getInstanceOrNull()?.sessionManager?.isConnected() == true

    if (metadata.requiresAuth && !isAuthenticated) {
        LaunchedEffect(route) {
            navController.navigateTo(Routes.LOGIN)
        }
        PlaceholderRoute("Authentication required", "Please sign in to continue.")
        return
    }

    val legacyActivity = metadata.legacyActivity
    if (legacyActivity != null && route != Routes.LOGIN) {
        LegacyActivityRoute(activityClass = legacyActivity, title = metadata.title)
    } else {
        PlaceholderRoute(metadata.title, metadata.subtitle ?: "Compose destination ready for migration.")
    }
}

@Composable
private fun LegacyActivityRoute(
    activityClass: KClass<out Activity>,
    title: String
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    PlaceholderRoute(title, "Legacy activity opens as leaf destination.") {
        launchLegacyActivity(context, activityClass)
    }
}

private fun launchLegacyActivity(context: Context, activityClass: KClass<out Activity>) {
    context.startActivity(Intent(context, activityClass.java))
}

@Composable
private fun PlaceholderRoute(
    title: String,
    subtitle: String,
    onOpenLegacy: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall)
        Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
        onOpenLegacy?.let {
            Button(onClick = it, modifier = Modifier.padding(top = 16.dp)) {
                Text("Open legacy screen")
            }
        }
    }
}
