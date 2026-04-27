package com.linkpoint.ui.navigation

/**
 * Metadata for routes rendered by [LinkpointAppShell].
 */
data class RouteSpec(
    val title: String,
    val backBehavior: BackBehavior = BackBehavior.NAVIGATE_UP,
    val showBottomMenu: Boolean = false,
    val showDrawerMenu: Boolean = false
)

enum class BackBehavior {
    NONE,
    NAVIGATE_UP,
    EXIT_APP
}

object RouteSpecs {
    private val specs: Map<String, RouteSpec> = mapOf(
        Routes.LOGIN to RouteSpec("Login", backBehavior = BackBehavior.NONE),
        Routes.WORLD to RouteSpec("World", backBehavior = BackBehavior.EXIT_APP, showBottomMenu = true, showDrawerMenu = true),
        Routes.CHAT to RouteSpec("Chat", showBottomMenu = true, showDrawerMenu = true),
        Routes.INVENTORY to RouteSpec("Inventory", showBottomMenu = true, showDrawerMenu = true),
        Routes.FRIENDS to RouteSpec("Friends", showBottomMenu = true, showDrawerMenu = true),
        Routes.GROUPS to RouteSpec("Groups", showDrawerMenu = true),
        Routes.PROFILE to RouteSpec("Profile", showDrawerMenu = true),
        Routes.MY_PROFILE to RouteSpec("My Profile", showDrawerMenu = true),
        Routes.SETTINGS to RouteSpec("Settings", showDrawerMenu = true),
        Routes.MAP to RouteSpec("Map", showBottomMenu = true, showDrawerMenu = true),
        Routes.MINIMAP to RouteSpec("Minimap", showDrawerMenu = true),
        Routes.SEARCH to RouteSpec("Search", showDrawerMenu = true),
        Routes.TELEPORT_HISTORY to RouteSpec("Teleport History", showDrawerMenu = true),
        Routes.NEARBY_PEOPLE to RouteSpec("Nearby People", showDrawerMenu = true),
        Routes.MY_AVATAR to RouteSpec("My Avatar", showDrawerMenu = true),
        Routes.TOS to RouteSpec("Terms of Service"),
        Routes.THEME_PICKER to RouteSpec("Theme Picker"),
        Routes.SLURL to RouteSpec("SLURL"),
        Routes.XR_WORLD to RouteSpec("XR World"),
        Routes.WALLET to RouteSpec("Wallet", showDrawerMenu = true),
        Routes.NOTIFICATIONS_FEED to RouteSpec("Notifications", showDrawerMenu = true),
        Routes.VOICE_DEEP to RouteSpec("Voice", showDrawerMenu = true),
        Routes.PLACE_DETAIL to RouteSpec("Place Details", showDrawerMenu = true),
        Routes.PLACE_EVENTS to RouteSpec("Place Events", showDrawerMenu = true),
        Routes.BUILD_TOOLS to RouteSpec("Build Tools", showDrawerMenu = true),
        Routes.ONBOARDING_WELCOME to RouteSpec("Onboarding", backBehavior = BackBehavior.NONE),
        Routes.ONBOARDING_AVATAR_SETUP to RouteSpec("Avatar Setup", backBehavior = BackBehavior.NONE),
        Routes.ONBOARDING_VOICE_SETUP to RouteSpec("Voice Setup", backBehavior = BackBehavior.NONE),
        Routes.ONBOARDING_FINISH to RouteSpec("You're ready", backBehavior = BackBehavior.NONE),
        Routes.PERMISSIONS_OVERVIEW to RouteSpec("Permissions", backBehavior = BackBehavior.NONE),
        Routes.PERMISSIONS_MICROPHONE to RouteSpec("Microphone Permission", backBehavior = BackBehavior.NONE),
        Routes.PERMISSIONS_NOTIFICATIONS to RouteSpec("Notification Permission", backBehavior = BackBehavior.NONE)
    )

    fun forRoute(route: String?): RouteSpec {
        if (route == null) return RouteSpec("Linkpoint")
        return specs[normalizeRoute(route)] ?: RouteSpec("Linkpoint")
    }

    private fun normalizeRoute(route: String): String = when {
        route.startsWith("profile/") && route != Routes.MY_PROFILE -> Routes.PROFILE
        route.startsWith("slurl/") -> Routes.SLURL
        route.startsWith("place/") && route.endsWith("/events") -> Routes.PLACE_EVENTS
        route.startsWith("place/") -> Routes.PLACE_DETAIL
        else -> route
    }
}
