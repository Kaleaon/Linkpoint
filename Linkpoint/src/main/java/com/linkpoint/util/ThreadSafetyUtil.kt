package com.linkpoint.util

import android.os.Looper
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.selects.select
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

/**
 * Utility class for thread safety operations in the Linkpoint app.
 * 
 * Provides modern, coroutine-based alternatives to legacy threading patterns
 * including Thread, Handler, and runOnUiThread. Follows Second Life naming
 * conventions and Kotlin best practices.
 * 
 * Design Philosophy:
 * - Prefer coroutines over threads for concurrency
 * - Use structured concurrency for lifecycle management
 * - Prefer Flow/Channel over Handler for communication
 * - Ensure proper cancellation to prevent leaks
 * - Use atomic operations for shared state
 * 
 * Key Features:
 * - Lifecycle-aware coroutine scope management
 * - Safe thread switching utilities
 * - Atomic state management
 * - Channel-based event systems
 * - Flow-based reactive programming
 * 
 * @see CoroutineScope
 * @see SupervisorJob
 * @see Flow
 * 
 * Usage Example:
 * ```kotlin
 * class MyComponent {
 *     private val scope = ThreadSafetyUtil.createLifecycleScope()
 *     
 *     fun performOperation() {
 *         scope.launch {
 *             val result = withContext(Dispatchers.IO) {
 *                 // Background work
 *             }
 *             ThreadSafetyUtil.runOnMainThread {
 *                 // UI update
 *             }
 *         }
 *     }
 *     
 *     fun onDestroy() {
 *         scope.cancel()
 *     }
 * }
 * ```
 * 
 * @author SuperNinja AI
 * @since 1.0
 */
object ThreadSafetyUtil {
    
    /**
     * Default timeout for coroutine operations.
     */
    private const val DEFAULT_TIMEOUT_MS = 30_000L // 30 seconds
    
    /**
     * Default capacity for bounded channels.
     */
    private const val DEFAULT_CHANNEL_CAPACITY = Channel.BUFFERED
    
