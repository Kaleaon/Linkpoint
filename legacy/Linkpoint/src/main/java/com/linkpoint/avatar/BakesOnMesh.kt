package com.linkpoint.avatar

import java.util.UUID

/**
 * Bakes-on-Mesh (BoM) magic UUIDs and resolver helpers.
 *
 * BoM lets a mesh attachment reference one of the system-avatar baked-texture
 * "slots" (head, upper body, lower body, eyes, skirt, hair, plus the four
 * universal AUX layers added in 2018) by using a fixed sentinel UUID in its
 * TextureEntry instead of a real asset UUID. The viewer detects these
 * sentinels, looks up the avatar's actual baked texture for that slot, and
 * passes that to the renderer — so a mesh body painted with IMG_USE_BAKED_HEAD
 * automatically picks up whatever skin / makeup / tattoos the user has on
 * their system head.
 *
 * UUIDs are taken from the Linden reference viewer:
 *   indra/llappearance/lltexlayer.cpp  IMG_USE_BAKED_*
 *   See also: https://wiki.secondlife.com/wiki/Bakes_on_Mesh
 *
 * This module is read-only data + a small resolver. The actual upload pipeline
 * (AgentSetAppearance → BakedTextureUploader → CDN) is owned by AvatarBaker.
 */
object BakesOnMesh {
    /** SL bake slot indices (matches LL viewer's `EBakedTextureIndex`). */
    const val SLOT_HEAD = 0
    const val SLOT_UPPER = 1
    const val SLOT_LOWER = 2
    const val SLOT_EYES = 3
    const val SLOT_SKIRT = 4
    const val SLOT_HAIR = 5
    const val SLOT_LEFT_ARM = 6
    const val SLOT_LEFT_LEG = 7
    const val SLOT_AUX1 = 8
    const val SLOT_AUX2 = 9
    const val SLOT_AUX3 = 10

    /** "Use my baked head texture" sentinel. */
    val IMG_USE_BAKED_HEAD: UUID = UUID.fromString("5a9f4a74-30f2-821c-b88d-70499d3e7183")
    val IMG_USE_BAKED_UPPER: UUID = UUID.fromString("ae2de45c-d252-50b8-5c6e-19f39ce79317")
    val IMG_USE_BAKED_LOWER: UUID = UUID.fromString("24daea5f-0539-cfcf-047f-fbc40b2786ba")
    val IMG_USE_BAKED_EYES: UUID = UUID.fromString("52cc6bb6-2ee5-e632-d3ad-50197b1dcb8a")
    val IMG_USE_BAKED_SKIRT: UUID = UUID.fromString("43529ce8-7faa-ad92-165a-bc4078371687")
    val IMG_USE_BAKED_HAIR: UUID = UUID.fromString("09aac1fb-6bce-0bee-7d44-caac6dbb6c63")
    val IMG_USE_BAKED_LEFTARM: UUID = UUID.fromString("ff62763f-d60a-9855-890b-0c96f8f8cd98")
    val IMG_USE_BAKED_LEFTLEG: UUID = UUID.fromString("8e915e25-31d1-cc95-ae08-d58a47488741")
    val IMG_USE_BAKED_AUX1: UUID = UUID.fromString("9742065b-19b5-297c-858a-29711d539043")
    val IMG_USE_BAKED_AUX2: UUID = UUID.fromString("03642e83-2bd1-4eb9-34b4-4c47ed586d2d")
    val IMG_USE_BAKED_AUX3: UUID = UUID.fromString("edd51b77-fc10-ce7a-4b3d-011dfc349e4f")

    private val SENTINEL_TO_SLOT: Map<UUID, Int> = mapOf(
        IMG_USE_BAKED_HEAD to SLOT_HEAD,
        IMG_USE_BAKED_UPPER to SLOT_UPPER,
        IMG_USE_BAKED_LOWER to SLOT_LOWER,
        IMG_USE_BAKED_EYES to SLOT_EYES,
        IMG_USE_BAKED_SKIRT to SLOT_SKIRT,
        IMG_USE_BAKED_HAIR to SLOT_HAIR,
        IMG_USE_BAKED_LEFTARM to SLOT_LEFT_ARM,
        IMG_USE_BAKED_LEFTLEG to SLOT_LEFT_LEG,
        IMG_USE_BAKED_AUX1 to SLOT_AUX1,
        IMG_USE_BAKED_AUX2 to SLOT_AUX2,
        IMG_USE_BAKED_AUX3 to SLOT_AUX3
    )

    /** Quick check: is `id` one of the BoM sentinel UUIDs? */
    fun isBakeSentinel(id: UUID): Boolean = SENTINEL_TO_SLOT.containsKey(id)

    /** Map a sentinel UUID to its SLOT_* index, or null if not a sentinel. */
    fun slotForSentinel(id: UUID): Int? = SENTINEL_TO_SLOT[id]

    /**
     * Resolve a TextureEntry face UUID. If [id] is a BoM sentinel, returns the
     * actual baked texture UUID for the agent's corresponding slot (looked up
     * via [bakedLookup]). Otherwise returns [id] unchanged.
     *
     * `bakedLookup` is typically `AvatarBaker.getBakedTextures()::get` for the
     * local agent, or a lookup keyed by the prim's owner avatar.
     */
    fun resolve(id: UUID, bakedLookup: (Int) -> UUID?): UUID {
        val slot = SENTINEL_TO_SLOT[id] ?: return id
        return bakedLookup(slot) ?: id
    }
}
