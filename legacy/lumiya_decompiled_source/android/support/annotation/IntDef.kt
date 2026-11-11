package android.support.annotation

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class IntDef(
    val flag: Boolean = false,
    val value: LongArray = longArrayOf()
)
