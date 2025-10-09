package com.linkpoint.slproto.mesh

import android.opengl.GLES20
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Maps
import com.linkpoint.Debug
import com.linkpoint.GlobalOptions
import com.linkpoint.render.RenderContext
import com.linkpoint.render.avatar.AvatarSkeleton
import com.linkpoint.render.glres.buffers.GLLoadableBuffer
import com.linkpoint.slproto.avatar.SLAttachmentPoint
import com.linkpoint.slproto.avatar.SLSkeletonBoneID
import com.linkpoint.slproto.llsd.LLSDException
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.rawbuffers.DirectByteBuffer
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.EnumMap
import java.util.Map
import java.util.zip.InflaterInputStream
import javax.annotation.Nullable

class MeshData {
    const val MAX_RIGGED_MESH_JOINTS: Int = 163
    private val Float[] bindShapeMatrix
    private val MeshFace[] faces
    private GLLoadableBuffer glJointIndexBuffer
    private GLLoadableBuffer glWeightsBuffer
    private val ImmutableMap<SLSkeletonBoneID, Float[]> jointTranslations
    private val Float pelvisOffset
    private val MeshRiggingData riggingData
    private MeshWeightsBuffer weightsBuffer

    public MeshData(File file) throws IOException {
        FileInputStream fileInputStream
        DataInputStream dataInputStream
        Float[] fArr
        Int[] iArr
        Float[] fArr2
        GlobalOptions.MeshRendering meshRendering = GlobalOptions.getInstance().getMeshRendering()
        if (meshRendering == GlobalOptions.MeshRendering.disabled) {
            throw IOException("Mesh rendering is disabled")
        }
        Debug.Printf("loading file '%s'", file.toString())
        Boolean z = false
        EnumMap enumMap = null
        Float f = 0.0f
        try {
            fileInputStream = FileInputStream(file)
            dataInputStream = DataInputStream(fileInputStream)
            LLSDNode fromBinary = LLSDNode.fromBinary(dataInputStream)
            Long position = fileInputStream.getChannel().position()
            LLSDNode lLSDNode = null
            if (fromBinary.keyExists(meshRendering.getLODName())) {
                lLSDNode = fromBinary.byKey(meshRendering.getLODName())
            } else {
                GlobalOptions.MeshRendering[] values = GlobalOptions.MeshRendering.values()
                Int ordinal = meshRendering.ordinal() + 1
                while (true) {
                    if (ordinal < values.length) {
                        String lODName = values[ordinal].getLODName()
                        if (lODName != null && fromBinary.keyExists(lODName)) {
                            lLSDNode = fromBinary.byKey(lODName)
                            break
                        }
                        ordinal++
                    } else {
                        break
                    }
                }
                if (lLSDNode == null) {
                    Int ordinal2 = meshRendering.ordinal() - 1
                    while (true) {
                        if (ordinal2 >= 0) {
                            String lODName2 = values[ordinal2].getLODName()
                            if (lODName2 != null && fromBinary.keyExists(lODName2)) {
                                lLSDNode = fromBinary.byKey(lODName2)
                                break
                            }
                            ordinal2--
                        } else {
                            break
                        }
                    }
                }
            }
            if (lLSDNode == null) {
                throw IOException("Mesh LOD not found")
            }
            fileInputStream.getChannel().position(((Long) lLSDNode.byKey("offset").asInt()) + position)
            InflaterInputStream inflaterInputStream = InflaterInputStream(dataInputStream)
            DataInputStream dataInputStream2 = DataInputStream(inflaterInputStream)
            LLSDNode fromBinary2 = LLSDNode.fromBinary(dataInputStream2)
            Int count = fromBinary2.getCount()
            this.faces = MeshFace[count]
            for (Int i2 = 0; i2 < count; i2++) {
                this.faces[i2] = MeshFace(fromBinary2.byIndex(i2))
            }
            if (fromBinary.keyExists("skin")) {
                fileInputStream.getChannel().position(((Long) fromBinary.byKey("skin").byKey("offset").asInt()) + position)
                InflaterInputStream inflaterInputStream2 = InflaterInputStream(dataInputStream)
                DataInputStream dataInputStream3 = DataInputStream(inflaterInputStream2)
                LLSDNode fromBinary3 = LLSDNode.fromBinary(dataInputStream3)
                dataInputStream3.close()
                inflaterInputStream2.close()
                if (fromBinary3.keyExists("bind_shape_matrix")) {
                    fArr = Float[16]
                    for (Int i3 = 0; i3 < 16; i3++) {
                        fArr[i3] = (Float) fromBinary3.byKey("bind_shape_matrix").byIndex(i3).asDouble()
                    }
                } else {
                    fArr = null
                }
                if (fromBinary3.keyExists("joint_names")) {
                    LLSDNode byKey = fromBinary3.byKey("joint_names")
                    Int min = Math.min(byKey.getCount(), MAX_RIGGED_MESH_JOINTS)
                    Int[] iArr2 = Int[min]
                    for (Int i4 = 0; i4 < min; i4++) {
                        String asString = byKey.byIndex(i4).asString()
                        Int i5 = -1
                        SLSkeletonBoneID sLSkeletonBoneID = SLSkeletonBoneID.bones.get(asString)
                        i5 = sLSkeletonBoneID != null ? sLSkeletonBoneID.ordinal() : i5
                        if (sLSkeletonBoneID == null || i5 == -1) {
                            SLAttachmentPoint sLAttachmentPoint = SLAttachmentPoint.pointsByName.get(asString)
                            if (sLAttachmentPoint != null) {
                                sLSkeletonBoneID = sLAttachmentPoint.bone
                                i = sLAttachmentPoint.nonHUDindex + SLSkeletonBoneID.VALUES.length
                            } else {
                                i = i5
                            }
                        } else {
                            i = i5
                        }
                        if (sLSkeletonBoneID != null && sLSkeletonBoneID.isExtended) {
                            z = true
                        }
                        iArr2[i4] = i
                    }
                    iArr = iArr2
                } else {
                    iArr = null
                }
                if (!fromBinary3.keyExists("inverse_bind_matrix")) {
                    fArr2 = null
                } else if (iArr != null) {
                    LLSDNode byKey2 = fromBinary3.byKey("inverse_bind_matrix")
                    fArr2 = Float[(iArr.length * 16)]
                    for (Int i6 = 0; i6 < byKey2.getCount(); i6++) {
                        if (i6 < iArr.length) {
                            LLSDNode byIndex = byKey2.byIndex(i6)
                            for (Int i7 = 0; i7 < 16; i7++) {
                                fArr2[(i6 * 16) + i7] = (Float) byIndex.byIndex(i7).asDouble()
                            }
                        }
                    }
                    Debug.Printf("inverseBindMatrix count %d", Integer.valueOf(byKey2.getCount()))
                } else {
                    fArr2 = null
                }
                if (fromBinary3.keyExists("alt_inverse_bind_matrix")) {
                    LLSDNode byKey3 = fromBinary3.byKey("alt_inverse_bind_matrix")
                    Float[] fArr3 = Float[(byKey3.getCount() * 16)]
                    for (Int i8 = 0; i8 < byKey3.getCount(); i8++) {
                        LLSDNode byIndex2 = byKey3.byIndex(i8)
                        for (Int i9 = 0; i9 < 16; i9++) {
                            fArr3[(i8 * 16) + i9] = (Float) byIndex2.byIndex(i9).asDouble()
                        }
                    }
                    if (iArr != null) {
                        enumMap = EnumMap(SLSkeletonBoneID.class)
                        for (Int i10 = 0; i10 < iArr.length; i10++) {
                            Int i11 = iArr[i10]
                            if (i11 >= 0 && i11 < SLSkeletonBoneID.VALUES.length) {
                                SLSkeletonBoneID sLSkeletonBoneID2 = SLSkeletonBoneID.VALUES[i11]
                                Float[] fArr4 = Float[3]
                                for (Int i12 = 0; i12 < 3; i12++) {
                                    fArr4[i12] = fArr3[(i10 * 16) + 12 + i12]
                                }
                                enumMap.put(sLSkeletonBoneID2, fArr4)
                            }
                        }
                    }
                    Debug.Printf("alt_inverse_bind_matrix count %d", Integer.valueOf(byKey3.getCount()))
                }
                if (fromBinary3.keyExists("pelvis_offset")) {
                    f = (Float) fromBinary3.byKey("pelvis_offset").asDouble()
                    Debug.Printf("Pelvis offset: %f", Float.valueOf(f))
                }
                dataInputStream2.close()
                inflaterInputStream.close()
            } else {
                fArr = null
                iArr = null
                fArr2 = null
            }
            dataInputStream.close()
            fileInputStream.close()
            if (iArr == null || fArr == null || fArr2 == null) {
                this.riggingData = null
                this.bindShapeMatrix = null
                this.jointTranslations = null
            } else {
                this.riggingData = MeshRiggingData.create(iArr, fArr2, z)
                this.bindShapeMatrix = fArr
                this.jointTranslations = enumMap != null ? Maps.immutableEnumMap(enumMap) : null
            }
            this.pelvisOffset = f
        } catch (LLSDException e) {
            throw IOException(e.getMessage(), e)
        } catch (Throwable th) {
            dataInputStream.close()
            fileInputStream.close()
            throw th
        }
    }

