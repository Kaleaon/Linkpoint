package com.linkpoint.pwa;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * File System Plugin for Linkpoint PWA
 * Provides native file system access with read/write capabilities
 */
@CapacitorPlugin(name = "FileSystem")
public class FileSystemPlugin extends Plugin {

    /**
     * Write text to a file
     */
    @PluginMethod
    public void writeFile(PluginCall call) {
        String path = call.getString("path");
        String data = call.getString("data");
        String directory = call.getString("directory", "data");
        boolean append = call.getBoolean("append", false);

        if (path == null || data == null) {
            call.reject("Missing path or data");
            return;
        }

        try {
            File file = getFile(directory, path);
            ensureDirectoryExists(file.getParentFile());

            FileOutputStream fos = new FileOutputStream(file, append);
            fos.write(data.getBytes(StandardCharsets.UTF_8));
            fos.close();

            JSObject result = new JSObject();
            result.put("success", true);
            result.put("path", file.getAbsolutePath());
            call.resolve(result);
        } catch (IOException e) {
            call.reject("Failed to write file: " + e.getMessage());
        }
    }

    /**
     * Read text from a file
     */
    @PluginMethod
    public void readFile(PluginCall call) {
        String path = call.getString("path");
        String directory = call.getString("directory", "data");

        if (path == null) {
            call.reject("Missing path");
            return;
        }

        try {
            File file = getFile(directory, path);
            if (!file.exists()) {
                call.reject("File does not exist");
                return;
            }

            StringBuilder text = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line).append("\n");
            }
            reader.close();

