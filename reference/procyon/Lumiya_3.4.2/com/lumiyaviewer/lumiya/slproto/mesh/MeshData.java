// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.mesh;

import com.lumiyaviewer.lumiya.render.avatar.AvatarSkeleton;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import android.opengl.GLES20;
import com.lumiyaviewer.lumiya.render.RenderContext;
import java.util.Iterator;
import java.util.Map;
import com.google.common.collect.Maps;
import java.util.EnumMap;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import java.util.zip.InflaterInputStream;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import com.lumiyaviewer.lumiya.Debug;
import java.io.IOException;
import com.lumiyaviewer.lumiya.GlobalOptions;
import java.io.File;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import javax.annotation.Nullable;

public class MeshData
{
    public static final int MAX_RIGGED_MESH_JOINTS = 163;
    @Nullable
    private final float[] bindShapeMatrix;
    private final MeshFace[] faces;
    private GLLoadableBuffer glJointIndexBuffer;
    private GLLoadableBuffer glWeightsBuffer;
    @Nullable
    private final ImmutableMap<SLSkeletonBoneID, float[]> jointTranslations;
    private final float pelvisOffset;
    @Nullable
    private final MeshRiggingData riggingData;
    private MeshWeightsBuffer weightsBuffer;
    
    public MeshData(final File file) throws IOException {
        final GlobalOptions.MeshRendering meshRendering = GlobalOptions.getInstance().getMeshRendering();
        if (meshRendering == GlobalOptions.MeshRendering.disabled) {
            throw new IOException("Mesh rendering is disabled");
        }
        boolean b;
        boolean b2;
        boolean b3;
        Map<SLSkeletonBoneID, ? extends V> map;
        Map<SLSkeletonBoneID, ? extends V> map2;
        float n;
        FileInputStream in;
        DataInputStream dataInputStream;
        LLSDNode fromBinary = null;
        long position;
        LLSDNode llsdNode = null;
        while (true) {
            Debug.Printf("loading file '%s'", file.toString());
            b = false;
            b2 = false;
            b3 = false;
            final Object o = null;
            map = null;
            map2 = null;
            n = 0.0f;
            final float n2 = 0.0f;
        Label_0133:
            while (true) {
                Label_0177: {
                    try {
                        in = new FileInputStream(file);
                        dataInputStream = new DataInputStream(in);
                        try {
                            fromBinary = LLSDNode.fromBinary(dataInputStream);
                            position = in.getChannel().position();
                            llsdNode = null;
                            if (!fromBinary.keyExists(meshRendering.getLODName())) {
                                break Label_0177;
                            }
                            llsdNode = fromBinary.byKey(meshRendering.getLODName());
                            if (llsdNode == null) {
                                throw new IOException("Mesh LOD not found");
                            }
                            break;
                        }
                        finally {
                            dataInputStream.close();
                            in.close();
                        }
                    }
                    catch (final LLSDException cause) {
                        throw new IOException(cause.getMessage(), cause);
                    }
                }
                final GlobalOptions.MeshRendering[] values = GlobalOptions.MeshRendering.values();
                int n3 = meshRendering.ordinal() + 1;
                LLSDNode byKey;
                while (true) {
                    byKey = llsdNode;
                    if (n3 >= values.length) {
                        break;
                    }
                    final String lodName = values[n3].getLODName();
                    if (lodName != null && fromBinary.keyExists(lodName)) {
                        byKey = fromBinary.byKey(lodName);
                        break;
                    }
                    ++n3;
                }
                if ((llsdNode = byKey) == null) {
                    int n4 = meshRendering.ordinal() - 1;
                    String lodName2;
                    while (true) {
                        llsdNode = byKey;
                        if (n4 < 0) {
                            continue Label_0133;
                        }
                        lodName2 = values[n4].getLODName();
                        if (lodName2 != null && fromBinary.keyExists(lodName2)) {
                            break;
                        }
                        --n4;
                    }
                    llsdNode = fromBinary.byKey(lodName2);
                    continue Label_0133;
                }
                continue Label_0133;
            }
        }
        in.getChannel().position(llsdNode.byKey("offset").asInt() + position);
        final InflaterInputStream in2 = new InflaterInputStream(dataInputStream);
        final DataInputStream dataInputStream2 = new DataInputStream(in2);
        final LLSDNode fromBinary2 = LLSDNode.fromBinary(dataInputStream2);
        final int count = fromBinary2.getCount();
        this.faces = new MeshFace[count];
        for (int i = 0; i < count; ++i) {
            this.faces[i] = new MeshFace(fromBinary2.byIndex(i));
        }
        Object o;
        float n2;
        float[] bindShapeMatrix;
        int[] array2;
        float[] array3;
        if (fromBinary.keyExists("skin")) {
            in.getChannel().position(fromBinary.byKey("skin").byKey("offset").asInt() + position);
            final InflaterInputStream in3 = new InflaterInputStream(dataInputStream);
            final DataInputStream dataInputStream3 = new DataInputStream(in3);
            final LLSDNode fromBinary3 = LLSDNode.fromBinary(dataInputStream3);
            dataInputStream3.close();
            in3.close();
            if (fromBinary3.keyExists("bind_shape_matrix")) {
                final float[] array = new float[16];
                int n5 = 0;
                while (true) {
                    bindShapeMatrix = array;
                    if (n5 >= 16) {
                        break;
                    }
                    array[n5] = (float)fromBinary3.byKey("bind_shape_matrix").byIndex(n5).asDouble();
                    ++n5;
                }
            }
            else {
                bindShapeMatrix = null;
            }
            if (fromBinary3.keyExists("joint_names")) {
                final LLSDNode byKey2 = fromBinary3.byKey("joint_names");
                final int min = Math.min(byKey2.getCount(), 163);
                array2 = new int[min];
                boolean b4;
                for (int j = 0; j < min; ++j, b3 = b4) {
                    final String string = byKey2.byIndex(j).asString();
                    int ordinal = -1;
                    SLSkeletonBoneID bone = SLSkeletonBoneID.bones.get(string);
                    if (bone != null) {
                        ordinal = bone.ordinal();
                    }
                    if (bone == null || ordinal == -1) {
                        final SLAttachmentPoint slAttachmentPoint = SLAttachmentPoint.pointsByName.get(string);
                        if (slAttachmentPoint != null) {
                            bone = slAttachmentPoint.bone;
                            ordinal = slAttachmentPoint.nonHUDindex + SLSkeletonBoneID.VALUES.length;
                        }
                    }
                    b4 = b3;
                    if (bone != null) {
                        b4 = b3;
                        if (bone.isExtended) {
                            b4 = true;
                        }
                    }
                    array2[j] = ordinal;
                }
            }
            else {
                array2 = null;
                b3 = b;
            }
            if (fromBinary3.keyExists("inverse_bind_matrix")) {
                if (array2 != null) {
                    final LLSDNode byKey3 = fromBinary3.byKey("inverse_bind_matrix");
                    array3 = new float[array2.length * 16];
                    for (int k = 0; k < byKey3.getCount(); ++k) {
                        if (k < array2.length) {
                            final LLSDNode byIndex = byKey3.byIndex(k);
                            for (int l = 0; l < 16; ++l) {
                                array3[k * 16 + l] = (float)byIndex.byIndex(l).asDouble();
                            }
                        }
                    }
                    Debug.Printf("inverseBindMatrix count %d", byKey3.getCount());
                }
                else {
                    array3 = null;
                }
            }
            else {
                array3 = null;
            }
            if (fromBinary3.keyExists("alt_inverse_bind_matrix")) {
                final LLSDNode byKey4 = fromBinary3.byKey("alt_inverse_bind_matrix");
                final float[] array4 = new float[byKey4.getCount() * 16];
                for (int n6 = 0; n6 < byKey4.getCount(); ++n6) {
                    final LLSDNode byIndex2 = byKey4.byIndex(n6);
                    for (int n7 = 0; n7 < 16; ++n7) {
                        array4[n6 * 16 + n7] = (float)byIndex2.byIndex(n7).asDouble();
                    }
                }
                o = map2;
                if (array2 != null) {
                    final EnumMap<SLSkeletonBoneID, float[]> enumMap = new EnumMap<SLSkeletonBoneID, float[]>(SLSkeletonBoneID.class);
                    int n8 = 0;
                    while (true) {
                        o = enumMap;
                        if (n8 >= array2.length) {
                            break;
                        }
                        final int n9 = array2[n8];
                        if (n9 >= 0 && n9 < SLSkeletonBoneID.VALUES.length) {
                            final SLSkeletonBoneID key = SLSkeletonBoneID.VALUES[n9];
                            final float[] value = new float[3];
                            for (int n10 = 0; n10 < 3; ++n10) {
                                value[n10] = array4[n8 * 16 + 12 + n10];
                            }
                            enumMap.put(key, value);
                        }
                        ++n8;
                    }
                }
                Debug.Printf("alt_inverse_bind_matrix count %d", byKey4.getCount());
            }
            if (fromBinary3.keyExists("pelvis_offset")) {
                n2 = (float)fromBinary3.byKey("pelvis_offset").asDouble();
                Debug.Printf("Pelvis offset: %f", n2);
            }
            dataInputStream2.close();
            in2.close();
        }
        else {
            bindShapeMatrix = null;
            array2 = null;
            array3 = null;
            n2 = n;
            o = map;
            b3 = b2;
        }
        dataInputStream.close();
        in.close();
        if (array2 != null && bindShapeMatrix != null && array3 != null) {
            this.riggingData = MeshRiggingData.create(array2, array3, b3);
            this.bindShapeMatrix = bindShapeMatrix;
            ImmutableMap<SLSkeletonBoneID, Object> immutableEnumMap;
            if (o != null) {
                immutableEnumMap = (ImmutableMap<SLSkeletonBoneID, Object>)Maps.immutableEnumMap((Map<SLSkeletonBoneID, ? extends float[]>)o);
            }
            else {
                immutableEnumMap = null;
            }
            this.jointTranslations = (ImmutableMap<SLSkeletonBoneID, float[]>)immutableEnumMap;
        }
        else {
            this.riggingData = null;
            this.bindShapeMatrix = null;
            this.jointTranslations = null;
        }
        this.pelvisOffset = n2;
    }
    