    /**
     * Creates a new CoroutineScope with IO dispatcher and SupervisorJob.
     * 
     * This is the recommended scope for background operations that need
     * to handle failures gracefully without cancelling the entire scope.
     * 
     * Use Case: Network operations, file I/O, database operations.
     * 
     * @return A new CoroutineScope configured for background work
     * 
     * Example:
     * ```kotlin
     * val scope = ThreadSafetyUtil.createBackgroundScope()
     * scope.launch {
     *     val result = fetchDataFromNetwork()
     *     processData(result)
     * }
     * ```
     */
    fun createBackgroundScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.IO + SupervisorJob())
    }
    
    /**
     * Creates a new CoroutineScope with Main dispatcher and SupervisorJob.
     * 
     * This is the recommended scope for UI-related operations that need
     * to run on the main thread.
     * 
     * Use Case: UI updates, animation control, user interaction handling.
     * 
     * @return A new CoroutineScope configured for main thread work
     * 
     * Example:
     * ```kotlin
     * val scope = ThreadSafetyUtil.createMainScope()
     * scope.launch {
     *     updateUI()
     * }
     * ```
     */
    fun createMainScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.Main + SupervisorJob())
    }
    
    /**
     * Creates a new CoroutineScope with a custom dispatcher.
     * 
     * Allows flexibility to use different dispatchers for specific use cases.
     * 
     * @param dispatcher The CoroutineDispatcher to use
     * @return A new CoroutineScope with the specified dispatcher
     * 
     * Example:
     * ```kotlin
     * val scope = ThreadSafetyUtil.createCustomScope(Dispatchers.Default)
     * scope.launch {
     *     performComputation()
     * }
     * ```
     */
    fun createCustomScope(dispatcher: CoroutineDispatcher): CoroutineScope {
        return CoroutineScope(dispatcher + SupervisorJob())
    }
    
    /**
     * Launches a coroutine on the IO dispatcher.
     * 
     * Convenience method for launching background operations without
     * explicitly managing a scope.
     * 
     * Use Case: One-off background operations where scope management
     * is handled elsewhere.
     * 
     * @param scope The CoroutineScope to launch in
     * @param block The suspend function to execute
     * @return Job reference to the launched coroutine
     * 
     * Example:
     * ```kotlin
     * ThreadSafetyUtil.launchIO(scope) {
     *     val data = fetchData()
     *     processData(data)
     * }
     * ```
     */
    fun launchIO(
        scope: CoroutineScope,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return scope.launch(Dispatchers.IO) { block() }
    }
    
    /**
     * Launches a coroutine on the Main dispatcher.
     * 
     * Modern alternative to runOnUiThread with proper coroutine semantics.
     * 
     * Use Case: UI updates from background coroutines.
     * 
     * @param scope The CoroutineScope to launch in
     * @param block The suspend function to execute
     * @return Job reference to the launched coroutine
     * 
     * Example:
     * ```kotlin
     * ThreadSafetyUtil.launchMain(scope) {
     *     updateUI()
     * }
     * ```
     */
    fun launchMain(
        scope: CoroutineScope,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return scope.launch(Dispatchers.Main) { block() }
    }
    
    /**
     * Launches a coroutine on the Default dispatcher.
     * 
     * Use Case: CPU-intensive computations that are not blocking I/O.
     * 
     * @param scope The CoroutineScope to launch in
     * @param block The suspend function to execute
     * @return Job reference to the launched coroutine
     * 
     * Example:
     * ```kotlin
     * ThreadSafetyUtil.launchDefault(scope) {
     *     val result = performComplexCalculation()
     *     updateUI(result)
     * }
     * ```
     */
    fun launchDefault(
        scope: CoroutineScope,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return scope.launch(Dispatchers.Default) { block() }
    }
    
    /**
     * Switches to the IO dispatcher and executes a suspend block.
     * 
     * Use Case: Performing I/O operations from any dispatcher.
     * 
     * @param block The suspend function to execute on IO dispatcher
     * @return The result of the block
     * 
     * Example:
     * ```kotlin
     * val data = ThreadSafetyUtil.withIO {
     *     fetchDataFromNetwork()
     * }
     * ```
     */
    suspend fun <T> withIO(block: suspend CoroutineScope.() -> T): T {
        return withContext(Dispatchers.IO) { block() }
    }
    
    /**
     * Switches to the Main dispatcher and executes a suspend block.
     * 
     * Modern alternative to runOnUiThread with proper coroutine semantics.
     * 
     * Use Case: Updating UI from background coroutines.
     * 
     * @param block The suspend function to execute on Main dispatcher
     * @return The result of the block
     * 
     * Example:
     * ```kotlin
     * ThreadSafetyUtil.withMain {
     *     textView.text = "Updated"
     * }
     * ```
     */
    suspend fun <T> withMain(block: suspend CoroutineScope.() -> T): T {
        return withContext(Dispatchers.Main) { block() }
    }
    
    /**
     * Switches to the Default dispatcher and executes a suspend block.
     * 
     * Use Case: Performing CPU-intensive operations.
     * 
     * @param block The suspend function to execute on Default dispatcher
     * @return The result of the block
     * 
     * Example:
     * ```kotlin
     * val result = ThreadSafetyUtil.withDefault {
     *     performComplexCalculation()
     * }
     * ```
     */
    suspend fun <T> withDefault(block: suspend CoroutineScope.() -> T): T {
        return withContext(Dispatchers.Default) { block() }
    }
    
    /**
     * Executes a block on the main thread (non-suspend version).
     * 
     * Use this when you need to run a non-suspend block on the main thread.
     * This is a coroutine-based alternative to Handler.post().
     * 
     * @param scope The CoroutineScope to use
     * @param block The non-suspend function to execute
     * @return Job reference to the launched coroutine
     * 
     * Example:
     * ```kotlin
     * ThreadSafetyUtil.runOnMainThread(scope) {
     *     updateUI()
     * }
     * ```
     */
    fun runOnMainThread(
        scope: CoroutineScope,
        block: () -> Unit
    ): Job {
        return scope.launch(Dispatchers.Main) { block() }
    }
    
    /**
     * Checks if the current thread is the main thread.
     * 
     * Use Case: Debugging and assertions to verify thread safety.
     * 
     * @return true if current thread is main thread, false otherwise
     * 
     * Example:
     * ```kotlin
     * assert(ThreadSafetyUtil.isMainThread()) {
     *     "Must be on main thread"
     * }
     * ```
     */
    fun isMainThread(): Boolean {
        return Looper.myLooper() == Looper.getMainLooper()
    }
    
    /**
     * Asserts that the current thread is the main thread.
     * 
     * Throws IllegalStateException if not on main thread.
     * 
     * Use Case: Enforcing thread safety invariants in debug builds.
     * 
     * @throws IllegalStateException if not on main thread
     * 
     * Example:
     * ```kotlin
     * fun updateUI() {
     *     ThreadSafetyUtil.assertMainThread()
     *     textView.text = "Updated"
     * }
     * ```
     */
    fun assertMainThread() {
        if (!isMainThread()) {
            throw IllegalStateException(
                "Must be called on main thread, but was called on ${Thread.currentThread().name}"
            )
        }
    }
    
    /**
     * Creates a bounded channel for coroutine communication.
     * 
     * Channels are coroutine-safe alternatives to Handler for
     * inter-coroutine communication.
     * 
     * Use Case: Producer-consumer patterns, event streams, message passing.
     * 
     * @param capacity The channel capacity (use Channel.UNLIMITED for unlimited)
     * @param T The type of elements in the channel
     * @return A new Channel
     * 
     * Example:
     * ```kotlin
     * val eventChannel = ThreadSafetyUtil.createChannel<Event>()
     * 
     * // Producer
     * launch {
     *     eventChannel.send(Event("data"))
     * }
     * 
     * // Consumer
     * launch {
     *     for (event in eventChannel) {
     *         handleEvent(event)
     *     }
     * }
     * ```
     */
    fun <T> createChannel(capacity: Int = DEFAULT_CHANNEL_CAPACITY): Channel<T> {
        return Channel(capacity)
    }
    
    /**
     * Creates a StateFlow for reactive state management.
     * 
     * StateFlow is a coroutine-safe alternative to observable state
     * with built-in concurrency guarantees.
     * 
     * Use Case: UI state, configuration, shared state.
     * 
     * @param initialValue The initial state value
     * @return A new StateFlow
     * 
     * Example:
     * ```kotlin
     * val connectionState = ThreadSafetyUtil.createStateFlow(
     *     ConnectionState.DISCONNECTED
     * )
     * 
     * // Update state
     * connectionState.value = ConnectionState.CONNECTED
     * 
     * // Observe state
     * launch {
     *     connectionState.collect { state ->
     *         updateUI(state)
     *     }
     * }
     * ```
     */
    fun <T> createStateFlow(initialValue: T): MutableStateFlow<T> {
        return MutableStateFlow(initialValue)
    }
    
    /**
     * Creates a SharedFlow for hot event streams.
     * 
     * SharedFlow is ideal for event broadcasting to multiple collectors.
     * 
     * Use Case: Events, notifications, broadcasts.
     * 
     * @param replay Number of values to replay to new subscribers
     * @param extraBufferCapacity Number of extra buffer slots
     * @param onBufferOverflow Strategy for buffer overflow
     * @return A new SharedFlow
     * 
     * Example:
     * ```kotlin
     * val eventFlow = ThreadSafetyUtil.createSharedFlow<Event>(
     *     replay = 0,
     *     extraBufferCapacity = 10,
     *     onBufferOverflow = BufferOverflow.DROP_OLDEST
     * )
     * 
     * // Emit events
     * eventFlow.tryEmit(Event("data"))
     * 
     * // Collect events
     * launch {
     *     eventFlow.collect { event ->
     *         handleEvent(event)
     *     }
     * }
     * ```
     */
    fun <T> createSharedFlow(
        replay: Int = 0,
        extraBufferCapacity: Int = 0,
        onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND
    ): MutableSharedFlow<T> {
        return MutableSharedFlow(replay, extraBufferCapacity, onBufferOverflow)
    }
    
    /**
     * Executes a block with a timeout.
     * 
     * Use Case: Preventing indefinite hangs in coroutines.
     * 
     * @param timeoutMillis Timeout in milliseconds
     * @param block The suspend function to execute
     * @return The result of the block
     * @throws TimeoutCancellationException if timeout is exceeded
     * 
     * Example:
     * ```kotlin
     * val result = ThreadSafetyUtil.withTimeout(5000) {
     *     fetchDataFromNetwork()
     * }
     * ```
     */
    suspend fun <T> withTimeout(
        timeoutMillis: Long = DEFAULT_TIMEOUT_MS,
        block: suspend CoroutineScope.() -> T
    ): T {
        return kotlinx.coroutines.withTimeout(timeoutMillis) { block() }
    }
    
    /**
     * Executes a block with a timeout, returning null on timeout.
     * 
     * Use Case: Graceful handling of timeouts without exceptions.
     * 
     * @param timeoutMillis Timeout in milliseconds
     * @param block The suspend function to execute
     * @return The result of the block, or null if timeout is exceeded
     * 
     * Example:
     * ```kotlin
     * val result = ThreadSafetyUtil.withTimeoutOrNull(5000) {
     *     fetchDataFromNetwork()
     * } ?: defaultResult
     * ```
     */
    suspend fun <T> withTimeoutOrNull(
        timeoutMillis: Long = DEFAULT_TIMEOUT_MS,
        block: suspend CoroutineScope.() -> T
    ): T? {
        return kotlinx.coroutines.withTimeoutOrNull(timeoutMillis) { block() }
    }
    
    /**
     * Retries a block with exponential backoff.
     * 
     * Use Case: Resilient network operations, retrying transient failures.
     * 
     * @param times Maximum number of retry attempts
     * @param initialDelayMillis Initial delay between retries
     * @param maxDelayMillis Maximum delay between retries
     * @param factor Exponential backoff factor
     * @param block The suspend function to retry
     * @return The result of the block
     * @throws Exception if all retries fail
     * 
     * Example:
     * ```kotlin
     * val result = ThreadSafetyUtil.retryWithBackoff(
     *     times = 3,
     *     initialDelayMillis = 1000,
     *     maxDelayMillis = 10000
     * ) {
     *     fetchDataFromNetwork()
     * }
     * ```
     */
    suspend fun <T> retryWithBackoff(
        times: Int = 3,
        initialDelayMillis: Long = 1000,
        maxDelayMillis: Long = 30000,
        factor: Double = 2.0,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelayMillis
        repeat(times - 1) {
            try {
                return block()
            } catch (e: Exception) {
                delay(currentDelay)
                currentDelay = minOf((currentDelay * factor).toLong(), maxDelayMillis)
            }
        }
        return block() // Last attempt
    }
    
    /**
     * Executes multiple operations concurrently and waits for all to complete.
     * 
     * Use Case: Parallel independent operations.
     * 
     * @param blocks List of suspend functions to execute
     * @return List of results in the same order as the blocks
     * 
     * Example:
     * ```kotlin
     * val results = ThreadSafetyUtil.parallel(
     *     { fetchUserData() },
     *     { fetchUserInventory() },
     *     { fetchUserFriends() }
     * )
     * val (userData, inventory, friends) = results
     * ```
     */
    suspend fun <T> parallel(vararg blocks: suspend () -> T): List<T> = coroutineScope {
        blocks.map { block ->
            async { block() }
        }.awaitAll()
    }
    
    /**
     * Executes multiple operations concurrently and returns the first result.
     * 
     * Use Case: Racing multiple data sources, using the fastest response.
     * 
     * @param blocks List of suspend functions to race
     * @return The result of the first completed block
     * 
     * Example:
     * ```kotlin
     * val result = ThreadSafetyUtil.race(
     *     { fetchDataFromCache() },
     *     { fetchDataFromNetwork() }
     * )
     * ```
     */
    suspend fun <T> race(vararg blocks: suspend () -> T): T = coroutineScope {
        select {
            blocks.forEach { block ->
                async { block() }.onAwait { result ->
                    result
                }
            }
        }
    }
}

