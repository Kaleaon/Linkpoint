package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public class PrimVolumeParams {
    public static final byte LL_SCULPT_FLAG_INVERT = 64;
    public static final byte LL_SCULPT_FLAG_MIRROR = Byte.MIN_VALUE;
    public static final byte LL_SCULPT_TYPE_CYLINDER = 4;
    public static final byte LL_SCULPT_TYPE_MASK = 7;
    public static final byte LL_SCULPT_TYPE_MESH = 5;
    public static final byte LL_SCULPT_TYPE_NONE = 0;
    public static final byte LL_SCULPT_TYPE_PLANE = 3;
    public static final byte LL_SCULPT_TYPE_SPHERE = 1;
    public static final byte LL_SCULPT_TYPE_TORUS = 2;
    public static final short PARAMS_FLEXIBLE = 16;
    public static final short PARAMS_LIGHT = 32;
    public static final short PARAMS_LIGHT_IMAGE = 64;
    public static final short PARAMS_MESH = 96;
    public static final short PARAMS_RESERVED = 80;
    public static final short PARAMS_SCULPT = 48;
    public PrimFlexibleParams FlexiParams;
    public PrimPathParams PathParams;
    public PrimProfileParams ProfileParams;
    public UUID SculptID;
    public byte SculptType;

    public static PrimVolumeParams createFromObjectUpdate(ObjectUpdate.ObjectData objectData) {
        PrimVolumeParams primVolumeParams = new PrimVolumeParams();
        primVolumeParams.PathParams = PrimParamsPool.get(new PrimPathParams(objectData));
        primVolumeParams.ProfileParams = PrimParamsPool.get(PrimProfileParams.createFromObjectUpdate(objectData));
        return primVolumeParams;
    }

    public static PrimVolumeParams createFromPackedData(ByteBuffer byteBuffer) {
        PrimVolumeParams primVolumeParams = new PrimVolumeParams();
        primVolumeParams.PathParams = PrimParamsPool.get(new PrimPathParams(byteBuffer));
        primVolumeParams.ProfileParams = PrimParamsPool.get(PrimProfileParams.createFromPackedData(byteBuffer));
        return primVolumeParams;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PrimVolumeParams)) {
            return false;
        }
        PrimVolumeParams primVolumeParams = (PrimVolumeParams) obj;
        if (this.SculptType != primVolumeParams.SculptType) {
            return false;
        }
        if ((this.SculptID == null) != (primVolumeParams.SculptID == null)) {
            return false;
        }
        if (this.SculptID != null && !this.SculptID.equals(primVolumeParams.SculptID)) {
            return false;
        }
        if ((this.ProfileParams == null) != (primVolumeParams.ProfileParams == null)) {
            return false;
        }
        if (this.ProfileParams != null && !this.ProfileParams.equals(primVolumeParams.ProfileParams)) {
            return false;
        }
        if ((this.PathParams == null) != (primVolumeParams.PathParams == null)) {
            return false;
        }
        if (this.PathParams != null && !this.PathParams.equals(primVolumeParams.PathParams)) {
            return false;
        }
        if ((this.FlexiParams == null) != (primVolumeParams.FlexiParams == null)) {
            return false;
        }
        return this.FlexiParams == null || this.FlexiParams.equals(primVolumeParams.FlexiParams);
    }

    public int hashCode() {
        int i = (this.SculptType * 17) + 0;
        if (this.SculptID != null) {
            i += this.SculptID.hashCode() * 3;
        }
        int hashCode = i + (this.PathParams.hashCode() * 37) + this.ProfileParams.hashCode();
        return this.FlexiParams != null ? hashCode + this.FlexiParams.hashCode() : hashCode;
    }

    public boolean isFlexible() {
        return this.FlexiParams != null;
    }

    public boolean isMesh() {
        return this.SculptID != null && (this.SculptType & 7) == 5;
    }

    public boolean isSculpt() {
        return this.SculptID != null;
    }

    public String toString() {
        return "{Volume: SculptType 0x" + Integer.toHexString(this.SculptType) + ", SculptID " + (this.SculptID != null ? this.SculptID.toString() : "null") + ", Path = (" + this.PathParams.toString() + "), Profile = (" + this.ProfileParams.toString() + ")}";
    }

    public void unpackExtraParams(ByteBuffer byteBuffer) {
        try {
            byte b = byteBuffer.get();
            for (int i = 0; i < b; i++) {
                short s = byteBuffer.getShort();
                int i2 = byteBuffer.getInt() + byteBuffer.position();
                switch (s) {
                    case 16:
                        this.FlexiParams = new PrimFlexibleParams(byteBuffer, i2);
                        break;
                    case 48:
                    case 96:
                        byteBuffer.order(ByteOrder.BIG_ENDIAN);
                        this.SculptID = UUIDPool.getUUID(new UUID(byteBuffer.getLong(), byteBuffer.getLong()));
                        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                        this.SculptType = byteBuffer.get();
                        break;
                }
                byteBuffer.position(i2);
            }
        } catch (BufferUnderflowException e) {
            Debug.Warning(e);
        }
    }
}
