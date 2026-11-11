package android.support.annotation

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class RequiresApi(
    @IntRange(from = 1) val api: Int = 1,
    @IntRange(from = 1) val value: Int = 1
)
