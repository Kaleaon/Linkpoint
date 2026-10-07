package com.linkpoint.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.linkpoint.ui.adaptive.LocalWindowSizeClass
import com.linkpoint.ui.components.linkpoint2.primitives.L2Tab
import com.linkpoint.ui.components.linkpoint2.primitives.L2TopBar
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2
import com.linkpoint.ui.linkpoint2.screens.Linkpoint2BottomTabs
import com.linkpoint.ui.overlay.OverlayManager
import kotlinx.coroutines.launch

/**
 * Linkpoint 2.0 top-level shell — delegates responsive layout rendering to
 * [NavigationSuiteScaffold], switching dynamically between bottom bar, side rail,
 * and permanent side drawer based on [LocalWindowSizeClass].
 *
 * Routes flagged with [MenuPlacement.NONE] (e.g. login, modal screens, world)
 * suppress navigation chrome completely ([NavigationSuiteType.None]).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkpointAppShell(
    navController: NavHostController,
    currentRoute: String?,
    title: String,
    subtitle: String?,
    bottomTabs: List<LinkpointMenuDestination> = LinkpointMenus.bottomTabs,
    drawerSections: List<LinkpointDrawerSection> = LinkpointMenus.drawerSections,
    content: @Composable (Modifier) -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val metadata = resolveRouteMetadata(currentRoute)
    val showChrome = metadata.menuPlacement != MenuPlacement.NONE
    val windowSizeClass = LocalWindowSizeClass.current

    val layoutType = when {
        !showChrome -> NavigationSuiteType.None
        windowSizeClass.isCompact -> NavigationSuiteType.NavigationBar
        windowSizeClass.isMedium -> NavigationSuiteType.NavigationRail
        else -> NavigationSuiteType.NavigationDrawer
    }

    LaunchedEffect(currentRoute) {
        if (currentRoute != null && currentRoute != Routes.WORLD && currentRoute != Routes.XR_WORLD && currentRoute != Routes.LOGIN) {
            val routeMeta = resolveRouteMetadata(currentRoute)
            OverlayManager.getInstance().showOverlay(
                id = "route_$currentRoute",
                type = OverlayManager.OverlayType.FULL_SCREEN_2D,
                title = routeMeta.title
            )
        } else if (currentRoute == Routes.WORLD || currentRoute == Routes.XR_WORLD) {
            OverlayManager.getInstance().clearAllOverlays()
        }
    }

    val l2Tabs: List<L2Tab> = Linkpoint2BottomTabs.takeIf { it.isNotEmpty() }
        ?: bottomTabs.map {
            L2Tab(
                id = it.route,
                label = it.label,
                icon = getRouteIcon(it.route),
            )
        }

    val activeTabId = currentRoute ?: l2Tabs.firstOrNull()?.id ?: Routes.WORLD

    val tokens = Linkpoint2.tokens

    val l2NavigationSuiteColors = NavigationSuiteDefaults.colors(
        navigationBarContainerColor = tokens.glass.background,
        navigationBarContentColor = MaterialTheme.colorScheme.onSurface,
        navigationRailContainerColor = tokens.glass.background,
        navigationRailContentColor = MaterialTheme.colorScheme.onSurface,
        navigationDrawerContainerColor = tokens.glass.background,
        navigationDrawerContentColor = MaterialTheme.colorScheme.onSurface,
    )

    val l2ItemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
            unselectedIconColor = tokens.onSurfaceDim,
            unselectedTextColor = tokens.onSurfaceDim,
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
            unselectedIconColor = tokens.onSurfaceDim,
            unselectedTextColor = tokens.onSurfaceDim,
        ),
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = tokens.onSurfaceDim,
            unselectedTextColor = tokens.onSurfaceDim,
        ),
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showChrome && !windowSizeClass.isExpanded,
        drawerContent = {
            if (drawerSections.isNotEmpty() && !windowSizeClass.isExpanded) {
                ModalDrawerSheet {
                    drawerSections.forEach { section ->
                        Text(
                            text = section.title,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            style = MaterialTheme.typography.titleSmall,
                        )
                        section.items.forEach { item ->
                            NavigationDrawerItem(
                                label = { Text(item.label) },
                                selected = currentRoute == item.route,
                                onClick = {
                                    navController.navigateTo(item.route)
                                    scope.launch { drawerState.close() }
                                },
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        },
    ) {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                if (showChrome) {
                    l2Tabs.forEach { tab ->
                        item(
                            selected = activeTabId == tab.id,
                            onClick = { navController.navigateTo(tab.id) },
                            icon = { Icon(imageVector = tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                            colors = l2ItemColors,
                        )
                    }
                    if (layoutType == NavigationSuiteType.NavigationDrawer && drawerSections.isNotEmpty()) {
                        drawerSections.forEach { section ->
                            section.items.forEach { item ->
                                if (l2Tabs.none { it.id == item.route }) {
                                    item(
                                        selected = currentRoute == item.route,
                                        onClick = { navController.navigateTo(item.route) },
                                        icon = { Icon(imageVector = getRouteIcon(item.route), contentDescription = item.label) },
                                        label = { Text(item.label) },
                                        colors = l2ItemColors,
                                    )
                                }
                            }
                        }
                    }
                }
            },
            layoutType = layoutType,
            containerColor = Color.Transparent,
            navigationSuiteColors = l2NavigationSuiteColors,
        ) {
            Scaffold(
                topBar = {
                    if (showChrome) {
                        L2TopBar(
                            title = title,
                            subtitle = subtitle,
                            leading = if (drawerSections.isNotEmpty() && !windowSizeClass.isExpanded) {
                                {
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                                    }
                                }
                            } else null,
                        )
                    }
                },
            ) { paddingValues ->
                content(Modifier.padding(paddingValues))
            }
        }
    }
}

private fun getRouteIcon(route: String): ImageVector {
    return when {
        route == Routes.CHAT || route == Routes.IM_LIST -> Icons.AutoMirrored.Filled.Chat
        route == Routes.WORLD || route == Routes.GRID_MANAGEMENT || route == Routes.XR_WORLD -> Icons.Default.Public
        route == Routes.MAP || route == Routes.MINIMAP -> Icons.Default.Map
        route == Routes.INVENTORY -> Icons.Default.Work
        route == Routes.MY_PROFILE || route == Routes.PROFILE -> Icons.Default.Person
        route == Routes.FRIENDS || route == Routes.NEARBY_PEOPLE -> Icons.Default.People
        route == Routes.GROUPS || route.startsWith("group_profile") -> Icons.Default.Group
        route == Routes.PLACES_SEARCH || route.startsWith("places/") -> Icons.Default.Explore
        route == Routes.PLACES_EVENTS -> Icons.Default.Event
        route == Routes.SEARCH -> Icons.Default.Search
        route == Routes.VOICE_DEEP -> Icons.Default.Mic
        route == Routes.BUILD_TOOLS -> Icons.Default.Build
        route == Routes.OUTFIT_PICKER -> Icons.Default.Checkroom
        route == Routes.OUTFIT_COMPOSER -> Icons.Default.Style
        route == Routes.CAMERA_MODE -> Icons.Default.PhotoCamera
        route == Routes.NOTIFICATIONS_FEED -> Icons.Default.Notifications
        route == Routes.MY_AVATAR -> Icons.Default.Face
        route == Routes.WALLET -> Icons.Default.AccountBalanceWallet
        route == Routes.SETTINGS || route == Routes.GRAPHICS_SETTINGS || route == Routes.PRIVACY_SETTINGS -> Icons.Default.Settings
        route == Routes.THEME_PICKER -> Icons.Default.Palette
        else -> Icons.Default.Menu
    }
}
