package com.linkpoint.ui.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import com.linkpoint.ui.slurl.SLURLActivity
import com.linkpoint.ui.teleport.TeleportHistoryActivity
import com.linkpoint.ui.tos.TosActivity
import com.linkpoint.ui.world.WorldViewActivity
import com.linkpoint.ui.xr.XRWorldActivity

/**
 * Navigation routes for the Linkpoint app.
 */
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

    // Planned destinations
    const val WALLET = "wallet"
    const val NOTIFICATIONS_FEED = "notifications_feed"
    const val VOICE_DEEP = "voice_deep"
    const val PLACE_DETAIL = "place/{region}/{x}/{y}/{z}"
    const val PLACE_EVENTS = "place/{region}/{x}/{y}/{z}/events"
    const val BUILD_TOOLS = "build_tools"

    // Onboarding steps
    const val ONBOARDING_WELCOME = "onboarding/welcome"
    const val ONBOARDING_AVATAR_SETUP = "onboarding/avatar_setup"
    const val ONBOARDING_VOICE_SETUP = "onboarding/voice_setup"
    const val ONBOARDING_FINISH = "onboarding/finish"

    // Permission flow
    const val PERMISSIONS_OVERVIEW = "permissions/overview"
    const val PERMISSIONS_MICROPHONE = "permissions/microphone"
    const val PERMISSIONS_NOTIFICATIONS = "permissions/notifications"

    fun profile(userId: String) = "profile/$userId"
    fun slurl(slurl: String) = "slurl/${android.net.Uri.encode(slurl)}"
    fun placeDetail(region: String, x: String, y: String, z: String) = "place/$region/$x/$y/$z"
    fun placeEvents(region: String, x: String, y: String, z: String) = "place/$region/$x/$y/$z/events"
}

fun NavHostController.navigateTo(route: String) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
    }
}

fun NavHostController.navigateBack() {
    popBackStack()
}

@Composable
fun LinkpointNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.LOGIN
) {
    LinkpointAppShell(navController = navController) { shellModifier ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = shellModifier
        ) {
            composable(Routes.LOGIN) { LegacyRouteBridge(LoginActivity::class.java) }
            composable(Routes.WORLD) { LegacyRouteBridge(WorldViewActivity::class.java) }
            composable(Routes.CHAT) { LegacyRouteBridge(ChatActivity::class.java) }
            composable(Routes.INVENTORY) { LegacyRouteBridge(InventoryActivity::class.java) }
            composable(Routes.FRIENDS) { LegacyRouteBridge(FriendsActivity::class.java) }
            composable(Routes.GROUPS) { LegacyRouteBridge(GroupsActivity::class.java) }
            composable(Routes.PROFILE) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                LegacyRouteBridge(ProfileActivity::class.java) { putExtra("userId", userId) }
            }
            composable(Routes.MY_PROFILE) { LegacyRouteBridge(ProfileActivity::class.java) { putExtra("isMe", true) } }
            composable(Routes.SETTINGS) { LegacyRouteBridge(SettingsActivity::class.java) }
            composable(Routes.MAP) { LegacyRouteBridge(MapActivity::class.java) }
            composable(Routes.MINIMAP) { LegacyRouteBridge(MinimapActivity::class.java) }
            composable(Routes.SEARCH) { LegacyRouteBridge(SearchActivity::class.java) }
            composable(Routes.TELEPORT_HISTORY) { LegacyRouteBridge(TeleportHistoryActivity::class.java) }
            composable(Routes.NEARBY_PEOPLE) { LegacyRouteBridge(NearbyPeopleActivity::class.java) }
            composable(Routes.MY_AVATAR) { LegacyRouteBridge(MyAvatarActivity::class.java) }
            composable(Routes.TOS) { LegacyRouteBridge(TosActivity::class.java) }
            composable(Routes.THEME_PICKER) { PlaceholderRoute("Theme Picker") }
            composable(Routes.SLURL) { backStackEntry ->
                val slurl = backStackEntry.arguments?.getString("slurl") ?: return@composable
                LegacyRouteBridge(SLURLActivity::class.java) {
                    data = android.net.Uri.parse(android.net.Uri.decode(slurl))
                }
            }
            composable(Routes.XR_WORLD) { LegacyRouteBridge(XRWorldActivity::class.java) }

            composable(Routes.WALLET) { PlaceholderRoute("Wallet") }
            composable(Routes.NOTIFICATIONS_FEED) { PlaceholderRoute("Notifications Feed") }
            composable(Routes.VOICE_DEEP) { PlaceholderRoute("Voice (Deep)") }
            composable(Routes.PLACE_DETAIL) { PlaceholderRoute("Place Detail") }
            composable(Routes.PLACE_EVENTS) { PlaceholderRoute("Place Events") }
            composable(Routes.BUILD_TOOLS) { LegacyRouteBridge(BuildActivity::class.java) }

            composable(Routes.ONBOARDING_WELCOME) { PlaceholderRoute("Onboarding Welcome") }
            composable(Routes.ONBOARDING_AVATAR_SETUP) { PlaceholderRoute("Onboarding Avatar Setup") }
            composable(Routes.ONBOARDING_VOICE_SETUP) { PlaceholderRoute("Onboarding Voice Setup") }
            composable(Routes.ONBOARDING_FINISH) { PlaceholderRoute("Onboarding Finish") }

            composable(Routes.PERMISSIONS_OVERVIEW) { PlaceholderRoute("Permissions Overview") }
            composable(Routes.PERMISSIONS_MICROPHONE) { PlaceholderRoute("Permissions: Microphone") }
            composable(Routes.PERMISSIONS_NOTIFICATIONS) { PlaceholderRoute("Permissions: Notifications") }
        }
    }
}

@Composable
private fun PlaceholderRoute(label: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = label)
    }
}

@Composable
private fun LegacyRouteBridge(
    activityClass: Class<out Activity>,
    configureIntent: Intent.() -> Unit = {}
) {
    val context = LocalContext.current
    LaunchedEffect(activityClass) {
        context.startActivity(Intent(context, activityClass).apply(configureIntent))
    }
    PlaceholderRoute("Opening ${activityClass.simpleName}")
}

fun Context.resolveRouteFromIntent(intent: Intent?): String? = RouteResolver.resolve(intent)
