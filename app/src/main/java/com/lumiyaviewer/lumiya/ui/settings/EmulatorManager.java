package com.lumiyaviewer.lumiya.ui.settings;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Android Emulator Management Integration
 * 
 * Provides integration between the app settings and the emulator_manager.sh script
 * for CLI-based Android emulator management functionality.
 */
public class EmulatorManager {
    private static final String TAG = "EmulatorManager";
    
    private final Context context;
    private final File scriptPath;
    
    // Emulator status callback interface
    public interface EmulatorCallback {
        void onSuccess(String message);
        void onError(String error);
        void onStatusUpdate(String status);
    }
    
    // AVD information class
    public static class AVDInfo {
        public String name;
        public String device;
        public String apiLevel;
        public String abi;
        public String status;
        
        public AVDInfo(String name, String device, String apiLevel, String abi, String status) {
            this.name = name;
            this.device = device;
            this.apiLevel = apiLevel;
            this.abi = abi;
            this.status = status;
        }
        
        @Override
        public String toString() {
            return String.format("%s (API %s, %s)", name, apiLevel, abi);
        }
    }
    
    public EmulatorManager(Context context) {
        this.context = context;
        
        // Find the script path relative to the app
        // In a real deployment, this would be in assets or internal storage
        // For development, we use the project structure
        File projectRoot = new File(context.getFilesDir().getParent(), "../../../");
        this.scriptPath = new File(projectRoot, "scripts/emulator_manager.sh");
        
        Log.d(TAG, "EmulatorManager initialized with script path: " + scriptPath.getAbsolutePath());
    }
    
    /**
     * Check if emulator management is available
     */
    public boolean isAvailable() {
        // Check if Android SDK is available
        String androidHome = System.getenv("ANDROID_HOME");
        if (androidHome == null || androidHome.isEmpty()) {
            return false;
        }
        
        // Check if script exists
        if (!scriptPath.exists() || !scriptPath.canExecute()) {
            Log.w(TAG, "Emulator script not found or not executable: " + scriptPath.getAbsolutePath());
            return false;
        }
        
        return true;
    }
    
    /**
     * List all available AVDs
     */
    public void listAVDs(EmulatorCallback callback) {
        new EmulatorTask("list-avds", callback).execute();
    }
    
    /**
     * Create a new AVD
     */
    public void createAVD(String name, String device, String apiLevel, String abi, EmulatorCallback callback) {
        String[] args = {"create", name, "--device", device, "--api", apiLevel, "--abi", abi};
        new EmulatorTask(args, callback).execute();
    }
    
    /**
     * Start an AVD
     */
    public void startAVD(String name, EmulatorCallback callback) {
        new EmulatorTask("start", name, callback).execute();
    }
    
    /**
     * Stop an AVD
     */
    public void stopAVD(String name, EmulatorCallback callback) {
        new EmulatorTask("stop", name, callback).execute();
    }
    
    /**
     * Delete an AVD
     */
    public void deleteAVD(String name, EmulatorCallback callback) {
        new EmulatorTask("delete", name, callback).execute();
    }
    
    /**
     * Get emulator status
     */
    public void getStatus(EmulatorCallback callback) {
        new EmulatorTask("status", callback).execute();
    }
    
    /**
     * Install system image
     */
    public void installSystemImage(String apiLevel, String abi, String tag, EmulatorCallback callback) {
        String[] args = {"install-image", apiLevel, abi, tag};
        new EmulatorTask(args, callback).execute();
    }
    
    /**
     * List available device profiles
     */
    public void listDevices(EmulatorCallback callback) {
        new EmulatorTask("list-devices", callback).execute();
    }
    
    /**
     * List available system images
     */
    public void listSystemImages(EmulatorCallback callback) {
        new EmulatorTask("list-images", callback).execute();
    }
    
    /**
     * Get default configuration for new AVDs
     */
    public static class EmulatorDefaults {
        public static final String DEFAULT_DEVICE = "pixel_2";
        public static final String DEFAULT_API = "34";
        public static final String DEFAULT_ABI = "x86_64";
        public static final String DEFAULT_TAG = "google_apis";
        
        public static final String[] SUPPORTED_APIS = {"30", "31", "32", "33", "34"};
        public static final String[] SUPPORTED_ABIS = {"x86_64", "arm64-v8a", "x86"};
        public static final String[] SUPPORTED_TAGS = {"google_apis", "google_apis_playstore", "default"};
        public static final String[] POPULAR_DEVICES = {"pixel_2", "pixel_3", "pixel_4", "pixel_5", "Nexus_5X"};
    }
    
