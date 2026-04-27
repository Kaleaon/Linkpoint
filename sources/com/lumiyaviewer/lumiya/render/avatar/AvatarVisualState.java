// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.avatar;

import com.google.common.collect.ImmutableSet;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.DrawableStore;
import com.lumiyaviewer.lumiya.render.spatial.SpatialIndex;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAnimation;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// Referenced classes of package com.lumiyaviewer.lumiya.render.avatar:
//            AvatarTextures, AnimationSequenceInfo, DrawableAvatar, AvatarShapeParams, 
//            DrawableAvatarStub

public class AvatarVisualState
{

    private static final ImmutableSet basicAnimations = (new com.google.common.collect.ImmutableSet.Builder()).add(UUID.fromString("2408fe9e-df1d-1d7d-f4ff-1384fa7b350f")).add(UUID.fromString("15468e00-3400-bb66-cecc-646d7c14458e")).add(UUID.fromString("370f3a20-6ca6-9971-848c-9a01bc42ae3c")).add(UUID.fromString("42b46214-4b44-79ae-deb8-0df61424ff4b")).add(UUID.fromString("f22fed8b-a5ed-2c93-64d5-bdd8b93c889f")).add(UUID.fromString("201f3fdf-cb1f-dbec-201f-7333e328ae7c")).add(UUID.fromString("47f5f6fb-22e5-ae44-f871-73aaaf4a6022")).add(UUID.fromString("aec4610c-757f-bc4e-c092-c6e9caf18daf")).add(UUID.fromString("2b5a38b2-5e00-3a97-a495-4c826bc443e6")).add(UUID.fromString("4ae8016b-31b9-03bb-c401-b1ea941db41d")).add(UUID.fromString("20f063ea-8306-2562-0b07-5c853b37b31e")).add(UUID.fromString("62c5de58-cb33-5743-3d07-9e4cd4352864")).add(UUID.fromString("05ddbff8-aaa9-92a1-2b74-8fe77a29b445")).add(UUID.fromString("6ed24bd8-91aa-4b12-ccc7-c97c857ab4e0")).add(UUID.fromString("f5fc7433-043d-e819-8298-f519a119b688")).build();
    private static final UUID defaultStandingAnimation = UUID.fromString("2408fe9e-df1d-1d7d-f4ff-1384fa7b350f");
    private final UUID agentUUID;
    private final Map animations = new ConcurrentHashMap();
    private final SLObjectAvatarInfo avatarObject;
    private volatile AvatarShapeParams avatarShapeParams;
    private final UUID avatarUUID;
    private final AvatarTextures textures = new AvatarTextures();

    public AvatarVisualState(UUID uuid, SLObjectAvatarInfo slobjectavatarinfo, UUID uuid1)
    {
        agentUUID = uuid;
        avatarObject = slobjectavatarinfo;
        avatarUUID = uuid1;
    }

    private void startAnimation(UUID uuid, int i, long l, DrawableAvatar drawableavatar)
    {
        AnimationSequenceInfo animationsequenceinfo = (AnimationSequenceInfo)animations.get(uuid);
        if (animationsequenceinfo == null)
        {
            Debug.Printf("Anim: Starting new animation %s seqID %d", new Object[] {
                uuid.toString(), Integer.valueOf(i)
            });
            animationsequenceinfo = AnimationSequenceInfo.newSequence(uuid, l, i);
            animations.put(uuid, animationsequenceinfo);
            uuid = animationsequenceinfo;
            i = 1;
        } else
        if (i != animationsequenceinfo.sequenceID)
        {
            animationsequenceinfo = AnimationSequenceInfo.restartSequence(l, i, animationsequenceinfo);
            animations.put(uuid, animationsequenceinfo);
            uuid = animationsequenceinfo;
            i = 1;
        } else
        {
            uuid = animationsequenceinfo;
            i = 0;
        }
        if (i != 0 && drawableavatar != null)
        {
            drawableavatar.AnimationUpdate(uuid);
        }
    }

