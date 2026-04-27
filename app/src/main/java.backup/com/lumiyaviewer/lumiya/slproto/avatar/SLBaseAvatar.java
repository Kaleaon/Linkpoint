package com.lumiyaviewer.lumiya.slproto.avatar;

import android.content.res.AssetManager;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.LumiyaApp;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

public class SLBaseAvatar {
    private Map<MeshIndex, MeshEntry> meshes;

    private static class InstanceHolder {
        /* access modifiers changed from: private */
        public static final SLBaseAvatar Instance = new SLBaseAvatar((SLBaseAvatar) null);

        private InstanceHolder() {
        }
    }

    public static class MeshEntry {
        public String meshName;
        public SLPolyMesh polyMesh;
        public AvatarTextureFaceIndex textureFaceIndex;
        public BakedTextureIndex textureIndex;

        public MeshEntry(BakedTextureIndex bakedTextureIndex, AvatarTextureFaceIndex avatarTextureFaceIndex, String str, SLPolyMesh sLPolyMesh) {
            this.textureIndex = bakedTextureIndex;
            this.textureFaceIndex = avatarTextureFaceIndex;
            this.meshName = str;
            this.polyMesh = sLPolyMesh;
        }
    }

    private SLBaseAvatar() {
        this.meshes = new EnumMap(MeshIndex.class);
        this.meshes.put(MeshIndex.MESH_ID_HAIR, new MeshEntry(BakedTextureIndex.BAKED_HAIR, AvatarTextureFaceIndex.TEX_HAIR_BAKED, "hairMesh", loadMesh("avatar_hair")));
        this.meshes.put(MeshIndex.MESH_ID_HEAD, new MeshEntry(BakedTextureIndex.BAKED_HEAD, AvatarTextureFaceIndex.TEX_HEAD_BAKED, "headMesh", loadMesh("avatar_head")));
        this.meshes.put(MeshIndex.MESH_ID_EYELASH, new MeshEntry(BakedTextureIndex.BAKED_HEAD, AvatarTextureFaceIndex.TEX_HEAD_BAKED, "eyelashMesh", loadMesh("avatar_eyelashes")));
        this.meshes.put(MeshIndex.MESH_ID_UPPER_BODY, new MeshEntry(BakedTextureIndex.BAKED_UPPER, AvatarTextureFaceIndex.TEX_UPPER_BAKED, "upperBodyMesh", loadMesh("avatar_upper_body")));
        this.meshes.put(MeshIndex.MESH_ID_LOWER_BODY, new MeshEntry(BakedTextureIndex.BAKED_LOWER, AvatarTextureFaceIndex.TEX_LOWER_BAKED, "lowerBodyMesh", loadMesh("avatar_lower_body")));
        this.meshes.put(MeshIndex.MESH_ID_EYEBALL_LEFT, new MeshEntry(BakedTextureIndex.BAKED_EYES, AvatarTextureFaceIndex.TEX_EYES_BAKED, "eyeBallLeftMesh", loadMesh("avatar_eye")));
        this.meshes.put(MeshIndex.MESH_ID_EYEBALL_RIGHT, new MeshEntry(BakedTextureIndex.BAKED_EYES, AvatarTextureFaceIndex.TEX_EYES_BAKED, "eyeBallRightMesh", loadMesh("avatar_eye")));
        this.meshes.put(MeshIndex.MESH_ID_SKIRT, new MeshEntry(BakedTextureIndex.BAKED_SKIRT, AvatarTextureFaceIndex.TEX_SKIRT_BAKED, "skirtMesh", loadMesh("avatar_skirt")));
    }

    /* synthetic */ SLBaseAvatar(SLBaseAvatar sLBaseAvatar) {
        this();
    }

    public static SLBaseAvatar getInstance() {
        return InstanceHolder.Instance;
    }

    private SLPolyMesh loadMesh(String str) {
        DataInputStream dataInputStream;
        InputStream inputStream;
        Debug.Printf("BaseAvatar: loading mesh for " + str, new Object[0]);
        try {
            AssetManager assetManager = LumiyaApp.getAssetManager();
            InputStream open = assetManager.open("character/" + str + ".lbm");
            if (str.equals("avatar_head")) {
                inputStream = assetManager.open("character/" + str + ".lbm_0");
                dataInputStream = new DataInputStream(inputStream);
            } else {
                dataInputStream = null;
                inputStream = null;
            }
            SLPolyMesh sLPolyMesh = new SLPolyMesh(new DataInputStream(open), dataInputStream);
            open.close();
            if (inputStream != null) {
                inputStream.close();
            }
            return sLPolyMesh;
        } catch (Exception e) {
            Debug.Warning(e);
            return null;
        }
    }

    public MeshEntry getMeshEntry(MeshIndex meshIndex) {
        return this.meshes.get(meshIndex);
    }
}