    /**
     * AsyncTask to execute emulator commands
     */
    private class EmulatorTask extends AsyncTask<Void, String, String> {
        private final String[] command;
        private final EmulatorCallback callback;
        private boolean hasError = false;
        
        public EmulatorTask(String action, EmulatorCallback callback) {
            this.command = new String[]{scriptPath.getAbsolutePath(), action};
            this.callback = callback;
        }
        
        public EmulatorTask(String action, String arg, EmulatorCallback callback) {
            this.command = new String[]{scriptPath.getAbsolutePath(), action, arg};
            this.callback = callback;
        }
        
        public EmulatorTask(String[] args, EmulatorCallback callback) {
            this.command = new String[args.length + 1];
            this.command[0] = scriptPath.getAbsolutePath();
            System.arraycopy(args, 0, this.command, 1, args.length);
            this.callback = callback;
        }
        
        @Override
        protected void onPreExecute() {
            callback.onStatusUpdate("Executing emulator command...");
        }
        
        @Override
        protected String doInBackground(Void... voids) {
            try {
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.redirectErrorStream(true);
                Process process = pb.start();
                
                StringBuilder output = new StringBuilder();
                StringBuilder error = new StringBuilder();
                
                // Read output
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                        publishProgress(line);
                    }
                }
                
                // Wait for process to complete
                int exitCode = process.waitFor();
                
                if (exitCode != 0) {
                    hasError = true;
                    return "Command failed with exit code: " + exitCode + "\n" + output.toString();
                }
                
                return output.toString();
                
            } catch (Exception e) {
                hasError = true;
                Log.e(TAG, "Error executing emulator command", e);
                return "Error executing command: " + e.getMessage();
            }
        }
        
        @Override
        protected void onProgressUpdate(String... values) {
            if (callback != null && values.length > 0) {
                callback.onStatusUpdate(values[0]);
            }
        }
        
        @Override
        protected void onPostExecute(String result) {
            if (callback != null) {
                if (hasError) {
                    callback.onError(result);
                } else {
                    callback.onSuccess(result);
                }
            }
        }
    }
    
    /**
     * Parse AVD list output into structured data
     */
    public static List<AVDInfo> parseAVDList(String output) {
        List<AVDInfo> avds = new ArrayList<>();
        
        String[] lines = output.split("\n");
        AVDInfo currentAVD = null;
        
        for (String line : lines) {
            line = line.trim();
            
            if (line.startsWith("Name:")) {
                if (currentAVD != null) {
                    avds.add(currentAVD);
                }
                String name = line.substring(5).trim();
                currentAVD = new AVDInfo(name, "", "", "", "");
            } else if (currentAVD != null) {
                if (line.startsWith("Device:")) {
                    currentAVD.device = line.substring(7).trim();
                } else if (line.startsWith("API level:") || line.startsWith("Based on:")) {
                    String apiInfo = line.substring(line.indexOf(":") + 1).trim();
                    // Extract API number if present
                    if (apiInfo.contains("API level")) {
                        String[] parts = apiInfo.split("API level");
                        if (parts.length > 1) {
                            currentAVD.apiLevel = parts[1].trim().split("\\s+")[0];
                        }
                    }
                } else if (line.startsWith("ABI:") || line.contains("Target:")) {
                    if (line.contains("x86_64")) {
                        currentAVD.abi = "x86_64";
                    } else if (line.contains("arm64-v8a")) {
                        currentAVD.abi = "arm64-v8a";
                    } else if (line.contains("x86")) {
                        currentAVD.abi = "x86";
                    }
                }
            }
        }
        
        // Add the last AVD if exists
        if (currentAVD != null) {
            avds.add(currentAVD);
        }
        
        return avds;
    }
    
    /**
     * Create a formatted summary of AVD information
     */
    public static String formatAVDSummary(List<AVDInfo> avds) {
        if (avds.isEmpty()) {
            return "No AVDs found";
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("Available AVDs (").append(avds.size()).append("):\n");
        
        for (AVDInfo avd : avds) {
            summary.append("• ").append(avd.name);
            if (!avd.apiLevel.isEmpty()) {
                summary.append(" (API ").append(avd.apiLevel).append(")");
            }
            if (!avd.abi.isEmpty()) {
                summary.append(" - ").append(avd.abi);
            }
            summary.append("\n");
        }
        
        return summary.toString();
    }
}