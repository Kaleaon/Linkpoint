package com.linkpoint.linden.llappearance

data class VisualParamInfo(
    val id: Int,
    val name: String,
    val group: Int,
    val minValue: Float,
    val maxValue: Float,
    val defaultValue: Float,
    val label: String = name,
    val labelMin: String = "",
    val labelMax: String = "",
)

class LLVisualParam(val info: VisualParamInfo) {

    val id: Int get() = info.id
    val name: String get() = info.name

    @get:JvmName("getWeightProp")
    var weight: Float = info.defaultValue
        private set

    var weightBeforeChange: Float = info.defaultValue
        private set

    var isAnimating: Boolean = false
        private set

    private val morphTargetAssociations: MutableList<String> = mutableListOf()
    private val driverAssociations: MutableList<Int> = mutableListOf()

    fun setWeight(w: Float, uploadBake: Boolean = false) {
        weightBeforeChange = weight
        weight = w.coerceIn(info.minValue, info.maxValue)
    }

    fun setWeightBeforeChange(w: Float) {
        weightBeforeChange = w.coerceIn(info.minValue, info.maxValue)
    }

    fun startAnimating() { isAnimating = true }
    fun stopAnimating() { isAnimating = false }

    fun getWeight(): Float = weight

    fun getDefaultWeight(): Float = info.defaultValue

    fun addMorphTarget(name: String) { morphTargetAssociations.add(name) }

    fun addDriver(paramId: Int) { driverAssociations.add(paramId) }

    fun getMorphTargets(): List<String> = morphTargetAssociations.toList()

    fun getDrivers(): List<Int> = driverAssociations.toList()

    fun reset() {
        weight = info.defaultValue
        weightBeforeChange = info.defaultValue
        isAnimating = false
    }

    override fun toString(): String =
        "LLVisualParam(id=${info.id}, name=${info.name}, weight=$weight)"
}
