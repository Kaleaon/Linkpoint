package com.linkpoint.auth

import android.content.Context
import com.linkpoint.utils.SecurePreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Encapsulates a saved user account profile.
 */
data class SavedAccount(
    val firstName: String,
    val lastName: String = "Resident",
    val gridId: String = "secondlife",
    val encryptedPassword: String,
    val lastUsedTimestamp: Long = System.currentTimeMillis()
) {
    val displayName: String
        get() = if (lastName.isBlank() || lastName.equals("Resident", ignoreCase = true)) {
            firstName
        } else {
            "$firstName $lastName"
        }

    val accountKey: String
        get() = "${firstName.lowercase().trim()}_${lastName.lowercase().trim().ifBlank { "resident" }}_${gridId.lowercase().trim()}"
}

/**
 * Repository for storing, loading, and deleting encrypted saved account profiles.
 * Account data (including passwords) is encrypted using [SecurePreferences].
 * Deleting an account profile also clears its associated MFA hash from [MfaHashStorage].
 */
class SavedAccountRepository private constructor(context: Context) {

    private val applicationContext = context.applicationContext
    private val prefs = SecurePreferences.getEncryptedPreferences(applicationContext, PREFS_NAME)
    private val mfaHashStorage = MfaHashStorage(applicationContext)

    companion object {
        private const val PREFS_NAME = "saved_account_profiles"
        private const val ACCOUNT_KEY_PREFIX = "account_profile_"

        @Volatile
        private var instance: SavedAccountRepository? = null

        fun getInstance(context: Context): SavedAccountRepository {
            return instance ?: synchronized(this) {
                instance ?: SavedAccountRepository(context.applicationContext).also { instance = it }
            }
        }
    }

    /**
     * Retrieve all saved account profiles, sorted with most recently used first.
     */
    suspend fun getSavedAccounts(): List<SavedAccount> = withContext(Dispatchers.IO) {
        val accounts = mutableListOf<SavedAccount>()
        val allEntries = prefs.all
        for ((key, value) in allEntries) {
            if (key.startsWith(ACCOUNT_KEY_PREFIX) && value is String) {
                try {
                    val json = JSONObject(value)
                    val account = SavedAccount(
                        firstName = json.getString("firstName"),
                        lastName = json.optString("lastName", "Resident"),
                        gridId = json.optString("gridId", "secondlife"),
                        encryptedPassword = json.getString("encryptedPassword"),
                        lastUsedTimestamp = json.optLong("lastUsedTimestamp", 0L)
                    )
                    accounts.add(account)
                } catch (e: Exception) {
                    // Ignore malformed account entries
                }
            }
        }
        accounts.sortedByDescending { it.lastUsedTimestamp }
    }

    /**
     * Save or update an account profile with encrypted password storage.
     */
    suspend fun saveAccount(
        firstName: String,
        lastName: String,
        gridId: String,
        password: String,
        timestamp: Long = System.currentTimeMillis()
    ): SavedAccount = withContext(Dispatchers.IO) {
        val cleanLastName = lastName.trim().ifBlank { "Resident" }
        val account = SavedAccount(
            firstName = firstName.trim(),
            lastName = cleanLastName,
            gridId = gridId.trim().ifBlank { "secondlife" },
            encryptedPassword = password,
            lastUsedTimestamp = timestamp
        )
        saveAccountInternal(account)
        account
    }

    /**
     * Save or update an account profile.
     */
    suspend fun saveAccount(account: SavedAccount) = withContext(Dispatchers.IO) {
        saveAccountInternal(account)
    }

    private fun saveAccountInternal(account: SavedAccount) {
        val json = JSONObject().apply {
            put("firstName", account.firstName)
            put("lastName", account.lastName)
            put("gridId", account.gridId)
            put("encryptedPassword", account.encryptedPassword)
            put("lastUsedTimestamp", account.lastUsedTimestamp)
        }
        val key = ACCOUNT_KEY_PREFIX + account.accountKey
        prefs.edit().putString(key, json.toString()).apply()
    }

    /**
     * Delete an account profile from encrypted storage and clear its MFA hash.
     */
    suspend fun deleteAccount(account: SavedAccount) = withContext(Dispatchers.IO) {
        deleteAccount(account.firstName, account.lastName, account.gridId)
    }

    /**
     * Delete an account profile by credentials and clear its MFA hash.
     */
    suspend fun deleteAccount(firstName: String, lastName: String, gridId: String) = withContext(Dispatchers.IO) {
        val cleanLastName = lastName.trim().ifBlank { "Resident" }
        val tempAccount = SavedAccount(
            firstName = firstName.trim(),
            lastName = cleanLastName,
            gridId = gridId.trim().ifBlank { "secondlife" },
            encryptedPassword = ""
        )
        val key = ACCOUNT_KEY_PREFIX + tempAccount.accountKey
        prefs.edit().remove(key).apply()

        // Remove associated MFA hash from MfaHashStorage
        val fullUser = "${tempAccount.firstName} ${tempAccount.lastName}"
        val dotUser = "${tempAccount.firstName}.${tempAccount.lastName}"
        mfaHashStorage.clearMfaHash(fullUser)
        mfaHashStorage.clearMfaHash(dotUser)
        mfaHashStorage.clearMfaHash(tempAccount.firstName)
    }

    /**
     * Retrieve a specific account profile if stored.
     */
    suspend fun getAccount(firstName: String, lastName: String, gridId: String): SavedAccount? = withContext(Dispatchers.IO) {
        val cleanLastName = lastName.trim().ifBlank { "Resident" }
        val tempAccount = SavedAccount(
            firstName = firstName.trim(),
            lastName = cleanLastName,
            gridId = gridId.trim().ifBlank { "secondlife" },
            encryptedPassword = ""
        )
        val key = ACCOUNT_KEY_PREFIX + tempAccount.accountKey
        val jsonStr = prefs.getString(key, null) ?: return@withContext null
        try {
            val json = JSONObject(jsonStr)
            SavedAccount(
                firstName = json.getString("firstName"),
                lastName = json.optString("lastName", "Resident"),
                gridId = json.optString("gridId", "secondlife"),
                encryptedPassword = json.getString("encryptedPassword"),
                lastUsedTimestamp = json.optLong("lastUsedTimestamp", 0L)
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Clear all saved account profiles.
     */
    suspend fun clearAllAccounts() = withContext(Dispatchers.IO) {
        val allEntries = prefs.all
        val editor = prefs.edit()
        for ((key, _) in allEntries) {
            if (key.startsWith(ACCOUNT_KEY_PREFIX)) {
                editor.remove(key)
            }
        }
        editor.apply()
    }
}
