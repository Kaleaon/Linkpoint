// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.avatar;

import android.opengl.GLES11;
import android.opengl.GLES20;
import android.opengl.Matrix;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.DrawableObject;
import com.lumiyaviewer.lumiya.render.DrawableStore;
import com.lumiyaviewer.lumiya.render.MatrixStack;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.picking.CollisionBox;
import com.lumiyaviewer.lumiya.render.picking.GLRayTrace;
import com.lumiyaviewer.lumiya.render.picking.IntersectInfo;
import com.lumiyaviewer.lumiya.render.picking.IntersectPickable;
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo;
import com.lumiyaviewer.lumiya.render.shaders.AvatarProgram;
import com.lumiyaviewer.lumiya.render.spatial.DrawEntryList;
import com.lumiyaviewer.lumiya.render.spatial.DrawListEntry;
import com.lumiyaviewer.lumiya.render.spatial.DrawListPrimEntry;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.slproto.avatar.MeshIndex;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.slproto.avatar.SLBaseAvatar;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBone;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.utils.IdentityMatrix;
import com.lumiyaviewer.lumiya.utils.LinkedTreeNode;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

// Referenced classes of package com.lumiyaviewer.lumiya.render.avatar:
//            DrawableAvatarStub, DrawableAttachments, AnimationSkeletonData, DrawableAvatarPart, 
//            AnimationSequenceInfo, AvatarAnimationState, AvatarSkeleton, AvatarAnimationList, 
//            DrawableHUD, DrawableHoverText, AvatarTextures, AvatarShapeParams

