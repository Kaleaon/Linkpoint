package android.support.annotation

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.LOCAL_VARIABLE, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class FloatRange(
    val from: Double = Double.NEGATIVE_INFINITY,
    val fromInclusive: Boolean = true,
    val to: Double = Double.POSITIVE_INFINITY,
    val toInclusive: Boolean = true
)