    private MeshWeightsBuffer makeInfluenceBuffers() {
        Int i = 0
        for (MeshFace meshFace : this.faces) {
            if (meshFace != null) {
                i += meshFace.getNumVertices()
            }
        }
        MeshWeightsBuffer meshWeightsBuffer = MeshWeightsBuffer(i)
        Int i2 = 0
        for (MeshFace meshFace2 : this.faces) {
            if (meshFace2 != null) {
                meshFace2.PrepareInfluenceBuffer(meshWeightsBuffer, i2)
                i2 += meshFace2.getNumVertices()
            }
        }
        return meshWeightsBuffer
    }

    fun ApplyJointTranslations(MeshJointTranslations meshJointTranslations) {
        meshJointTranslations.pelvisOffset += this.pelvisOffset
        if (this.jointTranslations != null) {
            EnumMap<SLSkeletonBoneID, Float[]> enumMap = meshJointTranslations.jointTranslations
            for (Map.Entry entry : this.jointTranslations.entrySet()) {
                enumMap.put((SLSkeletonBoneID) entry.getKey(), (Float[]) entry.getValue())
            }
        }
    }

    fun PrepareInfluenceBuffers(RenderContext renderContext) {
        if (this.riggingData != null) {
            if (this.glJointIndexBuffer == null || this.glWeightsBuffer == null) {
                if (this.weightsBuffer == null) {
                    this.weightsBuffer = makeInfluenceBuffers()
                }
                if (this.glJointIndexBuffer == null) {
                    this.glJointIndexBuffer = GLLoadableBuffer(this.weightsBuffer.jointIndexBuffer)
                }
                if (this.glWeightsBuffer == null) {
                    this.glWeightsBuffer = GLLoadableBuffer(this.weightsBuffer.weightsBuffer)
                }
            }
            this.riggingData.PrepareInfluenceBuffers(renderContext, this.bindShapeMatrix)
        }
    }

