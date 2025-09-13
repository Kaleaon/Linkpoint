package com.lumiyaviewer.lumiya.slproto;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PointerIconCompat;
import android.support.v4.view.ViewCompat;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.messages.SLMessageFactory;
import com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import com.lumiyaviewer.lumiya.slproto.types.LLVector4;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public abstract class SLMessage implements Parcelable {
    public static final Creator<SLMessage> CREATOR = new Creator<SLMessage>() {
        public SLMessage createFromParcel(Parcel parcel) {
            byte[] bArr = new byte[parcel.readInt()];
            parcel.readByteArray(bArr);
            ByteBuffer order = ByteBuffer.wrap(bArr).order(ByteOrder.nativeOrder());
            SLMessage CreateByID = SLMessageFactory.CreateByID(SLMessage.DecodeMessageIDGeneric(order));
            if (CreateByID == null) {
                return null;
            }
            CreateByID.UnpackPayload(order);
            return CreateByID;
        }

        public SLMessage[] newArray(int i) {
            return null;
        }
    };
    private static final byte LL_ACK_FLAG = (byte) 16;
    private static final byte LL_RELIABLE_FLAG = (byte) 64;
    private static final byte LL_RESENT_FLAG = (byte) 32;
    private static final byte LL_ZERO_CODE_FLAG = Byte.MIN_VALUE;
    public static final int MAX_MESSAGE_SIZE = 65536;
    public static final int MAX_PAYLOAD_SIZE = 1018;
    public static final int MAX_TRANSMIT_SIZE = 1024;
    public boolean isReliable;
    public boolean isResent;
    private SLMessageEventListener listener = null;
    public int retries;
    public long sentTimeMillis;
    public int seqNum;
    public boolean zeroCoded;

    public static int DecodeMessageID(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get();
        if (b != (byte) -1) {
            return b;
        }
        b = byteBuffer.get();
        return b != (byte) -1 ? b | MotionEventCompat.ACTION_POINTER_INDEX_MASK : byteBuffer.getShort() | SupportMenu.CATEGORY_MASK;
    }

    public static int DecodeMessageIDGeneric(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get();
        if (b != (byte) -1) {
            return b;
        }
        b = byteBuffer.get();
        if (b != (byte) -1) {
            return b | MotionEventCompat.ACTION_POINTER_INDEX_MASK;
        }
        return (((byteBuffer.get() << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | SupportMenu.CATEGORY_MASK) | (byteBuffer.get() & 255);
    }

    private void PackPayloadLE(ByteBuffer byteBuffer) {
        ByteOrder order = byteBuffer.order();
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        PackPayload(byteBuffer);
        byteBuffer.order(order);
    }

    public static SLMessage Unpack(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, List<Integer> list) {
        boolean z = true;
        int limit = byteBuffer.limit();
        byte b = byteBuffer.get();
        int i = byteBuffer.getInt();
        byte b2 = byteBuffer.get();
        if (b2 != (byte) 0) {
            byteBuffer.position(b2 + byteBuffer.position());
        }
        if ((b & 16) != 0) {
            byte b3 = byteBuffer.get(byteBuffer.limit() - 1);
            int limit2 = (byteBuffer.limit() - 1) - (b3 * 4);
            for (b2 = (byte) 0; b2 < b3; b2++) {
                list.add(Integer.valueOf(byteBuffer.getInt(limit2)));
                limit2 += 4;
            }
            byteBuffer.limit(limit2);
        }
        if ((b & -128) != 0) {
            byteBuffer2.clear();
            byteBuffer2.order(ByteOrder.BIG_ENDIAN);
            ZeroDecode(byteBuffer2, byteBuffer);
            byteBuffer2.flip();
        } else {
            byteBuffer2 = byteBuffer;
        }
        SLMessage CreateByID = SLMessageFactory.CreateByID(DecodeMessageID(byteBuffer2));
        if (CreateByID == null) {
            CreateByID = new SLDefaultMessage();
        }
        CreateByID.seqNum = i;
        CreateByID.isReliable = (b & 64) != 0;
        CreateByID.isResent = (b & 32) != 0;
        if ((b & -128) == 0) {
            z = false;
        }
        CreateByID.zeroCoded = z;
        try {
            CreateByID.UnpackPayloadLE(byteBuffer2);
        } catch (BufferUnderflowException e) {
            Debug.Log("Message too short: " + CreateByID.getClass().getSimpleName());
        } catch (Exception e2) {
            Debug.Log("Failed to unpack (" + CreateByID.getClass().getSimpleName() + "), zeroCoded = " + CreateByID.zeroCoded);
            Debug.DumpBuffer("decodedPayload", byteBuffer2);
            Debug.DumpBuffer("origPacket w/o acks", byteBuffer);
            byteBuffer.limit(limit);
            Debug.DumpBuffer("origPacket", byteBuffer);
            e2.printStackTrace();
            return null;
        }
        return CreateByID;
    }

    private void UnpackPayloadLE(ByteBuffer byteBuffer) {
        ByteOrder order = byteBuffer.order();
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        UnpackPayload(byteBuffer);
        byteBuffer.order(order);
    }

    private static void ZeroDecode(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        byteBuffer.position(DirectByteBuffer.zeroDecode(byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.capacity() - byteBuffer.position(), byteBuffer2.array(), byteBuffer2.arrayOffset() + byteBuffer2.position(), byteBuffer2.remaining()) + byteBuffer.position());
    }

    private static void ZeroEncode(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        int i = 0;
        Object obj = null;
        while (byteBuffer.hasRemaining()) {
            byte b = byteBuffer.get();
            if (b != (byte) 0) {
                if (i != 0) {
                    byteBuffer2.put((byte) i);
                    i = 0;
                    obj = null;
                }
                byteBuffer2.put(b);
            } else {
                if (obj == null) {
                    byteBuffer2.put(b);
                    obj = 1;
                }
                i++;
            }
        }
        if (i != 0) {
            byteBuffer2.put((byte) i);
        }
    }

    public static int flipBytes(int i) {
        return (((byte) (i >>> 24)) & 255) | (((((byte) (i >>> 16)) << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | (((((byte) (i >>> 8)) << 16) & 16711680) | ((((byte) (i >>> 0)) << 24) & ViewCompat.MEASURED_STATE_MASK)));
    }

    public static String stringFromVariableOEM(byte[] bArr) {
        String str;
        try {
            str = new String(bArr, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            str = "";
        }
        return str.endsWith("\u0000") ? str.substring(0, str.length() - 1) : str;
    }

    public static String stringFromVariableUTF(byte[] bArr) {
        String str;
        try {
            str = new String(bArr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            str = "";
        }
        return str.endsWith("\u0000") ? str.substring(0, str.length() - 1) : str;
    }

    public static byte[] stringToVariableOEM(String str) {
        try {
            return (str + "\u0000").getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            return new byte[]{(byte) 0};
        }
    }

    public static byte[] stringToVariableUTF(String str) {
        try {
            return (str + "\u0000").getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return new byte[]{(byte) 0};
        }
    }

    public int AppendPendingAcks(ByteBuffer byteBuffer, List<Integer> list) {
        Iterator it = list.iterator();
        int i = 0;
        while (it.hasNext() && byteBuffer.position() <= PointerIconCompat.TYPE_ZOOM_OUT) {
            byteBuffer.putInt(((Integer) it.next()).intValue());
            i++;
        }
        if (i != 0) {
            byteBuffer.put(0, (byte) (byteBuffer.get(0) | 16));
            byteBuffer.put((byte) i);
        }
        return i;
    }

    public abstract int CalcPayloadSize();

    /* renamed from: Handle */
    public abstract void handleMessage(SLMessageHandler sLMessageHandler);

    public void Pack(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        byteBuffer.clear();
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        byte b = this.isReliable ? (byte) 64 : (byte) 0;
        if (this.isResent) {
            b = (byte) (b | 32);
        }
        byteBuffer.put(b);
        byteBuffer.putInt(this.seqNum);
        byteBuffer.put((byte) 0);
        if (this.zeroCoded) {
            byteBuffer2.clear();
            byteBuffer2.order(ByteOrder.BIG_ENDIAN);
            PackPayloadLE(byteBuffer2);
            byteBuffer2.flip();
            int limit = byteBuffer2.limit();
            int position = byteBuffer.position();
            ZeroEncode(byteBuffer2, byteBuffer);
            if (byteBuffer.position() - position < limit) {
                byteBuffer.put(0, (byte) (byteBuffer.get(0) | -128));
                return;
            }
            byteBuffer.position(position);
            byteBuffer2.rewind();
            byteBuffer.put(byteBuffer2);
            return;
        }
        PackPayloadLE(byteBuffer);
    }

    public abstract void PackPayload(ByteBuffer byteBuffer);

    public abstract void UnpackPayload(ByteBuffer byteBuffer);

    public int describeContents() {
        return 0;
    }

    public void handleMessageAcknowledged() {
        if (this.listener != null) {
            this.listener.onMessageAcknowledged(this);
        }
    }

    public void handleMessageTimeout() {
        if (this.listener != null) {
            this.listener.onMessageTimeout(this);
        }
    }

    protected void packBoolean(ByteBuffer byteBuffer, boolean z) {
        byteBuffer.put((byte) (z ? 1 : 0));
    }

    protected void packByte(ByteBuffer byteBuffer, byte b) {
        byteBuffer.put(b);
    }

    protected void packDouble(ByteBuffer byteBuffer, double d) {
        byteBuffer.putDouble(d);
    }

    protected void packFixed(ByteBuffer byteBuffer, byte[] bArr, int i) {
        if (bArr.length == i) {
            byteBuffer.put(bArr);
            return;
        }
        for (int i2 = 0; i2 < i; i2++) {
            if (i2 < bArr.length) {
                byteBuffer.put(bArr[i2]);
            } else {
                byteBuffer.put((byte) 0);
            }
        }
    }

    protected void packFloat(ByteBuffer byteBuffer, float f) {
        byteBuffer.putFloat(f);
    }

    protected void packIPAddress(ByteBuffer byteBuffer, Inet4Address inet4Address) {
        byteBuffer.put(inet4Address.getAddress());
    }

    protected void packInt(ByteBuffer byteBuffer, int i) {
        byteBuffer.putInt(i);
    }

    protected void packLLQuaternion(ByteBuffer byteBuffer, LLQuaternion lLQuaternion) {
        byteBuffer.putFloat(lLQuaternion.x);
        byteBuffer.putFloat(lLQuaternion.y);
        byteBuffer.putFloat(lLQuaternion.z);
    }

    protected void packLLVector3(ByteBuffer byteBuffer, LLVector3 lLVector3) {
        byteBuffer.putFloat(lLVector3.x);
        byteBuffer.putFloat(lLVector3.y);
        byteBuffer.putFloat(lLVector3.z);
    }

    protected void packLLVector3d(ByteBuffer byteBuffer, LLVector3d lLVector3d) {
        byteBuffer.putDouble(lLVector3d.x);
        byteBuffer.putDouble(lLVector3d.y);
        byteBuffer.putDouble(lLVector3d.z);
    }

    protected void packLLVector4(ByteBuffer byteBuffer, LLVector4 lLVector4) {
        byteBuffer.putFloat(lLVector4.x);
        byteBuffer.putFloat(lLVector4.y);
        byteBuffer.putFloat(lLVector4.z);
        byteBuffer.putFloat(lLVector4.w);
    }

    protected void packLong(ByteBuffer byteBuffer, long j) {
        byteBuffer.putLong(j);
    }

    protected void packShort(ByteBuffer byteBuffer, short s) {
        byteBuffer.putShort(s);
    }

    protected void packUUID(ByteBuffer byteBuffer, UUID uuid) {
        ByteOrder order = byteBuffer.order();
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        byteBuffer.order(order);
    }

    protected void packVariable(ByteBuffer byteBuffer, byte[] bArr, int i) {
        if (i == 1) {
            byteBuffer.put((byte) bArr.length);
        } else {
            byteBuffer.put((byte) (bArr.length & 255));
            byteBuffer.put((byte) ((bArr.length >>> 8) & 255));
        }
        byteBuffer.put(bArr);
    }

    public void setEventListener(SLMessageEventListener sLMessageEventListener) {
        this.listener = sLMessageEventListener;
    }

    protected boolean unpackBoolean(ByteBuffer byteBuffer) {
        return byteBuffer.get() != (byte) 0;
    }

    protected byte unpackByte(ByteBuffer byteBuffer) {
        return byteBuffer.get();
    }

    protected double unpackDouble(ByteBuffer byteBuffer) {
        return byteBuffer.getDouble();
    }

    protected byte[] unpackFixed(ByteBuffer byteBuffer, int i) {
        byte[] bArr = new byte[i];
        byteBuffer.get(bArr);
        return bArr;
    }

    protected float unpackFloat(ByteBuffer byteBuffer) {
        return byteBuffer.getFloat();
    }

    protected Inet4Address unpackIPAddress(ByteBuffer byteBuffer) {
        byte[] bArr = new byte[4];
        byteBuffer.get(bArr);
        try {
            return (Inet4Address) Inet4Address.getByAddress(bArr);
        } catch (UnknownHostException e) {
            return null;
        }
    }

    protected int unpackInt(ByteBuffer byteBuffer) {
        return byteBuffer.getInt();
    }

    protected LLQuaternion unpackLLQuaternion(ByteBuffer byteBuffer) {
        LLQuaternion lLQuaternion = new LLQuaternion();
        lLQuaternion.x = byteBuffer.getFloat();
        lLQuaternion.y = byteBuffer.getFloat();
        lLQuaternion.z = byteBuffer.getFloat();
        return lLQuaternion;
    }

    protected LLVector3 unpackLLVector3(ByteBuffer byteBuffer) {
        LLVector3 lLVector3 = new LLVector3();
        lLVector3.x = byteBuffer.getFloat();
        lLVector3.y = byteBuffer.getFloat();
        lLVector3.z = byteBuffer.getFloat();
        return lLVector3;
    }

    protected LLVector3d unpackLLVector3d(ByteBuffer byteBuffer) {
        LLVector3d lLVector3d = new LLVector3d();
        lLVector3d.x = byteBuffer.getDouble();
        lLVector3d.y = byteBuffer.getDouble();
        lLVector3d.z = byteBuffer.getDouble();
        return lLVector3d;
    }

    protected LLVector4 unpackLLVector4(ByteBuffer byteBuffer) {
        LLVector4 lLVector4 = new LLVector4();
        lLVector4.x = byteBuffer.getFloat();
        lLVector4.y = byteBuffer.getFloat();
        lLVector4.z = byteBuffer.getFloat();
        lLVector4.w = byteBuffer.getFloat();
        return lLVector4;
    }

    protected long unpackLong(ByteBuffer byteBuffer) {
        return byteBuffer.getLong();
    }

    protected short unpackShort(ByteBuffer byteBuffer) {
        return byteBuffer.getShort();
    }

    protected UUID unpackUUID(ByteBuffer byteBuffer) {
        ByteOrder order = byteBuffer.order();
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        long j = byteBuffer.getLong();
        long j2 = byteBuffer.getLong();
        byteBuffer.order(order);
        return new UUID(j, j2);
    }

    protected byte[] unpackVariable(ByteBuffer byteBuffer, int i) {
        byte[] bArr = new byte[(i == 1 ? byteBuffer.get() & 255 : (byteBuffer.get() & 255) | ((byteBuffer.get() & 255) << 8))];
        byteBuffer.get(bArr);
        return bArr;
    }

    public void writeToParcel(Parcel parcel, int i) {
        int CalcPayloadSize = CalcPayloadSize();
        byte[] bArr = new byte[CalcPayloadSize];
        PackPayload(ByteBuffer.wrap(bArr).order(ByteOrder.nativeOrder()));
        parcel.writeInt(CalcPayloadSize);
        parcel.writeByteArray(bArr);
    }
}
