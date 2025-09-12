// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.avatar;

import android.content.res.AssetManager;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.LumiyaApp;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.avatar:
//            MeshIndex, BakedTextureIndex, AvatarTextureFaceIndex, SLPolyMesh

public class SLBaseAvatar
{
    private static class InstanceHolder
    {

        private static final SLBaseAvatar Instance = new SLBaseAvatar(null);

        static SLBaseAvatar _2D_get0()
        {
            return Instance;
        }


        private InstanceHolder()
        {
        }
    }

    public static class MeshEntry
    {

        public String meshName;
        public SLPolyMesh polyMesh;
        public AvatarTextureFaceIndex textureFaceIndex;
        public BakedTextureIndex textureIndex;

        public MeshEntry(BakedTextureIndex bakedtextureindex, AvatarTextureFaceIndex avatartexturefaceindex, String s, SLPolyMesh slpolymesh)
        {
            textureIndex = bakedtextureindex;
            textureFaceIndex = avatartexturefaceindex;
            meshName = s;
            polyMesh = slpolymesh;
        }
    }


    private Map meshes;

    private SLBaseAvatar()
    {
        meshes = new EnumMap(com/lumiyaviewer/lumiya/slproto/avatar/MeshIndex);
        meshes.put(MeshIndex.MESH_ID_HAIR, new MeshEntry(BakedTextureIndex.BAKED_HAIR, AvatarTextureFaceIndex.TEX_HAIR_BAKED, "hairMesh", loadMesh("avatar_hair")));
        meshes.put(MeshIndex.MESH_ID_HEAD, new MeshEntry(BakedTextureIndex.BAKED_HEAD, AvatarTextureFaceIndex.TEX_HEAD_BAKED, "headMesh", loadMesh("avatar_head")));
        meshes.put(MeshIndex.MESH_ID_EYELASH, new MeshEntry(BakedTextureIndex.BAKED_HEAD, AvatarTextureFaceIndex.TEX_HEAD_BAKED, "eyelashMesh", loadMesh("avatar_eyelashes")));
        meshes.put(MeshIndex.MESH_ID_UPPER_BODY, new MeshEntry(BakedTextureIndex.BAKED_UPPER, AvatarTextureFaceIndex.TEX_UPPER_BAKED, "upperBodyMesh", loadMesh("avatar_upper_body")));
        meshes.put(MeshIndex.MESH_ID_LOWER_BODY, new MeshEntry(BakedTextureIndex.BAKED_LOWER, AvatarTextureFaceIndex.TEX_LOWER_BAKED, "lowerBodyMesh", loadMesh("avatar_lower_body")));
        meshes.put(MeshIndex.MESH_ID_EYEBALL_LEFT, new MeshEntry(BakedTextureIndex.BAKED_EYES, AvatarTextureFaceIndex.TEX_EYES_BAKED, "eyeBallLeftMesh", loadMesh("avatar_eye")));
        meshes.put(MeshIndex.MESH_ID_EYEBALL_RIGHT, new MeshEntry(BakedTextureIndex.BAKED_EYES, AvatarTextureFaceIndex.TEX_EYES_BAKED, "eyeBallRightMesh", loadMesh("avatar_eye")));
        meshes.put(MeshIndex.MESH_ID_SKIRT, new MeshEntry(BakedTextureIndex.BAKED_SKIRT, AvatarTextureFaceIndex.TEX_SKIRT_BAKED, "skirtMesh", loadMesh("avatar_skirt")));
    }

    SLBaseAvatar(SLBaseAvatar slbaseavatar)
    {
        this();
    }

    public static SLBaseAvatar getInstance()
    {
        return InstanceHolder._2D_get0();
    }

    private SLPolyMesh loadMesh(String s)
    {
        Debug.Printf((new StringBuilder()).append("BaseAvatar: loading mesh for ").append(s).toString(), new Object[0]);
        Object obj;
        InputStream inputstream;
        obj = LumiyaApp.getAssetManager();
        inputstream = ((AssetManager) (obj)).open((new StringBuilder()).append("character/").append(s).append(".lbm").toString());
        if (!s.equals("avatar_head"))
        {
            break MISSING_BLOCK_LABEL_144;
        }
        s = ((AssetManager) (obj)).open((new StringBuilder()).append("character/").append(s).append(".lbm_0").toString());
        obj = new DataInputStream(s);
_L1:
        obj = new SLPolyMesh(new DataInputStream(inputstream), ((DataInputStream) (obj)));
        inputstream.close();
        if (s != null)
        {
            try
            {
                s.close();
            }
            // Misplaced declaration of an exception variable
            catch (String s)
            {
                Debug.Warning(s);
                return null;
            }
        }
        return ((SLPolyMesh) (obj));
        obj = null;
        s = null;
          goto _L1
    }

    public MeshEntry getMeshEntry(MeshIndex meshindex)
    {
        return (MeshEntry)meshes.get(meshindex);
    }
}
