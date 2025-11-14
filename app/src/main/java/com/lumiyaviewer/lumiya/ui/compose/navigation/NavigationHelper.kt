package com.lumiyaviewer.lumiya.ui.compose.navigation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.navOptions

/**
 * Navigation Helper for Linkpoint
 * 
 * Provides utilities for navigation including:
 * - Route definitions
 * - Navigation animations
 * - Deep linking support
 * - Back stack management
 * - Navigation state persistence
 */

/**
 * Navigation Routes
 * 
 * Centralized definition of all navigation routes in the app.
 */
object NavigationRoutes {
    const val LOGIN = "login"
    const val MAIN = "main"
    const val CHAT = "chat"
    const val INVENTORY = "inventory"
    const val WORLD = "world"
    const val SETTINGS = "settings"
    const val PROFILE = "profile/{userId}"
    const val OBJECT_DETAILS = "object/{objectId}"
    const val PLACE_DETAILS = "place/{placeId}"
    
    // Route builders with parameters
    fun profile(userId: String) = "profile/$userId"
    fun objectDetails(objectId: String) = "object/$objectId"
    fun placeDetails(placeId: String) = "place/$placeId"
}

/**
 * Navigation Arguments
 * 
 * Keys for navigation arguments.
 */
object NavigationArgs {
    const val USER_ID = "userId"
    const val OBJECT_ID = "objectId"
    const val PLACE_ID = "placeId"
    const val CHAT_ID = "chatId"
    const val MESSAGE = "message"
    const val TITLE = "title"
}

/**
 * Navigation Animations
 * 
 * Predefined animations for screen transitions.
 */
object NavigationAnimations {
    
    /**
     * Slide in from right animation
     */
    @OptIn(ExperimentalAnimationApi::class)
    fun slideInFromRight(): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(300)
        ) + fadeIn(animationSpec = tween(300))
    }
    
    /**
     * Slide out to left animation
     */
    @OptIn(ExperimentalAnimationApi::class)
    fun slideOutToLeft(): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(300)
        ) + fadeOut(animationSpec = tween(300))
    }
    
    /**
     * Slide in from left animation
     */
    @OptIn(ExperimentalAnimationApi::class)
    fun slideInFromLeft(): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(300)
        ) + fadeIn(animationSpec = tween(300))
    }
    
    /**
     * Slide out to right animation
     */
    @OptIn(ExperimentalAnimationApi::class)
    fun slideOutToRight(): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(300)
        ) + fadeOut(animationSpec = tween(300))
    }
    
    /**
     * Fade in animation
     */
    fun fadeIn(): EnterTransition {
        return fadeIn(animationSpec = tween(300))
    }
    
    /**
     * Fade out animation
     */
    fun fadeOut(): ExitTransition {
        return fadeOut(animationSpec = tween(300))
    }
    
    /**
     * Scale in animation
     */
    @OptIn(ExperimentalAnimationApi::class)
    fun scaleIn(): EnterTransition {
        return scaleIn(
            initialScale = 0.9f,
            animationSpec = tween(300)
        ) + fadeIn(animationSpec = tween(300))
    }
    
    /**
     * Scale out animation
     */
    @OptIn(ExperimentalAnimationApi::class)
    fun scaleOut(): ExitTransition {
        return scaleOut(
            targetScale = 0.9f,
            animationSpec = tween(300)
        ) + fadeOut(animationSpec = tween(300))
    }
}

/**
 * Navigation Helper Class
 * 
 * Provides helper methods for navigation operations.
 */
class NavigationHelper(private val navController: NavHostController) {
    
    /**
     * Navigate to a route with optional arguments
     */
    fun navigateTo(
        route: String,
        args: Bundle? = null,
        clearBackStack: Boolean = false,
        singleTop: Boolean = false
    ) {
        val options = navOptions {
            if (clearBackStack) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
            launchSingleTop = singleTop
            
            // Add animations
            anim {
                enter = android.R.anim.slide_in_left
                exit = android.R.anim.slide_out_right
                popEnter = android.R.anim.slide_in_left
                popExit = android.R.anim.slide_out_right
            }
        }
        
        navController.navigate(route, options)
    }
    
    /**
     * Navigate back
     */
    fun navigateBack(): Boolean {
        return navController.popBackStack()
    }
    
    /**
     * Navigate to login screen (clearing back stack)
     */
    fun navigateToLogin() {
        navigateTo(NavigationRoutes.LOGIN, clearBackStack = true)
    }
    
    /**
     * Navigate to main screen (clearing back stack)
     */
    fun navigateToMain() {
        navigateTo(NavigationRoutes.MAIN, clearBackStack = true)
    }
    
    /**
     * Navigate to chat screen
     */
    fun navigateToChat(chatId: String? = null) {
        val route = if (chatId != null) {
            "${NavigationRoutes.CHAT}?chatId=$chatId"
        } else {
            NavigationRoutes.CHAT
        }
        navigateTo(route)
    }
    
    /**
     * Navigate to inventory screen
     */
    fun navigateToInventory() {
        navigateTo(NavigationRoutes.INVENTORY)
    }
    
    /**
     * Navigate to world screen
     */
    fun navigateToWorld() {
        navigateTo(NavigationRoutes.WORLD)
    }
    
