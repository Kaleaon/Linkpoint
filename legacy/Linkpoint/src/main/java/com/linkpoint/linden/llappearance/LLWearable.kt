package com.linkpoint.linden.llappearance

import com.linkpoint.linden.llcommon.LLUUID

class LLWearable(
    val type: LLWearableType,
    var name: String = type.label,
    var description: String = "",
) {

    var assetId: LLUUID = LLUUID.NULL
    var isModified: Boolean = false
        private set

    private val visualParams: MutableMap<Int, LLVisualParam> = mutableMapOf()
    private val textures: MutableMap<Int, LLUUID> = mutableMapOf()

    fun addVisualParam(param: LLVisualParam) {
        visualParams[param.id] = param
    }

    fun getVisualParam(id: Int): LLVisualParam? = visualParams[id]

    fun setParamWeight(id: Int, weight: Float) {
        visualParams[id]?.setWeight(weight)
        isModified = true
    }

    fun getParamWeight(id: Int): Float =
        visualParams[id]?.getWeight() ?: 0f

    fun setTexture(index: Int, textureId: LLUUID) {
        textures[index] = textureId
        isModified = true
    }

    fun getTexture(index: Int): LLUUID = textures[index] ?: LLUUID.NULL

    fun getVisualParams(): Map<Int, LLVisualParam> = visualParams.toMap()

    fun getTextures(): Map<Int, LLUUID> = textures.toMap()

    fun exportStream(): String {
        val sb = StringBuilder()
        sb.appendLine("LLWearable version 22")
        sb.appendLine(name)
        sb.appendLine()
        sb.appendLine("\tpermissions 0")
        sb.appendLine("\t{")
        sb.appendLine("\t}")
        sb.appendLine("\tsale_info 0")
        sb.appendLine("\t{")
        sb.appendLine("\t}")
        sb.appendLine("type ${type.layerOrder}")
        sb.appendLine("parameters ${visualParams.size}")
        visualParams.forEach { (id, param) ->
            sb.appendLine("$id ${param.getWeight()}")
        }
        sb.appendLine("textures ${textures.size}")
        textures.forEach { (idx, uuid) ->
            sb.appendLine("$idx $uuid")
        }
        return sb.toString()
    }

    fun importLegacyStream(data: String): Boolean {
        val lines = data.lines().map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.isEmpty()) return false
        var i = 0
        while (i < lines.size) {
            val line = lines[i++]
            when {
                line.startsWith("parameters ") -> {
                    val count = line.removePrefix("parameters ").trim().toIntOrNull() ?: 0
                    repeat(count) {
                        if (i < lines.size) {
                            val parts = lines[i++].split(" ")
                            if (parts.size >= 2) {
                                val id = parts[0].toIntOrNull() ?: return@repeat
                                val w = parts[1].toFloatOrNull() ?: return@repeat
                                visualParams[id]?.setWeight(w)
                            }
                        }
                    }
                }
                line.startsWith("textures ") -> {
                    val count = line.removePrefix("textures ").trim().toIntOrNull() ?: 0
                    repeat(count) {
                        if (i < lines.size) {
                            val parts = lines[i++].split(" ")
                            if (parts.size >= 2) {
                                val idx = parts[0].toIntOrNull() ?: return@repeat
                                textures[idx] = LLUUID(parts[1])
                            }
                        }
                    }
                }
            }
        }
        isModified = false
        return true
    }

    fun clearModified() { isModified = false }

    override fun toString(): String = "LLWearable(type=${type.typeName}, name=$name, params=${visualParams.size})"
}
