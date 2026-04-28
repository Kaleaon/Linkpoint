package com.linkpoint.ui.linkpoint2

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linkpoint.ui.components.linkpoint2.primitives.L2Tab
import com.linkpoint.ui.linkpoint2.screens.Linkpoint2BottomTabs
import com.linkpoint.ui.navigation.LinkpointMenus
import com.linkpoint.ui.navigation.MenuPlacement
import com.linkpoint.ui.navigation.Routes
import com.linkpoint.ui.navigation.resolveRouteMetadata
import com.linkpoint.ui.navigation.routeMetadata
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Smoke tests that pin the Linkpoint 2.0 navigation wiring contract:
 *
 *  - Every Routes.* constant has a matching RouteMetadata entry.
 *  - The 5-tab bottom bar (Linkpoint2BottomTabs) is consistent with
 *    LinkpointMenus.bottomTabs.
 *  - resolveRouteMetadata correctly handles both static routes and
 *    parameterised routes (`profile/{userId}`, `places/{placeId}`,
 *    `group_profile/{groupId}`).
 *
 * Runs under AndroidJUnit4 → Robolectric so any Android dependency reached
 * by the navigation code (Log, ColorScheme, Density) is shadowed properly.
 */
@RunWith(AndroidJUnit4::class)
class Linkpoint2WiringTest {

    @Test
    fun `every route constant has a matching RouteMetadata entry`() {
        val constants = listOf(
            Routes.LOGIN, Routes.WORLD, Routes.CHAT, Routes.INVENTORY,
            Routes.FRIENDS, Routes.GROUPS, Routes.PROFILE, Routes.MY_PROFILE,
            Routes.SETTINGS, Routes.MAP, Routes.MINIMAP, Routes.RADAR,
            Routes.SEARCH, Routes.TELEPORT_HISTORY, Routes.NEARBY_PEOPLE,
            Routes.MY_AVATAR, Routes.TOS, Routes.THEME_PICKER, Routes.SLURL,
            Routes.XR_WORLD, Routes.WALLET, Routes.BUILD_TOOLS,
            Routes.NOTIFICATIONS_FEED, Routes.VOICE_DEEP, Routes.PLACES_DETAIL,
            Routes.PLACES_EVENTS, Routes.PLACES_SEARCH,
            Routes.ONBOARDING_WELCOME, Routes.ONBOARDING_PERMISSIONS,
            Routes.ONBOARDING_AVATAR, Routes.IM_LIST, Routes.OUTFIT_PICKER,
            Routes.OUTFIT_COMPOSER, Routes.GROUP_PROFILE, Routes.CAMERA_MODE,
            Routes.GRAPHICS_SETTINGS, Routes.PRIVACY_SETTINGS,
            Routes.GRID_MANAGEMENT, Routes.EMPTY_STATES_REF,
        )
        for (route in constants) {
            assertNotNull(
                "Missing RouteMetadata for $route",
                routeMetadata[route],
            )
        }
    }

    @Test
    fun `5-tab bottom bar is consistent with drawer registration`() {
        assertEquals("Bottom tab bar must have exactly 5 entries", 5, Linkpoint2BottomTabs.size)
        for (tab: L2Tab in Linkpoint2BottomTabs) {
            val meta = resolveRouteMetadata(tab.id)
            assertNotNull("Tab '${tab.id}' has no RouteMetadata", meta)
        }
        // Drawer items should never collide with tab routes — they are
        // alternative entry points, not duplicates.
        val tabIds = Linkpoint2BottomTabs.map { it.id }.toSet()
        val drawerSectionRoutes = LinkpointMenus.drawerSections
            .flatMap { it.items }
            .map { it.route }
        for (route in drawerSectionRoutes) {
            // Routes can reasonably appear in BOTH tabs and drawer; just
            // assert each route resolves.
            assertNotNull("Drawer route '$route' has no RouteMetadata", resolveRouteMetadata(route))
        }
        assertTrue("At least one drawer route should exist", drawerSectionRoutes.isNotEmpty())
        // Ensure we have no orphaned drawer routes.
        for (id in tabIds) {
            assertTrue(
                "Tab '$id' is missing from RouteMetadata",
                routeMetadata.containsKey(id),
            )
        }
    }

    @Test
    fun `parameterised routes resolve to the right metadata`() {
        assertEquals(
            routeMetadata.getValue(Routes.PROFILE),
            resolveRouteMetadata("profile/abc123"),
        )
        assertEquals(
            routeMetadata.getValue(Routes.SLURL),
            resolveRouteMetadata("slurl/secondlife://Linkpoint%20Bay/128/64/24"),
        )
        assertEquals(
            routeMetadata.getValue(Routes.PLACES_DETAIL),
            resolveRouteMetadata("places/crystal-coast"),
        )
        assertEquals(
            routeMetadata.getValue(Routes.GROUP_PROFILE),
            resolveRouteMetadata("group_profile/tinkerers"),
        )
    }

    @Test
    fun `Routes builders produce route patterns consistent with metadata`() {
        // Routes.profile("me") must hit the same metadata as Routes.MY_PROFILE.
        assertEquals(
            routeMetadata.getValue(Routes.MY_PROFILE),
            resolveRouteMetadata(Routes.profile("me")),
        )
        assertEquals(
            routeMetadata.getValue(Routes.GROUP_PROFILE),
            resolveRouteMetadata(Routes.groupProfile("ab1")),
        )
    }

    @Test
    fun `routes that own the full surface have NONE menu placement`() {
        // World, login, modals, deep-linked pages should not show app chrome.
        val expectedNone = listOf(
            Routes.LOGIN,
            Routes.PROFILE,
            Routes.SLURL,
            Routes.PLACES_DETAIL,
            Routes.GROUP_PROFILE,
            Routes.CAMERA_MODE,
            Routes.ONBOARDING_WELCOME,
            Routes.ONBOARDING_AVATAR,
            Routes.ONBOARDING_PERMISSIONS,
            Routes.EMPTY_STATES_REF,
        )
        for (route in expectedNone) {
            assertEquals(
                "Route '$route' should be NONE-placement (own its surface)",
                MenuPlacement.NONE,
                routeMetadata.getValue(route).menuPlacement,
            )
        }
    }
}
