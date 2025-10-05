package com.linkpoint.slproto.avatar

enum class MeshIndex {
    MESH_ID_HAIR,
    MESH_ID_HEAD,
    MESH_ID_EYELASH,
    MESH_ID_UPPER_BODY,
    MESH_ID_LOWER_BODY,
    MESH_ID_EYEBALL_LEFT,
    MESH_ID_EYEBALL_RIGHT,
    MESH_ID_SKIRT;

    companion object {
        @JvmField
        val VALUES = values()
    }
}