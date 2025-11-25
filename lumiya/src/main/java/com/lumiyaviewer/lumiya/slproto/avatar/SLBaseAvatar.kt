package com.lumiyaviewer.lumiya.slproto.avatar

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.LumiyaApp
import java.io.DataInputStream
import java.io.InputStream
import java.util.EnumMap

class SLBaseAvatar private constructor() {

    private val meshes: EnumMap<MeshIndex, MeshEntry> = EnumMap(MeshIndex::class.java)

    init {
        meshes[MeshIndex.MESH_ID_HAIR] = MeshEntry(BakedTextureIndex.BAKED_HAIR, AvatarTextureFaceIndex.TEX_HAIR_BAKED, "hairMesh", loadMesh("avatar_hair"))
        meshes[MeshIndex.MESH_ID_HEAD] = MeshEntry(BakedTextureIndex.BAKED_HEAD, AvatarTextureFaceIndex.TEX_HEAD_BAKED, "headMesh", loadMesh("avatar_head"))
        meshes[MeshIndex.MESH_ID_EYELASH] = MeshEntry(BakedTextureIndex.BAKED_HEAD, AvatarTextureFaceIndex.TEX_HEAD_BAKED, "eyelashMesh", loadMesh("avatar_eyelashes"))
        meshes[MeshIndex.MESH_ID_UPPER_BODY] = MeshEntry(BakedTextureIndex.BAKED_UPPER, AvatarTextureFaceIndex.TEX_UPPER_BAKED, "upperBodyMesh", loadMesh("avatar_upper_body"))
        meshes[MeshIndex.MESH_ID_LOWER_BODY] = MeshEntry(BakedTextureIndex.BAKED_LOWER, AvatarTextureFaceIndex.TEX_LOWER_BAKED, "lowerBodyMesh", loadMesh("avatar_lower_body"))
        meshes[MeshIndex.MESH_ID_EYEBALL_LEFT] = MeshEntry(BakedTextureIndex.BAKED_EYES, AvatarTextureFaceIndex.TEX_EYES_BAKED, "eyeBallLeftMesh", loadMesh("avatar_eye"))
        meshes[MeshIndex.MESH_ID_EYEBALL_RIGHT] = MeshEntry(BakedTextureIndex.BAKED_EYES, AvatarTextureFaceIndex.TEX_EYES_BAKED, "eyeBallRightMesh", loadMesh("avatar_eye"))
        meshes[MeshIndex.MESH_ID_SKIRT] = MeshEntry(BakedTextureIndex.BAKED_SKIRT, AvatarTextureFaceIndex.TEX_SKIRT_BAKED, "skirtMesh", loadMesh("avatar_skirt"))
    }

    class MeshEntry(
        val textureIndex: BakedTextureIndex,
        val textureFaceIndex: AvatarTextureFaceIndex,
        val meshName: String,
        val polyMesh: SLPolyMesh?
    )

    companion object {
        val instance: SLBaseAvatar by lazy { SLBaseAvatar() }
    }

    private fun loadMesh(str: String): SLPolyMesh? {
        Debug.Printf("BaseAvatar: loading mesh for $str")
        var inputStream: InputStream? = null
        var dataInputStream: DataInputStream? = null
        var open: InputStream? = null
        return try {
            val assetManager = LumiyaApp.getAssetManager()
            open = assetManager.open("character/$str.lbm")
            
            if (str == "avatar_head") {
                inputStream = assetManager.open("character/$str.lbm_0")
                dataInputStream = DataInputStream(inputStream)
            }
            
            val mesh = SLPolyMesh(DataInputStream(open), dataInputStream)
            mesh
        } catch (e: Exception) {
            Debug.Warning(e)
            null
        } finally {
            try {
                open?.close()
                inputStream?.close()
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun getMeshEntry(meshIndex: MeshIndex): MeshEntry? {
        return meshes[meshIndex]
    }
}
