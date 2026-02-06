package android.support.annotation

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.LOCAL_VARIABLE)
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class RawRes
