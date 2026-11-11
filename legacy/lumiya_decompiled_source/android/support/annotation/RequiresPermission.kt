package android.support.annotation

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.BINARY)
annotation class RequiresPermission(
    val allOf: Array<String> = arrayOf(),
    val anyOf: Array<String> = arrayOf(),
    val conditional: Boolean = false,
    val value: String = ""
) {
    @Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
    @Retention(AnnotationRetention.BINARY)
    annotation class Read(
        val value: RequiresPermission = RequiresPermission()
    )

    @Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
    @Retention(AnnotationRetention.BINARY)
    annotation class Write(
        val value: RequiresPermission = RequiresPermission()
    )
}