/**
 * Atomic value holder for thread-safe state management.
 * 
 * Provides a coroutine-safe alternative to synchronized blocks
 * and volatile variables.
 * 
 * @param T The type of value being held
 * @property atomicReference The underlying AtomicReference
 */
class AtomicValue<T>(initialValue: T) {
    private val atomicReference = AtomicReference(initialValue)
    
    /**
     * Gets the current value.
     */
    fun get(): T = atomicReference.get()
    
    /**
     * Sets a new value.
     */
    fun set(value: T) {
        atomicReference.set(value)
    }
    
    /**
     * Gets and sets the value atomically.
     */
    fun getAndSet(value: T): T = atomicReference.getAndSet(value)
    
    /**
     * Compares and sets the value if it matches the expected value.
     */
    fun compareAndSet(expected: T, newValue: T): Boolean {
        return atomicReference.compareAndSet(expected, newValue)
    }
    
    /**
     * Updates the value using a transformation function.
     */
    fun update(transform: (T) -> T) {
        while (true) {
            val current = get()
            val newValue = transform(current)
            if (compareAndSet(current, newValue)) {
                break
            }
        }
    }
    
    /**
     * Gets the current value and updates it atomically.
     */
    fun getAndUpdate(transform: (T) -> T): T {
        while (true) {
            val current = get()
            val newValue = transform(current)
            if (compareAndSet(current, newValue)) {
                return current
            }
        }
    }
}

