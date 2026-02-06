package butterknife

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class BindInt(
    @IntegerRes val value: Int
)
