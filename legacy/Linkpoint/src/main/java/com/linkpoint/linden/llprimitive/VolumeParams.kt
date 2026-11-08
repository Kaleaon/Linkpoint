package com.linkpoint.linden.llprimitive

import com.linkpoint.linden.llcommon.LLUUID
import com.linkpoint.linden.llcommon.LLSD
import com.linkpoint.linden.llmath.Vector2

// Profile curve codes (stored in the low nibble of the profile byte)
object ProfileCurve {
    val CIRCLE: UByte       = 0x00u
    val SQUARE: UByte       = 0x01u
    val ISO_TRI: UByte      = 0x02u
    val EQUAL_TRI: UByte    = 0x03u
    val RIGHT_TRI: UByte    = 0x04u
    val CIRCLE_HALF: UByte  = 0x05u

    // Hole type (high nibble)
    val HOLE_SAME: UByte    = 0x00u
    val HOLE_CIRCLE: UByte  = 0x10u
    val HOLE_SQUARE: UByte  = 0x20u
    val HOLE_TRIANGLE: UByte = 0x30u
}

// Path curve codes
object PathCurve {
    val LINE: UByte         = 0x10u
    val CIRCLE: UByte       = 0x20u
    val CIRCLE2: UByte      = 0x30u
    val TEST: UByte         = 0x40u
    val FLEXIBLE: UByte     = 0x80u
}

// Sculpt types
object SculptType {
    val NONE: UByte         = 0u
    val SPHERE: UByte       = 1u
    val TORUS: UByte        = 2u
    val PLANE: UByte        = 3u
    val CYLINDER: UByte     = 4u
    val MESH: UByte         = 5u
    val GLTF: UByte         = 6u

    val FLAG_INVERT: UByte  = 64u
    val FLAG_MIRROR: UByte  = 128u
    val TYPE_MASK: UByte    = 0x3Fu
}

data class VolumeParams(
    // Profile (S axis)
    var profileCurve: UByte  = ProfileCurve.SQUARE,
    var beginS: Float        = 0f,
    var endS: Float          = 1f,
    var hollow: Float        = 0f,

    // Path (T axis)
    var pathCurve: UByte     = PathCurve.LINE,
    var beginT: Float        = 0f,
    var endT: Float          = 1f,
    var scaleX: Float        = 1f,
    var scaleY: Float        = 1f,
    var shearX: Float        = 0f,
    var shearY: Float        = 0f,
    var twist: Float         = 0f,
    var twistBegin: Float    = 0f,
    var radiusOffset: Float  = 0f,
    var taperX: Float        = 0f,
    var taperY: Float        = 0f,
    var revolutions: Float   = 1f,
    var skew: Float          = 0f,

    // Sculpt
    var sculptId: LLUUID     = LLUUID.NULL,
    var sculptType: UByte    = SculptType.NONE,
) {
    var taper: Vector2
        get() = Vector2(taperX, taperY)
        set(v) { taperX = v.x; taperY = v.y }

    fun toSD(): LLSD = LLSD.map(
        "profile_curve"   to LLSD.integer(profileCurve.toInt()),
        "begin_s"         to LLSD.real(beginS.toDouble()),
        "end_s"           to LLSD.real(endS.toDouble()),
        "hollow"          to LLSD.real(hollow.toDouble()),
        "path_curve"      to LLSD.integer(pathCurve.toInt()),
        "begin_t"         to LLSD.real(beginT.toDouble()),
        "end_t"           to LLSD.real(endT.toDouble()),
        "scale_x"         to LLSD.real(scaleX.toDouble()),
        "scale_y"         to LLSD.real(scaleY.toDouble()),
        "shear_x"         to LLSD.real(shearX.toDouble()),
        "shear_y"         to LLSD.real(shearY.toDouble()),
        "twist"           to LLSD.real(twist.toDouble()),
        "twist_begin"     to LLSD.real(twistBegin.toDouble()),
        "radius_offset"   to LLSD.real(radiusOffset.toDouble()),
        "taper_x"         to LLSD.real(taperX.toDouble()),
        "taper_y"         to LLSD.real(taperY.toDouble()),
        "revolutions"     to LLSD.real(revolutions.toDouble()),
        "skew"            to LLSD.real(skew.toDouble()),
        "sculpt_id"       to LLSD.uuid(sculptId),
        "sculpt_type"     to LLSD.integer(sculptType.toInt()),
    )

    companion object {
        fun fromSD(sd: LLSD): VolumeParams = VolumeParams(
            profileCurve  = sd["profile_curve"].asInt().toUByte(),
            beginS        = sd["begin_s"].asFloat(),
            endS          = sd["end_s"].asFloat(),
            hollow        = sd["hollow"].asFloat(),
            pathCurve     = sd["path_curve"].asInt().toUByte(),
            beginT        = sd["begin_t"].asFloat(),
            endT          = sd["end_t"].asFloat(),
            scaleX        = sd["scale_x"].asFloat(),
            scaleY        = sd["scale_y"].asFloat(),
            shearX        = sd["shear_x"].asFloat(),
            shearY        = sd["shear_y"].asFloat(),
            twist         = sd["twist"].asFloat(),
            twistBegin    = sd["twist_begin"].asFloat(),
            radiusOffset  = sd["radius_offset"].asFloat(),
            taperX        = sd["taper_x"].asFloat(),
            taperY        = sd["taper_y"].asFloat(),
            revolutions   = sd["revolutions"].asFloat(),
            skew          = sd["skew"].asFloat(),
            sculptId      = sd["sculpt_id"].asUUID(),
            sculptType    = sd["sculpt_type"].asInt().toUByte(),
        )

        fun cube(): VolumeParams = VolumeParams(
            profileCurve = ProfileCurve.SQUARE,
            pathCurve    = PathCurve.LINE,
            beginS = 0f, endS = 1f,
            beginT = 0f, endT = 1f,
        )
    }
}
