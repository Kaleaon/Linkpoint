package com.lumiyaviewer.lumiya

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Testing Framework for Linkpoint
 * 
 * Provides utilities and base classes for testing:
 * - Unit tests
 * - Integration tests
 * - Coroutine testing
 * - Mock data
 */

/**
 * Main Dispatcher Rule for Coroutine Testing
 * 
 * Replaces the main dispatcher with a test dispatcher for testing coroutines.
 */
@ExperimentalCoroutinesApi
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }
    
    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

/**
 * Base Test Class
 * 
 * Provides common setup and utilities for all tests.
 */
abstract class BaseTest {
    
    /**
     * Setup method called before each test
     */
    open fun setup() {
        // Override in subclasses
    }
    
    /**
     * Teardown method called after each test
     */
    open fun teardown() {
        // Override in subclasses
    }
}

/**
 * Mock Data Provider
 * 
 * Provides mock data for testing.
 */
object MockDataProvider {
    
    /**
     * Mock user data
     */
    fun mockUser(
        id: Long = 1L,
        username: String = "TestUser",
        displayName: String = "Test User"
    ) = mapOf(
        "id" to id,
        "username" to username,
        "displayName" to displayName
    )
    
    /**
     * Mock chat message
     */
    fun mockChatMessage(
        id: Long = 1L,
        senderId: Long = 1L,
        message: String = "Test message",
        timestamp: Long = System.currentTimeMillis()
    ) = mapOf(
        "id" to id,
        "senderId" to senderId,
        "message" to message,
        "timestamp" to timestamp
    )
    
    /**
     * Mock grid connection
     */
    fun mockGridConnection(
        gridUrl: String = "agni.lindenlab.com",
        connected: Boolean = true
    ) = mapOf(
        "gridUrl" to gridUrl,
        "connected" to connected
    )
}

/**
 * Test Assertions
 * 
 * Custom assertions for testing.
 */
object TestAssertions {
    
    /**
     * Assert that a value is within a range
     */
    fun assertInRange(value: Float, min: Float, max: Float, message: String = "") {
        if (value !in min..max) {
            throw AssertionError("$message: Expected $value to be in range [$min, $max]")
        }
    }
    
    /**
     * Assert that a collection is not empty
     */
    fun <T> assertNotEmpty(collection: Collection<T>, message: String = "") {
        if (collection.isEmpty()) {
            throw AssertionError("$message: Expected collection to not be empty")
        }
    }
    
    /**
     * Assert that a string contains a substring
     */
    fun assertContains(string: String, substring: String, message: String = "") {
        if (!string.contains(substring)) {
            throw AssertionError("$message: Expected '$string' to contain '$substring'")
        }
    }
}

/**
 * Test Utilities
 */
object TestUtils {
    
    /**
     * Create a temporary file for testing
     */
    fun createTempFile(content: String = ""): java.io.File {
        return java.io.File.createTempFile("test", ".tmp").apply {
            writeText(content)
            deleteOnExit()
        }
    }
    
    /**
     * Measure execution time
     */
    inline fun measureTime(block: () -> Unit): Long {
        val start = System.currentTimeMillis()
        block()
        return System.currentTimeMillis() - start
    }
}