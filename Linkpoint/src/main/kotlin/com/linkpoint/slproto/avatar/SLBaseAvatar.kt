package com.linkpoint.slproto.avatar

import android.content.res.AssetManager
import com.linkpoint.Debug
import com.linkpoint.LinkpointApp
import java.io.DataInputStream
import java.io.InputStream
import java.util.EnumMap
import java.util.Map

class SLBaseAvatar {
    private Map<MeshIndex, MeshEntry> meshes

    @JvmStatic
private class InstanceHolder {
        /* access modifiers changed from: private */
        const val SLBaseAvatar Instance = SLBaseAvatar((SLBaseAvatar) null)

        private InstanceHolder() {
        }
    }

    @JvmStatic
    class MeshEntry {
        public String meshName
        public SLPolyMesh polyMesh
        public AvatarTextureFaceIndex textureFaceIndex
        public BakedTextureIndex textureIndex

        public MeshEntry(BakedTextureIndex bakedTextureIndex, AvatarTextureFaceIndex avatarTextureFaceIndex, String str, SLPolyMesh sLPolyMesh) {
            this.textureIndex = bakedTextureIndex
            this.textureFaceIndex = avatarTextureFaceIndex
            this.meshName = str
            this.polyMesh = sLPolyMesh
        }
    }

    private SLBaseAvatar() {
        this.meshes = EnumMap(MeshIndex.class)
        this.meshes.put(MeshIndex.MESH_ID_HAIR, MeshEntry(BakedTextureIndex.BAKED_HAIR, AvatarTextureFaceIndex.TEX_HAIR_BAKED, "hairMesh", loadMesh("avatar_hair")))
        this.meshes.put(MeshIndex.MESH_ID_HEAD, MeshEntry(BakedTextureIndex.BAKED_HEAD, AvatarTextureFaceIndex.TEX_HEAD_BAKED, "headMesh", loadMesh("avatar_head")))
        this.meshes.put(MeshIndex.MESH_ID_EYELASH, MeshEntry(BakedTextureIndex.BAKED_HEAD, AvatarTextureFaceIndex.TEX_HEAD_BAKED, "eyelashMesh", loadMesh("avatar_eyelashes")))
        this.meshes.put(MeshIndex.MESH_ID_UPPER_BODY, MeshEntry(BakedTextureIndex.BAKED_UPPER, AvatarTextureFaceIndex.TEX_UPPER_BAKED, "upperBodyMesh", loadMesh("avatar_upper_body")))
        this.meshes.put(MeshIndex.MESH_ID_LOWER_BODY, MeshEntry(BakedTextureIndex.BAKED_LOWER, AvatarTextureFaceIndex.TEX_LOWER_BAKED, "lowerBodyMesh", loadMesh("avatar_lower_body")))
        this.meshes.put(MeshIndex.MESH_ID_EYEBALL_LEFT, MeshEntry(BakedTextureIndex.BAKED_EYES, AvatarTextureFaceIndex.TEX_EYES_BAKED, "eyeBallLeftMesh", loadMesh("avatar_eye")))
        this.meshes.put(MeshIndex.MESH_ID_EYEBALL_RIGHT, MeshEntry(BakedTextureIndex.BAKED_EYES, AvatarTextureFaceIndex.TEX_EYES_BAKED, "eyeBallRightMesh", loadMesh("avatar_eye")))
        this.meshes.put(MeshIndex.MESH_ID_SKIRT, MeshEntry(BakedTextureIndex.BAKED_SKIRT, AvatarTextureFaceIndex.TEX_SKIRT_BAKED, "skirtMesh", loadMesh("avatar_skirt")))
    }

    /* synthetic */ SLBaseAvatar(SLBaseAvatar sLBaseAvatar) {
        this()
    }

    @JvmStatic
     fun getInstance(): SLBaseAvatar {
        return InstanceHolder.Instance
    }

     private fun loadMesh(str: String): SLPolyMesh {
        DataInputStream dataInputStream
        InputStream inputStream
        Debug.Printf("BaseAvatar: loading mesh for " + str, Object[0])
        try {
            val assetManager: AssetManager = LinkpointApp.getAssetManager()
            val open: InputStream = assetManager.open("character/" + str + ".lbm")
            if (str.equals("avatar_head")) {
                inputStream = assetManager.open("character/" + str + ".lbm_0")
                dataInputStream = DataInputStream(inputStream)
            } else {
                dataInputStream = null
                inputStream = null
            }
            val sLPolyMesh: SLPolyMesh = SLPolyMesh(DataInputStream(open), dataInputStream)
            open.close()
            if (inputStream != null) {
                inputStream.close()
            }
            return sLPolyMesh
        } catch (Exception e) {
            Debug.Warning(e)
            return null
        }
    }

     public fun getMeshEntry(meshIndex: MeshIndex): MeshEntry {
        return this.meshes.get(meshIndex)
    }
}
