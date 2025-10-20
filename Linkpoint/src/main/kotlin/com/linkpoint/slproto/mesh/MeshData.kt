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
import java.util.zip.InflaterInputStream

/**
 * MeshData - Handles rigged mesh data for Second Life
 * Based on Firestorm's LLMeshRepository implementation
 */
class MeshData {
    
    companion object {
        const val MAX_RIGGED_MESH_JOINTS: Int = 163
    }
    
    private val bindShapeMatrix: FloatArray?
    private val faces: Array<MeshFace?>
    private var glJointIndexBuffer: GLLoadableBuffer? = null
    private var glWeightsBuffer: GLLoadableBuffer? = null
    private val jointTranslations: ImmutableMap<SLSkeletonBoneID, FloatArray>?
    private val pelvisOffset: Float
    private val riggingData: MeshRiggingData?
    private var weightsBuffer: MeshWeightsBuffer? = null

    @Throws(IOException::class)
    constructor(file: File) {
        val meshRendering = GlobalOptions.getInstance().meshRendering
        if (meshRendering == GlobalOptions.MeshRendering.disabled) {
            throw IOException("Mesh rendering is disabled")
        }
        
        Debug.Printf("loading file '%s'", file.toString())
        
        var fileInputStream: FileInputStream? = null
        var dataInputStream: DataInputStream? = null
        var bindShape: FloatArray? = null
        var jointMap: IntArray? = null
        var inverseBindMatrix: FloatArray? = null
        var jointTranslationsMap: EnumMap<SLSkeletonBoneID, FloatArray>? = null
        var pelvis = 0.0f
        var hasExtendedBones = false
        
        try {
            fileInputStream = FileInputStream(file)
            dataInputStream = DataInputStream(fileInputStream)
            
            // Parse LLSD header
            val rootNode = LLSDNode.fromBinary(dataInputStream)
            val filePosition = fileInputStream.channel.position()
            
            // Find appropriate LOD level
            var lodNode: LLSDNode? = null
            if (rootNode.keyExists(meshRendering.lodName)) {
                lodNode = rootNode.byKey(meshRendering.lodName)
            } else {
                // Try higher LOD levels first
                val values = GlobalOptions.MeshRendering.values()
                var ordinal = meshRendering.ordinal + 1
                while (ordinal < values.size) {
                    val lodName = values[ordinal].lodName
                    if (lodName != null && rootNode.keyExists(lodName)) {
                        lodNode = rootNode.byKey(lodName)
                        break
                    }
                    ordinal++
                }
                
                // Try lower LOD levels if higher not found
                if (lodNode == null) {
                    var ordinal2 = meshRendering.ordinal - 1
                    while (ordinal2 >= 0) {
                        val lodName2 = values[ordinal2].lodName
                        if (lodName2 != null && rootNode.keyExists(lodName2)) {
                            lodNode = rootNode.byKey(lodName2)
                            break
                        }
                        ordinal2--
                    }
                }
            }
            
            if (lodNode == null) {
                throw IOException("Mesh LOD not found")
            }
            
            // Read mesh face data
            fileInputStream.channel.position(lodNode.byKey("offset").asInt().toLong() + filePosition)
            val inflaterInputStream = InflaterInputStream(dataInputStream)
            val meshDataInputStream = DataInputStream(inflaterInputStream)
            val meshFacesNode = LLSDNode.fromBinary(meshDataInputStream)
            
            val numFaces = meshFacesNode.count
            this.faces = arrayOfNulls<MeshFace>(numFaces)
            for (i in 0 until numFaces) {
                this.faces[i] = MeshFace(meshFacesNode.byIndex(i))
            }
            
            // Read skin/rigging data if present
            if (rootNode.keyExists("skin")) {
                fileInputStream.channel.position(rootNode.byKey("skin").byKey("offset").asInt().toLong() + filePosition)
                val skinInflater = InflaterInputStream(dataInputStream)
                val skinDataInputStream = DataInputStream(skinInflater)
                val skinNode = LLSDNode.fromBinary(skinDataInputStream)
                skinDataInputStream.close()
                skinInflater.close()
                
                // Parse bind shape matrix
                if (skinNode.keyExists("bind_shape_matrix")) {
                    bindShape = FloatArray(16)
                    for (i3 in 0 until 16) {
                        bindShape[i3] = skinNode.byKey("bind_shape_matrix").byIndex(i3).asDouble().toFloat()
                    }
                }
                
                // Parse joint names and create joint map
                if (skinNode.keyExists("joint_names")) {
                    val jointNamesNode = skinNode.byKey("joint_names")
                    val numJoints = Math.min(jointNamesNode.count, MAX_RIGGED_MESH_JOINTS)
                    jointMap = IntArray(numJoints)
                    
                    for (i4 in 0 until numJoints) {
                        val jointName = jointNamesNode.byIndex(i4).asString()
                        var jointIndex = -1
                        
                        // Look up bone ID
                        var boneID = SLSkeletonBoneID.bones[jointName]
                        if (boneID != null) {
                            jointIndex = boneID.ordinal
                        } else {
                            // Check attachment points
                            val attachPoint = SLAttachmentPoint.pointsByName[jointName]
                            if (attachPoint != null) {
                                boneID = attachPoint.bone
                                jointIndex = attachPoint.nonHUDindex + SLSkeletonBoneID.VALUES.size
                            }
                        }
                        
                        // Check for extended bones
                        if (boneID != null && boneID.isExtended) {
                            hasExtendedBones = true
                        }
                        
                        jointMap[i4] = jointIndex
                    }
                }
                
                // Parse inverse bind matrices
                if (skinNode.keyExists("inverse_bind_matrix") && jointMap != null) {
                    val inverseBindNode = skinNode.byKey("inverse_bind_matrix")
                    inverseBindMatrix = FloatArray(jointMap.size * 16)
                    
                    for (i6 in 0 until inverseBindNode.count) {
                        if (i6 < jointMap.size) {
                            val matrixNode = inverseBindNode.byIndex(i6)
                            for (i7 in 0 until 16) {
                                inverseBindMatrix[i6 * 16 + i7] = matrixNode.byIndex(i7).asDouble().toFloat()
                            }
                        }
                    }
                    Debug.Printf("inverseBindMatrix count %d", inverseBindNode.count)
                }
                
                // Parse alternate inverse bind matrix (for joint translations)
                if (skinNode.keyExists("alt_inverse_bind_matrix")) {
                    val altInverseBindNode = skinNode.byKey("alt_inverse_bind_matrix")
                    val altInverseBindMatrix = FloatArray(altInverseBindNode.count * 16)
                    
                    for (i8 in 0 until altInverseBindNode.count) {
                        val matrixNode = altInverseBindNode.byIndex(i8)
                        for (i9 in 0 until 16) {
                            altInverseBindMatrix[i8 * 16 + i9] = matrixNode.byIndex(i9).asDouble().toFloat()
                        }
                    }
                    
                    // Extract joint translations from alt matrices
                    if (jointMap != null) {
                        jointTranslationsMap = EnumMap(SLSkeletonBoneID::class.java)
                        for (i10 in jointMap.indices) {
                            val jointIndex = jointMap[i10]
                            if (jointIndex >= 0 && jointIndex < SLSkeletonBoneID.VALUES.size) {
                                val boneID = SLSkeletonBoneID.VALUES[jointIndex]
                                val translation = FloatArray(3)
                                for (i12 in 0 until 3) {
                                    translation[i12] = altInverseBindMatrix[i10 * 16 + 12 + i12]
                                }
                                jointTranslationsMap[boneID] = translation
                            }
                        }
                    }
                    Debug.Printf("alt_inverse_bind_matrix count %d", altInverseBindNode.count)
                }
                
                // Parse pelvis offset
                if (skinNode.keyExists("pelvis_offset")) {
                    pelvis = skinNode.byKey("pelvis_offset").asDouble().toFloat()
                    Debug.Printf("Pelvis offset: %f", pelvis)
                }
            }
            
            dataInputStream.close()
            fileInputStream.close()
            
        } catch (e: LLSDException) {
            throw IOException(e.message, e)
        } finally {
            dataInputStream?.close()
            fileInputStream?.close()
        }
        
        // Initialize rigging data if we have it
        if (jointMap != null && bindShape != null && inverseBindMatrix != null) {
            this.riggingData = MeshRiggingData.create(jointMap, inverseBindMatrix, hasExtendedBones)
            this.bindShapeMatrix = bindShape
            this.jointTranslations = if (jointTranslationsMap != null) {
                Maps.immutableEnumMap(jointTranslationsMap)
            } else null
        } else {
            this.riggingData = null
            this.bindShapeMatrix = null
            this.jointTranslations = null
        }
        
        this.pelvisOffset = pelvis
    }

