package com.lumiyaviewer.lumiya.slproto.mesh

import android.opengl.GLES20
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Maps
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.GlobalOptions
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.avatar.AvatarSkeleton
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import com.lumiyaviewer.rawbuffers.DirectByteBuffer
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.EnumMap
import java.util.zip.InflaterInputStream

class MeshData(file: File) {
    val MAX_RIGGED_MESH_JOINTS: Int = 163
    
    private var bindShapeMatrix: FloatArray? = null
    private lateinit var faces: Array<MeshFace>
    private var glJointIndexBuffer: GLLoadableBuffer? = null
    private var glWeightsBuffer: GLLoadableBuffer? = null
    
    private var jointTranslations: ImmutableMap<SLSkeletonBoneID, FloatArray>? = null
    private var pelvisOffset: Float = 0.0f
    
    private var riggingData: MeshRiggingData? = null
    private var weightsBuffer: MeshWeightsBuffer? = null

    init {
        val meshRendering = GlobalOptions.getInstance().getMeshRendering()
        if (meshRendering == GlobalOptions.MeshRendering.disabled) {
            throw IOException("Mesh rendering is disabled")
        }
        Debug.Log("loading file '$file'")
        
        var fileInputStream: FileInputStream? = null
        var dataInputStream: DataInputStream? = null
        
        try {
            fileInputStream = FileInputStream(file)
            dataInputStream = DataInputStream(fileInputStream)
            val fromBinary = LLSDNode.fromBinary(dataInputStream)
            val position = fileInputStream.channel.position()
            
            var llsdNode: LLSDNode? = null
            if (fromBinary.keyExists(meshRendering.getLODName())) {
                llsdNode = fromBinary.byKey(meshRendering.getLODName())
            } else {
                // Fallback logic
                for (rendering in GlobalOptions.MeshRendering.values()) {
                    if (rendering.ordinal > meshRendering.ordinal) {
                         val lodName = rendering.getLODName()
                         if (lodName != null && fromBinary.keyExists(lodName)) {
                             llsdNode = fromBinary.byKey(lodName)
                             break
                         }
                    }
                }
                
                if (llsdNode == null) {
                    for (i in meshRendering.ordinal - 1 downTo 0) {
                         val rendering = GlobalOptions.MeshRendering.values()[i]
                         val lodName = rendering.getLODName()
                         if (lodName != null && fromBinary.keyExists(lodName)) {
                             llsdNode = fromBinary.byKey(lodName)
                             break
                         }
                    }
                }
            }
            
            if (llsdNode == null) {
                throw IOException("Mesh LOD not found")
            }

            fileInputStream.channel.position((llsdNode.byKey("offset").asInt()).toLong() + position)
            var inflaterInputStream = InflaterInputStream(dataInputStream)
            var dataInputStream2 = DataInputStream(inflaterInputStream)
            val fromBinary2 = LLSDNode.fromBinary(dataInputStream2)
            val count = fromBinary2.count
            
            // Assuming MeshFace constructor takes LLSDNode
            this.faces = Array(count) { i -> MeshFace(fromBinary2.byIndex(i)) }
            
            // Temp vars for initialization
            var tempBindShapeMatrix: FloatArray? = null
            var tempJointIndices: IntArray? = null
            var tempInverseBindMatrix: FloatArray? = null
            var hasExtended = false
            var tempEnumMap: EnumMap<SLSkeletonBoneID, FloatArray>? = null

            if (fromBinary.keyExists("skin")) {
                fileInputStream.channel.position((fromBinary.byKey("skin").byKey("offset").asInt()).toLong() + position)
                inflaterInputStream = InflaterInputStream(dataInputStream)
                dataInputStream2 = DataInputStream(inflaterInputStream)
                val fromBinary3 = LLSDNode.fromBinary(dataInputStream2)
                
                // Note: streams wrapped by InflaterInputStream shouldn't be closed if we want to keep using outer stream? 
                // But here we seem done with them for this block.
                // dataInputStream2.close() // This closes underlying stream too!
                
                if (fromBinary3.keyExists("bind_shape_matrix")) {
                    tempBindShapeMatrix = FloatArray(16)
                    val matrixNode = fromBinary3.byKey("bind_shape_matrix")
                    for (i in 0 until 16) {
                        tempBindShapeMatrix[i] = matrixNode.byIndex(i).asDouble().toFloat()
                    }
                }
                
                if (fromBinary3.keyExists("joint_names")) {
                    val byKey = fromBinary3.byKey("joint_names")
                    val min = kotlin.math.min(byKey.count, MAX_RIGGED_MESH_JOINTS)
                    tempJointIndices = IntArray(min)
                    
                    for (i in 0 until min) {
                        val asString = byKey.byIndex(i).asString()
                        var boneIndex = -1
                        var boneID = SLSkeletonBoneID.bones[asString]
                        
                        if (boneID != null) {
                             boneIndex = boneID.ordinal
                        }
                        
                        if (boneID == null || boneIndex == -1) {
                            val attachmentPoint = SLAttachmentPoint.pointsByName[asString]
                            if (attachmentPoint != null) {
                                boneID = attachmentPoint.bone
                                boneIndex = attachmentPoint.nonHUDindex + SLSkeletonBoneID.values().size
                            }
                        }
                        
                        if (boneID != null && boneID.isExtended) {
                            hasExtended = true
                        }
                        tempJointIndices[i] = boneIndex
                    }
                }

                if (fromBinary3.keyExists("inverse_bind_matrix") && tempJointIndices != null) {
                     val byKey2 = fromBinary3.byKey("inverse_bind_matrix")
                     tempInverseBindMatrix = FloatArray(tempJointIndices.size * 16)
                     for (i in 0 until byKey2.count) {
                         if (i < tempJointIndices.size) {
                             val byIndex = byKey2.byIndex(i)
                             for (j in 0 until 16) {
                                 tempInverseBindMatrix[(i * 16) + j] = byIndex.byIndex(j).asDouble().toFloat()
                             }
                         }
                     }
                     Debug.Log("inverseBindMatrix count ${byKey2.count}")
                }
                
                if (fromBinary3.keyExists("alt_inverse_bind_matrix")) {
                    val byKey3 = fromBinary3.byKey("alt_inverse_bind_matrix")
                    val tempAlt = FloatArray(byKey3.count * 16)
                    for (i in 0 until byKey3.count) {
                        val byIndex = byKey3.byIndex(i)
                        for (j in 0 until 16) {
                            tempAlt[(i * 16) + j] = byIndex.byIndex(j).asDouble().toFloat()
                        }
                    }
                    
                    if (tempJointIndices != null) {
                        tempEnumMap = EnumMap(SLSkeletonBoneID::class.java)
                        for (i in tempJointIndices.indices) {
                            val jointIdx = tempJointIndices[i]
                            if (jointIdx >= 0 && jointIdx < SLSkeletonBoneID.values().size) {
                                val boneID = SLSkeletonBoneID.values()[jointIdx]
                                val vec = FloatArray(3)
                                for (k in 0 until 3) {
                                    vec[k] = tempAlt[(i * 16) + 12 + k]
                                }
                                tempEnumMap[boneID] = vec
                            }
                        }
                    }
                    Debug.Log("alt_inverse_bind_matrix count ${byKey3.count}")
                }
                
                if (fromBinary3.keyExists("pelvis_offset")) {
                    this.pelvisOffset = fromBinary3.byKey("pelvis_offset").asDouble().toFloat()
                    Debug.Log("Pelvis offset: $pelvisOffset")
                }
            }

            if (tempJointIndices == null || tempBindShapeMatrix == null || tempInverseBindMatrix == null) {
                this.riggingData = null
                this.bindShapeMatrix = null
                this.jointTranslations = null
            } else {
                this.riggingData = MeshRiggingData.create(tempJointIndices, tempInverseBindMatrix, hasExtended)
                this.bindShapeMatrix = tempBindShapeMatrix
                this.jointTranslations = if (tempEnumMap != null) Maps.immutableEnumMap(tempEnumMap) else null
            }

        } catch (e: LLSDException) {
            throw IOException(e.message, e)
        } finally {
            dataInputStream?.close()
            fileInputStream?.close()
        }
    }

