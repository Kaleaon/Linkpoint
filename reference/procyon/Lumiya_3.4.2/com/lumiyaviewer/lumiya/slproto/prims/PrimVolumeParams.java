// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.prims;

import java.nio.BufferUnderflowException;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate;
import java.util.UUID;

public class PrimVolumeParams
{
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
    
    public static PrimVolumeParams createFromObjectUpdate(final ObjectUpdate.ObjectData objectData) {
        final PrimVolumeParams primVolumeParams = new PrimVolumeParams();
        primVolumeParams.PathParams = PrimParamsPool.get(new PrimPathParams(objectData));
        primVolumeParams.ProfileParams = PrimParamsPool.get(PrimProfileParams.createFromObjectUpdate(objectData));
        return primVolumeParams;
    }
    
    public static PrimVolumeParams createFromPackedData(final ByteBuffer byteBuffer) {
        final PrimVolumeParams primVolumeParams = new PrimVolumeParams();
        primVolumeParams.PathParams = PrimParamsPool.get(new PrimPathParams(byteBuffer));
        primVolumeParams.ProfileParams = PrimParamsPool.get(PrimProfileParams.createFromPackedData(byteBuffer));
        return primVolumeParams;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PrimVolumeParams)) {
            return false;
        }
        final PrimVolumeParams primVolumeParams = (PrimVolumeParams)o;
        if (this.SculptType != primVolumeParams.SculptType) {
            return false;
        }
        int n;
        if (this.SculptID == null) {
            n = 1;
        }
        else {
            n = 0;
        }
        int n2;
        if (primVolumeParams.SculptID == null) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n != n2) {
            return false;
        }
        if (this.SculptID != null && !this.SculptID.equals(primVolumeParams.SculptID)) {
            return false;
        }
        int n3;
        if (this.ProfileParams == null) {
            n3 = 1;
        }
        else {
            n3 = 0;
        }
        int n4;
        if (primVolumeParams.ProfileParams == null) {
            n4 = 1;
        }
        else {
            n4 = 0;
        }
        if (n3 != n4) {
            return false;
        }
        if (this.ProfileParams != null && !this.ProfileParams.equals(primVolumeParams.ProfileParams)) {
            return false;
        }
        int n5;
        if (this.PathParams == null) {
            n5 = 1;
        }
        else {
            n5 = 0;
        }
        int n6;
        if (primVolumeParams.PathParams == null) {
            n6 = 1;
        }
        else {
            n6 = 0;
        }
        if (n5 != n6) {
            return false;
        }
        if (this.PathParams != null && !this.PathParams.equals(primVolumeParams.PathParams)) {
            return false;
        }
        boolean b;
        if (this.FlexiParams == null) {
            b = true;
        }
        else {
            b = false;
        }
        boolean b2;
        if (primVolumeParams.FlexiParams == null) {
            b2 = true;
        }
        else {
            b2 = false;
        }
        return b == b2 && (this.FlexiParams == null || this.FlexiParams.equals(primVolumeParams.FlexiParams));
    }
    
    @Override
    public int hashCode() {
        int n = this.SculptType * 17 + 0;
        if (this.SculptID != null) {
            n += this.SculptID.hashCode() * 3;
        }
        int n2 = n + this.PathParams.hashCode() * 37 + this.ProfileParams.hashCode();
        if (this.FlexiParams != null) {
            n2 += this.FlexiParams.hashCode();
        }
        return n2;
    }
    
    public boolean isFlexible() {
        return this.FlexiParams != null;
    }
    
    public boolean isMesh() {
        boolean b = false;
        if (this.SculptID != null) {
            b = b;
            if ((this.SculptType & 0x7) == 0x5) {
                b = true;
            }
        }
        return b;
    }
    
    public boolean isSculpt() {
        return this.SculptID != null;
    }
    
    @Override
    public String toString() {
        final StringBuilder append = new StringBuilder().append("{Volume: SculptType 0x").append(Integer.toHexString(this.SculptType)).append(", SculptID ");
        String string;
        if (this.SculptID != null) {
            string = this.SculptID.toString();
        }
        else {
            string = "null";
        }
        return append.append(string).append(", Path = (").append(this.PathParams.toString()).append("), Profile = (").append(this.ProfileParams.toString()).append(")}").toString();
    }
    
    public void unpackExtraParams(final ByteBuffer byteBuffer) {
        while (true) {
            while (true) {
                int n = 0;
                Label_0145: {
                    try {
                        for (byte value = byteBuffer.get(), b = 0; b < value; ++b) {
                            final short short1 = byteBuffer.getShort();
                            n = byteBuffer.getInt() + byteBuffer.position();
                            switch (short1) {
                                case 48:
                                case 96: {
                                    byteBuffer.order(ByteOrder.BIG_ENDIAN);
                                    this.SculptID = UUIDPool.getUUID(new UUID(byteBuffer.getLong(), byteBuffer.getLong()));
                                    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                                    this.SculptType = byteBuffer.get();
                                    break;
                                }
                                case 16: {
                                    break Label_0145;
                                }
                            }
                            byteBuffer.position();
                        }
                    }
                    catch (final BufferUnderflowException ex) {
                        Debug.Warning(ex);
                    }
                    break;
                }
                this.FlexiParams = new PrimFlexibleParams(byteBuffer, n);
                continue;
            }
        }
    }
}
