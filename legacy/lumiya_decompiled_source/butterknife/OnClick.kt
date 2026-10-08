package butterknife

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class OnClick(
    @IdRes val value: IntArray = intArrayOf(-1)
)
