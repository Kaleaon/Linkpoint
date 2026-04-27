// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.mesh;

import android.opengl.GLES20;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.avatar.AvatarSkeleton;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import com.lumiyaviewer.lumiya.render.shaders.RiggedMeshProgram;
import com.lumiyaviewer.lumiya.render.shaders.RiggedMeshProgram30;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.InflaterInputStream;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.mesh:
//            MeshFace, MeshRiggingData, MeshWeightsBuffer, MeshJointTranslations

public class MeshData
{

    public static final int MAX_RIGGED_MESH_JOINTS = 163;
    private final float bindShapeMatrix[];
    private final MeshFace faces[];
    private GLLoadableBuffer glJointIndexBuffer;
    private GLLoadableBuffer glWeightsBuffer;
    private final ImmutableMap jointTranslations;
    private final float pelvisOffset;
    private final MeshRiggingData riggingData;
    private MeshWeightsBuffer weightsBuffer;

    public MeshData(File file)
        throws IOException
    {
        float f;
        float f1;
        Object obj;
        LLSDNode llsdnode;
        Object obj1;
        EnumMap enummap;
        LLSDNode llsdnode1;
        FileInputStream fileinputstream;
        DataInputStream datainputstream1;
        Object obj2;
        long l;
        boolean flag;
        boolean flag1;
        boolean flag2;
        obj2 = GlobalOptions.getInstance().getMeshRendering();
        if (obj2 == com.lumiyaviewer.lumiya.GlobalOptions.MeshRendering.disabled)
        {
            throw new IOException("Mesh rendering is disabled");
        }
        Debug.Printf("loading file '%s'", new Object[] {
            file.toString()
        });
        flag1 = false;
        flag2 = false;
        flag = false;
        obj1 = null;
        llsdnode1 = null;
        enummap = null;
        f1 = 0.0F;
        f = 0.0F;
        try
        {
            fileinputstream = new FileInputStream(file);
            datainputstream1 = new DataInputStream(fileinputstream);
        }
        // Misplaced declaration of an exception variable
        catch (File file)
        {
            throw new IOException(file.getMessage(), file);
        }
        llsdnode = LLSDNode.fromBinary(datainputstream1);
        l = fileinputstream.getChannel().position();
        obj = null;
        if (!llsdnode.keyExists(((com.lumiyaviewer.lumiya.GlobalOptions.MeshRendering) (obj2)).getLODName())) goto _L2; else goto _L1
_L1:
        obj = llsdnode.byKey(((com.lumiyaviewer.lumiya.GlobalOptions.MeshRendering) (obj2)).getLODName());
_L11:
        if (obj != null) goto _L4; else goto _L3
_L3:
        throw new IOException("Mesh LOD not found");
        file;
        datainputstream1.close();
        fileinputstream.close();
        throw file;
_L2:
        com.lumiyaviewer.lumiya.GlobalOptions.MeshRendering ameshrendering[];
        int i;
        ameshrendering = com.lumiyaviewer.lumiya.GlobalOptions.MeshRendering.values();
        i = ((com.lumiyaviewer.lumiya.GlobalOptions.MeshRendering) (obj2)).ordinal() + 1;
_L51:
        file = ((File) (obj));
        if (i >= ameshrendering.length) goto _L6; else goto _L5
_L5:
        file = ameshrendering[i].getLODName();
        if (file == null) goto _L8; else goto _L7
_L7:
        if (!llsdnode.keyExists(file)) goto _L8; else goto _L9
_L9:
        file = llsdnode.byKey(file);
_L6:
        obj = file;
        if (file != null) goto _L11; else goto _L10
_L10:
        i = ((com.lumiyaviewer.lumiya.GlobalOptions.MeshRendering) (obj2)).ordinal() - 1;
_L52:
        obj = file;
        if (i < 0) goto _L11; else goto _L12
_L12:
        obj = ameshrendering[i].getLODName();
        if (obj == null) goto _L14; else goto _L13
_L13:
        if (!llsdnode.keyExists(((String) (obj)))) goto _L14; else goto _L15
_L15:
        obj = llsdnode.byKey(((String) (obj)));
          goto _L11
_L4:
        DataInputStream datainputstream2;
        int j;
        i = ((LLSDNode) (obj)).byKey("offset").asInt();
        fileinputstream.getChannel().position((long)i + l);
        obj2 = new InflaterInputStream(datainputstream1);
        datainputstream2 = new DataInputStream(((java.io.InputStream) (obj2)));
        file = LLSDNode.fromBinary(datainputstream2);
        j = file.getCount();
        faces = new MeshFace[j];
        i = 0;
_L17:
        if (i >= j)
        {
            break; /* Loop/switch isn't completed */
        }
        faces[i] = new MeshFace(file.byIndex(i));
        i++;
        if (true) goto _L17; else goto _L16
_L16:
        if (!llsdnode.keyExists("skin")) goto _L19; else goto _L18
_L18:
        i = llsdnode.byKey("skin").byKey("offset").asInt();
        fileinputstream.getChannel().position((long)i + l);
        file = new InflaterInputStream(datainputstream1);
        DataInputStream datainputstream = new DataInputStream(file);
        llsdnode1 = LLSDNode.fromBinary(datainputstream);
        datainputstream.close();
        file.close();
        if (!llsdnode1.keyExists("bind_shape_matrix")) goto _L21; else goto _L20
_L20:
        file = new float[16];
        i = 0;
_L23:
        float af[];
        af = file;
        if (i >= 16)
        {
            break; /* Loop/switch isn't completed */
        }
        file[i] = (float)llsdnode1.byKey("bind_shape_matrix").byIndex(i).asDouble();
        i++;
        if (true) goto _L23; else goto _L22
_L22:
        if (!llsdnode1.keyExists("joint_names")) goto _L25; else goto _L24
_L24:
        int ai[];
        LLSDNode llsdnode2;
        int k;
        llsdnode2 = llsdnode1.byKey("joint_names");
        k = Math.min(llsdnode2.getCount(), 163);
        ai = new int[k];
        j = 0;
_L55:
        if (j >= k) goto _L27; else goto _L26
_L26:
        Object obj3 = llsdnode2.byIndex(j).asString();
        i = -1;
        file = (SLSkeletonBoneID)SLSkeletonBoneID.bones.get(obj3);
        if (file == null) goto _L29; else goto _L28
_L28:
        i = file.ordinal();
          goto _L29
_L53:
        obj3 = (SLAttachmentPoint)SLAttachmentPoint.pointsByName.get(obj3);
        if (obj3 == null)
        {
            break MISSING_BLOCK_LABEL_661;
        }
        file = ((SLAttachmentPoint) (obj3)).bone;
        i = ((SLAttachmentPoint) (obj3)).nonHUDindex + SLSkeletonBoneID.VALUES.length;
_L54:
        flag1 = flag;
        if (file == null) goto _L31; else goto _L30
_L30:
        flag1 = flag;
        if (((SLSkeletonBoneID) (file)).isExtended)
        {
            flag1 = true;
        }
          goto _L31
_L27:
        if (!llsdnode1.keyExists("inverse_bind_matrix")) goto _L33; else goto _L32
_L32:
        if (ai == null) goto _L35; else goto _L34
_L34:
        llsdnode2 = llsdnode1.byKey("inverse_bind_matrix");
        file = new float[ai.length * 16];
        i = 0;
_L56:
        if (i >= llsdnode2.getCount()) goto _L37; else goto _L36
_L36:
        if (i >= ai.length) goto _L39; else goto _L38
_L38:
        obj3 = llsdnode2.byIndex(i);
        j = 0;
_L40:
        if (j >= 16)
        {
            break; /* Loop/switch isn't completed */
        }
        file[i * 16 + j] = (float)((LLSDNode) (obj3)).byIndex(j).asDouble();
        j++;
        if (true) goto _L40; else goto _L39
_L37:
        Debug.Printf("inverseBindMatrix count %d", new Object[] {
            Integer.valueOf(llsdnode2.getCount())
        });
_L57:
        if (!llsdnode1.keyExists("alt_inverse_bind_matrix")) goto _L42; else goto _L41
_L41:
        float af1[];
        llsdnode2 = llsdnode1.byKey("alt_inverse_bind_matrix");
        af1 = new float[llsdnode2.getCount() * 16];
        i = 0;
_L58:
        if (i >= llsdnode2.getCount()) goto _L44; else goto _L43
_L43:
        obj1 = llsdnode2.byIndex(i);
        j = 0;
_L46:
        if (j >= 16)
        {
            break; /* Loop/switch isn't completed */
        }
        af1[i * 16 + j] = (float)((LLSDNode) (obj1)).byIndex(j).asDouble();
        j++;
        if (true) goto _L46; else goto _L45
_L44:
        obj1 = enummap;
        if (ai == null) goto _L48; else goto _L47
_L47:
        enummap = new EnumMap(com/lumiyaviewer/lumiya/slproto/avatar/SLSkeletonBoneID);
        i = 0;
_L60:
        obj1 = enummap;
        if (i >= ai.length) goto _L48; else goto _L49
_L49:
        j = ai[i];
        if (j < 0)
        {
            break MISSING_BLOCK_LABEL_1305;
        }
        float af2[];
        if (j >= SLSkeletonBoneID.VALUES.length)
        {
            break MISSING_BLOCK_LABEL_1305;
        }
        obj1 = SLSkeletonBoneID.VALUES[j];
        af2 = new float[3];
        break MISSING_BLOCK_LABEL_972;
_L59:
        enummap.put(((Enum) (obj1)), af2);
        break MISSING_BLOCK_LABEL_1305;
_L48:
        Debug.Printf("alt_inverse_bind_matrix count %d", new Object[] {
            Integer.valueOf(llsdnode2.getCount())
        });
_L42:
        if (llsdnode1.keyExists("pelvis_offset"))
        {
            f = (float)llsdnode1.byKey("pelvis_offset").asDouble();
            Debug.Printf("Pelvis offset: %f", new Object[] {
                Float.valueOf(f)
            });
        }
        datainputstream2.close();
        ((InflaterInputStream) (obj2)).close();
_L50:
        datainputstream1.close();
        fileinputstream.close();
        if (ai != null && af != null && file != null)
        {
            riggingData = MeshRiggingData.create(ai, file, flag);
            bindShapeMatrix = af;
            if (obj1 != null)
            {
                file = Maps.immutableEnumMap(((Map) (obj1)));
            } else
            {
                file = null;
            }
            jointTranslations = file;
        } else
        {
            riggingData = null;
            bindShapeMatrix = null;
            jointTranslations = null;
        }
        pelvisOffset = f;
        return;
_L19:
        af = null;
        ai = null;
        file = null;
        f = f1;
        obj1 = llsdnode1;
        flag = flag2;
        if (true) goto _L50; else goto _L8
_L8:
        i++;
          goto _L51
_L14:
        i--;
          goto _L52
_L21:
        af = null;
          goto _L22
_L29:
        if (file != null && i != -1) goto _L54; else goto _L53
_L31:
        ai[j] = i;
        j++;
        flag = flag1;
          goto _L55
_L25:
        ai = null;
        flag = flag1;
          goto _L27
_L39:
        i++;
          goto _L56
_L35:
        file = null;
          goto _L57
_L33:
        file = null;
          goto _L57
_L45:
        i++;
          goto _L58
        j = 0;
        while (j < 3) 
        {
            af2[j] = af1[i * 16 + 12 + j];
            j++;
        }
          goto _L59
        i++;
          goto _L60
    }

