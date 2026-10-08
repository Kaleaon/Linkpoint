package com.linkpoint.linden.llcharacter

import com.linkpoint.linden.llmath.Matrix3
import com.linkpoint.linden.llmath.Vector3

enum class LoadStatus {
    OK,
    EOF,
    NO_CONSTRAINT,
    NO_FILE,
    NO_HIER,
    NO_JOINT,
    NO_NAME,
    NO_OFFSET,
    NO_CHANNELS,
    NO_ROTATION,
    NO_AXIS,
    NO_MOTION,
    NO_FRAMES,
    NO_FRAME_TIME,
    NO_POS,
    NO_ROT,
    NO_XLT_FILE,
    NO_XLT_HEADER,
    NO_XLT_NAME,
    NO_XLT_IGNORE,
    NO_XLT_RELATIVE,
    NO_XLT_OUTNAME,
    NO_XLT_MATRIX,
    NO_XLT_MERGECHILD,
    NO_XLT_MERGEPARENT,
    NO_XLT_PRIORITY,
    NO_XLT_LOOP,
    NO_XLT_EASEIN,
    NO_XLT_EASEOUT,
    NO_XLT_HAND,
    NO_XLT_EMOTE,
    BAD_ROOT
}

enum class ConstraintType { POINT, PLANE }
enum class ConstraintTargetType { BODY, GROUND }

data class BvhKey(
    var pos: FloatArray = FloatArray(3),
    var rot: FloatArray = FloatArray(3),
    var ignorePos: Boolean = false,
    var ignoreRot: Boolean = false
)

data class BvhJoint(
    val name: String,
    var ignore: Boolean = false,
    var ignorePositions: Boolean = false,
    var relativePositionKey: Boolean = false,
    var relativeRotationKey: Boolean = false,
    var outName: String = name,
    var mergeParentName: String = "",
    var mergeChildName: String = "",
    var order: String = "XYZ",
    val keys: MutableList<BvhKey> = mutableListOf(),
    var numPosKeys: Int = 0,
    var numRotKeys: Int = 0,
    var childTreeMaxDepth: Int = 0,
    var priority: Int = 0,
    var numChannels: Int = 3,
    var frameMatrix: Matrix3 = Matrix3(),
    var offsetMatrix: Matrix3 = Matrix3(),
    var relativePosition: Vector3 = Vector3()
)

data class BvhConstraint(
    val sourceJointName: String,
    val targetJointName: String,
    val chainLength: Int,
    val sourceOffset: Vector3,
    val targetOffset: Vector3,
    val targetDir: Vector3,
    val easeInStart: Float,
    val easeInStop: Float,
    val easeOutStart: Float,
    val easeOutStop: Float,
    val constraintType: ConstraintType
)

class Translation {
    var outName: String = ""
    var ignore: Boolean = false
    var ignorePositions: Boolean = false
    var relativePositionKey: Boolean = false
    var relativeRotationKey: Boolean = false
    var frameMatrix: Matrix3 = Matrix3()
    var offsetMatrix: Matrix3 = Matrix3()
    var relativePosition: Vector3 = Vector3()
    var mergeParentName: String = ""
    var mergeChildName: String = ""
    var priorityModifier: Int = 0
}

class BVHLoader(buffer: String, jointAliasMap: Map<String, String> = emptyMap()) {

    val joints: MutableList<BvhJoint> = mutableListOf()
    val constraints: MutableList<BvhConstraint> = mutableListOf()
    val translations: MutableMap<String, Translation> = mutableMapOf()

    var priority: Int = 0
    var loop: Boolean = false
    var loopInPoint: Float = 0f
    var loopOutPoint: Float = 0f
    var easeIn: Float = 0f
    var easeOut: Float = 0f
    var hand: Int = 0
    var emoteName: String = ""
    var initialized: Boolean = false
    var status: LoadStatus = LoadStatus.NO_FILE

