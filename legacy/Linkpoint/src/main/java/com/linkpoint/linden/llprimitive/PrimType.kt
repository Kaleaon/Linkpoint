package com.linkpoint.linden.llprimitive

typealias LLPCode = UByte

// Raw prim byte-codes as defined in llvolume.h
enum class PrimCode(val code: UByte) {
    CUBE(1u),
    PRISM(2u),
    TETRAHEDRON(3u),
    PYRAMID(4u),
    CYLINDER(5u),
    CONE(6u),
    SPHERE(7u),
    TORUS(8u),
    VOLUME(9u),
    APP(14u),
    LEGACY(15u),

    // Legacy subtypes (high nibble encodes subtype, low nibble = LEGACY)
    LEGACY_AVATAR((0x20u or 15u).toUByte()),
    LEGACY_GRASS((0x50u or 15u).toUByte()),
    TREE_NEW((0x60u or 15u).toUByte()),
    LEGACY_PART_SYS((0x80u or 15u).toUByte()),
    LEGACY_ROCK((0x90u or 15u).toUByte()),
    LEGACY_TEXT_BUBBLE((0xE0u or 15u).toUByte()),
    LEGACY_TREE((0xF0u or 15u).toUByte()),

    // Hemi variants
    CYLINDER_HEMI((5u or 0x10u).toUByte()),
    CONE_HEMI((6u or 0x10u).toUByte()),
    SPHERE_HEMI((7u or 0x10u).toUByte()),
    TORUS_HEMI((8u or 0x10u).toUByte()),

    UNKNOWN(0xFFu);

    companion object {
        private val byCode = entries.associateBy { it.code }

        fun fromCode(code: UByte): PrimCode = byCode[code] ?: UNKNOWN
    }
}

// LSL-level prim types (as exposed to scripts and UI)
enum class PrimType(val id: Int) {
    BOX(0),
    CYLINDER(1),
    PRISM(2),
    SPHERE(3),
    TORUS(4),
    TUBE(5),
    RING(6),
    SCULPT(7);

    companion object {
        private val byId = entries.associateBy { it.id }

        fun fromId(id: Int): PrimType? = byId[id]

        fun fromPCode(code: UByte): PrimType? {
            val prim = PrimCode.fromCode(code)
            return when (prim) {
                PrimCode.CUBE        -> BOX
                PrimCode.CYLINDER,
                PrimCode.CYLINDER_HEMI -> CYLINDER
                PrimCode.PRISM       -> PRISM
                PrimCode.SPHERE,
                PrimCode.SPHERE_HEMI -> SPHERE
                PrimCode.TORUS,
                PrimCode.TORUS_HEMI  -> TORUS
                PrimCode.CONE,
                PrimCode.CONE_HEMI   -> TUBE
                PrimCode.VOLUME      -> SCULPT
                else                 -> null
            }
        }
    }
}
