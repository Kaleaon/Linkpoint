package com.linkpoint.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkpointAppShell(
    navController: NavHostController,
    currentRoute: String?,
    title: String,
    subtitle: String?,
    bottomTabs: List<LinkpointMenuDestination> = LinkpointMenus.bottomTabs,
    drawerSections: List<LinkpointDrawerSection> = LinkpointMenus.drawerSections,
    content: @Composable (Modifier) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            if (drawerSections.isNotEmpty()) {
                ModalDrawerSheet {
                    drawerSections.forEach { section ->
                        Text(
                            text = section.title,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            style = MaterialTheme.typography.titleSmall
                        )
                        section.items.forEach { item ->
                            androidx.compose.material3.NavigationDrawerItem(
                                label = { Text(item.label) },
                                selected = currentRoute == item.route,
                                onClick = {
                                    navController.navigateTo(item.route)
                                    scope.launch { drawerState.close() }
                                }
                            )
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(text = title)
                            if (!subtitle.isNullOrBlank()) {
                                Text(
                                    text = subtitle,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        if (drawerSections.isNotEmpty()) {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Text("≡")
                            }
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    bottomTabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = { navController.navigateTo(tab.route) },
                            icon = { Text(tab.label.take(1)) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        ) { paddingValues ->
            content(Modifier.padding(paddingValues))
        }
    }
}
