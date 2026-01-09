package com.linkpoint.slproto.mesh

import kotlin.math.*

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
import java.util.zip.InflaterInputStream

class MeshData {
    companion object {
        const val MAX_RIGGED_MESH_JOINTS: Int = 163
    }

    private var bindShapeMatrix: FloatArray? = null
    private var faces: Array<MeshFace?> = emptyArray()
    private var glJointIndexBuffer: GLLoadableBuffer? = null
    private var glWeightsBuffer: GLLoadableBuffer? = null
    private var jointTranslations: ImmutableMap<SLSkeletonBoneID, FloatArray>? = null
    private var pelvisOffset: Float = 0f
    private var riggingData: MeshRiggingData? = null
    private var weightsBuffer: MeshWeightsBuffer? = null

    @Throws(IOException::class)
    constructor(file: File) {
        val meshRendering = GlobalOptions.getInstance().getMeshRendering()
        if (meshRendering == GlobalOptions.MeshRendering.disabled) {
            throw IOException("Mesh rendering is disabled")
        }
        Debug.Printf("loading file '%s'", file.toString())
        var z = false
        var enumMap: EnumMap<SLSkeletonBoneID, FloatArray>? = null
        var f = 0.0f
        var fArr: FloatArray? = null
        var iArr: IntArray? = null
        var fArr2: FloatArray? = null
        
        val fileInputStream = FileInputStream(file)
        val dataInputStream = DataInputStream(fileInputStream)
        try {
            val fromBinary = LLSDNode.fromBinary(dataInputStream)
            val position = fileInputStream.channel.position()
            var lLSDNode: LLSDNode? = null
            
            if (fromBinary.keyExists(meshRendering.getLODName())) {
                lLSDNode = fromBinary.byKey(meshRendering.getLODName())
            } else {
                val values = GlobalOptions.MeshRendering.values()
                var ordinal = meshRendering.ordinal + 1
                while (ordinal < values.size) {
                    val lODName = values[ordinal].getLODName()
                    if (lODName != null && fromBinary.keyExists(lODName)) {
                        lLSDNode = fromBinary.byKey(lODName)
                        break
                    }
                    ordinal++
                }
                if (lLSDNode == null) {
                    var ordinal2 = meshRendering.ordinal - 1
                    while (ordinal2 >= 0) {
                        val lODName2 = values[ordinal2].getLODName()
                        if (lODName2 != null && fromBinary.keyExists(lODName2)) {
                            lLSDNode = fromBinary.byKey(lODName2)
                            break
                        }
                        ordinal2--
                    }
                }
            }
            if (lLSDNode == null) {
                throw IOException("Mesh LOD not found")
            }
            fileInputStream.channel.position(lLSDNode.byKey("offset").asInt().toLong() + position)
            val inflaterInputStream = InflaterInputStream(dataInputStream)
            val dataInputStream2 = DataInputStream(inflaterInputStream)
            val fromBinary2 = LLSDNode.fromBinary(dataInputStream2)
            val count = fromBinary2.getCount()
            this.faces = arrayOfNulls(count)
            for (i2 in 0 until count) {
                this.faces[i2] = MeshFace(fromBinary2.byIndex(i2))
            }
            if (fromBinary.keyExists("skin")) {
                fileInputStream.channel.position(fromBinary.byKey("skin").byKey("offset").asInt().toLong() + position)
                val inflaterInputStream2 = InflaterInputStream(dataInputStream)
                val dataInputStream3 = DataInputStream(inflaterInputStream2)
                val fromBinary3 = LLSDNode.fromBinary(dataInputStream3)
                dataInputStream3.close()
                inflaterInputStream2.close()
                
                if (fromBinary3.keyExists("bind_shape_matrix")) {
                    fArr = FloatArray(16)
                    for (i3 in 0 until 16) {
                        fArr[i3] = fromBinary3.byKey("bind_shape_matrix").byIndex(i3).asDouble().toFloat()
                    }
                }
                
                if (fromBinary3.keyExists("joint_names")) {
                    val byKey = fromBinary3.byKey("joint_names")
                    val min = min(byKey.getCount(), MAX_RIGGED_MESH_JOINTS)
                    val iArr2 = IntArray(min)
                    for (i4 in 0 until min) {
                        val asString = byKey.byIndex(i4).asString()
                        var i5 = -1
                        var sLSkeletonBoneID = SLSkeletonBoneID.bones[asString]
                        if (sLSkeletonBoneID != null) {
                            i5 = sLSkeletonBoneID.ordinal
                        }
                        var i = i5
                        if (sLSkeletonBoneID == null || i5 == -1) {
                            val sLAttachmentPoint = SLAttachmentPoint.pointsByName[asString]
                            if (sLAttachmentPoint != null) {
                                sLSkeletonBoneID = sLAttachmentPoint.bone
                                i = sLAttachmentPoint.nonHUDindex + SLSkeletonBoneID.VALUES.size
                            }
                        }
                        if (sLSkeletonBoneID != null && sLSkeletonBoneID.isExtended) {
                            z = true
                        }
                        iArr2[i4] = i
                    }
                    iArr = iArr2
                }
                
                if (fromBinary3.keyExists("inverse_bind_matrix") && iArr != null) {
                    val byKey2 = fromBinary3.byKey("inverse_bind_matrix")
                    fArr2 = FloatArray(iArr.size * 16)
                    for (i6 in 0 until byKey2.getCount()) {
                        if (i6 < iArr.size) {
                            val byIndex = byKey2.byIndex(i6)
                            for (i7 in 0 until 16) {
                                fArr2[(i6 * 16) + i7] = byIndex.byIndex(i7).asDouble().toFloat()
                            }
                        }
                    }
                    Debug.Printf("inverseBindMatrix count %d", Integer.valueOf(byKey2.getCount()))
                }
                
                if (fromBinary3.keyExists("alt_inverse_bind_matrix")) {
                    val byKey3 = fromBinary3.byKey("alt_inverse_bind_matrix")
                    val fArr3 = FloatArray(byKey3.getCount() * 16)
                    for (i8 in 0 until byKey3.getCount()) {
                        val byIndex2 = byKey3.byIndex(i8)
                        for (i9 in 0 until 16) {
                            fArr3[(i8 * 16) + i9] = byIndex2.byIndex(i9).asDouble().toFloat()
                        }
                    }
                    if (iArr != null) {
                        enumMap = EnumMap(SLSkeletonBoneID::class.java)
                        for (i10 in iArr.indices) {
                            val i11 = iArr[i10]
                            if (i11 >= 0 && i11 < SLSkeletonBoneID.VALUES.size) {
                                val sLSkeletonBoneID2 = SLSkeletonBoneID.VALUES[i11]
                                val fArr4 = FloatArray(3)
                                for (i12 in 0 until 3) {
                                    fArr4[i12] = fArr3[(i10 * 16) + 12 + i12]
                                }
                                enumMap[sLSkeletonBoneID2] = fArr4
                            }
                        }
                    }
                    Debug.Printf("alt_inverse_bind_matrix count %d", Integer.valueOf(byKey3.getCount()))
                }
                
                if (fromBinary3.keyExists("pelvis_offset")) {
                    f = fromBinary3.byKey("pelvis_offset").asDouble().toFloat()
                    Debug.Printf("Pelvis offset: %f", java.lang.Float.valueOf(f))
                }
                dataInputStream2.close()
                inflaterInputStream.close()
            }
            
            if (iArr == null || fArr == null || fArr2 == null) {
                this.riggingData = null
                this.bindShapeMatrix = null
                this.jointTranslations = null
            } else {
                this.riggingData = MeshRiggingData.create(iArr, fArr2, z)
                this.bindShapeMatrix = fArr
                this.jointTranslations = if (enumMap != null) Maps.immutableEnumMap(enumMap) else null
            }
            this.pelvisOffset = f
        } catch (e: LLSDException) {
            throw IOException(e.message, e)
        } finally {
            dataInputStream.close()
            fileInputStream.close()
        }
    }