    private fun makeInfluenceBuffers(): MeshWeightsBuffer {
        var totalVertices = 0
        for (face in faces) {
            if (face != null) {
                totalVertices += face.numVertices
            }
        }
        
        val weightsBuffer = MeshWeightsBuffer(totalVertices)
        var vertexOffset = 0
        for (face in faces) {
            if (face != null) {
                face.PrepareInfluenceBuffer(weightsBuffer, vertexOffset)
                vertexOffset += face.numVertices
            }
        }
        
        return weightsBuffer
    }

    fun ApplyJointTranslations(meshJointTranslations: MeshJointTranslations) {
        meshJointTranslations.pelvisOffset += this.pelvisOffset
        
        if (this.jointTranslations != null) {
            val targetMap = meshJointTranslations.jointTranslations
            for ((key, value) in this.jointTranslations.entries) {
                targetMap[key] = value
            }
        }
    }

    fun PrepareInfluenceBuffers(renderContext: RenderContext) {
        if (riggingData != null) {
            if (glJointIndexBuffer == null || glWeightsBuffer == null) {
                if (weightsBuffer == null) {
                    weightsBuffer = makeInfluenceBuffers()
                }
                if (glJointIndexBuffer == null) {
                    glJointIndexBuffer = GLLoadableBuffer(weightsBuffer!!.jointIndexBuffer)
                }
                if (glWeightsBuffer == null) {
                    glWeightsBuffer = GLLoadableBuffer(weightsBuffer!!.weightsBuffer)
                }
            }
            riggingData.PrepareInfluenceBuffers(renderContext, bindShapeMatrix)
        }
    }