    private MeshWeightsBuffer makeInfluenceBuffers() {
        final int n = 0;
        final MeshFace[] faces = this.faces;
        final int length = faces.length;
        int i = 0;
        int n2 = 0;
        while (i < length) {
            final MeshFace meshFace = faces[i];
            int n3 = n2;
            if (meshFace != null) {
                n3 = n2 + meshFace.getNumVertices();
            }
            ++i;
            n2 = n3;
        }
        final MeshWeightsBuffer meshWeightsBuffer = new MeshWeightsBuffer(n2);
        final MeshFace[] faces2 = this.faces;
        final int length2 = faces2.length;
        int n4 = 0;
        int n5;
        for (int j = n; j < length2; ++j, n4 = n5) {
            final MeshFace meshFace2 = faces2[j];
            n5 = n4;
            if (meshFace2 != null) {
                meshFace2.PrepareInfluenceBuffer(meshWeightsBuffer, n4);
                n5 = n4 + meshFace2.getNumVertices();
            }
        }
        return meshWeightsBuffer;
    }
    
    public void ApplyJointTranslations(final MeshJointTranslations meshJointTranslations) {
        meshJointTranslations.pelvisOffset += this.pelvisOffset;
        if (this.jointTranslations != null) {
            final EnumMap<SLSkeletonBoneID, float[]> jointTranslations = meshJointTranslations.jointTranslations;
            for (final Map.Entry entry : this.jointTranslations.entrySet()) {
                jointTranslations.put((SLSkeletonBoneID)entry.getKey(), (float[])entry.getValue());
            }
        }
    }
    
