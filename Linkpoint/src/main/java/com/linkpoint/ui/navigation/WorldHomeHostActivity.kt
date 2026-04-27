package com.linkpoint.ui.navigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.linkpoint.ui.theme.LinkpointTheme

/**
 * First migration host activity that adopts the Compose app shell.
 * Legacy activities remain leaf destinations launched from nav routes.
 */
class WorldHomeHostActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LinkpointTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LinkpointNavHost(startDestination = Routes.WORLD)
                }
            }
        }
    }
}
