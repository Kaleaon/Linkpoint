package com.linkpoint.protocol.messages

/**
 * Path/profile shape parameters as carried in ObjectUpdate ObjectData.
 *
 * SL prims are extruded volumes: a 2D **profile** (square / circle /
 * triangle / half-circle) is swept along a 1D **path** (line / circle /
 * helix). The protocol carries the raw bytes for each parameter; values
 * here are pre-scaled into rendering-friendly floats so PrimRenderer
 * doesn't have to repeat the `/50000` / `/100` conversions every prim.
 *
 * Mapping mostly mirrors the LL reference viewer's `LLPathParams` and
 * `LLProfileParams` (indra/llprimitive/llpathparams.h). We don't
 * implement the full `LLVolume::regenerate()` mesh tesselator yet — only
 * the high-level shape kind plus path/profile cuts and hollow are honoured;
 * twist/taper/skew/revolutions are captured for future work but currently
 * applied as a uniform scale or ignored.
 */
data class PrimShapeParams(
    /** Raw PathCurve byte (PATH_LINE=0x10, PATH_CIRCLE=0x20, PATH_CIRCLE2=0x30, PATH_FLEXIBLE=0x80). */
    val pathCurve: Int = PATH_LINE,
    /** Raw ProfileCurve byte (low 4 bits = base profile, high 4 = hollow shape). */
    val profileCurve: Int = PROFILE_SQUARE,

    /** Path begin in the 0..1 range (cuts the path at the start). */
    val pathBegin: Float = 0f,
    /** Path end in the 0..1 range (cuts the path at the end). 1.0 = full. */
    val pathEnd: Float = 1f,
    /** Path scale X / Y in the 0..2 range (pinch / stretch the profile). */
    val pathScaleX: Float = 1f,
    val pathScaleY: Float = 1f,
    /** Path shear X / Y in the -0.5..0.5 range. */
    val pathShearX: Float = 0f,
    val pathShearY: Float = 0f,
    /** Twist along the path in the -1..1 range (multiply by PI for radians). */
    val pathTwist: Float = 0f,
    val pathTwistBegin: Float = 0f,
    /** Radius offset for paths with curvature (-1..1). */
    val pathRadiusOffset: Float = 0f,
    /** Path taper X / Y in the -1..1 range. */
    val pathTaperX: Float = 0f,
    val pathTaperY: Float = 0f,
    /** Path revolutions in the 1..4 range. */
    val pathRevolutions: Float = 1f,
    /** Path skew in the -1..1 range. */
    val pathSkew: Float = 0f,

    /** Profile begin in the 0..1 range. */
    val profileBegin: Float = 0f,
    /** Profile end in the 0..1 range. */
    val profileEnd: Float = 1f,
    /** Profile hollow in the 0..1 range. 0 = no hollow. */
    val profileHollow: Float = 0f
) {
    /** Profile shape category from the low 4 bits of profileCurve. */
    val profileType: Int get() = profileCurve and 0x0F

    /** Hollow shape from the high 4 bits of profileCurve (default / square / circle / triangle). */
    val hollowType: Int get() = profileCurve and 0xF0

    companion object {
        // PathCurve constants (LL viewer LLPathParams::PATH_*).
        const val PATH_LINE = 0x10
        const val PATH_CIRCLE = 0x20
        const val PATH_CIRCLE2 = 0x30
        const val PATH_TEST = 0x40
        const val PATH_FLEXIBLE = 0x80

        // ProfileCurve low-nibble constants (LL viewer LLProfileParams::PROFILE_*).
        const val PROFILE_CIRCLE = 0x00
        const val PROFILE_SQUARE = 0x01
        const val PROFILE_ISO_TRI = 0x02
        const val PROFILE_EQUAL_TRI = 0x03
        const val PROFILE_RIGHT_TRI = 0x04
        const val PROFILE_HALF_CIRCLE = 0x05

        val DEFAULT = PrimShapeParams()

        /**
         * Parse the 23 path/profile bytes from the running position of the
         * ObjectUpdate buffer. Caller is responsible for being positioned
         * exactly at the PathCurve byte.
         */
        fun readFrom(buffer: java.nio.ByteBuffer): PrimShapeParams {
            val pathCurve = buffer.get().toInt() and 0xFF
            val profileCurve = buffer.get().toInt() and 0xFF
            val pathBegin = (buffer.short.toInt() and 0xFFFF) / 50000f
            val pathEnd = 1f - (buffer.short.toInt() and 0xFFFF) / 50000f
            val pathScaleX = (buffer.get().toInt() and 0xFF) / 100f
            val pathScaleY = (buffer.get().toInt() and 0xFF) / 100f
            val pathShearX = signedByteAsRange(buffer.get(), 0.5f)
            val pathShearY = signedByteAsRange(buffer.get(), 0.5f)
            val pathTwist = signedByteAsRange(buffer.get(), 1f)
            val pathTwistBegin = signedByteAsRange(buffer.get(), 1f)
            val pathRadiusOffset = signedByteAsRange(buffer.get(), 1f)
            val pathTaperX = signedByteAsRange(buffer.get(), 1f)
            val pathTaperY = signedByteAsRange(buffer.get(), 1f)
            val pathRevolutions = (buffer.get().toInt() and 0xFF) / 66.666f + 1f
            val pathSkew = signedByteAsRange(buffer.get(), 1f)
            val profileBegin = (buffer.short.toInt() and 0xFFFF) / 50000f
            val profileEnd = 1f - (buffer.short.toInt() and 0xFFFF) / 50000f
            val profileHollow = (buffer.short.toInt() and 0xFFFF) / 50000f
            return PrimShapeParams(
                pathCurve = pathCurve,
                profileCurve = profileCurve,
                pathBegin = pathBegin.coerceIn(0f, 1f),
                pathEnd = pathEnd.coerceIn(0f, 1f),
                pathScaleX = pathScaleX,
                pathScaleY = pathScaleY,
                pathShearX = pathShearX,
                pathShearY = pathShearY,
                pathTwist = pathTwist,
                pathTwistBegin = pathTwistBegin,
                pathRadiusOffset = pathRadiusOffset,
                pathTaperX = pathTaperX,
                pathTaperY = pathTaperY,
                pathRevolutions = pathRevolutions,
                pathSkew = pathSkew,
                profileBegin = profileBegin.coerceIn(0f, 1f),
                profileEnd = profileEnd.coerceIn(0f, 1f),
                profileHollow = profileHollow.coerceIn(0f, 0.95f)
            )
        }

        private fun signedByteAsRange(b: Byte, range: Float): Float {
            // S8 in range [-100, 100] maps linearly to [-range, +range].
            return (b.toInt() / 100f) * range
        }
    }
}
