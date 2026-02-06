package butterknife

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class BindString(
    @StringRes val value: Int
)
