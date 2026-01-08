package com.lumiyaviewer.lumiya.slproto.assets

import com.lumiyaviewer.lumiya.slproto.types.LLVector3d
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.util.UUID

class SLLandmark {
    var globalPos: LLVector3d = LLVector3d()
    var regionID: UUID = UUIDPool.ZeroUUID

    fun getGlobalPos(): LLVector3d {
        return this.globalPos
    }

    fun getRegionID(): UUID {
        return this.regionID
    }

    fun setGlobalPos(lLVector3d: LLVector3d) {
        this.globalPos = lLVector3d
    }

    fun setRegionID(uUID: UUID) {
        this.regionID = uUID
    }
}
