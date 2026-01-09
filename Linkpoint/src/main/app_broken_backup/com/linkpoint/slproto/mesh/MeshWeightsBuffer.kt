package com.linkpoint.slproto.mesh

import com.linkpoint.rawbuffers.DirectByteBuffer

class MeshWeightsBuffer(i: Int) {
    val jointIndexBuffer: DirectByteBuffer = DirectByteBuffer(i * 4)
    val weightsBuffer: DirectByteBuffer = DirectByteBuffer(i * 4 * 4)
}