package com.lumiyaviewer.lumiya.slproto.mesh;

import android.opengl.GLES20;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.avatar.AvatarSkeleton;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.zip.InflaterInputStream;
import javax.annotation.Nullable;

public class MeshData {
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

    public MeshData(File file) throws IOException {
        FileInputStream fileInputStream;
        DataInputStream dataInputStream;
        float[] fArr;
        int[] iArr;
        float[] fArr2;
        int i;
        GlobalOptions.MeshRendering meshRendering = GlobalOptions.getInstance().getMeshRendering();
        if (meshRendering == GlobalOptions.MeshRendering.disabled) {
            throw new IOException("Mesh rendering is disabled");
        }
        Debug.Printf("loading file '%s'", file.toString());
        boolean z = false;
        EnumMap enumMap = null;
        float f = 0.0f;
        try {
            fileInputStream = new FileInputStream(file);
            dataInputStream = new DataInputStream(fileInputStream);
            LLSDNode fromBinary = LLSDNode.fromBinary(dataInputStream);
            long position = fileInputStream.getChannel().position();
            LLSDNode lLSDNode = null;
            if (fromBinary.keyExists(meshRendering.getLODName())) {
                lLSDNode = fromBinary.byKey(meshRendering.getLODName());
            } else {
                GlobalOptions.MeshRendering[] values = GlobalOptions.MeshRendering.values();
                int ordinal = meshRendering.ordinal() + 1;
                while (true) {
                    if (ordinal < values.length) {
                        String lODName = values[ordinal].getLODName();
                        if (lODName != null && fromBinary.keyExists(lODName)) {
                            lLSDNode = fromBinary.byKey(lODName);
                            break;
                        }
                        ordinal++;
                    } else {
                        break;
                    }
                }
                if (lLSDNode == null) {
                    int ordinal2 = meshRendering.ordinal() - 1;
                    while (true) {
                        if (ordinal2 >= 0) {
                            String lODName2 = values[ordinal2].getLODName();
                            if (lODName2 != null && fromBinary.keyExists(lODName2)) {
                                lLSDNode = fromBinary.byKey(lODName2);
                                break;
                            }
                            ordinal2--;
                        } else {
                            break;
                        }
                    }
                }
            }
            if (lLSDNode == null) {
                throw new IOException("Mesh LOD not found");
            }
            fileInputStream.getChannel().position(((long) lLSDNode.byKey("offset").asInt()) + position);
            InflaterInputStream inflaterInputStream = new InflaterInputStream(dataInputStream);
            DataInputStream dataInputStream2 = new DataInputStream(inflaterInputStream);
            LLSDNode fromBinary2 = LLSDNode.fromBinary(dataInputStream2);
            int count = fromBinary2.getCount();
            this.faces = new MeshFace[count];
            for (int i2 = 0; i2 < count; i2++) {
                this.faces[i2] = new MeshFace(fromBinary2.byIndex(i2));
            }
            if (fromBinary.keyExists("skin")) {
                fileInputStream.getChannel().position(((long) fromBinary.byKey("skin").byKey("offset").asInt()) + position);
                InflaterInputStream inflaterInputStream2 = new InflaterInputStream(dataInputStream);
                DataInputStream dataInputStream3 = new DataInputStream(inflaterInputStream2);
                LLSDNode fromBinary3 = LLSDNode.fromBinary(dataInputStream3);
                dataInputStream3.close();
                inflaterInputStream2.close();
                if (fromBinary3.keyExists("bind_shape_matrix")) {
                    fArr = new float[16];
                    for (int i3 = 0; i3 < 16; i3++) {
                        fArr[i3] = (float) fromBinary3.byKey("bind_shape_matrix").byIndex(i3).asDouble();
                    }
                } else {
                    fArr = null;
                }
                if (fromBinary3.keyExists("joint_names")) {
                    LLSDNode byKey = fromBinary3.byKey("joint_names");
                    int min = Math.min(byKey.getCount(), MAX_RIGGED_MESH_JOINTS);
                    int[] iArr2 = new int[min];
                    for (int i4 = 0; i4 < min; i4++) {
                        String asString = byKey.byIndex(i4).asString();
                        int i5 = -1;
                        SLSkeletonBoneID sLSkeletonBoneID = SLSkeletonBoneID.bones.get(asString);
                        i5 = sLSkeletonBoneID != null ? sLSkeletonBoneID.ordinal() : i5;
                        if (sLSkeletonBoneID == null || i5 == -1) {
                            SLAttachmentPoint sLAttachmentPoint = SLAttachmentPoint.pointsByName.get(asString);
                            if (sLAttachmentPoint != null) {
                                sLSkeletonBoneID = sLAttachmentPoint.bone;
                                i = sLAttachmentPoint.nonHUDindex + SLSkeletonBoneID.VALUES.length;
                            } else {
                                i = i5;
                            }
                        } else {
                            i = i5;
                        }
                        if (sLSkeletonBoneID != null && sLSkeletonBoneID.isExtended) {
                            z = true;
                        }
                        iArr2[i4] = i;
                    }
                    iArr = iArr2;
                } else {
                    iArr = null;
                }
                if (!fromBinary3.keyExists("inverse_bind_matrix")) {
                    fArr2 = null;
                } else if (iArr != null) {
                    LLSDNode byKey2 = fromBinary3.byKey("inverse_bind_matrix");
                    fArr2 = new float[(iArr.length * 16)];
                    for (int i6 = 0; i6 < byKey2.getCount(); i6++) {
                        if (i6 < iArr.length) {
                            LLSDNode byIndex = byKey2.byIndex(i6);
                            for (int i7 = 0; i7 < 16; i7++) {
                                fArr2[(i6 * 16) + i7] = (float) byIndex.byIndex(i7).asDouble();
                            }
                        }
                    }
                    Debug.Printf("inverseBindMatrix count %d", Integer.valueOf(byKey2.getCount()));
                } else {
                    fArr2 = null;
                }
                if (fromBinary3.keyExists("alt_inverse_bind_matrix")) {
                    LLSDNode byKey3 = fromBinary3.byKey("alt_inverse_bind_matrix");
                    float[] fArr3 = new float[(byKey3.getCount() * 16)];
                    for (int i8 = 0; i8 < byKey3.getCount(); i8++) {
                        LLSDNode byIndex2 = byKey3.byIndex(i8);
                        for (int i9 = 0; i9 < 16; i9++) {
                            fArr3[(i8 * 16) + i9] = (float) byIndex2.byIndex(i9).asDouble();
                        }
                    }
                    if (iArr != null) {
                        enumMap = new EnumMap(SLSkeletonBoneID.class);
                        for (int i10 = 0; i10 < iArr.length; i10++) {
                            int i11 = iArr[i10];
                            if (i11 >= 0 && i11 < SLSkeletonBoneID.VALUES.length) {
                                SLSkeletonBoneID sLSkeletonBoneID2 = SLSkeletonBoneID.VALUES[i11];
                                float[] fArr4 = new float[3];
                                for (int i12 = 0; i12 < 3; i12++) {
                                    fArr4[i12] = fArr3[(i10 * 16) + 12 + i12];
                                }
                                enumMap.put(sLSkeletonBoneID2, fArr4);
                            }
                        }
                    }
                    Debug.Printf("alt_inverse_bind_matrix count %d", Integer.valueOf(byKey3.getCount()));
                }
                if (fromBinary3.keyExists("pelvis_offset")) {
                    f = (float) fromBinary3.byKey("pelvis_offset").asDouble();
                    Debug.Printf("Pelvis offset: %f", Float.valueOf(f));
                }
                dataInputStream2.close();
                inflaterInputStream.close();
            } else {
                fArr = null;
                iArr = null;
                fArr2 = null;
            }
            dataInputStream.close();
            fileInputStream.close();
            if (iArr == null || fArr == null || fArr2 == null) {
                this.riggingData = null;
                this.bindShapeMatrix = null;
                this.jointTranslations = null;
            } else {
                this.riggingData = MeshRiggingData.create(iArr, fArr2, z);
                this.bindShapeMatrix = fArr;
                this.jointTranslations = enumMap != null ? Maps.immutableEnumMap(enumMap) : null;
            }
            this.pelvisOffset = f;
        } catch (LLSDException e) {
            throw new IOException(e.getMessage(), e);
        } catch (Throwable th) {
            dataInputStream.close();
            fileInputStream.close();
            throw th;
        }
    }