/**
 * Atomic counter for thread-safe counting operations.
 * 
 * Use Case: Tracking counts across coroutines, metrics collection.
 * 
 * @property atomicInteger The underlying AtomicInteger
 */
class AtomicCounter(initialValue: Int = 0) {
    private val atomicInteger = AtomicInteger(initialValue)
    
    /**
     * Gets the current value.
     */
    fun get(): Int = atomicInteger.get()
    
    /**
     * Increments the value and returns the new value.
     */
    fun incrementAndGet(): Int = atomicInteger.incrementAndGet()
    
    /**
     * Gets the current value and increments it.
     */
    fun getAndIncrement(): Int = atomicInteger.getAndIncrement()
    
    /**
     * Decrements the value and returns the new value.
     */
    fun decrementAndGet(): Int = atomicInteger.decrementAndGet()
    
    /**
     * Gets the current value and decrements it.
     */
    fun getAndDecrement(): Int = atomicInteger.getAndDecrement()
    
    /**
     * Adds a delta to the value and returns the new value.
     */
    fun addAndGet(delta: Int): Int = atomicInteger.addAndGet(delta)
    
    /**
     * Compares and sets the value if it matches the expected value.
     */
    fun compareAndSet(expected: Int, newValue: Int): Boolean {
        return atomicInteger.compareAndSet(expected, newValue)
    }
}

/**
 * Atomic flag for thread-safe boolean state.
 * 
 * Use Case: Tracking enabled/disabled states, guarding one-time operations.
 * 
 * @property atomicBoolean The underlying AtomicBoolean
 */
class AtomicFlag(initialValue: Boolean = false) {
    private val atomicBoolean = AtomicBoolean(initialValue)
    
    /**
     * Gets the current value.
     */
    fun get(): Boolean = atomicBoolean.get()
    
    /**
     * Sets a new value.
     */
    fun set(value: Boolean) {
        atomicBoolean.set(value)
    }
    
    /**
     * Sets the value to true and returns the previous value.
     */
    fun getAndSet(value: Boolean): Boolean = atomicBoolean.getAndSet(value)
    
    /**
     * Sets the value to true if it's currently false.
     * 
     * @return true if the value was set, false if it was already true
     */
    fun compareAndSetFalse(): Boolean {
        return atomicBoolean.compareAndSet(false, true)
    }
    
    /**
     * Sets the value to false if it's currently true.
     * 
     * @return true if the value was cleared, false if it was already false
     */
    fun compareAndSetTrue(): Boolean {
        return atomicBoolean.compareAndSet(true, false)
    }
}