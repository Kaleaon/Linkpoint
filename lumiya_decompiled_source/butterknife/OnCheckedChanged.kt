package butterknife

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class OnCheckedChanged(
    @IdRes val value: IntArray = intArrayOf(-1)
)
