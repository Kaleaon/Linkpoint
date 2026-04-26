package com.linkpoint.util

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.linkpoint.network.NetworkLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.ConcurrentHashMap

/**
 * Lifecycle-aware coroutine scope manager for the Linkpoint app.
 * 
 * This utility provides automatic coroutine scope management tied to Android
 * lifecycle components (Activity, Fragment, Application). It prevents memory
 * leaks by automatically canceling scopes when components are destroyed.
 * 
 * Design Philosophy:
 * - Automatic lifecycle integration
 * - No manual scope management required
 * - Prevents memory leaks through automatic cancellation
 * - Supports component-scoped operations
 * - Follows Android lifecycle best practices
 * 
 * Key Features:
 * - Automatic scope creation for Activities/Fragments
 * - Automatic scope cancellation on lifecycle events
 * - Process-wide scope management
 * - Custom scope factories
 * - Thread-safe operations
 * - Debug logging for lifecycle events
 * 
 * @see Lifecycle
 * @see CoroutineScope
 * @see SupervisorJob
 * 
 * Usage Example (Activity):
 * ```kotlin
 * class MainActivity : AppCompatActivity() {
 *     private val scope = LifecycleAwareScopeManager.getScope(this)
 *     
 *     fun loadData() {
 *         scope.launch {
 *             val data = fetchData()
 *             updateUI(data)
 *         }
 *     }
 *     
 *     // No need to call scope.cancel() - handled automatically
 * }
 * ```
 * 
 * Usage Example (Fragment):
 * ```kotlin
 * class MyFragment : Fragment() {
 *     private val scope = LifecycleAwareScopeManager.getScope(this)
 *     
 *     fun loadData() {
 *         scope.launch {
 *             val data = fetchData()
 *             updateUI(data)
 *         }
 *     }
 *     
 *     // No need to call scope.cancel() - handled automatically
 * }
 * ```
 * 
 * Usage Example (Application-wide):
 * ```kotlin
 * class LinkpointApplication : Application() {
 *     override fun onCreate() {
 *         super.onCreate()
 *         LifecycleAwareScopeManager.initialize(this)
 *         
 *         val appScope = LifecycleAwareScopeManager.getApplicationScope()
 *         appScope.launch {
 *             // Application-wide operations
 *         }
 *     }
 * }
 * ```
 * 
 * @author SuperNinja AI
 * @since 1.0
 */
object LifecycleAwareScopeManager {
    
    private const val TAG = "LifecycleScopeManager"
    
    /**
     * Registry of active scopes mapped to lifecycle owners.
     * 
     * Uses ConcurrentHashMap for thread-safe operations across multiple threads.
     */
    private val scopeRegistry = ConcurrentHashMap<LifecycleOwner, ManagedScope>()
    
    /**
     * Application-wide scope for operations that should span the entire app lifecycle.
     * 
     * This scope is only cancelled when the application process is terminated.
     */
    private var applicationScope: CoroutineScope? = null
    
    /**
     * Flag indicating whether the manager has been initialized.
     */
    private var isInitialized = false
    
    /**
     * Factory function for creating custom scopes.
     */
    private var scopeFactory: ScopeFactory = DefaultScopeFactory()
    
    /**
     * Initializes the scope manager with application context.
     * 
     * This should be called in Application.onCreate() to set up
     * the application-wide scope and process lifecycle tracking.
     * 
     * @param application The application context
     * 
     * Example:
     * ```kotlin
     * class MyApplication : Application() {
     *     override fun onCreate() {
     *         super.onCreate()
     *         LifecycleAwareScopeManager.initialize(this)
     *     }
     * }
     * ```
     */
    fun initialize(application: Application) {
        if (isInitialized) {
            android.util.Log.w(TAG, "LifecycleAwareScopeManager already initialized")
            return
        }
        
        isInitialized = true
        
        // Create application-wide scope
        applicationScope = CoroutineScope(
            Dispatchers.Default + SupervisorJob()
        )
        
        // Observe process lifecycle events
        ProcessLifecycleOwner.get().lifecycle.addObserver(
            ProcessLifecycleObserver()
        )
        
        android.util.Log.d(TAG, "LifecycleAwareScopeManager initialized")
    }
    