    private void updateAvatarShape()
    {
        this;
        JVM INSTR monitorenter ;
        DrawableAvatar drawableavatar = SpatialIndex.getInstance().getDrawableAvatar(avatarObject);
        if (drawableavatar == null)
        {
            break MISSING_BLOCK_LABEL_25;
        }
        drawableavatar.UpdateShapeParams(avatarShapeParams);
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    private void updateTextures()
    {
        this;
        JVM INSTR monitorenter ;
        DrawableAvatar drawableavatar = SpatialIndex.getInstance().getDrawableAvatar(avatarObject);
        if (drawableavatar == null)
        {
            break MISSING_BLOCK_LABEL_25;
        }
        drawableavatar.UpdateTextures(textures);
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void ApplyAvatarAnimation(AvatarAnimation avataranimation)
    {
        this;
        JVM INSTR monitorenter ;
        DrawableAvatar drawableavatar;
        Object obj;
        Object obj1;
        long l;
        l = System.currentTimeMillis();
        obj = new HashSet();
        obj1 = new HashSet();
        ((Set) (obj1)).addAll(animations.keySet());
        drawableavatar = SpatialIndex.getInstance().getDrawableAvatar(avatarObject);
        com.lumiyaviewer.lumiya.slproto.messages.AvatarAnimation.AnimationList animationlist;
        UUID uuid;
        for (avataranimation = avataranimation.AnimationList_Fields.iterator(); avataranimation.hasNext(); startAnimation(uuid, animationlist.AnimSequenceID, l, drawableavatar))
        {
            animationlist = (com.lumiyaviewer.lumiya.slproto.messages.AvatarAnimation.AnimationList)avataranimation.next();
            uuid = animationlist.AnimID;
            ((Set) (obj)).add(uuid);
            ((Set) (obj1)).remove(uuid);
        }

        break MISSING_BLOCK_LABEL_130;
        avataranimation;
        throw avataranimation;
        if (Collections.disjoint(((java.util.Collection) (obj)), basicAnimations))
        {
            ((Set) (obj1)).remove(defaultStandingAnimation);
            startAnimation(defaultStandingAnimation, 1, l, drawableavatar);
        }
        obj = ((Iterable) (obj1)).iterator();
_L4:
        if (!((Iterator) (obj)).hasNext()) goto _L2; else goto _L1
_L1:
        obj1 = (UUID)((Iterator) (obj)).next();
        avataranimation = (AnimationSequenceInfo)animations.get(obj1);
        if (avataranimation == null) goto _L4; else goto _L3
_L3:
        if (((AnimationSequenceInfo) (avataranimation)).sequenceID == 0)
        {
            break MISSING_BLOCK_LABEL_364;
        }
        avataranimation = AnimationSequenceInfo.stopSequence(l, avataranimation);
        if (avataranimation == null) goto _L6; else goto _L5
_L5:
        animations.put(obj1, avataranimation);
        boolean flag;
        boolean flag1;
        flag1 = true;
        flag = false;
_L11:
        boolean flag2;
        flag2 = flag;
        if (avataranimation == null)
        {
            break MISSING_BLOCK_LABEL_265;
        }
        flag2 = flag | avataranimation.hasStopped(l);
        flag = flag2;
        if (drawableavatar == null) goto _L8; else goto _L7
_L7:
        if (!flag1 || !(flag2 ^ true) || avataranimation == null)
        {
            break MISSING_BLOCK_LABEL_294;
        }
        drawableavatar.AnimationUpdate(avataranimation);
        flag = flag2 | drawableavatar.IsAnimationStopped(((UUID) (obj1)));
_L8:
        if (!flag) goto _L4; else goto _L9
_L9:
        Debug.Printf("Anim: Stopping animation %s", new Object[] {
            ((UUID) (obj1)).toString()
        });
        animations.remove(obj1);
        if (drawableavatar == null) goto _L4; else goto _L10
_L10:
        drawableavatar.AnimationRemove(((UUID) (obj1)));
          goto _L4
_L6:
        flag1 = true;
        flag = true;
          goto _L11
_L2:
        this;
        JVM INSTR monitorexit ;
        return;
        flag1 = false;
        flag = false;
          goto _L11
    }

    public void ApplyAvatarAppearance(AvatarAppearance avatarappearance)
    {
        this;
        JVM INSTR monitorenter ;
        AvatarShapeParams avatarshapeparams = avatarShapeParams;
        avatarShapeParams = AvatarShapeParams.create(avatarshapeparams, avatarappearance);
        if (!avatarShapeParams.equals(avatarshapeparams))
        {
            updateAvatarShape();
        }
        if (textures.ApplyAvatarAppearance(avatarappearance))
        {
            updateTextures();
        }
        this;
        JVM INSTR monitorexit ;
        return;
        avatarappearance;
        throw avatarappearance;
    }

    public void ApplyTextures(SLTextureEntry sltextureentry, boolean flag)
    {
        this;
        JVM INSTR monitorenter ;
        if (textures.ApplyTextures(sltextureentry, flag))
        {
            updateTextures();
        }
        this;
        JVM INSTR monitorexit ;
        return;
        sltextureentry;
        throw sltextureentry;
    }

    public void ApplyVisualParams(int ai[])
    {
        this;
        JVM INSTR monitorenter ;
        AvatarShapeParams avatarshapeparams = avatarShapeParams;
        avatarShapeParams = AvatarShapeParams.create(avatarshapeparams, ai);
        if (!avatarShapeParams.equals(avatarshapeparams))
        {
            updateAvatarShape();
        }
        this;
        JVM INSTR monitorexit ;
        return;
        ai;
        throw ai;
    }

    public DrawableAvatar createDrawableAvatar(DrawableStore drawablestore)
    {
        this;
        JVM INSTR monitorenter ;
        drawablestore = new DrawableAvatar(drawablestore, agentUUID, avatarObject, avatarUUID, animations);
        drawablestore.UpdateShapeParams(avatarShapeParams);
        drawablestore.UpdateTextures(textures);
        this;
        JVM INSTR monitorexit ;
        return drawablestore;
        drawablestore;
        throw drawablestore;
    }

    public DrawableAvatarStub createDrawableAvatarStub(DrawableStore drawablestore)
    {
        this;
        JVM INSTR monitorenter ;
        drawablestore = new DrawableAvatarStub(drawablestore, agentUUID, avatarObject);
        this;
        JVM INSTR monitorexit ;
        return drawablestore;
        drawablestore;
        throw drawablestore;
    }

    public Set getRunningAnimations()
    {
        this;
        JVM INSTR monitorenter ;
        Set set = animations.keySet();
        this;
        JVM INSTR monitorexit ;
        return set;
        Exception exception;
        exception;
        throw exception;
    }

}
