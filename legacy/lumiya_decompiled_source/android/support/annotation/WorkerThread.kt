package android.support.annotation

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.CLASS)
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class WorkerThread
