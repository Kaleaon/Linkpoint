package com.linkpoint.pwa;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONException;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * Secure Storage Plugin for Linkpoint PWA
 * Provides encrypted storage using Android Keystore
 */
@CapacitorPlugin(name = "SecureStorage")
public class SecureStoragePlugin extends Plugin {

    private static final String PREFS_NAME = "linkpoint_secure_storage";
    private static final String KEY_ALIAS = "linkpoint_storage_key";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    private SharedPreferences sharedPreferences;
    private boolean encryptionAvailable = true;

    @Override
    public void load() {
        super.load();
        sharedPreferences = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        try {
            // Initialize encryption key if needed
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                initializeKey();
            } else {
                // Fallback to unencrypted storage for older devices
                encryptionAvailable = false;
            }
        } catch (Exception e) {
            encryptionAvailable = false;
        }
    }

    /**
     * Store a value with encryption
     */
    @PluginMethod
    public void set(PluginCall call) {
        String key = call.getString("key");
        String value = call.getString("value");

        if (key == null || value == null) {
            call.reject("Missing key or value");
            return;
        }

        try {
            String storedValue;
            if (encryptionAvailable && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                storedValue = encrypt(value);
            } else {
                storedValue = value; // Fallback to plain text
            }

            sharedPreferences.edit().putString(key, storedValue).apply();

            JSObject result = new JSObject();
            result.put("success", true);
            call.resolve(result);
        } catch (Exception e) {
            call.reject("Failed to store value: " + e.getMessage());
        }
    }

    /**
     * Retrieve a value with decryption
     */
    @PluginMethod
    public void get(PluginCall call) {
        String key = call.getString("key");

        if (key == null) {
            call.reject("Missing key");
            return;
        }

        try {
            String storedValue = sharedPreferences.getString(key, null);
            
            if (storedValue == null) {
                call.reject("Key not found");
                return;
            }

            String value;
            if (encryptionAvailable && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                value = decrypt(storedValue);
            } else {
                value = storedValue; // Plain text fallback
            }

            JSObject result = new JSObject();
            result.put("value", value);
            result.put("success", true);
            call.resolve(result);
        } catch (Exception e) {
            call.reject("Failed to retrieve value: " + e.getMessage());
        }
    }

    /**
     * Remove a value
     */
    @PluginMethod
    public void remove(PluginCall call) {
        String key = call.getString("key");

        if (key == null) {
            call.reject("Missing key");
            return;
        }

        sharedPreferences.edit().remove(key).apply();

        JSObject result = new JSObject();
        result.put("success", true);
        call.resolve(result);
    }

    /**
     * Clear all stored values
     */
    @PluginMethod
    public void clear(PluginCall call) {
        sharedPreferences.edit().clear().apply();

        JSObject result = new JSObject();
        result.put("success", true);
        call.resolve(result);
    }

    /**
     * Get all keys
     */
    @PluginMethod
    public void keys(PluginCall call) {
        try {
            Map<String, ?> all = sharedPreferences.getAll();
            JSArray keys = new JSArray();
            
            for (String key : all.keySet()) {
                keys.put(key);
            }

            JSObject result = new JSObject();
            result.put("keys", keys);
            result.put("success", true);
            call.resolve(result);
        } catch (JSONException e) {
            call.reject("Failed to get keys: " + e.getMessage());
        }
    }

    /**
     * Check if a key exists
     */
    @PluginMethod
    public void has(PluginCall call) {
        String key = call.getString("key");

        if (key == null) {
            call.reject("Missing key");
            return;
        }

        boolean exists = sharedPreferences.contains(key);

        JSObject result = new JSObject();
        result.put("exists", exists);
        call.resolve(result);
    }

    // Encryption helper methods (Android M+)

    private void initializeKey() throws Exception {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            if (!keyStore.containsAlias(KEY_ALIAS)) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, 
                    "AndroidKeyStore"
                );

                KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
                );

                builder.setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(true);

                keyGenerator.init(builder.build());
                keyGenerator.generateKey();
            }
        }
    }

    private String encrypt(String plainText) throws Exception {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            SecretKey secretKey = (SecretKey) keyStore.getKey(KEY_ALIAS, null);
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] iv = cipher.getIV();
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Combine IV and encrypted data
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.encodeToString(combined, Base64.DEFAULT);
        }
        return plainText;
    }

    private String decrypt(String encryptedText) throws Exception {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            SecretKey secretKey = (SecretKey) keyStore.getKey(KEY_ALIAS, null);
            
            byte[] combined = Base64.decode(encryptedText, Base64.DEFAULT);

            // Extract IV and encrypted data
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encrypted = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        }
        return encryptedText;
    }
}