    /**
     * Gets or creates a coroutine scope for the given Activity.
     * 
     * The scope is automatically cancelled when the Activity is destroyed,
     * preventing memory leaks and resource waste.
     * 
     * @param activity The Activity to create scope for
     * @return A CoroutineScope tied to the Activity's lifecycle
     * @throws IllegalStateException if manager is not initialized
     * 
     * Example:
     * ```kotlin
     * class MainActivity : AppCompatActivity() {
     *     private val scope = LifecycleAwareScopeManager.getScope(this)
     *     
     *     fun loadData() {
     *         scope.launch {
     *             // Background work
     *         }
     *     }
     * }
     * ```
     */
    fun getScope(activity: Activity): CoroutineScope {
        checkInitialized()
        // Modern Activities (ComponentActivity, AppCompatActivity) implement LifecycleOwner
        require(activity is LifecycleOwner) { 
            "Activity must implement LifecycleOwner (use ComponentActivity or AppCompatActivity)" 
        }
        return getOrCreateScope(activity as LifecycleOwner) { createActivityScope(activity) }
    }
    
    /**
     * Gets or creates a coroutine scope for the given Fragment.
     * 
     * The scope is automatically cancelled when the Fragment is destroyed,
     * preventing memory leaks and resource waste.
     * 
     * @param fragment The Fragment to create scope for
     * @return A CoroutineScope tied to the Fragment's lifecycle
     * @throws IllegalStateException if manager is not initialized
     * 
     * Example:
     * ```kotlin
     * class MyFragment : Fragment() {
     *     private val scope = LifecycleAwareScopeManager.getScope(this)
     *     
     *     fun loadData() {
     *         scope.launch {
     *             // Background work
     *         }
     *     }
     * }
     * ```
     */
    fun getScope(fragment: Fragment): CoroutineScope {
        checkInitialized()
        return getOrCreateScope(fragment) { createFragmentScope(fragment) }
    }
    
    /**
     * Gets or creates a coroutine scope for any LifecycleOwner.
     * 
     * Generic method for creating scopes for any lifecycle-aware component.
     * 
     * @param owner The LifecycleOwner to create scope for
     * @return A CoroutineScope tied to the lifecycle owner
     * @throws IllegalStateException if manager is not initialized
     * 
     * Example:
     * ```kotlin
     * class MyCustomView : LifecycleOwner {
     *     private val scope = LifecycleAwareScopeManager.getScope(this)
     *     
     *     fun doWork() {
     *         scope.launch {
     *             // Background work
     *         }
     *     }
     * }
     * ```
     */
    fun getScope(owner: LifecycleOwner): CoroutineScope {
        checkInitialized()
        return getOrCreateScope(owner) { createDefaultScope(owner) }
    }
    
    /**
     * Gets the application-wide coroutine scope.
     * 
     * This scope is not tied to any Activity or Fragment and persists
     * for the entire application lifecycle. Use this for operations
     * that should continue even when all Activities are destroyed.
     * 
     * @return The application-wide CoroutineScope
     * @throws IllegalStateException if manager is not initialized
     * 
     * Example:
     * ```kotlin
     * class MyApplication : Application() {
     *     override fun onCreate() {
     *         super.onCreate()
     *         LifecycleAwareScopeManager.initialize(this)
     *         
     *         val appScope = LifecycleAwareScopeManager.getApplicationScope()
     *         appScope.launch {
     *             // Background services, caching, etc.
     *         }
     *     }
     * }
     * ```
     */
    fun getApplicationScope(): CoroutineScope {
        checkInitialized()
        return applicationScope ?: throw IllegalStateException(
            "Application scope not available. Call initialize() first."
        )
    }
    
    /**
     * Manually cancels a scope for a specific lifecycle owner.
     * 
     * Use this if you need to cancel a scope before the lifecycle event
     * that would normally cancel it.
     * 
     * @param owner The LifecycleOwner whose scope should be cancelled
     * 
     * Example:
     * ```kotlin
     * fun onLogout() {
     *     LifecycleAwareScopeManager.cancelScope(this)
     *     // All pending coroutines for this Activity will be cancelled
     * }
     * ```
     */
    fun cancelScope(owner: LifecycleOwner) {
        val managedScope = scopeRegistry.remove(owner)
        managedScope?.let {
            it.scope.cancel()
            android.util.Log.d(TAG, "Cancelled scope for ${getOwnerName(owner)}")
        }
    }
    
