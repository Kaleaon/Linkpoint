// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.drawable.DrawableFaceTexture;
import com.lumiyaviewer.lumiya.render.glres.textures.GLTextureCache;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAnimatedMeshData;
import com.lumiyaviewer.lumiya.slproto.avatar.SLPolyMesh;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.Arrays;
import java.util.UUID;

public class DrawableAvatarPart
    implements ResourceConsumer
{

    public static final UUID DEFAULT_AVATAR_TEXTURE = UUID.fromString("c228d1cf-4b5d-4ba8-84f4-899a0796aa97");
    private final UUID avatarUUID;
    private final AvatarTextureFaceIndex faceIndex;
    private final boolean hasGL20;
    private volatile SLAnimatedMeshData meshData;
    private volatile boolean meshDataUpdated;
    private final Runnable meshUpdate = new Runnable() {

        final DrawableAvatarPart this$0;

        public void run()
        {
            Debug.Printf("Avatar: meshUpdate entered for part %s", new Object[] {
                DrawableAvatarPart._2D_get0(DrawableAvatarPart.this).toString()
            });
            Object obj = DrawableAvatarPart._2D_get5(DrawableAvatarPart.this);
            obj;
            JVM INSTR monitorenter ;
            Object obj1;
            float af[];
            obj1 = DrawableAvatarPart._2D_get3(DrawableAvatarPart.this);
            af = DrawableAvatarPart._2D_get2(DrawableAvatarPart.this);
            obj;
            JVM INSTR monitorexit ;
            if (af == null || obj1 == null) goto _L2; else goto _L1
_L1:
            Debug.Printf("Avatar: meshUpdate: part %s params %s", new Object[] {
                DrawableAvatarPart._2D_get0(DrawableAvatarPart.this).toString(), Arrays.toString(af)
            });
            obj = new SLAnimatedMeshData(DrawableAvatarPart._2D_get4(DrawableAvatarPart.this), DrawableAvatarPart._2D_get1(DrawableAvatarPart.this));
            DrawableAvatarPart._2D_get4(DrawableAvatarPart.this).applyMorphData(((com.lumiyaviewer.lumiya.slproto.avatar.SLMeshData) (obj)), af, ((com.lumiyaviewer.lumiya.render.GLTexture) (obj1)));
            obj1 = DrawableAvatarPart._2D_get5(DrawableAvatarPart.this);
            obj1;
            JVM INSTR monitorenter ;
            DrawableAvatarPart._2D_set0(DrawableAvatarPart.this, ((SLAnimatedMeshData) (obj)));
            DrawableAvatarPart._2D_set1(DrawableAvatarPart.this, true);
            obj1;
            JVM INSTR monitorexit ;
_L2:
            return;
            obj1;
            throw obj1;
            Exception exception;
            exception;
            throw exception;
        }

            
            {
                this$0 = DrawableAvatarPart.this;
                super();
            }
    };
    private volatile float partMorphParams[];
    private volatile OpenJPEG rawTexture;
    private final SLPolyMesh referenceMeshData;
    private volatile DrawableFaceTexture texture;
    private volatile UUID textureUUID;
    private final Object updateLock = new Object();

    static AvatarTextureFaceIndex _2D_get0(DrawableAvatarPart drawableavatarpart)
    {
        return drawableavatarpart.faceIndex;
    }

    static boolean _2D_get1(DrawableAvatarPart drawableavatarpart)
    {
        return drawableavatarpart.hasGL20;
    }

    static float[] _2D_get2(DrawableAvatarPart drawableavatarpart)
    {
        return drawableavatarpart.partMorphParams;
    }

    static OpenJPEG _2D_get3(DrawableAvatarPart drawableavatarpart)
    {
        return drawableavatarpart.rawTexture;
    }

    static SLPolyMesh _2D_get4(DrawableAvatarPart drawableavatarpart)
    {
        return drawableavatarpart.referenceMeshData;
    }

    static Object _2D_get5(DrawableAvatarPart drawableavatarpart)
    {
        return drawableavatarpart.updateLock;
    }

    static SLAnimatedMeshData _2D_set0(DrawableAvatarPart drawableavatarpart, SLAnimatedMeshData slanimatedmeshdata)
    {
        drawableavatarpart.meshData = slanimatedmeshdata;
        return slanimatedmeshdata;
    }

    static boolean _2D_set1(DrawableAvatarPart drawableavatarpart, boolean flag)
    {
        drawableavatarpart.meshDataUpdated = flag;
        return flag;
    }

    DrawableAvatarPart(UUID uuid, AvatarTextureFaceIndex avatartexturefaceindex, SLPolyMesh slpolymesh, boolean flag)
    {
        avatarUUID = uuid;
        faceIndex = avatartexturefaceindex;
        referenceMeshData = slpolymesh;
        hasGL20 = flag;
    }

    private void RequestMeshUpdate()
    {
        PrimComputeExecutor.getInstance().execute(meshUpdate);
    }

    public final void GLDraw(RenderContext rendercontext, float af[], boolean flag)
    {
        Object obj;
        SLAnimatedMeshData slanimatedmeshdata;
        DrawableFaceTexture drawablefacetexture;
        boolean flag1;
        if (rendercontext.hasGL20)
        {
            flag = false;
        }
        obj = updateLock;
        obj;
        JVM INSTR monitorenter ;
        slanimatedmeshdata = meshData;
        drawablefacetexture = texture;
        flag1 = flag;
        if (slanimatedmeshdata == null)
        {
            break MISSING_BLOCK_LABEL_61;
        }
        flag1 = flag;
        if (!meshDataUpdated)
        {
            break MISSING_BLOCK_LABEL_61;
        }
        flag1 = flag;
        if (af == null)
        {
            break MISSING_BLOCK_LABEL_61;
        }
        meshDataUpdated = false;
        flag1 = true;
        obj;
        JVM INSTR monitorexit ;
        if (slanimatedmeshdata != null)
        {
            if (flag1)
            {
                referenceMeshData.applySkeleton(slanimatedmeshdata, af);
                slanimatedmeshdata.setVerticesDirty();
            }
            slanimatedmeshdata.GLDraw(rendercontext, drawablefacetexture);
        }
        return;
        rendercontext;
        throw rendercontext;
    }

    public void OnResourceReady(Object obj, boolean flag)
    {
        Object obj1;
        String s = faceIndex.toString();
        if (obj != null)
        {
            obj1 = obj.toString();
        } else
        {
            obj1 = "null";
        }
        Debug.Printf("Avatar: (requesting meshUpdate) face %s texture %s", new Object[] {
            s, obj1
        });
        if (!(obj instanceof OpenJPEG))
        {
            break MISSING_BLOCK_LABEL_64;
        }
        obj1 = updateLock;
        obj1;
        JVM INSTR monitorenter ;
        rawTexture = (OpenJPEG)obj;
        obj1;
        JVM INSTR monitorexit ;
        RequestMeshUpdate();
        return;
        obj;
        throw obj;
    }

    public AvatarTextureFaceIndex getFaceIndex()
    {
        return faceIndex;
    }

    void setPartMorphParams(float af[])
    {
        Object obj = updateLock;
        obj;
        JVM INSTR monitorenter ;
        boolean flag = Arrays.equals(partMorphParams, af) ^ true;
        if (!flag)
        {
            break MISSING_BLOCK_LABEL_27;
        }
        partMorphParams = af;
        obj;
        JVM INSTR monitorexit ;
        if (flag)
        {
            Debug.Printf("Avatar: (requesting meshUpdate) new morphParams for part %s", new Object[] {
                faceIndex.toString()
            });
            RequestMeshUpdate();
        }
        return;
        af;
        throw af;
    }

    void setTexture(GLTextureCache gltexturecache, UUID uuid)
    {
        Object obj = updateLock;
        obj;
        JVM INSTR monitorenter ;
        String s = faceIndex.toString();
        if (uuid == null) goto _L2; else goto _L1
_L1:
        gltexturecache = uuid.toString();
_L12:
        Debug.Printf("Avatar: face %s texture %s", new Object[] {
            s, gltexturecache
        });
        gltexturecache = uuid;
        if (uuid == null) goto _L4; else goto _L3
_L3:
        if (!uuid.equals(UUIDPool.ZeroUUID)) goto _L6; else goto _L5
_L5:
        gltexturecache = null;
_L4:
        uuid = textureUUID;
        if (uuid == null) goto _L8; else goto _L7
_L7:
        if (gltexturecache == null) goto _L10; else goto _L9
_L9:
        boolean flag = uuid.equals(gltexturecache);
        if (!flag) goto _L10; else goto _L11
_L11:
        obj;
        JVM INSTR monitorexit ;
        return;
_L2:
        gltexturecache = "null";
          goto _L12
_L6:
        flag = uuid.equals(DEFAULT_AVATAR_TEXTURE);
        gltexturecache = uuid;
        if (flag)
        {
            gltexturecache = null;
        }
          goto _L4
_L8:
        if (gltexturecache == null)
        {
            return;
        }
_L10:
        textureUUID = gltexturecache;
        if (gltexturecache == null) goto _L14; else goto _L13
_L13:
        gltexturecache = DrawableTextureParams.create(gltexturecache, faceIndex, avatarUUID);
        TextureCache.getInstance().RequestResource(gltexturecache, this);
        texture = new DrawableFaceTexture(gltexturecache);
_L16:
        obj;
        JVM INSTR monitorexit ;
        return;
_L14:
        texture = null;
        meshData = null;
        rawTexture = null;
        if (true) goto _L16; else goto _L15
_L15:
        gltexturecache;
        throw gltexturecache;
          goto _L12
    }

}
