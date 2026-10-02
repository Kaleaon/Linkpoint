package com.linkpoint.ui.navigation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.linkpoint.ui.theme.LinkpointTheme

/**
 * Primary host activity that adopts the Jetpack Compose app shell and route navigation.
 */
class WorldHomeHostActivity : ComponentActivity() {

    companion object {
        const val EXTRA_ROUTE = "extra_route"
        const val EXTRA_SESSION_ID = "extra_session_id"

        fun createIntent(
            context: Context,
            route: String? = null,
            sessionId: String? = null,
        ): Intent {
            return Intent(context, WorldHomeHostActivity::class.java).apply {
                if (route != null) putExtra(EXTRA_ROUTE, route)
                if (sessionId != null) putExtra(EXTRA_SESSION_ID, sessionId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initialRoute = intent?.getStringExtra(EXTRA_ROUTE) ?: Routes.WORLD

        setContent {
            LinkpointTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LinkpointNavHost(startDestination = initialRoute)
                }
            }
        }
    }
}

