package butterknife

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class BindColor(
    @ColorRes val value: Int
)
