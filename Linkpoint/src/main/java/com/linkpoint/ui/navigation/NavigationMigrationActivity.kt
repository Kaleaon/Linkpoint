package com.linkpoint.ui.navigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.linkpoint.ui.theme.LinkpointTheme

/**
 * Temporary single-Activity Compose host used during navigation migration.
 */
class NavigationMigrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val resolvedRoute = RouteResolver.resolve(intent) ?: Routes.WORLD

        setContent {
            LinkpointTheme {
                LinkpointNavHost(startDestination = resolvedRoute)
            }
        }
    }
}