    private MeshWeightsBuffer makeInfluenceBuffers()
    {
        boolean flag = false;
        MeshFace ameshface[] = faces;
        int j1 = ameshface.length;
        int i = 0;
        int k;
        int l;
        for (k = 0; i < j1; k = l)
        {
            MeshFace meshface = ameshface[i];
            l = k;
            if (meshface != null)
            {
                l = k + meshface.getNumVertices();
            }
            i++;
        }

        MeshWeightsBuffer meshweightsbuffer = new MeshWeightsBuffer(k);
        MeshFace ameshface1[] = faces;
        j1 = ameshface1.length;
        k = 0;
        for (int j = ((flag) ? 1 : 0); j < j1;)
        {
            MeshFace meshface1 = ameshface1[j];
            int i1 = k;
            if (meshface1 != null)
            {
                meshface1.PrepareInfluenceBuffer(meshweightsbuffer, k);
                i1 = k + meshface1.getNumVertices();
            }
            j++;
            k = i1;
        }

        return meshweightsbuffer;
    }

    public void ApplyJointTranslations(MeshJointTranslations meshjointtranslations)
    {
        meshjointtranslations.pelvisOffset = meshjointtranslations.pelvisOffset + pelvisOffset;
        if (jointTranslations != null)
        {
            meshjointtranslations = meshjointtranslations.jointTranslations;
            java.util.Map.Entry entry;
            for (Iterator iterator = jointTranslations.entrySet().iterator(); iterator.hasNext(); meshjointtranslations.put((SLSkeletonBoneID)entry.getKey(), (float[])entry.getValue()))
            {
                entry = (java.util.Map.Entry)iterator.next();
            }

        }
    }

