package com.linkpoint.linden.llcharacter

import com.linkpoint.linden.llcommon.LLUUID

class LLFaceMotion : LLMotion(LLUUID.generate()) {

    enum class Expression(val id: Int) {
        NEUTRAL(0), SMILE(1), SAD(2), SURPRISED(3),
        ANGRY(4), WORRIED(5), TONGUE_OUT(6), TOOTHSMILE(7)
    }

    override var priority: Int = 1
    override var duration: Float = 0f
    override var loop: Boolean = true

    var expression: Expression = Expression.NEUTRAL
    var intensity: Float = 1f

    private val faceWeights: Map<Expression, Map<String, Float>> = mapOf(
        Expression.SMILE to mapOf(
            "mFaceLeft_Lip_Corner" to 0.6f,
            "mFaceRight_Lip_Corner" to 0.6f,
            "mFaceLeft_Cheek" to 0.3f,
            "mFaceRight_Cheek" to 0.3f
        ),
        Expression.SAD to mapOf(
            "mFaceLeft_Lip_Corner" to -0.4f,
            "mFaceRight_Lip_Corner" to -0.4f,
            "mFaceInner_Brow" to 0.5f
        ),
        Expression.SURPRISED to mapOf(
            "mFaceLeft_Brow" to 0.7f,
            "mFaceRight_Brow" to 0.7f,
            "mFaceEyeLeft" to 0.5f,
            "mFaceEyeRight" to 0.5f,
            "mFaceLipUpperLeft" to 0.2f,
            "mFaceLipUpperRight" to 0.2f
        ),
        Expression.ANGRY to mapOf(
            "mFaceLeft_Brow" to -0.5f,
            "mFaceRight_Brow" to -0.5f,
            "mFaceInner_Brow" to 0.6f
        )
    )

    override fun onUpdate(time: Float): Boolean = true

    fun getMorphWeight(morphName: String): Float {
        val weights = faceWeights[expression] ?: return 0f
        return (weights[morphName] ?: 0f) * intensity
    }
}
