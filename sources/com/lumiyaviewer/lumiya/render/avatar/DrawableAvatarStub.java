// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.avatar;

import android.opengl.Matrix;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.DrawableStore;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.render.avatar:
//            DrawableHoverText

public class DrawableAvatarStub
    implements com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
{

    final SLObjectAvatarInfo avatarObject;
    private final ChatterNameRetriever chatterNameRetriever;
    volatile DrawableHoverText drawableNameTag;
    protected final DrawableStore drawableStore;
    private volatile String nameTag;

    DrawableAvatarStub(DrawableStore drawablestore, UUID uuid, SLObjectAvatarInfo slobjectavatarinfo)
    {
        drawableStore = drawablestore;
        avatarObject = slobjectavatarinfo;
        chatterNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(uuid, slobjectavatarinfo.getId()), this, null);
    }

    private void setNameTag(String s)
    {
        if (!Objects.equal(nameTag, s))
        {
            nameTag = s;
            String s1;
            if (s != null)
            {
                s1 = s;
            } else
            {
                s1 = "null";
            }
            Debug.Printf("DrawableAvatar: setting: nameTag = %s", new Object[] {
                s1
            });
            if (s != null)
            {
                drawableNameTag = new DrawableHoverText(drawableStore.textTextureCache, s, 0x80000000);
            }
        }
    }

    public void DrawNameTag(RenderContext rendercontext)
    {
        DrawableHoverText drawablehovertext = drawableNameTag;
        float af[] = getWorldMatrix(rendercontext);
        if (drawablehovertext != null && af != null)
        {
            drawablehovertext.DrawAtWorld(rendercontext, af[12], af[13], 0.75F + af[14], 0.5F, rendercontext.projectionMatrix, false, 0);
        }
    }

    float[] getWorldMatrix(RenderContext rendercontext)
    {
        if (avatarObject.isMyAvatar() && avatarObject.parentID == 0)
        {
            float af[] = new float[32];
            LLQuaternion llquaternion = avatarObject.getRotation();
            if (llquaternion != null)
            {
                Matrix.setIdentityM(af, 16);
                Matrix.translateM(af, 16, rendercontext.myAviPosition.x, rendercontext.myAviPosition.y, rendercontext.myAviPosition.z);
                Matrix.multiplyMM(af, 0, af, 16, llquaternion.getInverseMatrix(), 0);
            }
            return af;
        } else
        {
            return avatarObject.worldMatrix;
        }
    }

    public void onChatterNameUpdated(ChatterNameRetriever chatternameretriever)
    {
        setNameTag(chatternameretriever.getResolvedName());
    }
}