    public void PrepareInfluenceBuffers(RenderContext rendercontext)
    {
        if (riggingData != null)
        {
            if (glJointIndexBuffer == null || glWeightsBuffer == null)
            {
                if (weightsBuffer == null)
                {
                    weightsBuffer = makeInfluenceBuffers();
                }
                if (glJointIndexBuffer == null)
                {
                    glJointIndexBuffer = new GLLoadableBuffer(weightsBuffer.jointIndexBuffer);
                }
                if (glWeightsBuffer == null)
                {
                    glWeightsBuffer = new GLLoadableBuffer(weightsBuffer.weightsBuffer);
                }
            }
            riggingData.PrepareInfluenceBuffers(rendercontext, bindShapeMatrix);
        }
    }

    public void PrepareInfluencesForFace(RenderContext rendercontext, int i)
    {
        if (glJointIndexBuffer != null)
        {
            glJointIndexBuffer.Bind20(rendercontext, rendercontext.riggedMeshProgram.vJoint, 4, 5121, 4, i * 4);
        }
        if (glWeightsBuffer != null)
        {
            glWeightsBuffer.Bind20(rendercontext, rendercontext.riggedMeshProgram.vWeight, 4, 5126, 16, i * 4 * 4);
        }
    }

    public void SetupBuffers30(RenderContext rendercontext)
    {
        rendercontext.bindRiggingMeshData(riggingData);
        GLES20.glUniformMatrix4fv(rendercontext.currentRiggedMeshProgram.uBindShapeMatrix, 1, false, bindShapeMatrix, 0);
    }

