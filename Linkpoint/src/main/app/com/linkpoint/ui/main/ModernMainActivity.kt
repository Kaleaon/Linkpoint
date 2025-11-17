package com.linkpoint.ui.main
```suggestion
import android.content.Intent
import android.os.Bundle
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.linkpoint.ui.compose.login.LinkpointTheme
import com.linkpoint.ui.compose.login.ModernLoginActivity
import com.linkpoint.ui.compose.main.MainScreen
import dagger.hilt.android.AndroidEntryPoint

/**
 * Modern main activity for Linkpoint using Jetpack Compose
 * 
 * Features:
 * - Bottom navigation
 * - Multiple screens (World, Chat, Friends, Inventory, Profile)
 * - Material Design 3
 * - Smooth transitions
 */
@AndroidEntryPoint
class ModernMainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            LinkpointTheme {
                MainScreen(
                    onLogout = { logout() }
                )
            }
        }
    }
    
    private fun logout() {
        // Clear session and navigate to login
        val intent = Intent(this, ModernLoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}