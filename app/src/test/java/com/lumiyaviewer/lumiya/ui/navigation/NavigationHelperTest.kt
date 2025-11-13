package com.lumiyaviewer.lumiya.ui.navigation

import com.lumiyaviewer.lumiya.TestingFramework
import com.lumiyaviewer.lumiya.ui.compose.navigation.NavigationHelper
import com.lumiyaviewer.lumiya.ui.compose.navigation.NavigationRoutes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for NavigationHelper
 * 
 * Tests:
 * - Route navigation
 * - Deep link parsing
 * - Back stack management
 * - Navigation state
 */
@ExperimentalCoroutinesApi
class NavigationHelperTest {
    
    @get:Rule
    val mainDispatcherRule = TestingFramework.MainDispatcherRule()
    
    private lateinit var navigationHelper: NavigationHelper
    
    @Before
    fun setup() {
        navigationHelper = NavigationHelper()
    }
    
    @Test
    fun `parseDeepLink extracts route correctly`() {
        val deepLink = "linkpoint://app/chat/123"
        
        val route = navigationHelper.parseDeepLink(deepLink)
        
        assertEquals("chat/123", route)
    }
    
    @Test
    fun `parseDeepLink handles invalid links`() {
        val deepLink = "invalid://link"
        
        val route = navigationHelper.parseDeepLink(deepLink)
        
        assertEquals(NavigationRoutes.MAIN, route)
    }
    
    @Test
    fun `buildChatRoute creates correct route`() {
        val chatterId = 123L
        
        val route = navigationHelper.buildChatRoute(chatterId)
        
        assertEquals("${NavigationRoutes.CHAT}/123", route)
    }
    
    @Test
    fun `buildProfileRoute creates correct route`() {
        val userId = 456L
        
        val route = navigationHelper.buildProfileRoute(userId)
        
        assertEquals("${NavigationRoutes.PROFILE}/456", route)
    }
    
    @Test
    fun `isValidRoute returns true for valid routes`() {
        assertTrue(navigationHelper.isValidRoute(NavigationRoutes.MAIN))
        assertTrue(navigationHelper.isValidRoute(NavigationRoutes.CHAT))
        assertTrue(navigationHelper.isValidRoute(NavigationRoutes.SETTINGS))
    }
}