    public void PrepareInfluenceBuffers(final RenderContext renderContext) {
        if (this.riggingData != null) {
            if (this.glJointIndexBuffer == null || this.glWeightsBuffer == null) {
                if (this.weightsBuffer == null) {
                    this.weightsBuffer = this.makeInfluenceBuffers();
                }
                if (this.glJointIndexBuffer == null) {
                    this.glJointIndexBuffer = new GLLoadableBuffer(this.weightsBuffer.jointIndexBuffer);
                }
                if (this.glWeightsBuffer == null) {
                    this.glWeightsBuffer = new GLLoadableBuffer(this.weightsBuffer.weightsBuffer);
                }
            }
            this.riggingData.PrepareInfluenceBuffers(renderContext, this.bindShapeMatrix);
        }
    }
    
    public void PrepareInfluencesForFace(final RenderContext renderContext, final int n) {
        if (this.glJointIndexBuffer != null) {
            this.glJointIndexBuffer.Bind20(renderContext, renderContext.riggedMeshProgram.vJoint, 4, 5121, 4, n * 4);
        }
        if (this.glWeightsBuffer != null) {
            this.glWeightsBuffer.Bind20(renderContext, renderContext.riggedMeshProgram.vWeight, 4, 5126, 16, n * 4 * 4);
        }
    }
    
