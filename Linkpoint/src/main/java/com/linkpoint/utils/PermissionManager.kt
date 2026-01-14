package com.linkpoint.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

/**
 * Centralized permission management for Linkpoint app.
 * 
 * Handles runtime permissions for:
 * - Storage (for logs and cache)
 * - Microphone (for voice chat)
 * - Bluetooth/Nearby Devices (for voice chat headsets)
 * - Notifications (for Android 13+)
 * - Camera (optional, for profile pictures)
 */
class PermissionManager(private val activity: AppCompatActivity) {
    
    companion object {
        private const val TAG = "PermissionManager"
        private const val PREFS_NAME = "permission_prefs"
        private const val KEY_PERMISSIONS_REQUESTED = "permissions_requested"
        
        /**
         * Essential permissions required for the app to function properly
         */
        fun getEssentialPermissions(): Array<String> {
            val permissions = mutableListOf<String>()
            
            // Storage permissions based on Android version
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            // Android 13+ uses scoped storage, no permission needed for app-specific dirs
            
            // Notification permission for Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }
            
            return permissions.toTypedArray()
        }
        
        /**
         * Voice chat permissions
         */
        fun getVoiceChatPermissions(): Array<String> {
            val permissions = mutableListOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
            )
            
            // Bluetooth permissions based on Android version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            } else {
                permissions.add(Manifest.permission.BLUETOOTH)
            }
            
