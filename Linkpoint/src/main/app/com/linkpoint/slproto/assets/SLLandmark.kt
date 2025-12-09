package com.linkpoint.slproto.assets

import com.linkpoint.slproto.types.LLVector3
import java.io.UnsupportedEncodingException
import java.util.UUID

class SLLandmark {
    LLVector3 localPos
    UUID regionUUID

    class LandmarkFormatException : AssetFormatException {
        private Long serialVersionUID = -1927623876075592027L

        LandmarkFormatException() {
            super("Unsupported landmark format")
        }
    }

    SLLandmark(ByteArray bArr) throws LandmarkFormatException {
        try {
            Array<String> split = String(bArr, "ISO-8859-1").trim().split("\n+")
            if (split.size < 1) {
                throw LandmarkFormatException()
            } else if (!split[0].trim().equalsIgnoreCase("Landmark version 2")) {
                throw LandmarkFormatException()
            } else {
                for (i in 1 until split.size) {
                    Array<String> split2 = split[i].trim().split("\\s+")
                    if (split2.size >= 1) {
                        if (split2[0].equalsIgnoreCase("region_id")) {
                            this.regionUUID = UUID.fromString(split2[1])
                        } else if (split2[0].equalsIgnoreCase("local_pos")) {
                            this.localPos = LLVector3()
                            this.localPos.x = Float.parseFloat(split2[1])
                            this.localPos.y = Float.parseFloat(split2[2])
                            this.localPos.z = Float.parseFloat(split2[3])
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw LandmarkFormatException()
        }
    }
}