    private fun makeInfluenceBuffers(): MeshWeightsBuffer {
        var i = 0
        for (meshFace in this.faces) {
            if (meshFace != null) {
                i += meshFace.getNumVertices()
            }
        }
        val meshWeightsBuffer = MeshWeightsBuffer(i)
        var i2 = 0
        for (meshFace2 in this.faces) {
            if (meshFace2 != null) {
                meshFace2.PrepareInfluenceBuffer(meshWeightsBuffer, i2)
                i2 += meshFace2.getNumVertices()
            }
        }
        return meshWeightsBuffer
    }

    fun ApplyJointTranslations(meshJointTranslations: MeshJointTranslations) {
        meshJointTranslations.pelvisOffset += this.pelvisOffset
        val jt = this.jointTranslations
        if (jt != null) {
            val enumMap = meshJointTranslations.jointTranslations
            for (entry in jt.entries) {
                enumMap[entry.key] = entry.value
            }
        }
    }

    fun PrepareInfluenceBuffers(renderContext: RenderContext) {
        val rigging = this.riggingData ?: return
        if (this.glJointIndexBuffer == null || this.glWeightsBuffer == null) {
            if (this.weightsBuffer == null) {
                this.weightsBuffer = makeInfluenceBuffers()
            }
            val wb = this.weightsBuffer!!
            if (this.glJointIndexBuffer == null) {
                this.glJointIndexBuffer = GLLoadableBuffer(wb.jointIndexBuffer)
            }
            if (this.glWeightsBuffer == null) {
                this.glWeightsBuffer = GLLoadableBuffer(wb.weightsBuffer)
            }
        }
        val bsm = this.bindShapeMatrix
        if (bsm != null) {
            rigging.PrepareInfluenceBuffers(renderContext, bsm)
        }
    }

