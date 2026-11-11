package com.linkpoint.linden.llprimitive

import com.linkpoint.linden.llcommon.LLUUID

// Material type byte codes (LL_MCODE_*)
object MaterialCode {
    const val STONE: UByte   = 0u
    const val METAL: UByte   = 1u
    const val GLASS: UByte   = 2u
    const val WOOD: UByte    = 3u
    const val FLESH: UByte   = 4u
    const val PLASTIC: UByte = 5u
    const val RUBBER: UByte  = 6u
    const val LIGHT: UByte   = 7u
    const val END: UByte     = 8u
    const val MASK: UByte    = 0x0Fu
}

data class MaterialInfo(
    val mcode: UByte,
    val name: String,
    val defaultTextureId: LLUUID,
    val density: Float,       // kg/m^3
    val friction: Float,
    val restitution: Float,
    val shatterSoundId: LLUUID = LLUUID.NULL,
    val hpModifier: Float = 1f,
    val damageModifier: Float = 1f,
    val epModifier: Float = 1f,
)

object MaterialTable {
    // Havok4-era friction values
    const val FRICTION_MIN: Float     = 0.15f
    const val FRICTION_GLASS: Float   = 0.13f
    const val FRICTION_LIGHT: Float   = 0.14f
    const val FRICTION_METAL: Float   = 0.22f
    const val FRICTION_PLASTIC: Float = 0.3f
    const val FRICTION_WOOD: Float    = 0.44f
    const val FRICTION_FLESH: Float   = 0.46f
    const val FRICTION_LAND: Float    = 0.58f
    const val FRICTION_STONE: Float   = 0.6f
    const val FRICTION_RUBBER: Float  = 0.67f
    const val FRICTION_MAX: Float     = 0.71f

    const val RESTITUTION_MIN: Float     = 0.02f
    const val RESTITUTION_LAND: Float    = RESTITUTION_MIN
    const val RESTITUTION_FLESH: Float   = 0.2f
    const val RESTITUTION_STONE: Float   = 0.4f
    const val RESTITUTION_METAL: Float   = 0.4f
    const val RESTITUTION_WOOD: Float    = 0.5f
    const val RESTITUTION_GLASS: Float   = 0.7f
    const val RESTITUTION_PLASTIC: Float = 0.7f
    const val RESTITUTION_LIGHT: Float   = 0.7f
    const val RESTITUTION_RUBBER: Float  = 0.9f
    const val RESTITUTION_MAX: Float     = 0.95f

    const val DEFAULT_FRICTION: Float    = 0.5f
    const val DEFAULT_RESTITUTION: Float = 0.4f

    const val DEFAULT_OBJECT_DENSITY: Float        = 1000f  // kg/m^3
    const val LEGACY_DEFAULT_OBJECT_DENSITY: Float = 10f
    const val DEFAULT_AVATAR_DENSITY: Float        = 445.3f

    // Material entries match initBasicTable() in llmaterialtable.cpp
    val entries: List<MaterialInfo> = listOf(
        MaterialInfo(
            mcode = MaterialCode.STONE, name = "Stone",
            defaultTextureId = LLUUID("87c41669-8252-0dc4-0be2-7ea25b8d0dd7"),
            density = 30f, friction = 0.8f, restitution = 0.4f,
            shatterSoundId = LLUUID("ea296329-0f09-4993-af1b-e6784bab1dc9"),
        ),
        MaterialInfo(
            mcode = MaterialCode.METAL, name = "Metal",
            defaultTextureId = LLUUID("779e4a2b-7e87-17d5-d633-aae6fa33a416"),
            density = 50f, friction = 0.3f, restitution = 0.4f,
            shatterSoundId = LLUUID("d1375446-1c4d-470b-9135-30132433b678"),
        ),
        MaterialInfo(
            mcode = MaterialCode.GLASS, name = "Glass",
            defaultTextureId = LLUUID("f547f700-ed1e-7531-7ee3-7e38e1c38b77"),
            density = 20f, friction = 0.2f, restitution = 0.7f,
            shatterSoundId = LLUUID("85cda060-b393-48e6-81c8-2cfdfb275351"),
        ),
        MaterialInfo(
            mcode = MaterialCode.WOOD, name = "Wood",
            defaultTextureId = LLUUID("89556747-24cb-43ed-920b-47caed15465f"),
            density = 10f, friction = 0.6f, restitution = 0.5f,
            shatterSoundId = LLUUID("6f00669f-15e0-4793-a63e-c03f62fee43a"),
        ),
        MaterialInfo(
            mcode = MaterialCode.FLESH, name = "Flesh",
            defaultTextureId = LLUUID("c07f6a4e-58cd-a5d8-54d8-db1b9572ce26"),
            density = 10f, friction = 0.9f, restitution = 0.3f,
            shatterSoundId = LLUUID("2d8c6f51-149e-4e23-8413-93a379b42b67"),
        ),
        MaterialInfo(
            mcode = MaterialCode.PLASTIC, name = "Plastic",
            defaultTextureId = LLUUID("aab41f14-7a03-2eb1-8640-f7a3e30f7d88"),
            density = 5f, friction = 0.4f, restitution = 0.7f,
            shatterSoundId = LLUUID("d55c7f3c-e1c3-4ddc-9eff-9ef805d9190e"),
        ),
        MaterialInfo(
            mcode = MaterialCode.RUBBER, name = "Rubber",
            defaultTextureId = LLUUID("d0b39b13-e049-9007-b6a5-0c56e9f4dd7a"),
            density = 0.5f, friction = 0.9f, restitution = 0.9f,
            shatterSoundId = LLUUID("212b6d1e-8d9c-4986-b3aa-f3c6df8d987d"),
        ),
        MaterialInfo(
            mcode = MaterialCode.LIGHT, name = "Light",
            defaultTextureId = LLUUID("aab41f14-7a03-2eb1-8640-f7a3e30f7d88"),
            density = 20f, friction = 0.2f, restitution = 0.7f,
            shatterSoundId = LLUUID("d55c7f3c-e1c3-4ddc-9eff-9ef805d9190e"),
        ),
    )

    private val byCode: Map<UByte, MaterialInfo> = entries.associateBy { it.mcode }
    private val byName: Map<String, MaterialInfo> = entries.associateBy { it.name }

    fun get(mcode: UByte): MaterialInfo? = byCode[mcode]
    fun get(name: String): MaterialInfo? = byName[name]

    fun getDensity(mcode: UByte): Float    = byCode[mcode]?.density ?: 0f
    fun getFriction(mcode: UByte): Float   = byCode[mcode]?.friction ?: DEFAULT_FRICTION
    fun getRestitution(mcode: UByte): Float = byCode[mcode]?.restitution ?: DEFAULT_RESTITUTION
    fun getName(mcode: UByte): String      = byCode[mcode]?.name ?: ""
    fun getMCode(name: String): UByte?     = byName[name]?.mcode
}
