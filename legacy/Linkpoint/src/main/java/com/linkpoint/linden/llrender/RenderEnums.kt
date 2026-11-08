package com.linkpoint.linden.llrender

enum class BlendFactor(val value: Int) {
    ONE(0),
    ZERO(1),
    DEST_COLOR(2),
    SOURCE_COLOR(3),
    ONE_MINUS_DEST_COLOR(4),
    ONE_MINUS_SOURCE_COLOR(5),
    DEST_ALPHA(6),
    SOURCE_ALPHA(7),
    ONE_MINUS_DEST_ALPHA(8),
    ONE_MINUS_SOURCE_ALPHA(9),
    UNDEF(10)
}

enum class BlendType {
    ALPHA,
    ADD,
    ADD_WITH_ALPHA,
    MULT,
    MULT_ALPHA,
    MULT_X2,
    REPLACE
}

enum class StencilOp {
    KEEP,
    ZERO,
    REPLACE,
    INCR,
    INCR_WRAP,
    DECR,
    DECR_WRAP,
    INVERT
}

enum class DepthFunc {
    NEVER,
    ALWAYS,
    LESS,
    LESS_EQUAL,
    EQUAL,
    NOT_EQUAL,
    GREATER_EQUAL,
    GREATER,
    DEFAULT
}

enum class CullFace {
    FRONT,
    BACK,
    FRONT_AND_BACK
}

enum class BufferType {
    TRIANGLES,
    TRIANGLE_STRIP,
    TRIANGLE_FAN,
    POINTS,
    LINES,
    LINE_STRIP,
    LINE_LOOP,
    QUADS
}

object RenderConstants {
    const val GL_TRIANGLES: UInt            = 0x0004u
    const val GL_TRIANGLE_STRIP: UInt       = 0x0005u
    const val GL_TRIANGLE_FAN: UInt         = 0x0006u
    const val GL_POINTS: UInt               = 0x0000u
    const val GL_LINES: UInt                = 0x0001u
    const val GL_LINE_STRIP: UInt           = 0x0003u
    const val GL_LINE_LOOP: UInt            = 0x0002u
    const val GL_QUADS: UInt                = 0x0007u

    const val GL_BLEND: UInt                = 0x0BE2u
    const val GL_DEPTH_TEST: UInt           = 0x0B71u
    const val GL_STENCIL_TEST: UInt         = 0x0B90u
    const val GL_CULL_FACE: UInt            = 0x0B44u
    const val GL_SCISSOR_TEST: UInt         = 0x0C11u
    const val GL_ALPHA_TEST: UInt           = 0x0BC0u

    const val GL_RGBA: UInt                 = 0x1908u
    const val GL_RGB: UInt                  = 0x1907u
    const val GL_RGBA16F: UInt              = 0x881Au
    const val GL_RGB16F: UInt               = 0x881Bu
    const val GL_RGBA32F: UInt              = 0x8814u
    const val GL_DEPTH_COMPONENT: UInt      = 0x1902u
    const val GL_DEPTH24_STENCIL8: UInt     = 0x88F0u

    const val GL_FRAMEBUFFER: UInt          = 0x8D40u
    const val GL_DRAW_FRAMEBUFFER: UInt     = 0x8CA9u
    const val GL_READ_FRAMEBUFFER: UInt     = 0x8CA8u
    const val GL_COLOR_ATTACHMENT0: UInt    = 0x8CE0u
    const val GL_COLOR_ATTACHMENT1: UInt    = 0x8CE1u
    const val GL_COLOR_ATTACHMENT2: UInt    = 0x8CE2u
    const val GL_COLOR_ATTACHMENT3: UInt    = 0x8CE3u
    const val GL_DEPTH_ATTACHMENT: UInt     = 0x8D00u
    const val GL_STENCIL_ATTACHMENT: UInt   = 0x8D20u

    const val GL_COLOR_BUFFER_BIT: UInt     = 0x4000u
    const val GL_DEPTH_BUFFER_BIT: UInt     = 0x0100u
    const val GL_STENCIL_BUFFER_BIT: UInt   = 0x0400u

    const val GL_NEAREST: UInt              = 0x2600u
    const val GL_LINEAR: UInt               = 0x2601u

    const val GL_TEXTURE_2D: UInt           = 0x0DE1u
    const val GL_TEXTURE_CUBE_MAP: UInt     = 0x8513u
    const val GL_TEXTURE_3D: UInt           = 0x806Fu
    const val GL_TEXTURE_2D_MULTISAMPLE: UInt = 0x9100u

    const val GL_UNSIGNED_SHORT: UInt       = 0x1403u
    const val GL_UNSIGNED_INT: UInt         = 0x1405u
    const val GL_FLOAT: UInt                = 0x1406u
    const val GL_UNSIGNED_BYTE: UInt        = 0x1401u

    const val NUM_TEXTURE_LAYERS: UInt      = 32u
    const val NUM_LIGHT_UNITS: UInt         = 8u
    const val MATRIX_STACK_DEPTH: Int       = 32
}