    fun PrepareInfluencesForFace(renderContext: RenderContext, i: Int) {
        this.glJointIndexBuffer?.Bind20(renderContext, renderContext.riggedMeshProgram.vJoint, 4, 5121, 4, i * 4)
        this.glWeightsBuffer?.Bind20(renderContext, renderContext.riggedMeshProgram.vWeight, 4, 5126, 16, i * 4 * 4)
    }

    fun SetupBuffers30(renderContext: RenderContext) {
        renderContext.bindRiggingMeshData(this.riggingData)
        val bsm = this.bindShapeMatrix
        if (bsm != null) {
            GLES20.glUniformMatrix4fv(renderContext.currentRiggedMeshProgram.uBindShapeMatrix, 1, false, bsm, 0)
        }
    }

    fun SetupFace30(renderContext: RenderContext, i: Int) {
        if (this.glJointIndexBuffer == null || this.glWeightsBuffer == null) {
            if (this.weightsBuffer == null) {
                this.weightsBuffer = makeInfluenceBuffers()
            }
            val wb = this.weightsBuffer!!
            if (this.glJointIndexBuffer == null) {
                this.glJointIndexBuffer = GLLoadableBuffer(wb.jointIndexBuffer)
            }
            if (this.glWeightsBuffer == null) {
                this.glWeightsBuffer = GLLoadableBuffer(wb.weightsBuffer)
            }
        }
        this.glJointIndexBuffer?.Bind30Integer(renderContext, renderContext.currentRiggedMeshProgram.vJoint, 4, 5121, 0, i * 4)
        this.glWeightsBuffer?.Bind20(renderContext, renderContext.currentRiggedMeshProgram.vWeight, 4, 5126, 16, i * 4 * 4)
    }

    fun UpdateRigged(i: Int, directByteBuffer: DirectByteBuffer, i2: Int) {
        val rigging = this.riggingData
        val bsm = this.bindShapeMatrix
        val face = this.faces.getOrNull(i)
        if (rigging != null && bsm != null && face != null) {
            rigging.UpdateRigged(face, bsm, directByteBuffer, i2)
        }
    }

    fun UpdateRiggedMatrices(avatarSkeleton: AvatarSkeleton) {
        this.riggingData?.UpdateRiggedMatrices(avatarSkeleton)
    }

    fun getFace(i: Int): MeshFace? {
        return this.faces.getOrNull(i)
    }

    fun getFaceCount(): Int {
        return this.faces.size
    }

    fun hasExtendedBones(): Boolean {
        return this.riggingData?.hasExtendedBones() ?: false
    }

    fun isRiggedMesh(): Boolean {
        return this.riggingData != null
    }

    fun riggingFitsGL20(): Boolean {
        return this.riggingData?.fitsGL20() ?: false
    }
}
