package com.linkpoint.ui.login

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
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

    companion object {
        private const val TAG = "ComposeLoginActivity"
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results -> onPermissionResults(results) }

    private val tosLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            renderShell()
            requestStartupPermissions()
        } else {
            finishAffinity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!TosActivity.hasAcceptedTos(this)) {
            // TOS gate must complete before we ask for runtime permissions
            // — Android system dialogs stack on top of whatever is showing,
            // and asking for storage/mic before the user has even agreed to
            // use the app comes across as aggressive. The tos launcher
            // callback re-enters this flow once acceptance lands.
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

    /**
     * Compute the set of declared runtime permissions the user has not yet
     * granted and launch the system request dialog for them. Permissions
     * already granted are skipped (the OS would no-op them, but we'd lose
     * the no-pending-request short-circuit below).
     */
    private fun requestStartupPermissions() {
        val pending = PermissionManager.getAllPermissions().filter { p ->
            ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED
        }
        if (pending.isEmpty()) {
            Log.i(TAG, "All startup permissions already granted")
            return
        }
        Log.i(TAG, "Requesting startup permissions: $pending")
        permissionLauncher.launch(pending.toTypedArray())
    }

    /**
     * Process the system permission dialog's result. Storage denial is
     * special-cased: log/cache export to `Documents/Linkpoint Logs/`
     * silently fails without it on API ≤ 32, so we surface a one-shot
     * rationale and let the user re-trigger via Settings if they're sure.
     * Mic/Bluetooth/notifications denial is non-fatal and only matters the
     * first time the dependent feature is used (voice chat, IMs, etc.).
     */
    private fun onPermissionResults(results: Map<String, Boolean>) {
        val granted = results.filter { it.value }.keys
        val denied = results.filter { !it.value }.keys
        if (granted.isNotEmpty()) Log.i(TAG, "Startup permissions granted: $granted")
        if (denied.isEmpty()) return

        Log.w(TAG, "Startup permissions denied: $denied")
        val storagePermissions = PermissionManager.getStoragePermissions().toSet()
        val storageDenied = denied.any { it in storagePermissions }
        if (storageDenied && !PermissionManager.areStoragePermissionsGranted(this)) {
            showStorageDenialDialog()
        }
    }

    private fun showStorageDenialDialog() {
        AlertDialog.Builder(this)
            .setTitle("Storage permission needed")
            .setMessage(
                "Linkpoint stores crash reports and network logs in " +
                    "Documents/Linkpoint Logs/. Without storage access these " +
                    "files won't be saved, which makes troubleshooting harder. " +
                    "You can grant the permission later from System Settings."
            )
            .setPositiveButton("OK", null)
            .setCancelable(true)
            .show()
    }
}