            JSObject result = new JSObject();
            result.put("data", text.toString());
            result.put("success", true);
            call.resolve(result);
        } catch (IOException e) {
            call.reject("Failed to read file: " + e.getMessage());
        }
    }

    /**
     * Write binary data to a file
     */
    @PluginMethod
    public void writeBinary(PluginCall call) {
        String path = call.getString("path");
        String data = call.getString("data"); // Base64 encoded
        String directory = call.getString("directory", "data");

        if (path == null || data == null) {
            call.reject("Missing path or data");
            return;
        }

        try {
            File file = getFile(directory, path);
            ensureDirectoryExists(file.getParentFile());

            byte[] decodedData = Base64.decode(data, Base64.DEFAULT);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(decodedData);
            fos.close();

            JSObject result = new JSObject();
            result.put("success", true);
            result.put("path", file.getAbsolutePath());
            call.resolve(result);
        } catch (IOException e) {
            call.reject("Failed to write binary file: " + e.getMessage());
        }
    }

    /**
     * Read binary data from a file
     */
    @PluginMethod
    public void readBinary(PluginCall call) {
        String path = call.getString("path");
        String directory = call.getString("directory", "data");

        if (path == null) {
            call.reject("Missing path");
            return;
        }

        try {
            File file = getFile(directory, path);
            if (!file.exists()) {
                call.reject("File does not exist");
                return;
            }

            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            String encodedData = Base64.encodeToString(data, Base64.DEFAULT);

            JSObject result = new JSObject();
            result.put("data", encodedData);
            result.put("success", true);
            call.resolve(result);
        } catch (IOException e) {
            call.reject("Failed to read binary file: " + e.getMessage());
        }
    }

    /**
     * Delete a file
     */
    @PluginMethod
    public void deleteFile(PluginCall call) {
        String path = call.getString("path");
        String directory = call.getString("directory", "data");

        if (path == null) {
            call.reject("Missing path");
            return;
        }

        try {
            File file = getFile(directory, path);
            boolean deleted = file.delete();

            JSObject result = new JSObject();
            result.put("success", deleted);
            call.resolve(result);
        } catch (Exception e) {
            call.reject("Failed to delete file: " + e.getMessage());
        }
    }

    /**
     * Check if file exists
     */
    @PluginMethod
    public void exists(PluginCall call) {
        String path = call.getString("path");
        String directory = call.getString("directory", "data");

        if (path == null) {
            call.reject("Missing path");
            return;
        }

        File file = getFile(directory, path);

        JSObject result = new JSObject();
        result.put("exists", file.exists());
        result.put("isFile", file.isFile());
        result.put("isDirectory", file.isDirectory());
        call.resolve(result);
    }

    /**
     * Create a directory
     */
    @PluginMethod
    public void mkdir(PluginCall call) {
        String path = call.getString("path");
        String directory = call.getString("directory", "data");
        boolean recursive = call.getBoolean("recursive", true);

        if (path == null) {
            call.reject("Missing path");
            return;
        }

        File file = getFile(directory, path);
        boolean created = recursive ? file.mkdirs() : file.mkdir();

        JSObject result = new JSObject();
        result.put("success", created);
        result.put("path", file.getAbsolutePath());
        call.resolve(result);
    }

    /**
     * List files in a directory
     */
    @PluginMethod
    public void readDir(PluginCall call) {
        String path = call.getString("path");
        String directory = call.getString("directory", "data");

        if (path == null) {
            call.reject("Missing path");
            return;
        }

        try {
            File dir = getFile(directory, path);
            if (!dir.exists() || !dir.isDirectory()) {
                call.reject("Directory does not exist");
                return;
            }

            File[] files = dir.listFiles();
            JSArray fileList = new JSArray();

            if (files != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                for (File file : files) {
                    JSObject fileObj = new JSObject();
                    fileObj.put("name", file.getName());
                    fileObj.put("path", file.getAbsolutePath());
                    fileObj.put("isFile", file.isFile());
                    fileObj.put("isDirectory", file.isDirectory());
                    fileObj.put("size", file.length());
                    fileObj.put("modified", dateFormat.format(new Date(file.lastModified())));
                    fileList.put(fileObj);
                }
            }

            JSObject result = new JSObject();
            result.put("files", fileList);
            result.put("success", true);
            call.resolve(result);
        } catch (JSONException e) {
            call.reject("Failed to read directory: " + e.getMessage());
        }
    }

    /**
     * Get file info
     */
    @PluginMethod
    public void stat(PluginCall call) {
        String path = call.getString("path");
        String directory = call.getString("directory", "data");

        if (path == null) {
            call.reject("Missing path");
            return;
        }

        File file = getFile(directory, path);
        if (!file.exists()) {
            call.reject("File does not exist");
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);

        JSObject result = new JSObject();
        result.put("name", file.getName());
        result.put("path", file.getAbsolutePath());
        result.put("isFile", file.isFile());
        result.put("isDirectory", file.isDirectory());
        result.put("size", file.length());
        result.put("modified", dateFormat.format(new Date(file.lastModified())));
        result.put("canRead", file.canRead());
        result.put("canWrite", file.canWrite());
        call.resolve(result);
    }

    /**
     * Get available storage paths
     */
    @PluginMethod
    public void getStoragePaths(PluginCall call) {
        JSObject result = new JSObject();
        result.put("dataDirectory", getContext().getFilesDir().getAbsolutePath());
        result.put("cacheDirectory", getContext().getCacheDir().getAbsolutePath());
        result.put("externalDirectory", getContext().getExternalFilesDir(null).getAbsolutePath());
        result.put("externalCacheDirectory", getContext().getExternalCacheDir().getAbsolutePath());
        
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (downloadDir != null) {
            result.put("downloadDirectory", downloadDir.getAbsolutePath());
        }

        call.resolve(result);
    }

    // Helper methods

    private File getFile(String directory, String path) {
        File baseDir;
        switch (directory.toLowerCase()) {
            case "cache":
                baseDir = getContext().getCacheDir();
                break;
            case "external":
                baseDir = getContext().getExternalFilesDir(null);
                break;
            case "external_cache":
                baseDir = getContext().getExternalCacheDir();
                break;
            case "download":
                baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                break;
            default:
                baseDir = getContext().getFilesDir();
                break;
        }

        return new File(baseDir, path);
    }

    private void ensureDirectoryExists(File directory) throws IOException {
        if (directory != null && !directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("Failed to create directory: " + directory.getAbsolutePath());
            }
        }
    }
}