    public void SetupBuffers30(final RenderContext renderContext) {
        renderContext.bindRiggingMeshData(this.riggingData);
        GLES20.glUniformMatrix4fv(renderContext.currentRiggedMeshProgram.uBindShapeMatrix, 1, false, this.bindShapeMatrix, 0);
    }
    
    public void SetupFace30(final RenderContext renderContext, final int n) {
        if (this.glJointIndexBuffer == null || this.glWeightsBuffer == null) {
            if (this.weightsBuffer == null) {
                this.weightsBuffer = this.makeInfluenceBuffers();
            }
            if (this.glJointIndexBuffer == null) {
                this.glJointIndexBuffer = new GLLoadableBuffer(this.weightsBuffer.jointIndexBuffer);
            }
            if (this.glWeightsBuffer == null) {
                this.glWeightsBuffer = new GLLoadableBuffer(this.weightsBuffer.weightsBuffer);
            }
        }
        this.glJointIndexBuffer.Bind30Integer(renderContext, renderContext.currentRiggedMeshProgram.vJoint, 4, 5121, 0, n * 4);
        this.glWeightsBuffer.Bind20(renderContext, renderContext.currentRiggedMeshProgram.vWeight, 4, 5126, 16, n * 4 * 4);
    }
    
    public void UpdateRigged(final int n, final DirectByteBuffer directByteBuffer, final int n2) {
        if (this.riggingData != null) {
            this.riggingData.UpdateRigged(this.faces[n], this.bindShapeMatrix, directByteBuffer, n2);
        }
    }
    
    public void UpdateRiggedMatrices(final AvatarSkeleton avatarSkeleton) {
        if (this.riggingData != null) {
            this.riggingData.UpdateRiggedMatrices(avatarSkeleton);
        }
    }
    
    public final MeshFace getFace(final int n) {
        return this.faces[n];
    }
    
    public final int getFaceCount() {
        return this.faces.length;
    }
    
    public final boolean hasExtendedBones() {
        return this.riggingData != null && this.riggingData.hasExtendedBones();
    }
    
    public final boolean isRiggedMesh() {
        return this.riggingData != null;
    }
    
    public final boolean riggingFitsGL20() {
        return this.riggingData != null && this.riggingData.fitsGL20();
    }
}
