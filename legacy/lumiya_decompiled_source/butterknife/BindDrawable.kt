package butterknife

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class BindDrawable(
    @AttrRes val tint: Int = 0,
    @DrawableRes val value: Int
)
