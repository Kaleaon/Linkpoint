package com.linkpoint.ui.navigation

import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import com.linkpoint.ui.adaptive.WindowSizeClass
import com.linkpoint.ui.adaptive.calculateWindowSizeClass
import com.linkpoint.ui.linkpoint2.screens.Linkpoint2BottomTabs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LinkpointAppShellTest {

    private fun resolveLayoutType(
        currentRoute: String?,
        windowSizeClass: WindowSizeClass
    ): NavigationSuiteType {
        val metadata = resolveRouteMetadata(currentRoute)
        val showChrome = metadata.menuPlacement != MenuPlacement.NONE
        return when {
            !showChrome -> NavigationSuiteType.None
            windowSizeClass.isCompact -> NavigationSuiteType.NavigationBar
            windowSizeClass.isMedium -> NavigationSuiteType.NavigationRail
            else -> NavigationSuiteType.NavigationDrawer
        }
    }

    @Test
    fun testLayoutTypeSwitchingForCompactViewport() {
        val compactClass = calculateWindowSizeClass(360)
        assertTrue(compactClass.isCompact)

        val layoutType = resolveLayoutType(Routes.CHAT, compactClass)
        assertEquals(NavigationSuiteType.NavigationBar, layoutType)
    }

    @Test
    fun testLayoutTypeSwitchingForMediumViewport() {
        val mediumClass = calculateWindowSizeClass(720)
        assertTrue(mediumClass.isMedium)

        val layoutType = resolveLayoutType(Routes.CHAT, mediumClass)
        assertEquals(NavigationSuiteType.NavigationRail, layoutType)
    }

    @Test
    fun testLayoutTypeSwitchingForExpandedViewport() {
        val expandedClass = calculateWindowSizeClass(1080)
        assertTrue(expandedClass.isExpanded)

        val layoutType = resolveLayoutType(Routes.CHAT, expandedClass)
        assertEquals(NavigationSuiteType.NavigationDrawer, layoutType)
    }

    @Test
    fun testNonChromeRoutesSuppressNavigationChromeAcrossAllViewports() {
        val noneRoutes = listOf(Routes.LOGIN, Routes.TOS, Routes.SLURL, Routes.PROFILE)
        val sizes = listOf(
            calculateWindowSizeClass(360),
            calculateWindowSizeClass(720),
            calculateWindowSizeClass(1080)
        )

        for (route in noneRoutes) {
            for (size in sizes) {
                val layoutType = resolveLayoutType(route, size)
                assertEquals(
                    "Route $route on size $size should suppress chrome (None)",
                    NavigationSuiteType.None,
                    layoutType
                )
            }
        }
    }

    @Test
    fun testPrimaryTabsAndDrawerSectionsIntegrity() {
        assertEquals(5, Linkpoint2BottomTabs.size)
        assertTrue(LinkpointMenus.drawerSections.isNotEmpty())

        val tabRoutes = Linkpoint2BottomTabs.map { it.id }.toSet()
        val drawerRoutes = LinkpointMenus.drawerSections.flatMap { it.items }.map { it.route }.toSet()

        // Verify primary tabs match bottomTabs
        for (tab in tabRoutes) {
            assertNotNull("Tab $tab has valid metadata", resolveRouteMetadata(tab))
        }

        // Verify secondary drawer routes resolve
        for (drawerRoute in drawerRoutes) {
            assertNotNull("Drawer route $drawerRoute has valid metadata", resolveRouteMetadata(drawerRoute))
        }
    }
}