public class DrawableAvatar extends DrawableAvatarStub
    implements IntersectPickable, com.lumiyaviewer.lumiya.render.spatial.DrawEntryList.EntryRemovalListener
{

    private final Object animationLock;
    private final AnimationSkeletonData animationSkeletonData;
    private final Map animations;
    private volatile boolean animationsInitialized;
    private final Set deadAttachmentsList;
    private final Object deadAttachmentsLock;
    private final AtomicInteger displayedHUDid;
    private volatile DrawableAttachments drawableAttachmentList;
    private final DrawEntryList drawableAttachments;
    private volatile DrawableHUD drawableHUD;
    private LLVector3 headPosition;
    private boolean jointMatrixUpdated;
    private final float localAviWorldMatrix[];
    private final Map parts;
    private float pelvisTranslateX;
    private float pelvisTranslateY;
    private float pelvisTranslateZ;
    private final Set riggedMeshes;
    private volatile AvatarAnimationList runningAnimations;
    private volatile AvatarShapeParams shapeParams;
    private final Runnable shapeParamsUpdate;
    private volatile AvatarSkeleton skeleton;
    private final Runnable updateAttachmentsRunnable;
    private final AtomicReference updatedSkeleton;

    public DrawableAvatar(DrawableStore drawablestore, UUID uuid, SLObjectAvatarInfo slobjectavatarinfo, UUID uuid1, Map map)
    {
        int i = 0;
        super(drawablestore, uuid, slobjectavatarinfo);
        updatedSkeleton = new AtomicReference(null);
        parts = new EnumMap(com/lumiyaviewer/lumiya/slproto/avatar/MeshIndex);
        animationLock = new Object();
        animations = new HashMap();
        runningAnimations = null;
        animationsInitialized = false;
        drawableAttachmentList = new DrawableAttachments();
        displayedHUDid = new AtomicInteger();
        drawableAttachments = new DrawEntryList(this);
        deadAttachmentsLock = new Object();
        deadAttachmentsList = Collections.newSetFromMap(new IdentityHashMap());
        riggedMeshes = Collections.newSetFromMap(new ConcurrentHashMap());
        animationSkeletonData = new AnimationSkeletonData();
        pelvisTranslateX = 0.0F;
        pelvisTranslateY = 0.0F;
        pelvisTranslateZ = 0.0F;
        localAviWorldMatrix = new float[16];
        jointMatrixUpdated = false;
        updateAttachmentsRunnable = new _2D_.Lambda._cls0R0mXpfMxrM5lCygN3JijOMDexU(this);
        shapeParamsUpdate = new _2D_.Lambda._cls0R0mXpfMxrM5lCygN3JijOMDexU._cls1(this);
        uuid = SLBaseAvatar.getInstance();
        slobjectavatarinfo = MeshIndex.VALUES;
        for (int j = slobjectavatarinfo.length; i < j; i++)
        {
            MeshIndex meshindex = slobjectavatarinfo[i];
            DrawableAvatarPart drawableavatarpart = new DrawableAvatarPart(uuid1, uuid.getMeshEntry(meshindex).textureFaceIndex, uuid.getMeshEntry(meshindex).polyMesh, drawablestore.hasGL20);
            parts.put(meshindex, drawableavatarpart);
        }

        drawablestore = ((DrawableStore) (animationLock));
        drawablestore;
        JVM INSTR monitorenter ;
        if (map == null)
        {
            break MISSING_BLOCK_LABEL_359;
        }
        for (uuid = map.values().iterator(); uuid.hasNext(); animations.put(((AnimationSequenceInfo) (slobjectavatarinfo)).animationID, new AvatarAnimationState(slobjectavatarinfo, this)))
        {
            slobjectavatarinfo = (AnimationSequenceInfo)uuid.next();
        }

        break MISSING_BLOCK_LABEL_359;
        uuid;
        throw uuid;
        animationsInitialized = true;
        updateRunningAnimations();
        drawablestore;
        JVM INSTR monitorexit ;
        updateAttachments();
        return;
    }

    private void DrawParts(RenderContext rendercontext)
    {
        AvatarSkeleton avatarskeleton = skeleton;
        if (avatarskeleton == null)
        {
            return;
        }
        float f = avatarskeleton.getBodySize();
        float f1 = avatarskeleton.getPelvisToFoot();
        float f3 = avatarskeleton.getPelvisOffset();
        MeshIndex ameshindex[];
        int i;
        int j;
        if (avatarObject.parentID == 0)
        {
            f = -f / 2.0F + f1 + f3;
        } else
        {
            f = 0.0F;
        }
        pelvisTranslateX = -avatarskeleton.rootBone.getPositionX();
        pelvisTranslateY = -avatarskeleton.rootBone.getPositionY();
        pelvisTranslateZ = f + -avatarskeleton.rootBone.getPositionZ();
        rendercontext.glObjWorldTranslatef(pelvisTranslateX, pelvisTranslateY, pelvisTranslateZ);
        rendercontext.objWorldMatrix.getMatrix(localAviWorldMatrix, 0);
        GLPrepare(rendercontext, avatarskeleton.jointMatrix);
        ameshindex = MeshIndex.VALUES;
        j = ameshindex.length;
        i = 0;
        while (i < j) 
        {
            Object obj = ameshindex[i];
            DrawableAvatarPart drawableavatarpart = (DrawableAvatarPart)parts.get(obj);
            float f2;
            float f4;
            if (obj == MeshIndex.MESH_ID_EYEBALL_LEFT)
            {
                obj = (SLSkeletonBone)avatarskeleton.bones.get(SLSkeletonBoneID.mEyeLeft);
            } else
            if (obj == MeshIndex.MESH_ID_EYEBALL_RIGHT)
            {
                obj = (SLSkeletonBone)avatarskeleton.bones.get(SLSkeletonBoneID.mEyeRight);
            } else
            {
                obj = null;
            }
            if (obj != null)
            {
                rendercontext.glObjWorldPushAndMultMatrixf(((SLSkeletonBone) (obj)).getGlobalMatrix(), 0);
            }
            drawableavatarpart.GLDraw(rendercontext, avatarskeleton.jointMatrix, jointMatrixUpdated);
            if (obj != null)
            {
                rendercontext.glObjWorldPopMatrix();
            }
            i++;
        }
        obj = (SLSkeletonBone)avatarskeleton.bones.get(SLSkeletonBoneID.mHead);
        if (obj != null)
        {
            rendercontext.glObjWorldPushAndMultMatrixf(((SLSkeletonBone) (obj)).getGlobalMatrix(), 0);
            obj = rendercontext.objWorldMatrix.getMatrixData();
            i = rendercontext.objWorldMatrix.getMatrixDataOffset();
            f = obj[i + 12];
            f2 = obj[i + 13];
            f4 = obj[i + 14];
            if (headPosition == null)
            {
                headPosition = new LLVector3();
            }
            headPosition.set(f, f2, f4);
            rendercontext.glObjWorldPopMatrix();
        }
        rendercontext.curPrimProgram = null;
        if (drawableAttachmentList.Draw(rendercontext, avatarskeleton, jointMatrixUpdated))
        {
            drawableAttachmentList = new DrawableAttachments(drawableAttachmentList);
        }
        jointMatrixUpdated = false;
    }

    private void GLPrepare(RenderContext rendercontext, float af[])
    {
        if (rendercontext.hasGL20)
        {
            GLES20.glUseProgram(rendercontext.avatarProgram.getHandle());
            GLES20.glUniform1i(rendercontext.avatarProgram.sTexture, 0);
            GLES20.glUniform4f(rendercontext.avatarProgram.uObjCoordScale, 1.0F, 1.0F, 1.0F, 1.0F);
            rendercontext.glModelApplyMatrix(rendercontext.avatarProgram.uMVPMatrix);
            rendercontext.avatarProgram.SetupLighting(rendercontext, rendercontext.windlightPreset);
            GLES20.glUniformMatrix4fv(rendercontext.avatarProgram.uJointMatrix, 133, false, af, 0);
            return;
        } else
        {
            GLES11.glMatrixMode(5890);
            GLES11.glLoadMatrixf(IdentityMatrix.getMatrix(), 0);
            GLES11.glMatrixMode(5888);
            return;
        }
    }

    private boolean animate(AvatarSkeleton avatarskeleton)
    {
        boolean flag = avatarskeleton.needForceAnimate();
        AvatarAnimationList avataranimationlist = runningAnimations;
        if (avataranimationlist.needAnimate(System.currentTimeMillis()) || flag)
        {
            animationSkeletonData.animate(avatarskeleton, avataranimationlist);
            return true;
        } else
        {
            return false;
        }
    }

    private AvatarAnimationList getRunningAnimations()
    {
        Object obj = animationLock;
        obj;
        JVM INSTR monitorenter ;
        AvatarAnimationList avataranimationlist = new AvatarAnimationList(animations.values());
        obj;
        JVM INSTR monitorexit ;
        return avataranimationlist;
        Exception exception;
        exception;
        throw exception;
    }

    private void processUpdateAttachments()
    {
        Object obj;
        ArrayListMultimap arraylistmultimap;
        Set set;
        int k;
        k = 0;
        arraylistmultimap = ArrayListMultimap.create();
        set = Collections.newSetFromMap(new IdentityHashMap());
        int i = displayedHUDid.get();
        LinkedTreeNode linkedtreenode = avatarObject.treeNode.getFirstChild();
        obj = null;
        while (linkedtreenode != null) 
        {
            SLObjectInfo slobjectinfo = (SLObjectInfo)linkedtreenode.getDataObject();
            if (slobjectinfo.isDead)
            {
                continue;
            }
            int j = slobjectinfo.attachmentID;
            if (j < 0 || j >= 56)
            {
                continue;
            }
            SLAttachmentPoint slattachmentpoint = SLAttachmentPoint.attachmentPoints[j];
            if (slattachmentpoint != null)
            {
                if (slattachmentpoint.isHUD)
                {
                    if (i == slobjectinfo.localID)
                    {
                        obj = new DrawableHUD(slattachmentpoint, drawableAttachments, slobjectinfo, drawableStore, this);
                    }
                } else
                {
                    updateAttachmentParts(slobjectinfo, arraylistmultimap, j);
                }
            }
            linkedtreenode = linkedtreenode.getNextChild();
        }
        Object obj2 = deadAttachmentsLock;
        obj2;
        JVM INSTR monitorenter ;
        if (deadAttachmentsList.isEmpty()) goto _L2; else goto _L1
_L1:
        Object obj1;
        obj1 = (DrawListEntry[])deadAttachmentsList.toArray(new DrawListEntry[deadAttachmentsList.size()]);
        deadAttachmentsList.clear();
_L4:
        obj2;
        JVM INSTR monitorexit ;
        boolean flag;
        obj2 = arraylistmultimap.values().iterator();
        flag = false;
        do
        {
            if (!((Iterator) (obj2)).hasNext())
            {
                break;
            }
            DrawableObject drawableobject1 = (DrawableObject)((Iterator) (obj2)).next();
            if (drawableobject1.isRiggedMesh())
            {
                if (riggedMeshes.add(drawableobject1))
                {
                    flag = true;
                }
                set.add(drawableobject1);
            }
        } while (true);
        break MISSING_BLOCK_LABEL_296;
        obj;
        throw obj;
        boolean flag1 = flag;
        if (obj1 != null)
        {
            int l = obj1.length;
            do
            {
                flag1 = flag;
                if (k >= l)
                {
                    break;
                }
                Object obj3 = obj1[k];
                drawableAttachments.removeEntry(((com.lumiyaviewer.lumiya.utils.InlineListEntry) (obj3)));
                if (obj3 instanceof DrawListPrimEntry)
                {
                    obj3 = ((DrawListPrimEntry)obj3).getDrawableObject();
                    if (obj3 != null)
                    {
                        set.remove(obj3);
                        if (riggedMeshes.remove(obj3))
                        {
                            flag = true;
                        }
                    }
                }
                k++;
            } while (true);
        }
        obj1 = riggedMeshes.iterator();
        do
        {
            if (!((Iterator) (obj1)).hasNext())
            {
                break;
            }
            DrawableObject drawableobject = (DrawableObject)((Iterator) (obj1)).next();
            if (!set.contains(drawableobject))
            {
                riggedMeshes.remove(drawableobject);
                flag1 = true;
            }
        } while (true);
        drawableHUD = ((DrawableHUD) (obj));
        drawableAttachmentList = new DrawableAttachments(arraylistmultimap);
        if (flag1)
        {
            updateRiggedMeshes();
        }
        return;
_L2:
        obj1 = null;
        if (true) goto _L4; else goto _L3
_L3:
    }

    private void updateAttachmentParts(SLObjectInfo slobjectinfo, Multimap multimap, int i)
    {
        com.lumiyaviewer.lumiya.render.spatial.DrawListObjectEntry drawlistobjectentry = slobjectinfo.getDrawListEntry();
        drawableAttachments.addEntry(drawlistobjectentry);
        if (drawlistobjectentry instanceof DrawListPrimEntry)
        {
            multimap.put(Integer.valueOf(i), ((DrawListPrimEntry)drawlistobjectentry).getDrawableAttachment(drawableStore, this));
        }
        for (slobjectinfo = slobjectinfo.treeNode.getFirstChild(); slobjectinfo != null; slobjectinfo = slobjectinfo.getNextChild())
        {
            SLObjectInfo slobjectinfo1 = (SLObjectInfo)slobjectinfo.getDataObject();
            if (slobjectinfo1 != null)
            {
                updateAttachmentParts(slobjectinfo1, multimap, i);
            }
        }

    }

    private void updateRiggedMeshes()
    {
        PrimComputeExecutor.getInstance().execute(shapeParamsUpdate);
    }

    void _2D_com_lumiyaviewer_lumiya_render_avatar_DrawableAvatar_2D_mthref_2D_0()
    {
        processUpdateAttachments();
    }

    void AnimationRemove(UUID uuid)
    {
        Object obj = animationLock;
        obj;
        JVM INSTR monitorenter ;
        uuid = ((UUID) (animations.remove(uuid)));
        boolean flag;
        if (uuid != null)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        obj;
        JVM INSTR monitorexit ;
        if (flag)
        {
            updateRunningAnimations();
        }
        return;
        uuid;
        throw uuid;
    }

    void AnimationUpdate(AnimationSequenceInfo animationsequenceinfo)
    {
        UUID uuid = animationsequenceinfo.animationID;
        Object obj = animationLock;
        obj;
        JVM INSTR monitorenter ;
        AvatarAnimationState avataranimationstate = (AvatarAnimationState)animations.get(uuid);
        if (avataranimationstate != null) goto _L2; else goto _L1
_L1:
        animations.put(uuid, new AvatarAnimationState(animationsequenceinfo, this));
_L4:
        obj;
        JVM INSTR monitorexit ;
        updateRunningAnimations();
        return;
_L2:
        avataranimationstate.updateSequenceInfo(animationsequenceinfo);
        if (true) goto _L4; else goto _L3
_L3:
        animationsequenceinfo;
        throw animationsequenceinfo;
    }

    public void Draw(RenderContext rendercontext)
    {
        float af[];
        af = getWorldMatrix(rendercontext);
        if (af == null)
        {
            break MISSING_BLOCK_LABEL_25;
        }
        rendercontext.glObjWorldPushAndMultMatrixf(af, 0);
        DrawParts(rendercontext);
        rendercontext.glObjWorldPopMatrix();
        return;
        rendercontext;
        Debug.Warning(rendercontext);
        return;
    }

    public void DrawNameTag(RenderContext rendercontext)
    {
label0:
        {
            DrawableHoverText drawablehovertext = drawableNameTag;
            if (drawablehovertext != null)
            {
                LLVector3 llvector3 = headPosition;
                if (llvector3 == null)
                {
                    break label0;
                }
                drawablehovertext.DrawAtWorld(rendercontext, llvector3.x, llvector3.y, llvector3.z, 0.5F, rendercontext.projectionMatrix, false, 0);
            }
            return;
        }
        super.DrawNameTag(rendercontext);
    }

    boolean IsAnimationStopped(UUID uuid)
    {
        Object obj = animationLock;
        obj;
        JVM INSTR monitorenter ;
        uuid = (AvatarAnimationState)animations.get(uuid);
        if (uuid == null) goto _L2; else goto _L1
_L1:
        boolean flag = uuid.hasStopped();
_L4:
        obj;
        JVM INSTR monitorexit ;
        return flag;
_L2:
        flag = false;
        if (true) goto _L4; else goto _L3
_L3:
        uuid;
        throw uuid;
    }

    public ObjectIntersectInfo PickObject(RenderContext rendercontext, float f, float f1, float f2)
    {
        Object obj;
        int ai[];
        AvatarSkeleton avatarskeleton;
        float af2[];
        af2 = getWorldMatrix(rendercontext);
        avatarskeleton = skeleton;
        ai = null;
        obj = ai;
        if (af2 == null) goto _L2; else goto _L1
_L1:
        obj = ai;
        if (avatarskeleton == null) goto _L2; else goto _L3
_L3:
        float af[];
        float af1[];
        Iterator iterator;
        ai = rendercontext.viewportRect;
        af = new float[32];
        af1 = new float[6];
        f1 = (float)ai[3] - f1;
        rendercontext.glObjWorldPushAndMultMatrixf(af2, 0);
        rendercontext.glObjWorldTranslatef(pelvisTranslateX, pelvisTranslateY, pelvisTranslateZ);
        iterator = avatarskeleton.bones.values().iterator();
_L7:
        int i;
        do
        {
            if (!iterator.hasNext())
            {
                break MISSING_BLOCK_LABEL_510;
            }
            obj = (SLSkeletonBone)iterator.next();
        } while (((SLSkeletonBone) (obj)).boneID.isJoint);
        rendercontext.glObjWorldPushAndMultMatrixf(avatarskeleton.jointWorldMatrix, ((SLSkeletonBone) (obj)).boneID.ordinal() * 16);
        float f3;
        LLVector3 llvector3;
        LLVector3 llvector3_1;
        LLVector3 allvector3[];
        if (rendercontext.hasGL20)
        {
            Matrix.scaleM(af, 0, rendercontext.objWorldMatrix.getMatrixData(), rendercontext.objWorldMatrix.getMatrixDataOffset(), 1.0F, 1.0F, 1.0F);
            RenderContext.gluUnProject(f, f1, 0.0F, af, 0, rendercontext.modelViewMatrix.getMatrixData(), rendercontext.modelViewMatrix.getMatrixDataOffset(), ai, 0, af1, 0);
            RenderContext.gluUnProject(f, f1, 1.0F, af, 0, rendercontext.modelViewMatrix.getMatrixData(), rendercontext.modelViewMatrix.getMatrixDataOffset(), ai, 0, af1, 3);
        } else
        {
            Matrix.scaleM(af, 16, rendercontext.objWorldMatrix.getMatrixData(), rendercontext.objWorldMatrix.getMatrixDataOffset(), 1.0F, 1.0F, 1.0F);
            Matrix.multiplyMM(af, 0, rendercontext.modelViewMatrix.getMatrixData(), rendercontext.modelViewMatrix.getMatrixDataOffset(), af, 16);
            RenderContext.gluUnProject(f, f1, 0.0F, af, 0, rendercontext.projectionMatrix.getMatrixData(), rendercontext.projectionMatrix.getMatrixDataOffset(), ai, 0, af1, 0);
            RenderContext.gluUnProject(f, f1, 1.0F, af, 0, rendercontext.projectionMatrix.getMatrixData(), rendercontext.projectionMatrix.getMatrixDataOffset(), ai, 0, af1, 3);
        }
        rendercontext.glObjWorldPopMatrix();
        llvector3 = new LLVector3(af1[0], af1[1], af1[2]);
        llvector3_1 = new LLVector3(af1[3], af1[4], af1[5]);
        allvector3 = CollisionBox.getInstance().vertices;
        obj = null;
        i = 0;
_L9:
        if (i >= 12)
        {
            continue; /* Loop/switch isn't completed */
        }
        obj = GLRayTrace.intersect_RayTriangle(llvector3, llvector3_1, allvector3, i * 3);
        if (obj == null) goto _L5; else goto _L4
_L4:
        if (obj == null) goto _L7; else goto _L6
_L6:
        f3 = GLRayTrace.getIntersectionDepth(rendercontext, ((com.lumiyaviewer.lumiya.render.picking.GLRayTrace.RayIntersectInfo) (obj)).intersectPoint, af);
        if (f3 < f2) goto _L7; else goto _L8
_L8:
        obj = new ObjectIntersectInfo(new IntersectInfo(((com.lumiyaviewer.lumiya.render.picking.GLRayTrace.RayIntersectInfo) (obj)).intersectPoint), avatarObject, f3);
_L10:
        rendercontext.glObjWorldPopMatrix();
_L2:
        return ((ObjectIntersectInfo) (obj));
_L5:
        i++;
          goto _L9
        obj = null;
          goto _L10
    }

    public void RunAnimations()
    {
        AvatarSkeleton avatarskeleton = (AvatarSkeleton)updatedSkeleton.getAndSet(null);
        if (avatarskeleton != null)
        {
            skeleton = avatarskeleton;
        }
        avatarskeleton = skeleton;
        if (avatarskeleton != null && animate(avatarskeleton))
        {
            avatarskeleton.UpdateGlobalPositions(animationSkeletonData);
            jointMatrixUpdated = jointMatrixUpdated | true;
        }
    }

    void UpdateShapeParams(AvatarShapeParams avatarshapeparams)
    {
        shapeParams = avatarshapeparams;
        PrimComputeExecutor.getInstance().execute(shapeParamsUpdate);
    }

    void UpdateTextures(AvatarTextures avatartextures)
    {
        java.util.Map.Entry entry;
        for (Iterator iterator = parts.entrySet().iterator(); iterator.hasNext(); ((DrawableAvatarPart)entry.getValue()).setTexture(drawableStore.glTextureCache, avatartextures.getTexture(((DrawableAvatarPart)entry.getValue()).getFaceIndex())))
        {
            entry = (java.util.Map.Entry)iterator.next();
        }

    }

    public DrawableHUD getDrawableHUD()
    {
        return drawableHUD;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_render_avatar_DrawableAvatar_15479()
    {
        Object obj = shapeParams;
        if (obj != null)
        {
            Debug.Printf("Avatar: shapeParamsUpdate: %d rigged meshes", new Object[] {
                Integer.valueOf(riggedMeshes.size())
            });
            MeshJointTranslations meshjointtranslations = new MeshJointTranslations();
            Iterator iterator1 = riggedMeshes.iterator();
            DrawableObject drawableobject;
            boolean flag;
            for (flag = false; iterator1.hasNext(); flag = drawableobject.hasExtendedBones() | flag)
            {
                drawableobject = (DrawableObject)iterator1.next();
                drawableobject.ApplyJointTranslations(meshjointtranslations);
            }

            obj = new AvatarSkeleton(((AvatarShapeParams) (obj)), meshjointtranslations, flag);
            updatedSkeleton.set(obj);
            java.util.Map.Entry entry;
            for (Iterator iterator = parts.entrySet().iterator(); iterator.hasNext(); ((DrawableAvatarPart)entry.getValue()).setPartMorphParams(((AvatarSkeleton) (obj)).getMorphParams((MeshIndex)entry.getKey())))
            {
                entry = (java.util.Map.Entry)iterator.next();
            }

        }
    }

    public void onEntryRemovalRequested(DrawListEntry drawlistentry)
    {
        Object obj = deadAttachmentsLock;
        obj;
        JVM INSTR monitorenter ;
        deadAttachmentsList.add(drawlistentry);
        obj;
        JVM INSTR monitorexit ;
        updateAttachments();
        return;
        drawlistentry;
        throw drawlistentry;
    }

    public void onRiggedMeshReady(DrawableObject drawableobject)
    {
        if (riggedMeshes.add(drawableobject))
        {
            updateRiggedMeshes();
        }
    }

    public void setDisplayedHUDid(int i)
    {
        if (displayedHUDid.getAndSet(i) != i)
        {
            updateAttachments();
        }
    }

    public void updateAttachments()
    {
        PrimComputeExecutor.getInstance().execute(updateAttachmentsRunnable);
    }

    void updateRunningAnimations()
    {
        if (animationsInitialized)
        {
            runningAnimations = getRunningAnimations();
            AvatarSkeleton avatarskeleton = skeleton;
            if (avatarskeleton != null)
            {
                avatarskeleton.setForceAnimate();
            }
        }
    }
}