    fun PrepareInfluencesForFace(renderContext: RenderContext, faceIndex: Int) {
        if (glJointIndexBuffer != null) {
            glJointIndexBuffer!!.Bind20(
                renderContext,
                renderContext.riggedMeshProgram.vJoint,
                4,
                GLES20.GL_UNSIGNED_BYTE,
                4,
                faceIndex * 4
            )
        }
        if (glWeightsBuffer != null) {
            glWeightsBuffer!!.Bind20(
                renderContext,
                renderContext.riggedMeshProgram.vWeight,
                4,
                GLES20.GL_FLOAT,
                16,
                faceIndex * 4 * 4
            )
        }
    }

    fun SetupBuffers30(renderContext: RenderContext) {
        renderContext.bindRiggingMeshData(riggingData)
        GLES20.glUniformMatrix4fv(
            renderContext.currentRiggedMeshProgram.uBindShapeMatrix,
            1,
            false,
            bindShapeMatrix,
            0
        )
    }

    fun SetupFace30(renderContext: RenderContext, faceIndex: Int) {
        if (glJointIndexBuffer == null || glWeightsBuffer == null) {
            if (weightsBuffer == null) {
                weightsBuffer = makeInfluenceBuffers()
            }
            if (glJointIndexBuffer == null) {
                glJointIndexBuffer = GLLoadableBuffer(weightsBuffer!!.jointIndexBuffer)
            }
            if (glWeightsBuffer == null) {
                glWeightsBuffer = GLLoadableBuffer(weightsBuffer!!.weightsBuffer)
            }
        }
        
        glJointIndexBuffer!!.Bind30Integer(
            renderContext,
            renderContext.currentRiggedMeshProgram.vJoint,
            4,
            GLES20.GL_UNSIGNED_BYTE,
            0,
            faceIndex * 4
        )
        glWeightsBuffer!!.Bind20(
            renderContext,
            renderContext.currentRiggedMeshProgram.vWeight,
            4,
            GLES20.GL_FLOAT,
            16,
            faceIndex * 4 * 4
        )
    }

    fun UpdateRigged(faceIndex: Int, targetBuffer: DirectByteBuffer, bufferOffset: Int) {
        if (riggingData != null) {
            riggingData.UpdateRigged(faces[faceIndex], bindShapeMatrix, targetBuffer, bufferOffset)
        }
    }

    fun UpdateRiggedMatrices(avatarSkeleton: AvatarSkeleton) {
        if (riggingData != null) {
            riggingData.UpdateRiggedMatrices(avatarSkeleton)
        }
    }

    fun getFace(faceIndex: Int): MeshFace? {
        return faces[faceIndex]
    }

    val faceCount: Int
        get() = faces.size

    fun hasExtendedBones(): Boolean {
        return riggingData?.hasExtendedBones() ?: false
    }

    fun isRiggedMesh(): Boolean {
        return riggingData != null
    }

    fun riggingFitsGL20(): Boolean {
        return riggingData?.fitsGL20() ?: false
    }
}
