package com.lumiyaviewer.lumiya.slproto.assets;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class SLLandmark {
    public LLVector3 localPos;
    public UUID regionUUID;

    public static class LandmarkFormatException extends AssetFormatException {
        private static final long serialVersionUID = -1927623876075592027L;

        public LandmarkFormatException() {
            super("Unsupported landmark format");
        }
    }

    public SLLandmark(byte[] bArr) throws LandmarkFormatException {
        try {
            String[] split = new String(bArr, "ISO-8859-1").trim().split("\n+");
            if (split.length < 1) {
                throw new LandmarkFormatException();
            } else if (!split[0].trim().equalsIgnoreCase("Landmark version 2")) {
                throw new LandmarkFormatException();
            } else {
                for (int i = 1; i < split.length; i++) {
                    String[] split2 = split[i].trim().split("\\s+");
                    if (split2.length >= 1) {
                        if (split2[0].equalsIgnoreCase("region_id")) {
                            this.regionUUID = UUID.fromString(split2[1]);
                        } else if (split2[0].equalsIgnoreCase("local_pos")) {
                            this.localPos = new LLVector3();
                            this.localPos.x = Float.parseFloat(split2[1]);
                            this.localPos.y = Float.parseFloat(split2[2]);
                            this.localPos.z = Float.parseFloat(split2[3]);
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new LandmarkFormatException();
        }
    }
}
