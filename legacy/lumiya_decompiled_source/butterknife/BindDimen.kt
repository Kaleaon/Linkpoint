package butterknife

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class BindDimen(
    @DimenRes val value: Int
)
