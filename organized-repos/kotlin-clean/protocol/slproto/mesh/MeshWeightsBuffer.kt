package com.linkpoint.slproto.mesh

import com.linkpoint.rawbuffers.DirectByteBuffer
import javax.annotation.Nonnull

class MeshWeightsBuffer {
    val DirectByteBuffer jointIndexBuffer
    val DirectByteBuffer weightsBuffer

    public MeshWeightsBuffer(Int i) {
        this.jointIndexBuffer = DirectByteBuffer(i * 4)
        this.weightsBuffer = DirectByteBuffer(i * 4 * 4)
    }
}