    private fun makeInfluenceBuffers(): MeshWeightsBuffer {
        var totalVertices = 0
        for (face in faces) {
            totalVertices += face.getNumVertices()
        }
        
        val buffer = MeshWeightsBuffer(totalVertices)
        var offset = 0
        for (face in faces) {
            face.PrepareInfluenceBuffer(buffer, offset)
            offset += face.getNumVertices()
        }
        return buffer
    }

    fun ApplyJointTranslations(meshJointTranslations: MeshJointTranslations) {
        meshJointTranslations.pelvisOffset += this.pelvisOffset
        val translations = this.jointTranslations
        if (translations != null) {
            val targetMap = meshJointTranslations.jointTranslations
            for ((key, value) in translations) {
                targetMap[key] = value
            }
        }
    }

    fun PrepareInfluenceBuffers(renderContext: RenderContext) {
        if (this.riggingData != null) {
            if (this.glJointIndexBuffer == null || this.glWeightsBuffer == null) {
                if (this.weightsBuffer == null) {
                    this.weightsBuffer = makeInfluenceBuffers()
                }
                // Assuming GLLoadableBuffer constructor takes the buffer
                if (this.glJointIndexBuffer == null) {
                    this.glJointIndexBuffer = GLLoadableBuffer(this.weightsBuffer!!.jointIndexBuffer)
                }
                if (this.glWeightsBuffer == null) {
                    this.glWeightsBuffer = GLLoadableBuffer(this.weightsBuffer!!.weightsBuffer)
                }
            }
            this.riggingData!!.PrepareInfluenceBuffers(renderContext, this.bindShapeMatrix!!)
        }
    }

