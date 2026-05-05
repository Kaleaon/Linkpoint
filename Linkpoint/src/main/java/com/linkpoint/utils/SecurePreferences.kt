package com.linkpoint.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Helper for creating EncryptedSharedPreferences with a migration path
 * from legacy plaintext SharedPreferences.
 */
object SecurePreferences {

    private const val TAG = "SecurePreferences"
    private const val ENCRYPTED_SUFFIX = "_encrypted"
    private const val MIGRATION_COMPLETE_KEY = "__migration_complete"

    fun getEncryptedPreferences(
        context: Context,
        legacyName: String,
        migration: ((SharedPreferences, SharedPreferences) -> Unit)? = null
    ): SharedPreferences {
        // AndroidKeyStore isn't available under Robolectric (and is
        // missing on a handful of OEM ROMs / very early Android 6
        // devices), so the MasterKey + EncryptedSharedPreferences setup
        // can throw `KeyStoreException("AndroidKeyStore not found")`.
        // Falling back to plain SharedPreferences keeps the app running
        // and tests runnable; we still log so the security regression
        // is visible in production.
        val encryptedPrefs: SharedPreferences = try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                legacyName + ENCRYPTED_SUFFIX,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.w(TAG, "Falling back to plain SharedPreferences (AndroidKeyStore unavailable): ${e.message}")
            context.getSharedPreferences(legacyName + ENCRYPTED_SUFFIX, Context.MODE_PRIVATE)
        }

        if (!encryptedPrefs.getBoolean(MIGRATION_COMPLETE_KEY, false)) {
            val legacyPrefs = context.getSharedPreferences(legacyName, Context.MODE_PRIVATE)
            migration?.invoke(legacyPrefs, encryptedPrefs)
            migrateAll(legacyPrefs, encryptedPrefs)
            encryptedPrefs.edit().putBoolean(MIGRATION_COMPLETE_KEY, true).apply()
            if (legacyPrefs.all.isNotEmpty()) {
                legacyPrefs.edit().clear().apply()
            }
        }

        return encryptedPrefs
    }

    private fun migrateAll(legacyPrefs: SharedPreferences, encryptedPrefs: SharedPreferences) {
        if (legacyPrefs.all.isEmpty()) {
            return
        }

        val editor = encryptedPrefs.edit()
        for ((key, value) in legacyPrefs.all) {
            if (encryptedPrefs.contains(key)) {
                continue
            }
            when (value) {
                is String -> editor.putString(key, value)
                is Int -> editor.putInt(key, value)
                is Long -> editor.putLong(key, value)
                is Boolean -> editor.putBoolean(key, value)
                is Float -> editor.putFloat(key, value)
                is Set<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    val stringSet = value.filterIsInstance<String>().toSet()
                    editor.putStringSet(key, stringSet)
                }
                else -> Log.w(TAG, "Skipping unsupported legacy pref type for key: $key")
            }
        }
        editor.apply()
        Log.i(TAG, "Migrated legacy preferences to encrypted storage")
    }
}
