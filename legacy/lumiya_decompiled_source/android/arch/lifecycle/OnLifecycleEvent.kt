package android.arch.lifecycle

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnLifecycleEvent(
    val value: Lifecycle.Event
)
