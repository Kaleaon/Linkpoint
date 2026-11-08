package butterknife

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class BindFont(
    @TypefaceStyle val style: Int = 0,
    val value: Int
) {
    @android.support.annotation.RestrictTo([android.support.annotation.RestrictTo.Scope.LIBRARY])
    annotation class TypefaceStyle
}