    /**
     * Sets a custom scope factory for specialized scope creation.
     * 
     * Use this to implement custom scope creation logic for specific
     * use cases or testing scenarios.
     * 
     * @param factory The custom ScopeFactory implementation
     * 
     * Example:
     * ```kotlin
     * // For testing
     * LifecycleAwareScopeManager.setScopeFactory(object : ScopeFactory {
     *     override fun createScope(owner: LifecycleOwner): ManagedScope {
     *         return ManagedScope(
     *             TestCoroutineScope(),
     *             owner.lifecycle
     *         )
     *     }
     * })
     * ```
     */
    fun setScopeFactory(factory: ScopeFactory) {
        this.scopeFactory = factory
    }
    
    /**
     * Resets the scope factory to the default implementation.
     * 
     * Useful for testing cleanup or switching back to default behavior.
     * 
     * Example:
     * ```kotlin
     * @After
     * fun tearDown() {
     *     LifecycleAwareScopeManager.setScopeFactory(null)
     * }
     * ```
     */
    fun resetScopeFactory() {
        this.scopeFactory = DefaultScopeFactory()
    }
    
    /**
     * Gets the number of active scopes.
     * 
     * Useful for debugging and monitoring resource usage.
     * 
     * @return The number of currently active scopes
     * 
     * Example:
     * ```kotlin
     * val activeScopes = LifecycleAwareScopeManager.getActiveScopeCount()
     * android.util.Log.d("Debug", "Active scopes: $activeScopes")
     * ```
     */
    fun getActiveScopeCount(): Int {
        return scopeRegistry.size
    }
    
    /**
     * Checks if the manager has been initialized.
     * 
     * @return true if initialized, false otherwise
     */
    fun isInitialized(): Boolean {
        return isInitialized
    }
    
    // ==================== Private Methods ====================
    
    /**
     * Checks if the manager is initialized and throws if not.
     */
    private fun checkInitialized() {
        if (!isInitialized) {
            throw IllegalStateException(
                "LifecycleAwareScopeManager not initialized. " +
                "Call initialize(application) before using."
            )
        }
    }
    
    /**
     * Gets or creates a scope for the given lifecycle owner.
     * 
     * Thread-safe operation that returns existing scope if available,
     * or creates a new one otherwise.
     */
    private fun getOrCreateScope(
        owner: LifecycleOwner,
        scopeCreator: () -> ManagedScope
    ): CoroutineScope {
        return scopeRegistry.getOrPut(owner) {
            val managedScope = scopeCreator()
            android.util.Log.d(
                TAG, 
                "Created scope for ${getOwnerName(owner)}"
            )
            managedScope
        }.scope
    }
    
    /**
     * Creates a scope optimized for Activity lifecycle.
     */
    private fun createActivityScope(activity: Activity): ManagedScope {
        // Activity should implement LifecycleOwner (checked in getScope)
        val owner = activity as LifecycleOwner
        val managedScope = scopeFactory.createScope(owner)
        owner.lifecycle.addObserver(
            ActivityLifecycleObserver(owner, managedScope)
        )
        return managedScope
    }
    
    /**
     * Creates a scope optimized for Fragment lifecycle.
     */
    private fun createFragmentScope(fragment: Fragment): ManagedScope {
        val managedScope = scopeFactory.createScope(fragment)
        fragment.lifecycle.addObserver(
            FragmentLifecycleObserver(fragment, managedScope)
        )
        
        // Also listen for fragment detachment
        if (fragment.activity is FragmentActivity) {
            (fragment.activity as FragmentActivity).supportFragmentManager
                .registerFragmentLifecycleCallbacks(
                    FragmentLifecycleCallback(fragment, managedScope),
                    true
                )
        }
        
        return managedScope
    }
    
    /**
     * Creates a default scope for generic LifecycleOwner.
     */
    private fun createDefaultScope(owner: LifecycleOwner): ManagedScope {
        val managedScope = scopeFactory.createScope(owner)
        owner.lifecycle.addObserver(
            GenericLifecycleObserver(owner, managedScope)
        )
        return managedScope
    }
    
    /**
     * Gets a descriptive name for a lifecycle owner for logging.
     */
    private fun getOwnerName(owner: LifecycleOwner): String {
        return when (owner) {
            is Activity -> owner.javaClass.simpleName
            is Fragment -> "${owner.javaClass.simpleName} (Fragment)"
            is Application -> owner.javaClass.simpleName
            else -> owner.javaClass.simpleName
        }
    }
    
