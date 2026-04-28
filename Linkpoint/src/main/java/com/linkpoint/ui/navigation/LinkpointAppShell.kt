package com.linkpoint.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.linkpoint.ui.components.linkpoint2.primitives.L2Tab
import com.linkpoint.ui.components.linkpoint2.primitives.L2TabBar
import com.linkpoint.ui.components.linkpoint2.primitives.L2TopBar
import com.linkpoint.ui.linkpoint2.screens.Linkpoint2BottomTabs
import kotlinx.coroutines.launch

/**
 * Linkpoint 2.0 top-level shell — drawer + L2TopBar + L2TabBar.
 *
 * Routes flagged with [MenuPlacement.NONE] (e.g. login, modal screens, world)
 * suppress the L2 chrome and let the route own the full surface. Everything
 * else gets the unified Linkpoint 2.0 chrome.
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

    val l2Tabs: List<L2Tab> = Linkpoint2BottomTabs.takeIf { it.isNotEmpty() }
        ?: bottomTabs.map {
            L2Tab(
                id = it.route,
                label = it.label,
                icon = Icons.Default.Menu,
            )
        }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showChrome,
        drawerContent = {
            if (drawerSections.isNotEmpty()) {
                ModalDrawerSheet {
                    drawerSections.forEach { section ->
                        Text(
                            text = section.title,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            style = MaterialTheme.typography.titleSmall,
                        )
                        section.items.forEach { item ->
                            androidx.compose.material3.NavigationDrawerItem(
                                label = { Text(item.label) },
                                selected = currentRoute == item.route,
                                onClick = {
                                    navController.navigateTo(item.route)
                                    scope.launch { drawerState.close() }
                                },
                            )
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        },
    ) {
        Scaffold(
            topBar = {
                if (showChrome) {
                    L2TopBar(
                        title = title,
                        subtitle = subtitle,
                        leading = if (drawerSections.isNotEmpty()) {
                            {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                                }
                            }
                        } else null,
                    )
                }
            },
            bottomBar = {
                if (showChrome && l2Tabs.isNotEmpty()) {
                    L2TabBar(
                        tabs = l2Tabs,
                        activeId = currentRoute ?: l2Tabs.first().id,
                        onSelect = { id -> navController.navigateTo(id) },
                    )
                }
            },
        ) { paddingValues ->
            content(Modifier.padding(paddingValues))
        }
    }
}
