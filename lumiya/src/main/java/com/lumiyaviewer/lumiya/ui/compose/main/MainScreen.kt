package com.lumiyaviewer.lumiya.ui.compose.main

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

/**
 * Main Screen with Bottom Navigation
 * 
 * Features:
 * - Bottom navigation bar
 * - Multiple screens (World, Chat, Friends, Inventory, Profile)
 * - Smooth navigation transitions
 * - Material Design 3
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLogout: () -> Unit,
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (currentDestination?.route) {
                            MainDestination.World.route -> "World"
                            MainDestination.Chat.route -> "Chat"
                            MainDestination.Friends.route -> "Friends"
                            MainDestination.Inventory.route -> "Inventory"
                            MainDestination.Profile.route -> "Profile"
                            else -> "Linkpoint"
                        }
                    )
                   },
                   actions = {
                       IconButton(onClick = onSettingsClick) {
                           Icon(
                               imageVector = Icons.Default.Settings,
                               contentDescription = "Settings"
                           )
                       }
                   },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                MainDestination.values().forEach { destination ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label
                            )
                        },
                        label = { Text(destination.label) },
                        selected = currentDestination?.hierarchy?.any {
                            it.route == destination.route
                        } == true,
                        onClick = {
                            navController.navigate(destination.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = MainDestination.World.route,
            modifier = modifier.padding(paddingValues)
        ) {
            composable(
                route = MainDestination.World.route,
                enterTransition = { fadeIn() + slideInHorizontally() },
                exitTransition = { fadeOut() + slideOutHorizontally() }
            ) {
                WorldScreen()
            }

            composable(
                route = MainDestination.Chat.route,
                enterTransition = { fadeIn() + slideInHorizontally() },
                exitTransition = { fadeOut() + slideOutHorizontally() }
            ) {
                ChatScreen()
            }

            composable(
                route = MainDestination.Friends.route,
                enterTransition = { fadeIn() + slideInHorizontally() },
                exitTransition = { fadeOut() + slideOutHorizontally() }
            ) {
                FriendsScreen()
            }

            composable(
                route = MainDestination.Inventory.route,
                enterTransition = { fadeIn() + slideInHorizontally() },
                exitTransition = { fadeOut() + slideOutHorizontally() }
            ) {
                InventoryScreen()
            }

            composable(
                route = MainDestination.Profile.route,
                enterTransition = { fadeIn() + slideInHorizontally() },
                exitTransition = { fadeOut() + slideOutHorizontally() }
            ) {
                ProfileScreen(onLogout = onLogout)
            }
        }
    }
}

/**
 * Main navigation destinations
 */
enum class MainDestination(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    World("world", Icons.Default.Public, "World"),
    Chat("chat", Icons.Default.Chat, "Chat"),
    Friends("friends", Icons.Default.People, "Friends"),
    Inventory("inventory", Icons.Default.Inventory, "Inventory"),
    Profile("profile", Icons.Default.Person, "Profile")
}

/**
 * Chat Screen
 */
@Composable
fun ChatScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Chat",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Chat conversations will be displayed here",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Friends Screen
 */
@Composable
fun FriendsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.People,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Friends",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your friends list will be displayed here",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Inventory Screen
 */
@Composable
fun InventoryScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Inventory,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Inventory",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your inventory items will be displayed here",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Profile Screen
 */
@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Your profile information will be displayed here",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout")
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}