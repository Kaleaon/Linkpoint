// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.avatar;

import java.io.InputStream;
import android.content.res.AssetManager;
import java.io.DataInputStream;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.Debug;
import java.util.EnumMap;
import java.util.Map;

public class SLBaseAvatar
{
    private Map<MeshIndex, MeshEntry> meshes;
    
    private SLBaseAvatar() {
        (this.meshes = new EnumMap<MeshIndex, MeshEntry>(MeshIndex.class)).put(MeshIndex.MESH_ID_HAIR, new MeshEntry(BakedTextureIndex.BAKED_HAIR, AvatarTextureFaceIndex.TEX_HAIR_BAKED, "hairMesh", this.loadMesh("avatar_hair")));
        this.meshes.put(MeshIndex.MESH_ID_HEAD, new MeshEntry(BakedTextureIndex.BAKED_HEAD, AvatarTextureFaceIndex.TEX_HEAD_BAKED, "headMesh", this.loadMesh("avatar_head")));
        this.meshes.put(MeshIndex.MESH_ID_EYELASH, new MeshEntry(BakedTextureIndex.BAKED_HEAD, AvatarTextureFaceIndex.TEX_HEAD_BAKED, "eyelashMesh", this.loadMesh("avatar_eyelashes")));
        this.meshes.put(MeshIndex.MESH_ID_UPPER_BODY, new MeshEntry(BakedTextureIndex.BAKED_UPPER, AvatarTextureFaceIndex.TEX_UPPER_BAKED, "upperBodyMesh", this.loadMesh("avatar_upper_body")));
        this.meshes.put(MeshIndex.MESH_ID_LOWER_BODY, new MeshEntry(BakedTextureIndex.BAKED_LOWER, AvatarTextureFaceIndex.TEX_LOWER_BAKED, "lowerBodyMesh", this.loadMesh("avatar_lower_body")));
        this.meshes.put(MeshIndex.MESH_ID_EYEBALL_LEFT, new MeshEntry(BakedTextureIndex.BAKED_EYES, AvatarTextureFaceIndex.TEX_EYES_BAKED, "eyeBallLeftMesh", this.loadMesh("avatar_eye")));
        this.meshes.put(MeshIndex.MESH_ID_EYEBALL_RIGHT, new MeshEntry(BakedTextureIndex.BAKED_EYES, AvatarTextureFaceIndex.TEX_EYES_BAKED, "eyeBallRightMesh", this.loadMesh("avatar_eye")));
        this.meshes.put(MeshIndex.MESH_ID_SKIRT, new MeshEntry(BakedTextureIndex.BAKED_SKIRT, AvatarTextureFaceIndex.TEX_SKIRT_BAKED, "skirtMesh", this.loadMesh("avatar_skirt")));
    }
    
    public static SLBaseAvatar getInstance() {
        return InstanceHolder.Instance;
    }
    
    private SLPolyMesh loadMesh(final String str) {
        while (true) {
            Debug.Printf("BaseAvatar: loading mesh for " + str, new Object[0]);
            while (true) {
                try {
                    final AssetManager assetManager = LumiyaApp.getAssetManager();
                    final InputStream open = assetManager.open("character/" + str + ".lbm");
                    if (str.equals("avatar_head")) {
                        final InputStream open2 = assetManager.open("character/" + str + ".lbm_0");
                        final DataInputStream dataInputStream = new DataInputStream(open2);
                        final SLPolyMesh slPolyMesh = new SLPolyMesh(new DataInputStream(open), dataInputStream);
                        open.close();
                        if (open2 != null) {
                            open2.close();
                        }
                        return slPolyMesh;
                    }
                }
                catch (final Exception ex) {
                    Debug.Warning(ex);
                    return null;
                }
                final DataInputStream dataInputStream = null;
                final InputStream open2 = null;
                continue;
            }
        }
    }
    
    public MeshEntry getMeshEntry(final MeshIndex meshIndex) {
        return this.meshes.get(meshIndex);
    }
    
    private static class InstanceHolder
    {
        private static final SLBaseAvatar Instance;
        
        static {
            Instance = new SLBaseAvatar(null);
        }
    }
    
    public static class MeshEntry
    {
        public String meshName;
        public SLPolyMesh polyMesh;
        public AvatarTextureFaceIndex textureFaceIndex;
        public BakedTextureIndex textureIndex;
        
        public MeshEntry(final BakedTextureIndex textureIndex, final AvatarTextureFaceIndex textureFaceIndex, final String meshName, final SLPolyMesh polyMesh) {
            this.textureIndex = textureIndex;
            this.textureFaceIndex = textureFaceIndex;
            this.meshName = meshName;
            this.polyMesh = polyMesh;
        }
    }
}