            return permissions.toTypedArray()
        }
        
        /**
         * All permissions the app might need
         */
        fun getAllPermissions(): Array<String> {
            return getEssentialPermissions() + getVoiceChatPermissions()
        }
        
        /**
         * Check if a specific permission is granted
         */
        fun isPermissionGranted(context: Context, permission: String): Boolean {
            return ContextCompat.checkSelfPermission(context, permission) == 
                PackageManager.PERMISSION_GRANTED
        }
        
        /**
         * Check if all essential permissions are granted
         */
        fun areEssentialPermissionsGranted(context: Context): Boolean {
            return getEssentialPermissions().all { isPermissionGranted(context, it) }
        }
        
        /**
         * Check if voice chat permissions are granted
         */
        fun areVoiceChatPermissionsGranted(context: Context): Boolean {
            return getVoiceChatPermissions().all { isPermissionGranted(context, it) }
        }
    }
    
    // Permission request launcher
    private var permissionLauncher: ActivityResultLauncher<Array<String>>? = null
    private var onPermissionResult: ((Map<String, Boolean>) -> Unit)? = null
    
    /**
     * Register the permission launcher. Must be called in onCreate before any permission requests.
     */
    fun registerPermissionLauncher(
        launcher: ActivityResultLauncher<Array<String>>
    ) {
        permissionLauncher = launcher
    }
    
    /**
     * Create a permission launcher for use with ActivityResultContracts
     */
    fun createPermissionLauncher(): ActivityResultLauncher<Array<String>> {
        return activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            handlePermissionResults(permissions)
        }
    }
    
    /**
     * Request essential permissions on app startup
     */
    fun requestEssentialPermissions(
        onResult: (allGranted: Boolean, denied: List<String>) -> Unit
    ) {
        requestPermissions(getEssentialPermissions()) { results ->
            val denied = results.filter { !it.value }.keys.toList()
            onResult(denied.isEmpty(), denied)
        }
    }
    
    /**
     * Request voice chat permissions when user wants to use voice
     */
    fun requestVoiceChatPermissions(
        onResult: (allGranted: Boolean, denied: List<String>) -> Unit
    ) {
        requestPermissions(getVoiceChatPermissions()) { results ->
            val denied = results.filter { !it.value }.keys.toList()
            onResult(denied.isEmpty(), denied)
        }
    }
    
    /**
     * Request specific permissions
     */
    fun requestPermissions(
        permissions: Array<String>,
        onResult: (Map<String, Boolean>) -> Unit
    ) {
        // Filter out already granted permissions
        val permissionsToRequest = permissions.filter { 
            !isPermissionGranted(activity, it) 
        }.toTypedArray()
        
        if (permissionsToRequest.isEmpty()) {
            // All permissions already granted
            onResult(permissions.associateWith { true })
            return
        }
        
        onPermissionResult = onResult
        
        permissionLauncher?.launch(permissionsToRequest)
            ?: Log.e(TAG, "Permission launcher not registered! Call registerPermissionLauncher first.")
    }
    
    private fun handlePermissionResults(results: Map<String, Boolean>) {
        val granted = results.filter { it.value }.keys
        val denied = results.filter { !it.value }.keys
        
        if (granted.isNotEmpty()) {
            Log.i(TAG, "Permissions granted: $granted")
        }
        if (denied.isNotEmpty()) {
            Log.w(TAG, "Permissions denied: $denied")
        }
        
        onPermissionResult?.invoke(results)
        onPermissionResult = null
    }
    
    /**
     * Show rationale dialog explaining why permissions are needed
     */
    fun showPermissionRationale(
        title: String,
        message: String,
        onAccept: () -> Unit,
        onDeny: () -> Unit
    ) {
        AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Grant") { _, _ -> onAccept() }
            .setNegativeButton("Deny") { _, _ -> onDeny() }
            .setCancelable(false)
            .show()
    }
    
    /**
     * Show microphone permission rationale
     */
    fun showMicrophoneRationale(onAccept: () -> Unit, onDeny: () -> Unit) {
        showPermissionRationale(
            title = "Microphone Permission Required",
            message = "Linkpoint needs microphone access for voice chat in Second Life. " +
                "Without this permission, you won't be able to talk with other residents.",
            onAccept = onAccept,
            onDeny = onDeny
        )
    }
    
    /**
     * Show storage permission rationale
     */
    fun showStorageRationale(onAccept: () -> Unit, onDeny: () -> Unit) {
        showPermissionRationale(
            title = "Storage Permission Required",
            message = "Linkpoint needs storage access to save login logs, cache textures, " +
                "and store your chat history. This improves performance and lets you review logs.",
            onAccept = onAccept,
            onDeny = onDeny
        )
    }
    
    /**
     * Show Bluetooth permission rationale
     */
    fun showBluetoothRationale(onAccept: () -> Unit, onDeny: () -> Unit) {
        showPermissionRationale(
            title = "Bluetooth Permission Required",
            message = "Linkpoint needs Bluetooth access to use wireless headsets and " +
                "audio devices for voice chat.",
            onAccept = onAccept,
            onDeny = onDeny
        )
    }
    
    /**
     * Check if we should show rationale for a permission
     */
    fun shouldShowRationale(permission: String): Boolean {
        return activity.shouldShowRequestPermissionRationale(permission)
    }
    
    /**
     * Get human-readable permission name
     */
    fun getPermissionDisplayName(permission: String): String {
        return when (permission) {
            Manifest.permission.RECORD_AUDIO -> "Microphone"
            Manifest.permission.CAMERA -> "Camera"
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE -> "Storage"
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_CONNECT -> "Bluetooth"
            Manifest.permission.POST_NOTIFICATIONS -> "Notifications"
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION -> "Location"
            else -> permission.substringAfterLast(".")
        }
    }
    
    /**
     * Check and request all startup permissions
     */
    fun checkAndRequestStartupPermissions(
        onComplete: (success: Boolean) -> Unit
    ) {
        val prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        // Check if we've already requested on first launch
        val alreadyRequested = prefs.getBoolean(KEY_PERMISSIONS_REQUESTED, false)
        
        val essentialPermissions = getEssentialPermissions()
        val missingPermissions = essentialPermissions.filter { 
            !isPermissionGranted(activity, it) 
        }
        
        if (missingPermissions.isEmpty()) {
            Log.i(TAG, "All essential permissions already granted")
            onComplete(true)
            return
        }
        
        if (!alreadyRequested) {
            // First time - request without rationale
            prefs.edit().putBoolean(KEY_PERMISSIONS_REQUESTED, true).apply()
            requestEssentialPermissions { allGranted, _ ->
                onComplete(allGranted)
            }
        } else {
            // Already requested before - just continue
            Log.i(TAG, "Permissions already requested once, continuing with current state")
            onComplete(areEssentialPermissionsGranted(activity))
        }
    }
}