    // ==================== Inner Classes ====================
    
    /**
     * Managed scope wrapper that holds both the scope and lifecycle reference.
     */
    data class ManagedScope(
        val scope: CoroutineScope,
        val lifecycle: Lifecycle
    )
    
    /**
     * Factory interface for creating custom scopes.
     */
    interface ScopeFactory {
        fun createScope(owner: LifecycleOwner): ManagedScope
    }
    
    /**
     * Default scope factory implementation.
     */
    private class DefaultScopeFactory : ScopeFactory {
        override fun createScope(owner: LifecycleOwner): ManagedScope {
            val scope = when (owner) {
                is Application -> CoroutineScope(
                    Dispatchers.Default + SupervisorJob()
                )
                is Activity -> CoroutineScope(
                    Dispatchers.Main.immediate + SupervisorJob()
                )
                is Fragment -> CoroutineScope(
                    Dispatchers.Main.immediate + SupervisorJob()
                )
                else -> CoroutineScope(
                    Dispatchers.Default + SupervisorJob()
                )
            }
            
            return ManagedScope(scope, owner.lifecycle)
        }
    }
    
    /**
     * Lifecycle observer for Activity components.
     */
    private class ActivityLifecycleObserver(
        private val activity: LifecycleOwner,
        private val managedScope: ManagedScope
    ) : DefaultLifecycleObserver {
        
        override fun onDestroy(owner: LifecycleOwner) {
            if (managedScope.scope.isActive) {
                managedScope.scope.cancel()
                scopeRegistry.remove(activity)
                android.util.Log.d(TAG, "Cancelled scope for ${activity.javaClass.simpleName}")
            }
        }
    }
    
    /**
     * Lifecycle observer for Fragment components.
     */
    private class FragmentLifecycleObserver(
        private val fragment: Fragment,
        private val managedScope: ManagedScope
    ) : DefaultLifecycleObserver {
        
        override fun onDestroy(owner: LifecycleOwner) {
            if (managedScope.scope.isActive) {
                managedScope.scope.cancel()
                scopeRegistry.remove(fragment)
                android.util.Log.d(TAG, "Cancelled scope for ${fragment.javaClass.simpleName} (Fragment)")
            }
        }
    }
    
    /**
     * Lifecycle observer for generic LifecycleOwner components.
     */
    private class GenericLifecycleObserver(
        private val owner: LifecycleOwner,
        private val managedScope: ManagedScope
    ) : DefaultLifecycleObserver {
        
        override fun onDestroy(owner: LifecycleOwner) {
            if (managedScope.scope.isActive) {
                managedScope.scope.cancel()
                scopeRegistry.remove(owner)
                android.util.Log.d(TAG, "Cancelled scope for ${owner.javaClass.simpleName}")
            }
        }
    }
    
    /**
     * Fragment lifecycle callback for handling fragment detachment.
     */
    private class FragmentLifecycleCallback(
        private val targetFragment: Fragment,
        private val managedScope: ManagedScope
    ) : FragmentManager.FragmentLifecycleCallbacks() {
        
        override fun onFragmentDetached(
            fm: FragmentManager,
            f: Fragment
        ) {
            if (f == targetFragment && managedScope.scope.isActive) {
                managedScope.scope.cancel()
                scopeRegistry.remove(targetFragment)
                android.util.Log.d(TAG, "Cancelled scope for detached fragment: ${targetFragment.javaClass.simpleName}")
            }
        }
    }
    
    /**
     * Process lifecycle observer for application-level events.
     */
    private class ProcessLifecycleObserver : DefaultLifecycleObserver {
        
        override fun onStop(owner: LifecycleOwner) {
            android.util.Log.d(TAG, "Application moved to background")
            NetworkLogger.log(
                NetworkLogger.Level.INFO,
                NetworkLogger.Category.LIFECYCLE,
                "📱 App → background (process onStop)"
            )
        }

        override fun onStart(owner: LifecycleOwner) {
            android.util.Log.d(TAG, "Application moved to foreground")
            NetworkLogger.log(
                NetworkLogger.Level.INFO,
                NetworkLogger.Category.LIFECYCLE,
                "📱 App → foreground (process onStart)"
            )
        }
    }
}