    fun PrepareInfluencesForFace(renderContext: RenderContext, i: Int) {
        this.glJointIndexBuffer?.Bind20(renderContext, renderContext.riggedMeshProgram.vJoint, 4, 5121, 4, i * 4)
        this.glWeightsBuffer?.Bind20(renderContext, renderContext.riggedMeshProgram.vWeight, 4, 5126, 16, i * 4 * 4)
    }

    fun SetupBuffers30(renderContext: RenderContext) {
        renderContext.bindRiggingMeshData(this.riggingData)
        GLES20.glUniformMatrix4fv(renderContext.currentRiggedMeshProgram.uBindShapeMatrix, 1, false, this.bindShapeMatrix, 0)
    }

    fun SetupFace30(renderContext: RenderContext, i: Int) {
        if (this.glJointIndexBuffer == null || this.glWeightsBuffer == null) {
            if (this.weightsBuffer == null) {
                this.weightsBuffer = makeInfluenceBuffers()
            }
            if (this.glJointIndexBuffer == null) {
                this.glJointIndexBuffer = GLLoadableBuffer(this.weightsBuffer!!.jointIndexBuffer)
            }
            if (this.glWeightsBuffer == null) {
                this.glWeightsBuffer = GLLoadableBuffer(this.weightsBuffer!!.weightsBuffer)
            }
        }
        this.glJointIndexBuffer!!.Bind30Integer(renderContext, renderContext.currentRiggedMeshProgram.vJoint, 4, 5121, 0, i * 4)
        this.glWeightsBuffer!!.Bind20(renderContext, renderContext.currentRiggedMeshProgram.vWeight, 4, 5126, 16, i * 4 * 4)
    }

    fun UpdateRigged(i: Int, directByteBuffer: DirectByteBuffer, i2: Int) {
        this.riggingData?.UpdateRigged(this.faces[i], this.bindShapeMatrix!!, directByteBuffer, i2)
    }

    fun UpdateRiggedMatrices(avatarSkeleton: AvatarSkeleton) {
        this.riggingData?.UpdateRiggedMatrices(avatarSkeleton)
    }

    fun getFace(i: Int): MeshFace {
        return this.faces[i]
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
