package com.linkpoint.ui.login

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.linkpoint.ui.navigation.WorldHomeHostActivity
import com.linkpoint.ui.theme.LinkpointTheme
import com.linkpoint.ui.tos.TosActivity
import com.linkpoint.utils.PermissionManager

/**
 * Linkpoint 2.0 Compose-first launcher.
 *
 * Replaces the legacy XML LoginActivity. Drives:
 * - First-run Terms of Service acceptance gate
 * - Runtime permission requests (notifications, microphone, storage, etc.)
 * - The same `protocol.login(...)` flow via [L2LoginRoute] embedded in
 *   the Linkpoint 2.0 nav graph at [WorldHomeHostActivity].
 */
class ComposeLoginActivity : AppCompatActivity() {

    private lateinit var permissionManager: PermissionManager
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* results surface in PermissionManager state; no-op here */ }

    private val tosLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            renderShell()
        } else {
            finishAffinity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionManager = PermissionManager(this).also {
            it.registerPermissionLauncher(permissionLauncher)
        }

        if (!TosActivity.hasAcceptedTos(this)) {
            tosLauncher.launch(TosActivity.createIntent(this, requireAcceptance = true))
            return
        }

        renderShell()
        requestStartupPermissions()
    }

    private fun renderShell() {
        setContent {
            LinkpointTheme(darkTheme = true) {
                L2LoginRoute(
                    onLoginSuccess = {
                        startActivity(Intent(this, WorldHomeHostActivity::class.java))
                        finish()
                    },
                    onOpenSettings = {
                        startActivity(
                            Intent(this, com.linkpoint.ui.settings.SettingsActivity::class.java)
                        )
                    },
                )
            }
        }
    }

    private fun requestStartupPermissions() {
        val pending = PermissionManager.getAllPermissions().filter { p ->
            ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED
        }
        if (pending.isNotEmpty()) {
            permissionLauncher.launch(pending.toTypedArray())
        }
    }
}
