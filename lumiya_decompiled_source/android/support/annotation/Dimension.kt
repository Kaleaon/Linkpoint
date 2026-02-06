package android.support.annotation

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.LOCAL_VARIABLE, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class Dimension(
    val unit: Int = 1
)
