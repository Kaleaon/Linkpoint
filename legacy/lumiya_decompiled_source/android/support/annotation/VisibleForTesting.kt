package android.support.annotation

@Retention(AnnotationRetention.BINARY)
annotation class VisibleForTesting(
    val otherwise: Int = 2
)
