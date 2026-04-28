package com.linkpoint.linden.llprimitive

import com.linkpoint.linden.llcommon.LLSD
import com.linkpoint.linden.llcommon.LLUUID
import com.linkpoint.linden.llmath.Vector2

data class LLMaterial(
    var id: LLMaterialID = LLMaterialID.generate(),
    var normalMapId: LLUUID = LLUUID.NULL,
    var specularMapId: LLUUID = LLUUID.NULL,
    var albedoMapId: LLUUID = LLUUID.NULL,
    var normalRepeat: Vector2 = Vector2(1f, 1f),
    var normalOffset: Vector2 = Vector2(0f, 0f),
    var normalRotation: Float = 0f,
    var specularRepeat: Vector2 = Vector2(1f, 1f),
    var specularOffset: Vector2 = Vector2(0f, 0f),
    var specularRotation: Float = 0f,
    var specularColor: FloatArray = floatArrayOf(1f, 1f, 1f, 1f),
    var specularExp: Int = 0,
    var envIntensity: Int = 0,
    var diffuseAlphaMode: Int = 0,
    var alphaThreshold: Int = 0
) {
    fun toLLSD(): LLSD = LLSD.ofMap(mapOf(
        "NormMap"   to LLSD.of(normalMapId),
        "SpecMap"   to LLSD.of(specularMapId),
        "AlbedoMap" to LLSD.of(albedoMapId),
        "NormRepX"  to LLSD.of(normalRepeat.x.toDouble()),
        "NormRepY"  to LLSD.of(normalRepeat.y.toDouble()),
        "NormOffX"  to LLSD.of(normalOffset.x.toDouble()),
        "NormOffY"  to LLSD.of(normalOffset.y.toDouble()),
        "NormRot"   to LLSD.of(normalRotation.toDouble()),
        "SpecRepX"  to LLSD.of(specularRepeat.x.toDouble()),
        "SpecRepY"  to LLSD.of(specularRepeat.y.toDouble()),
        "SpecOffX"  to LLSD.of(specularOffset.x.toDouble()),
        "SpecOffY"  to LLSD.of(specularOffset.y.toDouble()),
        "SpecRot"   to LLSD.of(specularRotation.toDouble()),
        "SpecExp"   to LLSD.of(specularExp),
        "EnvInt"    to LLSD.of(envIntensity),
        "DiffAlpMode" to LLSD.of(diffuseAlphaMode),
        "AlphaMaskCutoff" to LLSD.of(alphaThreshold)
    ))

    companion object {
        fun fromLLSD(sd: LLSD): LLMaterial = LLMaterial(
            normalMapId    = sd["NormMap"].asUUID(),
            specularMapId  = sd["SpecMap"].asUUID(),
            albedoMapId    = sd["AlbedoMap"].asUUID(),
            normalRepeat   = Vector2(sd["NormRepX"].asReal().toFloat(), sd["NormRepY"].asReal().toFloat()),
            normalOffset   = Vector2(sd["NormOffX"].asReal().toFloat(), sd["NormOffY"].asReal().toFloat()),
            normalRotation = sd["NormRot"].asReal().toFloat(),
            specularRepeat = Vector2(sd["SpecRepX"].asReal().toFloat(), sd["SpecRepY"].asReal().toFloat()),
            specularOffset = Vector2(sd["SpecOffX"].asReal().toFloat(), sd["SpecOffY"].asReal().toFloat()),
            specularRotation = sd["SpecRot"].asReal().toFloat(),
            specularExp    = sd["SpecExp"].asInt(),
            envIntensity   = sd["EnvInt"].asInt(),
            diffuseAlphaMode = sd["DiffAlpMode"].asInt(),
            alphaThreshold = sd["AlphaMaskCutoff"].asInt()
        )
    }
}
