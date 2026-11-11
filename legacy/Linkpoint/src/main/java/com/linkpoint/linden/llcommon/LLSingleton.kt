package com.linkpoint.linden.llcommon

import java.util.concurrent.atomic.AtomicReference

/**
 * Marker interface equivalent to LLIsSingleton.
 * Attach to any singleton companion or object that participates in the
 * singleton registry.
 */
interface IsSingleton

/**
 * Abstract base for CRTP-style singletons.  Subclasses must provide a
 * no-argument factory via [create] so [getInstance] can construct the first
 * instance lazily in a thread-safe manner.
 *
 * Usage:
 *
 *   class Foo private constructor() : Singleton<Foo>() {
 *       companion object : SingletonFactory<Foo>({ Foo() })
 *   }
 *
 *   val foo = Foo.getInstance()
 */
abstract class Singleton<T : Singleton<T>> : IsSingleton {

    companion object Registry {
        private val registry = mutableListOf<AtomicReference<*>>()

        /** Called by deleteAll() – for test-teardown convenience. */
        @Synchronized
        fun deleteAll() {
            registry.forEach { it.set(null) }
            registry.clear()
        }
    }

    /**
     * Each concrete subclass exposes its own holder via its companion object
     * extending this class.
     */
    abstract class SingletonFactory<T : Singleton<T>>(
        private val factory: () -> T,
    ) : IsSingleton {
        @Volatile
        private var instance: T? = null
        private val ref = AtomicReference<T>(null)

        init {
            @Suppress("LeakingThis")
            Registry.registry.add(ref)
        }

        fun getInstance(): T {
            val existing = ref.get()
            if (existing != null) return existing
            synchronized(this) {
                val checked = ref.get()
                if (checked != null) return checked
                val created = factory()
                ref.set(created)
                created.initSingleton()
                return created
            }
        }

        fun deleteSingleton() {
            synchronized(this) {
                ref.get()?.cleanupSingleton()
                ref.set(null)
            }
        }
    }

    /** Override to perform post-construction initialization. */
    protected open fun initSingleton() {}

    /** Override to perform pre-destruction cleanup. */
    protected open fun cleanupSingleton() {}
}
