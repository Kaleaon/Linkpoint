package butterknife

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class OnItemClick(
    @IdRes val value: IntArray = intArrayOf(-1)
)
