package com.linkpoint.auth

import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SavedAccountRepositoryTest {

    private lateinit var repository: SavedAccountRepository
    private lateinit var mfaStorage: MfaHashStorage

    @Before
    fun setUp() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        repository = SavedAccountRepository.getInstance(context)
        mfaStorage = MfaHashStorage(context)
        repository.clearAllAccounts()
        mfaStorage.clearAllMfaHashes()
    }

    @Test
    fun testSaveAndGetAccount() = runBlocking {
        val saved = repository.saveAccount(
            firstName = "Test",
            lastName = "Resident",
            gridId = "secondlife",
            password = "secret_password_123"
        )

        assertEquals("Test", saved.firstName)
        assertEquals("Resident", saved.lastName)
        assertEquals("secondlife", saved.gridId)
        assertEquals("secret_password_123", saved.encryptedPassword)

        val retrieved = repository.getAccount("Test", "Resident", "secondlife")
        assertNotNull(retrieved)
        assertEquals("Test", retrieved?.firstName)
        assertEquals("secret_password_123", retrieved?.encryptedPassword)
    }

    @Test
    fun testGetSavedAccountsSortedByTimestamp() = runBlocking {
        repository.saveAccount("Alice", "Resident", "secondlife", "pass1", timestamp = 1000L)
        repository.saveAccount("Bob", "Builder", "secondlife", "pass2", timestamp = 3000L)
        repository.saveAccount("Charlie", "Resident", "osgrid", "pass3", timestamp = 2000L)

        val accounts = repository.getSavedAccounts()
        assertTrue(accounts.size >= 3)

        // Bob should be first because timestamp 3000L > 2000L > 1000L
        val bIndex = accounts.indexOfFirst { it.firstName == "Bob" }
        val cIndex = accounts.indexOfFirst { it.firstName == "Charlie" }
        val aIndex = accounts.indexOfFirst { it.firstName == "Alice" }

        assertTrue(bIndex < cIndex)
        assertTrue(cIndex < aIndex)
    }

    @Test
    fun testDeleteAccountClearsProfileAndMfaHash() = runBlocking {
        val firstName = "Delete"
        val lastName = "User"
        val username = "$firstName $lastName"

        // Save MFA hash in MfaHashStorage
        mfaStorage.saveMfaHash(username, "mfa_hash_99999")
        assertEquals("mfa_hash_99999", mfaStorage.getMfaHash(username))

        // Save account profile
        repository.saveAccount(firstName, lastName, "secondlife", "password_to_delete")
        assertNotNull(repository.getAccount(firstName, lastName, "secondlife"))

        // Delete account profile
        repository.deleteAccount(firstName, lastName, "secondlife")

        // Assert profile is deleted
        assertNull(repository.getAccount(firstName, lastName, "secondlife"))

        // Assert MFA hash is also cleared from MfaHashStorage
        assertNull(mfaStorage.getMfaHash(username))
    }

    @Test
    fun testSavedAccountDisplayName() {
        val acc1 = SavedAccount("John", "Doe", "secondlife", "pass")
        assertEquals("John Doe", acc1.displayName)

        val acc2 = SavedAccount("Jane", "Resident", "secondlife", "pass")
        assertEquals("Jane", acc2.displayName)

        val acc3 = SavedAccount("Bob", "", "secondlife", "pass")
        assertEquals("Bob", acc3.displayName)
    }
}
