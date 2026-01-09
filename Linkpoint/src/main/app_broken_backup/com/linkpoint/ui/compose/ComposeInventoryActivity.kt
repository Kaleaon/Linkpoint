package com.linkpoint.ui.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.linkpoint.ui.theme.LinkpointTheme

/**
 * Activity host for the modern Inventory screen built with Jetpack Compose
 * Provides full Material Design 3 inventory management interface
 */
class ComposeInventoryActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            LinkpointTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    InventoryScreen(navController = navController)
                }
            }
        }
    }
}