    private MeshWeightsBuffer makeInfluenceBuffers() {
        int i = 0;
        for (MeshFace meshFace : this.faces) {
            if (meshFace != null) {
                i += meshFace.getNumVertices();
            }
        }
        MeshWeightsBuffer meshWeightsBuffer = new MeshWeightsBuffer(i);
        int i2 = 0;
        for (MeshFace meshFace2 : this.faces) {
            if (meshFace2 != null) {
                meshFace2.PrepareInfluenceBuffer(meshWeightsBuffer, i2);
                i2 += meshFace2.getNumVertices();
            }
        }
        return meshWeightsBuffer;
    }

    public void ApplyJointTranslations(MeshJointTranslations meshJointTranslations) {
        meshJointTranslations.pelvisOffset += this.pelvisOffset;
        if (this.jointTranslations != null) {
            EnumMap<SLSkeletonBoneID, float[]> enumMap = meshJointTranslations.jointTranslations;
            for (Map.Entry entry : this.jointTranslations.entrySet()) {
                enumMap.put((SLSkeletonBoneID) entry.getKey(), (float[]) entry.getValue());
            }
        }
    }

    public void PrepareInfluenceBuffers(RenderContext renderContext) {
        if (this.riggingData != null) {
            if (this.glJointIndexBuffer == null || this.glWeightsBuffer == null) {
                if (this.weightsBuffer == null) {
                    this.weightsBuffer = makeInfluenceBuffers();
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

    public void PrepareInfluencesForFace(RenderContext renderContext, int i) {
        if (this.glJointIndexBuffer != null) {
            this.glJointIndexBuffer.Bind20(renderContext, renderContext.riggedMeshProgram.vJoint, 4, 5121, 4, i * 4);
        }
        if (this.glWeightsBuffer != null) {
            this.glWeightsBuffer.Bind20(renderContext, renderContext.riggedMeshProgram.vWeight, 4, 5126, 16, i * 4 * 4);
        }
    }

    public void SetupBuffers30(RenderContext renderContext) {
        renderContext.bindRiggingMeshData(this.riggingData);
        GLES20.glUniformMatrix4fv(renderContext.currentRiggedMeshProgram.uBindShapeMatrix, 1, false, this.bindShapeMatrix, 0);
    }

    public void SetupFace30(RenderContext renderContext, int i) {
        if (this.glJointIndexBuffer == null || this.glWeightsBuffer == null) {
            if (this.weightsBuffer == null) {
                this.weightsBuffer = makeInfluenceBuffers();
            }
            if (this.glJointIndexBuffer == null) {
                this.glJointIndexBuffer = new GLLoadableBuffer(this.weightsBuffer.jointIndexBuffer);
            }
            if (this.glWeightsBuffer == null) {
                this.glWeightsBuffer = new GLLoadableBuffer(this.weightsBuffer.weightsBuffer);
            }
        }
        this.glJointIndexBuffer.Bind30Integer(renderContext, renderContext.currentRiggedMeshProgram.vJoint, 4, 5121, 0, i * 4);
        this.glWeightsBuffer.Bind20(renderContext, renderContext.currentRiggedMeshProgram.vWeight, 4, 5126, 16, i * 4 * 4);
    }

    public void UpdateRigged(int i, DirectByteBuffer directByteBuffer, int i2) {
        if (this.riggingData != null) {
            this.riggingData.UpdateRigged(this.faces[i], this.bindShapeMatrix, directByteBuffer, i2);
        }
    }

    public void UpdateRiggedMatrices(AvatarSkeleton avatarSkeleton) {
        if (this.riggingData != null) {
            this.riggingData.UpdateRiggedMatrices(avatarSkeleton);
        }
    }

    public final MeshFace getFace(int i) {
        return this.faces[i];
    }

    public final int getFaceCount() {
        return this.faces.length;
    }

    public final boolean hasExtendedBones() {
        if (this.riggingData != null) {
            return this.riggingData.hasExtendedBones();
        }
        return false;
    }

    public final boolean isRiggedMesh() {
        return this.riggingData != null;
    }

    public final boolean riggingFitsGL20() {
        if (this.riggingData != null) {
            return this.riggingData.fitsGL20();
        }
        return false;
    }
}
