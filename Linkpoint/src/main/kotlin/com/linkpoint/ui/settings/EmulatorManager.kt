package com.linkpoint.ui.settings

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * Android Emulator Management Integration
 * 
 * Provides integration between the app settings and the emulator_manager.sh script
 * for CLI-based Android emulator management functionality.
 * 
 * Modernized with Kotlin coroutines instead of deprecated AsyncTask
 */
class EmulatorManager(private val context: Context) {
    
    companion object {
        private const val TAG = "EmulatorManager"
        
        /**
         * Default configuration for AVDs
         */
        object Defaults {
            const val DEFAULT_DEVICE = "pixel_2"
            const val DEFAULT_API = "34"
            const val DEFAULT_ABI = "x86_64"
            const val DEFAULT_TAG = "google_apis"
            
            val SUPPORTED_APIS = arrayOf("30", "31", "32", "33", "34")
            val SUPPORTED_ABIS = arrayOf("x86_64", "arm64-v8a", "x86")
            val SUPPORTED_TAGS = arrayOf("google_apis", "google_apis_playstore", "default")
            val POPULAR_DEVICES = arrayOf("pixel_2", "pixel_3", "pixel_4", "pixel_5", "Nexus_5X")
        }
        
        /**
         * Parse AVD list output
         */
        fun parseAVDList(output: String): List<AVDInfo> {
            val avds = mutableListOf<AVDInfo>()
            val lines = output.split("\n")
            
            for (line in lines) {
                if (line.contains("|")) {
                    val parts = line.split("|").map { it.trim() }
                    if (parts.size >= 5) {
                        avds.add(
                            AVDInfo(
                                name = parts[0],
                                device = parts[1],
                                apiLevel = parts[2],
                                abi = parts[3],
                                status = parts[4]
                            )
                        )
                    }
                }
            }
            
            return avds
        }
        
        /**
         * Format AVD list summary
         */
        fun formatAVDSummary(avds: List<AVDInfo>): String {
            return buildString {
                appendLine("Available AVDs: ${avds.size}")
                avds.forEach { avd ->
                    appendLine("  • ${avd.name} (API ${avd.apiLevel}, ${avd.abi}) - ${avd.status}")
                }
            }
        }
    }
    
    private val scriptPath: File
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    init {
        // Find the script path relative to the app
        // In a real deployment, this would be in assets or internal storage
        // For development, we use the project structure
        val projectRoot = File(context.filesDir.parent, "../../../")
        scriptPath = File(projectRoot, "scripts/emulator_manager.sh")
        
        Log.d(TAG, "EmulatorManager initialized with script path: ${scriptPath.absolutePath}")
    }
    
    // Emulator status callback interface
    interface EmulatorCallback {
        fun onSuccess(message: String)
        fun onError(error: String)
        fun onStatusUpdate(status: String)
    }
    
    // AVD information data class
    data class AVDInfo(
        val name: String,
        val device: String,
        val apiLevel: String,
        val abi: String,
        val status: String
    ) {
        override fun toString(): String = "$name (API $apiLevel, $abi)"
    }
    
    /**
     * Check if emulator management is available
     */
    fun isAvailable(): Boolean {
        // Check if Android SDK is available
        val androidHome = System.getenv("ANDROID_HOME")
        if (androidHome.isNullOrEmpty()) {
            return false
        }
        
        // Check if script exists
        if (!scriptPath.exists() || !scriptPath.canExecute()) {
            Log.w(TAG, "Emulator script not found or not executable: ${scriptPath.absolutePath}")
            return false
        }
        
        return true
    }
    
    /**
     * List all available AVDs
     */
    fun listAVDs(callback: EmulatorCallback) {
        executeCommand(arrayOf("list-avds"), callback)
    }
    
    /**
     * Create an AVD
     */
    fun createAVD(
        name: String,
        device: String,
        apiLevel: String,
        abi: String,
        callback: EmulatorCallback
    ) {
        val args = arrayOf("create", name, "--device", device, "--api", apiLevel, "--abi", abi)
        executeCommand(args, callback)
    }
    
    /**
     * Start an AVD
     */
    fun startAVD(name: String, callback: EmulatorCallback) {
        executeCommand(arrayOf("start", name), callback)
    }
    
    /**
     * Stop an AVD
     */
    fun stopAVD(name: String, callback: EmulatorCallback) {
        executeCommand(arrayOf("stop", name), callback)
    }
    
    /**
     * Delete an AVD
     */
    fun deleteAVD(name: String, callback: EmulatorCallback) {
        executeCommand(arrayOf("delete", name), callback)
    }
    
    /**
     * Get emulator status
     */
    fun getStatus(callback: EmulatorCallback) {
        executeCommand(arrayOf("status"), callback)
    }
    
    /**
     * Install system image
     */
    fun installSystemImage(apiLevel: String, abi: String, tag: String, callback: EmulatorCallback) {
        val args = arrayOf("install-image", apiLevel, abi, tag)
        executeCommand(args, callback)
    }
    
    /**
     * List available device profiles
     */
    fun listDevices(callback: EmulatorCallback) {
        executeCommand(arrayOf("list-devices"), callback)
    }
    
    /**
     * List available system images
     */
    fun listSystemImages(callback: EmulatorCallback) {
        executeCommand(arrayOf("list-images"), callback)
    }
    
    /**
     * Execute emulator command using coroutines (modern approach)
     */
    private fun executeCommand(args: Array<String>, callback: EmulatorCallback) {
        scope.launch {
            try {
                // Update status on main thread
                withContext(Dispatchers.Main) {
                    callback.onStatusUpdate("Executing emulator command...")
                }
                
                // Build command array
                val command = arrayOf(scriptPath.absolutePath) + args
                
                // Execute command in IO context
                val result = withContext(Dispatchers.IO) {
                    val processBuilder = ProcessBuilder(*command)
                    processBuilder.redirectErrorStream(true)
                    val process = processBuilder.start()
                    
                    val output = StringBuilder()
                    var hasError = false
                    
                    BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                        reader.lineSequence().forEach { line ->
                            Log.d(TAG, "Emulator output: $line")
                            output.appendLine(line)
                            
                            // Check for errors
                            if (line.contains("ERROR", ignoreCase = true) ||
                                line.contains("FAILED", ignoreCase = true)) {
                                hasError = true
                            }
                            
                            // Update status on main thread
                            launch(Dispatchers.Main) {
                                callback.onStatusUpdate(line)
                            }
                        }
                    }
                    
                    val exitCode = process.waitFor()
                    
                    Pair(output.toString(), hasError || exitCode != 0)
                }
                
                // Report result on main thread
                withContext(Dispatchers.Main) {
                    if (result.second) {
                        callback.onError(result.first.ifEmpty { "Command failed with no output" })
                    } else {
                        callback.onSuccess(result.first)
                    }
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error executing emulator command", e)
                withContext(Dispatchers.Main) {
                    callback.onError("Command execution failed: ${e.message}")
                }
            }
        }
    }
    
    /**
     * Cancel all pending operations
     */
    fun cancelAll() {
        scope.coroutineContext.cancelChildren()
    }
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        scope.cancel()
    }
}
