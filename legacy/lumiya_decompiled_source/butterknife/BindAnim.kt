package butterknife

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class BindAnim(
    @AnimRes val value: Int
)
