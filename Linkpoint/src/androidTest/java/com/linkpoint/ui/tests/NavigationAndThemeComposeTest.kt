package com.linkpoint.ui.tests

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.linkpoint.ui.theme.BuiltInThemes
import com.linkpoint.ui.theme.LinkpointTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NavigationAndThemeComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun bottom_tab_routing_updates_selected_route() {
        composeRule.setContent { BottomTabHarness() }

        composeRule.onNodeWithText("Route: home").assertIsDisplayed()
        composeRule.onNodeWithText("Chat").performClick()
        composeRule.onNodeWithText("Route: chat").assertIsDisplayed()
        composeRule.onNodeWithText("Map").performClick()
        composeRule.onNodeWithText("Route: map").assertIsDisplayed()
    }

    @Test
    fun drawer_section_routing_updates_selected_route() {
        composeRule.setContent { DrawerHarness() }

        composeRule.onNodeWithContentDescription("Open drawer").performClick()
        composeRule.onNodeWithText("Inventory").performClick()
        composeRule.onNodeWithText("Section: inventory").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Open drawer").performClick()
        composeRule.onNodeWithText("Groups").performClick()
        composeRule.onNodeWithText("Section: groups").assertIsDisplayed()
    }

    @Test
    fun primary_controls_apply_theme_primary_color() {
        composeRule.setContent {
            LinkpointTheme(themePack = BuiltInThemes.FIRESTORM) {
                androidx.compose.material3.Button(
                    onClick = {},
                    modifier = Modifier.testTag("primary_button")
                ) {
                    Text("Primary")
                }
            }
        }

        val image = composeRule.onNodeWithTag("primary_button").captureToImage()
        val primaryArgb = BuiltInThemes.FIRESTORM.parseColor(BuiltInThemes.FIRESTORM.colorPrimary)
        val pixelMap = image.toPixelMap()

        var foundPrimary = false
        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                if (pixelMap[x, y].toArgb() == primaryArgb) {
                    foundPrimary = true
                    break
                }
            }
            if (foundPrimary) break
        }

        assertTrue("Expected primary button to render FIRESTORM primary color", foundPrimary)
    }
}

@Composable
private fun BottomTabHarness() {
    var selectedRoute by remember { mutableStateOf("home") }
    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf(
                    "home" to ("Home" to Icons.Default.Home),
                    "chat" to ("Chat" to Icons.Default.Chat),
                    "map" to ("Map" to Icons.Default.Map)
                ).forEach { (route, meta) ->
                    NavigationBarItem(
                        selected = selectedRoute == route,
                        onClick = { selectedRoute = route },
                        label = { Text(meta.first) },
                        icon = { Icon(meta.second, contentDescription = meta.first) }
                    )
                }
            }
        }
    ) {
        Text(
            text = "Route: $selectedRoute",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun DrawerHarness() {
    var selectedSection by remember { mutableStateOf("home") }
    var openDrawer by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerContent = {
            if (openDrawer) {
                ModalDrawerSheet {
                    NavigationDrawerItem(
                        label = { Text("Inventory") },
                        selected = selectedSection == "inventory",
                        onClick = {
                            selectedSection = "inventory"
                            openDrawer = false
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Groups") },
                        selected = selectedSection == "groups",
                        onClick = {
                            selectedSection = "groups"
                            openDrawer = false
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Drawer test") },
                    navigationIcon = {
                        IconButton(onClick = { openDrawer = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Open drawer")
                        }
                    }
                )
            }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text("Section: $selectedSection")
            }
        }
    }
}