    var numFrames: Int = 0
    var frameTime: Float = 0f
    var duration: Float = 0f

    private var lineNumber: Int = 0
    private val lines: List<String> = buffer.lines()
    private var linePos: Int = 0

    init {
        status = parseBVH(jointAliasMap)
        initialized = status == LoadStatus.OK
        if (initialized) duration = numFrames * frameTime
    }

    private fun nextLine(): String? {
        while (linePos < lines.size) {
            val line = lines[linePos++].trim()
            lineNumber++
            if (line.isNotEmpty()) return line
        }
        return null
    }

    private fun parseBVH(aliasMap: Map<String, String>): LoadStatus {
        val header = nextLine() ?: return LoadStatus.EOF
        if (!header.startsWith("HIERARCHY", ignoreCase = true)) return LoadStatus.NO_HIER

        var line = nextLine() ?: return LoadStatus.EOF
        if (!line.startsWith("ROOT", ignoreCase = true)) return LoadStatus.BAD_ROOT

        val rootName = line.substringAfter("ROOT").trim()
        val rootJoint = BvhJoint(name = rootName, outName = aliasMap[rootName] ?: rootName)
        val parseResult = parseJoint(rootJoint, aliasMap)
        if (parseResult != LoadStatus.OK) return parseResult
        joints.add(rootJoint)

        val motionMarker = nextLine() ?: return LoadStatus.EOF
        if (!motionMarker.startsWith("MOTION", ignoreCase = true)) return LoadStatus.NO_MOTION

        val framesLine = nextLine() ?: return LoadStatus.NO_FRAMES
        numFrames = framesLine.substringAfter(":").trim().toIntOrNull() ?: return LoadStatus.NO_FRAMES

        val frameTimeLine = nextLine() ?: return LoadStatus.NO_FRAME_TIME
        frameTime = frameTimeLine.substringAfter(":").trim().toFloatOrNull() ?: return LoadStatus.NO_FRAME_TIME

        repeat(numFrames) {
            val keyLine = nextLine() ?: return LoadStatus.NO_POS
            val vals = keyLine.trim().split(Regex("\\s+")).mapNotNull { it.toFloatOrNull() }
            distributeFrameData(vals)
        }

        return LoadStatus.OK
    }

    private fun parseJoint(joint: BvhJoint, aliasMap: Map<String, String>): LoadStatus {
        val openBrace = nextLine() ?: return LoadStatus.NO_JOINT
        if (openBrace != "{") return LoadStatus.NO_JOINT

        var line = nextLine() ?: return LoadStatus.EOF
        while (line != "}") {
            when {
                line.startsWith("OFFSET", ignoreCase = true) -> {
                    val parts = line.split(Regex("\\s+"))
                    if (parts.size < 4) return LoadStatus.NO_OFFSET
                    joint.relativePosition = Vector3(
                        parts[1].toFloatOrNull() ?: 0f,
                        parts[2].toFloatOrNull() ?: 0f,
                        parts[3].toFloatOrNull() ?: 0f
                    )
                }
                line.startsWith("CHANNELS", ignoreCase = true) -> {
                    val parts = line.split(Regex("\\s+"))
                    joint.numChannels = parts.getOrNull(1)?.toIntOrNull() ?: return LoadStatus.NO_CHANNELS
                    joint.order = parts.drop(2).take(3)
                        .map { it.first().uppercaseChar() }
                        .filter { it in listOf('X', 'Y', 'Z') }
                        .joinToString("")
                }
                line.startsWith("JOINT", ignoreCase = true) -> {
                    val childName = line.substringAfter("JOINT").trim()
                    val child = BvhJoint(name = childName, outName = aliasMap[childName] ?: childName)
                    val result = parseJoint(child, aliasMap)
                    if (result != LoadStatus.OK) return result
                    joints.add(child)
                }
                line.startsWith("End Site", ignoreCase = true) -> {
                    skipBlock()
                }
            }
            line = nextLine() ?: return LoadStatus.EOF
        }
        return LoadStatus.OK
    }

