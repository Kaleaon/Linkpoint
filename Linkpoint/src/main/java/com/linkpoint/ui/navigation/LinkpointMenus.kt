package com.linkpoint.ui.navigation

/**
 * Menu model used by the app shell while we migrate from legacy Activity navigation.
 */
data class MenuDestination(
    val label: String,
    val route: String
)

data class DrawerSection(
    val title: String,
    val items: List<MenuDestination>
)

object LinkpointMenus {
    val bottomTabs: List<MenuDestination> = listOf(
        MenuDestination("World", Routes.WORLD),
        MenuDestination("Chat", Routes.CHAT),
        MenuDestination("People", Routes.FRIENDS),
        MenuDestination("Inventory", Routes.INVENTORY),
        MenuDestination("Map", Routes.MAP)
    )

    val drawerSections: List<DrawerSection> = listOf(
        DrawerSection(
            title = "Me",
            items = listOf(
                MenuDestination("My Profile", Routes.MY_PROFILE),
                MenuDestination("My Avatar", Routes.MY_AVATAR),
                MenuDestination("Wallet", Routes.WALLET),
                MenuDestination("Notifications", Routes.NOTIFICATIONS_FEED)
            )
        ),
        DrawerSection(
            title = "Explore",
            items = listOf(
                MenuDestination("Search", Routes.SEARCH),
                MenuDestination("Nearby", Routes.NEARBY_PEOPLE),
                MenuDestination("Teleport History", Routes.TELEPORT_HISTORY),
                MenuDestination("Build Tools", Routes.BUILD_TOOLS),
                MenuDestination("Voice", Routes.VOICE_DEEP)
            )
        ),
        DrawerSection(
            title = "System",
            items = listOf(
                MenuDestination("Settings", Routes.SETTINGS),
                MenuDestination("Permissions", Routes.PERMISSIONS_OVERVIEW),
                MenuDestination("Onboarding", Routes.ONBOARDING_WELCOME),
                MenuDestination("Terms", Routes.TOS),
                MenuDestination("XR World", Routes.XR_WORLD)
            )
        )
    )
}