    public void SetupFace30(RenderContext rendercontext, int i)
    {
        if (glJointIndexBuffer == null || glWeightsBuffer == null)
        {
            if (weightsBuffer == null)
            {
                weightsBuffer = makeInfluenceBuffers();
            }
            if (glJointIndexBuffer == null)
            {
                glJointIndexBuffer = new GLLoadableBuffer(weightsBuffer.jointIndexBuffer);
            }
            if (glWeightsBuffer == null)
            {
                glWeightsBuffer = new GLLoadableBuffer(weightsBuffer.weightsBuffer);
            }
        }
        glJointIndexBuffer.Bind30Integer(rendercontext, rendercontext.currentRiggedMeshProgram.vJoint, 4, 5121, 0, i * 4);
        glWeightsBuffer.Bind20(rendercontext, rendercontext.currentRiggedMeshProgram.vWeight, 4, 5126, 16, i * 4 * 4);
    }

    public void UpdateRigged(int i, DirectByteBuffer directbytebuffer, int j)
    {
        if (riggingData != null)
        {
            riggingData.UpdateRigged(faces[i], bindShapeMatrix, directbytebuffer, j);
        }
    }

    public void UpdateRiggedMatrices(AvatarSkeleton avatarskeleton)
    {
        if (riggingData != null)
        {
            riggingData.UpdateRiggedMatrices(avatarskeleton);
        }
    }

    public final MeshFace getFace(int i)
    {
        return faces[i];
    }

    public final int getFaceCount()
    {
        return faces.length;
    }

    public final boolean hasExtendedBones()
    {
        if (riggingData != null)
        {
            return riggingData.hasExtendedBones();
        } else
        {
            return false;
        }
    }

    public final boolean isRiggedMesh()
    {
        return riggingData != null;
    }

    public final boolean riggingFitsGL20()
    {
        if (riggingData != null)
        {
            return riggingData.fitsGL20();
        } else
        {
            return false;
        }
    }
}