    /**
     * Navigate to settings screen
     */
    fun navigateToSettings() {
        navigateTo(NavigationRoutes.SETTINGS)
    }
    
    /**
     * Navigate to profile screen
     */
    fun navigateToProfile(userId: String) {
        navigateTo(NavigationRoutes.profile(userId))
    }
    
    /**
     * Navigate to object details screen
     */
    fun navigateToObjectDetails(objectId: String) {
        navigateTo(NavigationRoutes.objectDetails(objectId))
    }
    
    /**
     * Navigate to place details screen
     */
    fun navigateToPlaceDetails(placeId: String) {
        navigateTo(NavigationRoutes.placeDetails(placeId))
    }
    
    /**
     * Check if can navigate back
     */
    fun canNavigateBack(): Boolean {
        return navController.previousBackStackEntry != null
    }
    
    /**
     * Get current route
     */
    fun getCurrentRoute(): String? {
        return navController.currentBackStackEntry?.destination?.route
    }
    
    /**
     * Clear back stack up to route
     */
    fun clearBackStackUpTo(route: String, inclusive: Boolean = false) {
        navController.popBackStack(route, inclusive)
    }
}

/**
 * Remember navigation helper
 */
@Composable
fun rememberNavigationHelper(navController: NavHostController): NavigationHelper {
    return remember(navController) {
        NavigationHelper(navController)
    }
}

/**
 * Deep Link Handler
 * 
 * Handles deep links for the app.
 */
object DeepLinkHandler {
    
    private const val SCHEME = "linkpoint"
    private const val HOST = "app"
    
    /**
     * Parse deep link intent
     */
    fun parseDeepLink(intent: Intent): DeepLinkData? {
        val data = intent.data ?: return null
        
        if (data.scheme != SCHEME || data.host != HOST) {
            return null
        }
        
        val path = data.path ?: return null
        val segments = path.split("/").filter { it.isNotEmpty() }
        
        return when (segments.firstOrNull()) {
            "profile" -> segments.getOrNull(1)?.let { 
                DeepLinkData.Profile(it) 
            }
            "object" -> segments.getOrNull(1)?.let { 
                DeepLinkData.ObjectDetails(it) 
            }
            "place" -> segments.getOrNull(1)?.let { 
                DeepLinkData.PlaceDetails(it) 
            }
            "chat" -> segments.getOrNull(1)?.let { 
                DeepLinkData.Chat(it) 
            }
            else -> null
        }
    }
    
    /**
     * Create deep link URI
     */
    fun createDeepLink(data: DeepLinkData): String {
        return when (data) {
            is DeepLinkData.Profile -> "$SCHEME://$HOST/profile/${data.userId}"
            is DeepLinkData.ObjectDetails -> "$SCHEME://$HOST/object/${data.objectId}"
            is DeepLinkData.PlaceDetails -> "$SCHEME://$HOST/place/${data.placeId}"
            is DeepLinkData.Chat -> "$SCHEME://$HOST/chat/${data.chatId}"
        }
    }
    
    /**
     * Handle deep link navigation
     */
    fun handleDeepLink(
        deepLinkData: DeepLinkData,
        navigationHelper: NavigationHelper
    ) {
        when (deepLinkData) {
            is DeepLinkData.Profile -> navigationHelper.navigateToProfile(deepLinkData.userId)
            is DeepLinkData.ObjectDetails -> navigationHelper.navigateToObjectDetails(deepLinkData.objectId)
            is DeepLinkData.PlaceDetails -> navigationHelper.navigateToPlaceDetails(deepLinkData.placeId)
            is DeepLinkData.Chat -> navigationHelper.navigateToChat(deepLinkData.chatId)
        }
    }
}

/**
 * Deep Link Data
 * 
 * Sealed class representing different types of deep links.
 */
sealed class DeepLinkData {
    data class Profile(val userId: String) : DeepLinkData()
    data class ObjectDetails(val objectId: String) : DeepLinkData()
    data class PlaceDetails(val placeId: String) : DeepLinkData()
    data class Chat(val chatId: String) : DeepLinkData()
}

/**
 * Navigation State Saver
 * 
 * Saves and restores navigation state.
 */
object NavigationStateSaver {
    
    private const val PREFS_NAME = "navigation_state"
    private const val KEY_CURRENT_ROUTE = "current_route"
    private const val KEY_BACK_STACK = "back_stack"
    
    /**
     * Save navigation state
     */
    fun saveState(context: Context, navController: NavHostController) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        
        prefs.edit().apply {
            putString(KEY_CURRENT_ROUTE, currentRoute)
            // TODO: Save full back stack if needed
            apply()
        }
    }
    
    /**
     * Restore navigation state
     */
    fun restoreState(context: Context, navController: NavHostController) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedRoute = prefs.getString(KEY_CURRENT_ROUTE, null)
        
        if (savedRoute != null && savedRoute != navController.currentDestination?.route) {
            try {
                navController.navigate(savedRoute)
            } catch (e: Exception) {
                android.util.Log.e("NavigationStateSaver", "Failed to restore route: $savedRoute", e)
            }
        }
    }
    
    /**
     * Clear saved state
     */
    fun clearState(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}