package android.support.annotation

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class StringDef(
    val value: Array<String> = arrayOf()
)