    fun PrepareInfluencesForFace(RenderContext renderContext, Int i) {
        if (this.glJointIndexBuffer != null) {
            this.glJointIndexBuffer.Bind20(renderContext, renderContext.riggedMeshProgram.vJoint, 4, 5121, 4, i * 4)
        }
        if (this.glWeightsBuffer != null) {
            this.glWeightsBuffer.Bind20(renderContext, renderContext.riggedMeshProgram.vWeight, 4, 5126, 16, i * 4 * 4)
        }
    }

    fun SetupBuffers30(RenderContext renderContext) {
        renderContext.bindRiggingMeshData(this.riggingData)
        GLES20.glUniformMatrix4fv(renderContext.currentRiggedMeshProgram.uBindShapeMatrix, 1, false, this.bindShapeMatrix, 0)
    }

    fun SetupFace30(RenderContext renderContext, Int i) {
        if (this.glJointIndexBuffer == null || this.glWeightsBuffer == null) {
            if (this.weightsBuffer == null) {
                this.weightsBuffer = makeInfluenceBuffers()
            }
            if (this.glJointIndexBuffer == null) {
                this.glJointIndexBuffer = GLLoadableBuffer(this.weightsBuffer.jointIndexBuffer)
            }
            if (this.glWeightsBuffer == null) {
                this.glWeightsBuffer = GLLoadableBuffer(this.weightsBuffer.weightsBuffer)
            }
        }
        this.glJointIndexBuffer.Bind30Integer(renderContext, renderContext.currentRiggedMeshProgram.vJoint, 4, 5121, 0, i * 4)
        this.glWeightsBuffer.Bind20(renderContext, renderContext.currentRiggedMeshProgram.vWeight, 4, 5126, 16, i * 4 * 4)
    }

    fun UpdateRigged(Int i, DirectByteBuffer directByteBuffer, Int i2) {
        if (this.riggingData != null) {
            this.riggingData.UpdateRigged(this.faces[i], this.bindShapeMatrix, directByteBuffer, i2)
        }
    }

    fun UpdateRiggedMatrices(AvatarSkeleton avatarSkeleton) {
        if (this.riggingData != null) {
            this.riggingData.UpdateRiggedMatrices(avatarSkeleton)
        }
    }

    val MeshFace getFace(Int i) {
        return this.faces[i]
    }

    val Int getFaceCount() {
        return this.faces.length
    }

    val Boolean hasExtendedBones() {
        if (this.riggingData != null) {
            return this.riggingData.hasExtendedBones()
        }
        return false
    }

    val Boolean isRiggedMesh() {
        return this.riggingData != null
    }

    val Boolean riggingFitsGL20() {
        if (this.riggingData != null) {
            return this.riggingData.fitsGL20()
        }
        return false
    }
}