    private fun skipBlock() {
        var depth = 0
        while (true) {
            val line = nextLine() ?: return
            when (line) {
                "{" -> depth++
                "}" -> { if (depth == 0) return else depth-- }
            }
        }
    }

    private fun distributeFrameData(values: List<Float>) {
        var idx = 0
        for (joint in joints) {
            val key = BvhKey()
            if (!joint.ignorePositions && joint.numChannels >= 6) {
                key.pos[0] = values.getOrElse(idx++) { 0f }
                key.pos[1] = values.getOrElse(idx++) { 0f }
                key.pos[2] = values.getOrElse(idx++) { 0f }
            } else if (joint.numChannels >= 6) {
                idx += 3
                key.ignorePos = true
            }
            key.rot[0] = values.getOrElse(idx++) { 0f }
            key.rot[1] = values.getOrElse(idx++) { 0f }
            key.rot[2] = values.getOrElse(idx++) { 0f }
            joint.keys.add(key)
        }
    }

    fun applyTranslations() {
        for (joint in joints) {
            val t = translations[joint.name] ?: continue
            if (t.ignore) { joint.ignore = true; continue }
            joint.outName = t.outName.ifEmpty { joint.outName }
            joint.ignorePositions = t.ignorePositions
            joint.relativePositionKey = t.relativePositionKey
            joint.relativeRotationKey = t.relativeRotationKey
            joint.mergeParentName = t.mergeParentName
            joint.mergeChildName = t.mergeChildName
            joint.priority += t.priorityModifier
            joint.frameMatrix = t.frameMatrix
            joint.offsetMatrix = t.offsetMatrix
            joint.relativePosition = t.relativePosition
        }
    }

    fun optimize() {
        for (joint in joints) {
            if (joint.keys.size < 2) continue
            val redundantRot = joint.keys.zipWithNext().all { (a, b) ->
                a.rot.zip(b.rot.toList()).all { (x, y) -> kotlin.math.abs(x - y) < 1e-6f }
            }
            if (redundantRot) joint.keys.forEach { it.ignoreRot = true }
        }
    }

    fun serialize(): ByteArray {
        val sb = StringBuilder()
        sb.appendLine("version\t${CharacterConstants.MAX_ANIM_DURATION}")
        sb.appendLine("frames\t$numFrames")
        sb.appendLine("frameTime\t$frameTime")
        sb.appendLine("duration\t$duration")
        sb.appendLine("loop\t$loop")
        sb.appendLine("loopIn\t$loopInPoint")
        sb.appendLine("loopOut\t$loopOutPoint")
        sb.appendLine("easeIn\t$easeIn")
        sb.appendLine("easeOut\t$easeOut")
        sb.appendLine("emote\t$emoteName")
        sb.appendLine("joints\t${joints.size}")
        for (joint in joints) {
            if (joint.ignore) continue
            sb.appendLine("joint\t${joint.outName}\t${joint.priority}")
            for (key in joint.keys) {
                if (!key.ignorePos)
                    sb.appendLine("pos\t${key.pos[0]}\t${key.pos[1]}\t${key.pos[2]}")
                if (!key.ignoreRot)
                    sb.appendLine("rot\t${key.rot[0]}\t${key.rot[1]}\t${key.rot[2]}")
            }
        }
        return sb.toString().toByteArray(Charsets.UTF_8)
    }

    fun getLineNumber(): Int = lineNumber
    fun isInitialized(): Boolean = initialized

    fun reset() {
        joints.clear()
        constraints.clear()
        numFrames = 0
        frameTime = 0f
        duration = 0f
        loop = false
        loopInPoint = 0f
        loopOutPoint = 0f
        easeIn = 0f
        easeOut = 0f
        emoteName = ""
        initialized = false
        status = LoadStatus.NO_FILE
    }
}
