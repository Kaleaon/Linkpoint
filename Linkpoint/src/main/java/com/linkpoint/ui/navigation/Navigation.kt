package com.linkpoint.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
    
    fun profile(userId: String) = "profile/$userId"
    fun slurl(slurl: String) = "slurl/$slurl"
}

/**
 * Extension function to navigate with animation
 */
fun NavHostController.navigateTo(route: String) {
    navigate(route) {
        // Pop up to the start destination to avoid building up a large stack
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Extension function to navigate back
 */
fun NavHostController.navigateBack() {
    popBackStack()
}

/**
 * Main navigation composable that can be used as the app's navigation structure.
 * 
 * Note: This is a framework that can be used when migrating from Activities to 
 * single-Activity + Compose Navigation architecture. Currently the app still uses
 * Activity-based navigation, but these routes and NavHost setup can be adopted
 * incrementally.
 * 
 * Usage in MainActivity:
 * ```kotlin
 * setContent {
 *     LinkpointTheme {
 *         val navController = rememberNavController()
 *         LinkpointNavHost(navController = navController)
 *     }
 * }
 * ```
 */
@Composable
fun LinkpointNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.LOGIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Login screen - entry point
        composable(Routes.LOGIN) {
            // LoginScreen would be called here with proper ViewModel integration
            // For now, this serves as the navigation structure
        }
        
        // Main world view
        composable(Routes.WORLD) {
            // WorldScreen composable
        }
        
        // Chat screen
        composable(Routes.CHAT) {
            // ChatScreen composable
        }
        
        // Inventory screen
        composable(Routes.INVENTORY) {
            // InventoryScreen composable
        }
        
        // Friends screen
        composable(Routes.FRIENDS) {
            // FriendsScreen composable
        }
        
        // Groups screen
        composable(Routes.GROUPS) {
            // GroupsScreen composable
        }
        
        // Profile screen (other users)
        composable(Routes.PROFILE) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            // ProfileScreen with userId
        }
        
        // My profile screen
        composable(Routes.MY_PROFILE) {
            // ProfileScreen for current user
        }
        
        // Settings screen
        composable(Routes.SETTINGS) {
            // SettingsScreen composable
        }
        
        // World map screen
        composable(Routes.MAP) {
            // MapScreen composable
        }
        
        // Minimap screen
        composable(Routes.MINIMAP) {
            // MinimapScreen composable
        }
        
        // Search screen
        composable(Routes.SEARCH) {
            // SearchScreen composable
        }
        
        // Teleport history screen
        composable(Routes.TELEPORT_HISTORY) {
            // TeleportHistoryScreen composable
        }
        
        // Nearby people screen
        composable(Routes.NEARBY_PEOPLE) {
            // NearbyPeopleScreen composable
        }
        
        // My avatar screen
        composable(Routes.MY_AVATAR) {
            // MyAvatarScreen composable
        }
        
        // Terms of Service screen
        composable(Routes.TOS) {
            // TosScreen composable
        }
        
        // Theme picker screen
        composable(Routes.THEME_PICKER) {
            // ThemePickerScreen composable
        }
        
        // SLURL handler
        composable(Routes.SLURL) { backStackEntry ->
            val slurl = backStackEntry.arguments?.getString("slurl") ?: return@composable
            // Handle SLURL navigation
        }
        
        // XR World (VR/AR mode)
        composable(Routes.XR_WORLD) {
            // XRWorldScreen composable
        }
    }
}
