package com.lumiyaviewer.lumiya.ui.settings

import android.content.Context
import android.os.AsyncTask
import android.util.Log

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.List

/**
 * Android Emulator Management Integration
 * 
 * Provides integration between the app settings and the emulator_manager.sh script
 * for CLI-based Android emulator management functionality.
 */
class EmulatorManager {
    private val TAG: String = "EmulatorManager"
    
    private Context context
    private File scriptPath
    
    // Emulator status callback interface
    interface EmulatorCallback {
        Unit onSuccess(String message)
        Unit onError(String error)
        Unit onStatusUpdate(String status)
    }
    
    // AVD information class
    class AVDInfo {
        String name
        String device
        String apiLevel
        String abi
        String status
        
        AVDInfo(String name, String device, String apiLevel, String abi, String status) {
            this.name = name
            this.device = device
            this.apiLevel = apiLevel
            this.abi = abi
            this.status = status
        }
        
        @Override
        String toString() {
            return String.format("%s (API %s, %s)", name, apiLevel, abi)
        }
    }
    
    EmulatorManager(Context context) {
        this.context = context
        
        // Find the script path relative to the app
        // In a real deployment, this would be in assets or internal storage
        // For development, we use the project structure
        File projectRoot = File(context.getFilesDir().getParent(), "../../../")
        this.scriptPath = File(projectRoot, "scripts/emulator_manager.sh")
        
        Log.d(TAG, "EmulatorManager initialized with script path: " + scriptPath.getAbsolutePath())
    }
    
    /**
     * Check if emulator management is available
     */
    Boolean isAvailable() {
        // Check if Android SDK is available
        String androidHome = System.getenv("ANDROID_HOME")
        if (androidHome == null || androidHome.isEmpty()) {
            return false
        }
        
        // Check if script exists
        if (!scriptPath.exists() || !scriptPath.canExecute()) {
            Log.w(TAG, "Emulator script not found or not executable: " + scriptPath.getAbsolutePath())
            return false
        }
        
        return true
    }
    
    /**
     * List all available AVDs
     */
    Unit listAVDs(EmulatorCallback callback) {
        EmulatorTask("list-avds", callback).execute()
    }
    
    /**
     * Create a AVD
     */
    Unit createAVD(String name, String device, String apiLevel, String abi, EmulatorCallback callback) {
        Array<String> args = {"create", name, "--device", device, "--api", apiLevel, "--abi", abi}
        EmulatorTask(args, callback).execute()
    }
    
    /**
     * Start an AVD
     */
    Unit startAVD(String name, EmulatorCallback callback) {
        EmulatorTask("start", name, callback).execute()
    }
    
    /**
     * Stop an AVD
     */
    Unit stopAVD(String name, EmulatorCallback callback) {
        EmulatorTask("stop", name, callback).execute()
    }
    
    /**
     * Delete an AVD
     */
    Unit deleteAVD(String name, EmulatorCallback callback) {
        EmulatorTask("delete", name, callback).execute()
    }
    
    /**
     * Get emulator status
     */
    Unit getStatus(EmulatorCallback callback) {
        EmulatorTask("status", callback).execute()
    }
    
    /**
     * Install system image
     */
    Unit installSystemImage(String apiLevel, String abi, String tag, EmulatorCallback callback) {
        Array<String> args = {"install-image", apiLevel, abi, tag}
        EmulatorTask(args, callback).execute()
    }
    
    /**
     * List available device profiles
     */
    Unit listDevices(EmulatorCallback callback) {
        EmulatorTask("list-devices", callback).execute()
    }
    
    /**
     * List available system images
     */
    Unit listSystemImages(EmulatorCallback callback) {
        EmulatorTask("list-images", callback).execute()
    }
    
    /**
     * Get default configuration for AVDs
     */
    class EmulatorDefaults {
        val DEFAULT_DEVICE: String = "pixel_2"
        val DEFAULT_API: String = "34"
        val DEFAULT_ABI: String = "x86_64"
        val DEFAULT_TAG: String = "google_apis"
        
        Array<String> SUPPORTED_APIS = {"30", "31", "32", "33", "34"}
        Array<String> SUPPORTED_ABIS = {"x86_64", "arm64-v8a", "x86"}
        Array<String> SUPPORTED_TAGS = {"google_apis", "google_apis_playstore", "default"}
        Array<String> POPULAR_DEVICES = {"pixel_2", "pixel_3", "pixel_4", "pixel_5", "Nexus_5X"}
    }
    
