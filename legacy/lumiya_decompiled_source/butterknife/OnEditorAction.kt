package butterknife

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class OnEditorAction(
    @IdRes val value: IntArray = intArrayOf(-1)
)
