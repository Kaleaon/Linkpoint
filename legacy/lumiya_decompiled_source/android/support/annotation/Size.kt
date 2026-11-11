package android.support.annotation

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.LOCAL_VARIABLE, AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Size(
    val max: Long = Long.MAX_VALUE,
    val min: Long = Long.MIN_VALUE,
    val multiple: Long = 1,
    val value: Long = -1
)