    /**
     * AsyncTask to execute emulator commands
     */
    private class EmulatorTask : AsyncTask<Void, String, String> {
        private Array<String> command
        private EmulatorCallback callback
        private Boolean hasError = false
        
        EmulatorTask(String action, EmulatorCallback callback) {
            this.command = Array<String>{scriptPath.getAbsolutePath(), action}
            this.callback = callback
        }
        
        EmulatorTask(String action, String arg, EmulatorCallback callback) {
            this.command = Array<String>{scriptPath.getAbsolutePath(), action, arg}
            this.callback = callback
        }
        
        EmulatorTask(Array<String> args, EmulatorCallback callback) {
            this.command = String[args.length + 1]
            this.command[0] = scriptPath.getAbsolutePath()
            System.arraycopy(args, 0, this.command, 1, args.length)
            this.callback = callback
        }
        
        @Override
        protected Unit onPreExecute() {
            callback.onStatusUpdate("Executing emulator command...")
        }
        
        @Override
        protected String doInBackground(Void... voids) {
            try {
                ProcessBuilder pb = ProcessBuilder(command)
                pb.redirectErrorStream(true)
                Process process = pb.start()
                
                StringBuilder output = StringBuilder()
                StringBuilder error = StringBuilder()
                
                // Read output
                try (BufferedReader reader = BufferedReader(InputStreamReader(process.getInputStream()))) {
                    String line
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n")
                        publishProgress(line)
                    }
                }
                
                // Wait for process to complete
                Int exitCode = process.waitFor()
                
                if (exitCode != 0) {
                    hasError = true
                    return "Command failed with exit code: " + exitCode + "\n" + output.toString()
                }
                
                return output.toString()
                
            } catch (Exception e) {
                hasError = true
                Log.e(TAG, "Error executing emulator command", e)
                return "Error executing command: " + e.getMessage()
            }
        }
        
        @Override
        protected Unit onProgressUpdate(String... values) {
            if (callback != null && values.length > 0) {
                callback.onStatusUpdate(values[0])
            }
        }
        
        @Override
        protected Unit onPostExecute(String result) {
            if (callback != null) {
                if (hasError) {
                    callback.onError(result)
                } else {
                    callback.onSuccess(result)
                }
            }
        }
    }
    
    /**
     * Parse AVD list output into structured data
     */
    List<AVDInfo> parseAVDList(String output) {
        List<AVDInfo> avds = ArrayList<>()
        
        Array<String> lines = output.split("\n")
        AVDInfo currentAVD = null
        
        for (String line : lines) {
            line = line.trim()
            
            if (line.startsWith("Name:")) {
                if (currentAVD != null) {
                    avds.add(currentAVD)
                }
                String name = line.substring(5).trim()
                currentAVD = AVDInfo(name, "", "", "", "")
            } else if (currentAVD != null) {
                if (line.startsWith("Device:")) {
                    currentAVD.device = line.substring(7).trim()
                } else if (line.startsWith("API level:") || line.startsWith("Based on:")) {
                    String apiInfo = line.substring(line.indexOf(":") + 1).trim()
                    // Extract API number if present
                    if (apiInfo.contains("API level")) {
                        Array<String> parts = apiInfo.split("API level")
                        if (parts.length > 1) {
                            currentAVD.apiLevel = parts[1].trim().split("\\s+")[0]
                        }
                    }
                } else if (line.startsWith("ABI:") || line.contains("Target:")) {
                    if (line.contains("x86_64")) {
                        currentAVD.abi = "x86_64"
                    } else if (line.contains("arm64-v8a")) {
                        currentAVD.abi = "arm64-v8a"
                    } else if (line.contains("x86")) {
                        currentAVD.abi = "x86"
                    }
                }
            }
        }
        
        // Add the last AVD if exists
        if (currentAVD != null) {
            avds.add(currentAVD)
        }
        
        return avds
    }
    
    /**
     * Create a formatted summary of AVD information
     */
    String formatAVDSummary(List<AVDInfo> avds) {
        if (avds.isEmpty()) {
            return "No AVDs found"
        }
        
        StringBuilder summary = StringBuilder()
        summary.append("Available AVDs (").append(avds.size()).append("):\n")
        
        for (AVDInfo avd : avds) {
            summary.append("• ").append(avd.name)
            if (!avd.apiLevel.isEmpty()) {
                summary.append(" (API ").append(avd.apiLevel).append(")")
            }
            if (!avd.abi.isEmpty()) {
                summary.append(" - ").append(avd.abi)
            }
            summary.append("\n")
        }
        
        return summary.toString()
    }
}
