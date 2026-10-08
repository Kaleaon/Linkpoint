package com.linkpoint.linden.llprimitive

import com.linkpoint.linden.llcommon.LLUUID
import com.linkpoint.linden.llmath.Quaternion
import com.linkpoint.linden.llmath.Vector3
import java.nio.ByteBuffer
import java.nio.ByteOrder

// Flags mirroring LLPrimitive static constants
object PrimFlags {
    val PHANTOM: UInt              = 0x1u shl 0
    val VOLUME_DETECT: UInt        = 0x1u shl 1
    val DYNAMIC: UInt              = 0x1u shl 2
    val AVATAR: UInt               = 0x1u shl 3
    val SCULPT: UInt               = 0x1u shl 4
    val COLLISION_CALLBACK: UInt   = 0x1u shl 5
    val CONVEX: UInt               = 0x1u shl 6
    val DEFAULT_VOLUME: UInt       = 0x1u shl 7
    val SITTING: UInt              = 0x1u shl 8
    val SITTING_ON_GROUND: UInt    = 0x1u shl 9
}

// Extra network parameter type IDs
object ExtraParamType {
    val FLEXIBLE: UShort         = 0x10u
    val LIGHT: UShort            = 0x20u
    val SCULPT: UShort           = 0x30u
    val LIGHT_IMAGE: UShort      = 0x40u
    val MESH: UShort             = 0x60u
    val EXTENDED_MESH: UShort    = 0x70u
    val RENDER_MATERIAL: UShort  = 0x80u
    val REFLECTION_PROBE: UShort = 0x90u
}

class Primitive(
    var id: LLUUID             = LLUUID.NULL,
    var position: Vector3      = Vector3.ZERO,
    var rotation: Quaternion   = Quaternion.DEFAULT,
    var scale: Vector3         = Vector3(1f, 1f, 1f),
    var velocity: Vector3      = Vector3.ZERO,
    var angularVelocity: Vector3  = Vector3.ZERO,
    var acceleration: Vector3  = Vector3.ZERO,
    var primCode: PrimCode     = PrimCode.VOLUME,
    var material: UByte        = 0u,
    var clickAction: UByte     = 0u,
    var miscFlags: UInt        = 0u,
    var textureList: TextureList = TextureList(),
    var volumeParams: VolumeParams = VolumeParams(),
) {
    val primType: PrimType? get() = PrimType.fromPCode(primCode.code)

    fun isAvatar(): Boolean =
        primCode == PrimCode.LEGACY_AVATAR

    fun isSittingAvatar(): Boolean =
        isAvatar() && checkFlags(PrimFlags.SITTING or PrimFlags.SITTING_ON_GROUND)

    fun checkFlags(flags: UInt): Boolean = (miscFlags and flags) != 0u

    fun addFlags(flags: UInt) { miscFlags = miscFlags or flags }
    fun removeFlags(flags: UInt) { miscFlags = miscFlags and flags.inv() }

    // Terse object update: position (12 bytes compressed), velocity (6 bytes), accel (6 bytes),
    // rotation (8 bytes compressed), angular velocity (6 bytes).
    // This writes a simplified uncompressed form for reference.
    fun packTerseObjectUpdate(buf: ByteBuffer) {
        buf.order(ByteOrder.LITTLE_ENDIAN)
        buf.putFloat(position.x)
        buf.putFloat(position.y)
        buf.putFloat(position.z)
        buf.putFloat(velocity.x)
        buf.putFloat(velocity.y)
        buf.putFloat(velocity.z)
        buf.putFloat(acceleration.x)
        buf.putFloat(acceleration.y)
        buf.putFloat(acceleration.z)
        buf.putFloat(rotation.x)
        buf.putFloat(rotation.y)
        buf.putFloat(rotation.z)
        buf.putFloat(rotation.w)
        buf.putFloat(angularVelocity.x)
        buf.putFloat(angularVelocity.y)
        buf.putFloat(angularVelocity.z)
    }

    // Full object update writer — writes core identity and spatial fields.
    fun packObjectUpdate(buf: ByteBuffer) {
        buf.order(ByteOrder.LITTLE_ENDIAN)
        buf.put(id.toBytes())
        buf.put(primCode.code.toByte())
        buf.put(material.toByte())
        buf.put(clickAction.toByte())
        buf.putFloat(scale.x)
        buf.putFloat(scale.y)
        buf.putFloat(scale.z)
        packTerseObjectUpdate(buf)
    }

    companion object {
        fun create(code: PrimCode): Primitive = Primitive(primCode = code)
    }
}
