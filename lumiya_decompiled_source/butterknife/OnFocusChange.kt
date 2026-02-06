package butterknife

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class OnFocusChange(
    @IdRes val value: IntArray = intArrayOf(-1)
)
