package com.linkpoint.ui.linkpoint2.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Work
import com.linkpoint.ui.components.linkpoint2.primitives.L2Tab
import com.linkpoint.ui.navigation.Routes

/**
 * Stable mapping from [Routes] to the 5-tab L2 bottom bar.
 *
 * This is the single source of truth for which routes are bottom tabs in
 * Linkpoint 2.0; the order matches the JSX TabBar in design/primitives.jsx.
 */
val Linkpoint2BottomTabs: List<L2Tab> = listOf(
    L2Tab(id = Routes.CHAT, label = "Chat", icon = Icons.AutoMirrored.Filled.Chat),
    L2Tab(id = Routes.WORLD, label = "World", icon = Icons.Default.Public),
    L2Tab(id = Routes.MAP, label = "Map", icon = Icons.Default.Map),
    L2Tab(id = Routes.INVENTORY, label = "Inventory", icon = Icons.Default.Work),
    L2Tab(id = Routes.MY_PROFILE, label = "Me", icon = Icons.Default.Person),
)
