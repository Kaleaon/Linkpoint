// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto;

import java.nio.Buffer;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.types.LLVector4;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import java.net.Inet4Address;
import com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler;
import java.util.Iterator;
import java.io.UnsupportedEncodingException;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import java.nio.BufferUnderflowException;
import com.lumiyaviewer.lumiya.Debug;
import java.util.List;
import com.lumiyaviewer.lumiya.slproto.messages.SLMessageFactory;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.os.Parcelable;

public abstract class SLMessage implements Parcelable
{
    public static final Parcelable$Creator<SLMessage> CREATOR;
    private static final byte LL_ACK_FLAG = 16;
    private static final byte LL_RELIABLE_FLAG = 64;
    private static final byte LL_RESENT_FLAG = 32;
    private static final byte LL_ZERO_CODE_FLAG = Byte.MIN_VALUE;
    public static final int MAX_MESSAGE_SIZE = 65536;
    public static final int MAX_PAYLOAD_SIZE = 1018;
    public static final int MAX_TRANSMIT_SIZE = 1024;
    public boolean isReliable;
    public boolean isResent;
    private SLMessageEventListener listener;
    public int retries;
    public long sentTimeMillis;
    public int seqNum;
    public boolean zeroCoded;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<SLMessage>() {
            public SLMessage createFromParcel(final Parcel parcel) {
                final byte[] array = new byte[parcel.readInt()];
                parcel.readByteArray(array);
                final ByteBuffer order = ByteBuffer.wrap(array).order(ByteOrder.nativeOrder());
                final SLMessage createByID = SLMessageFactory.CreateByID(SLMessage.DecodeMessageIDGeneric(order));
                if (createByID != null) {
                    createByID.UnpackPayload(order);
                    return createByID;
                }
                return null;
            }
            
            public SLMessage[] newArray(final int n) {
                return null;
            }
        };
    }
    
    public SLMessage() {
        this.listener = null;
    }
    
    public static int DecodeMessageID(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        if (value != -1) {
            return value;
        }
        final byte value2 = byteBuffer.get();
        if (value2 != -1) {
            return value2 | 0xFF00;
        }
        return byteBuffer.getShort() | 0xFFFF0000;
    }
    
    public static int DecodeMessageIDGeneric(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        if (value != -1) {
            return value;
        }
        final byte value2 = byteBuffer.get();
        if (value2 != -1) {
            return value2 | 0xFF00;
        }
        return (byteBuffer.get() << 8 & 0xFF00) | 0xFFFF0000 | (byteBuffer.get() & 0xFF);
    }
    
    private void PackPayloadLE(final ByteBuffer byteBuffer) {
        final ByteOrder order = byteBuffer.order();
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        this.PackPayload(byteBuffer);
        byteBuffer.order(order);
    }
    
    public static SLMessage Unpack(final ByteBuffer ex, ByteBuffer byteBuffer, List<Integer> createByID) {
        final boolean b = true;
        final int limit = ((Buffer)ex).limit();
        final byte value = ((ByteBuffer)ex).get();
        final int int1 = ((ByteBuffer)ex).getInt();
        final byte value2 = ((ByteBuffer)ex).get();
        if (value2 != 0) {
            ((ByteBuffer)ex).position();
        }
        if ((value & 0x10) != 0x0) {
            final byte value3 = ((ByteBuffer)ex).get(((Buffer)ex).limit() - 1);
            int n = ((Buffer)ex).limit() - 1 - value3 * 4;
            for (byte b2 = 0; b2 < value3; ++b2) {
                ((List<Integer>)createByID).add(Integer.valueOf(((ByteBuffer)ex).getInt(n)));
                n += 4;
            }
            ((ByteBuffer)ex).limit();
        }
        boolean isReliable = false;
        boolean zeroCoded;
        boolean isResent = false;
        Label_0225_Outer:Label_0250_Outer:
        while (true) {
            Label_0362: {
                if ((value & 0xFFFFFF80) == 0x0) {
                    break Label_0362;
                }
                byteBuffer.clear();
                byteBuffer.order(ByteOrder.BIG_ENDIAN);
                ZeroDecode(byteBuffer, (ByteBuffer)ex);
                byteBuffer.flip();
                if ((createByID = SLMessageFactory.CreateByID(DecodeMessageID(byteBuffer))) == null) {
                    createByID = new SLDefaultMessage();
                }
                createByID.seqNum = int1;
                while (true) {
                Label_0244_Outer:
                    while (true) {
                        Label_0208: {
                            while (true) {
                                Label_0191: {
                                    if ((value & 0x40) != 0x0) {
                                        isReliable = true;
                                        break Label_0191;
                                    }
                                    Label_0238: {
                                        break Label_0238;
                                        while (true) {
                                            createByID.zeroCoded = zeroCoded;
                                            try {
                                                createByID.UnpackPayloadLE(byteBuffer);
                                                return createByID;
                                                isReliable = false;
                                                break;
                                                isResent = false;
                                                break Label_0208;
                                                zeroCoded = false;
                                                continue Label_0225_Outer;
                                            }
                                            catch (final Exception ex2) {
                                                Debug.Log("Failed to unpack (" + ((List<Integer>)createByID).getClass().getSimpleName() + "), zeroCoded = " + createByID.zeroCoded);
                                                Debug.DumpBuffer("decodedPayload", byteBuffer);
                                                Debug.DumpBuffer("origPacket w/o acks", (ByteBuffer)ex);
                                                ((ByteBuffer)ex).limit();
                                                Debug.DumpBuffer("origPacket", (ByteBuffer)ex);
                                                ex2.printStackTrace();
                                                return null;
                                            }
                                            catch (final BufferUnderflowException ex) {
                                                Debug.Log("Message too short: " + ((List<Integer>)createByID).getClass().getSimpleName());
                                                return createByID;
                                            }
                                            break Label_0362;
                                        }
                                    }
                                }
                                createByID.isReliable = isReliable;
                                if ((value & 0x20) == 0x0) {
                                    continue Label_0250_Outer;
                                }
                                break;
                            }
                            isResent = true;
                        }
                        createByID.isResent = isResent;
                        if ((value & 0xFFFFFF80) != 0x0) {
                            zeroCoded = b;
                            continue Label_0244_Outer;
                        }
                        break;
                    }
                    continue;
                }
            }
            byteBuffer = (ByteBuffer)ex;
            continue;
        }
    }
    
    private void UnpackPayloadLE(final ByteBuffer byteBuffer) {
        final ByteOrder order = byteBuffer.order();
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        this.UnpackPayload(byteBuffer);
        byteBuffer.order(order);
    }
    
    private static void ZeroDecode(final ByteBuffer byteBuffer, final ByteBuffer byteBuffer2) {
        byteBuffer.position();
    }
    
    private static void ZeroEncode(final ByteBuffer byteBuffer, final ByteBuffer byteBuffer2) {
        int n = 0;
        int n2 = 0;
        while (byteBuffer.hasRemaining()) {
            final byte value = byteBuffer.get();
            if (value != 0) {
                int n3;
                if ((n3 = n) != 0) {
                    byteBuffer2.put((byte)n);
                    n3 = 0;
                    n2 = 0;
                }
                byteBuffer2.put(value);
                n = n3;
            }
            else {
                int n4;
                if ((n4 = n2) == 0) {
                    byteBuffer2.put(value);
                    n4 = 1;
                }
                ++n;
                n2 = n4;
            }
        }
        if (n != 0) {
            byteBuffer2.put((byte)n);
        }
    }
    
    public static int flipBytes(final int n) {
        return ((byte)(n >>> 24) & 0xFF) | (((byte)(n >>> 16) << 8 & 0xFF00) | (((byte)(n >>> 8) << 16 & 0xFF0000) | ((byte)(n >>> 0) << 24 & 0xFF000000)));
    }
    
    public static String stringFromVariableOEM(final byte[] bytes) {
        while (true) {
            try {
                final String s = new String(bytes, "ISO-8859-1");
                String substring = s;
                if (s.endsWith("\u0000")) {
                    substring = s.substring(0, s.length() - 1);
                }
                return substring;
            }
            catch (final UnsupportedEncodingException ex) {
                final String s = "";
                continue;
            }
            break;
        }
    }
    
    public static String stringFromVariableUTF(final byte[] bytes) {
        while (true) {
            try {
                final String s = new String(bytes, "UTF-8");
                String substring = s;
                if (s.endsWith("\u0000")) {
                    substring = s.substring(0, s.length() - 1);
                }
                return substring;
            }
            catch (final UnsupportedEncodingException ex) {
                final String s = "";
                continue;
            }
            break;
        }
    }
    
    public static byte[] stringToVariableOEM(final String str) {
        try {
            return (str + "\u0000").getBytes("ISO-8859-1");
        }
        catch (final UnsupportedEncodingException ex) {
            return new byte[] { 0 };
        }
    }
    
    public static byte[] stringToVariableUTF(final String str) {
        try {
            return (str + "\u0000").getBytes("UTF-8");
        }
        catch (final UnsupportedEncodingException ex) {
            return new byte[] { 0 };
        }
    }
    
    public int AppendPendingAcks(final ByteBuffer byteBuffer, final List<Integer> list) {
        final Iterator<Integer> iterator = list.iterator();
        int n = 0;
        while (iterator.hasNext() && byteBuffer.position() <= 1019) {
            byteBuffer.putInt(iterator.next());
            ++n;
        }
        if (n != 0) {
            byteBuffer.put(0, (byte)(byteBuffer.get(0) | 0x10));
            byteBuffer.put((byte)n);
        }
        return n;
    }
    
    public abstract int CalcPayloadSize();
    
    public abstract void Handle(final SLMessageHandler p0);
    
    public void Pack(final ByteBuffer byteBuffer, final ByteBuffer src) {
        byteBuffer.clear();
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        byte b;
        if (this.isReliable) {
            b = 64;
        }
        else {
            b = 0;
        }
        byte b2 = b;
        if (this.isResent) {
            b2 = (byte)(b | 0x20);
        }
        byteBuffer.put(b2);
        byteBuffer.putInt(this.seqNum);
        byteBuffer.put((byte)0);
        if (this.zeroCoded) {
            src.clear();
            src.order(ByteOrder.BIG_ENDIAN);
            this.PackPayloadLE(src);
            src.flip();
            final int limit = src.limit();
            final int position = byteBuffer.position();
            ZeroEncode(src, byteBuffer);
            if (byteBuffer.position() - position < limit) {
                byteBuffer.put(0, (byte)(byteBuffer.get(0) | 0xFFFFFF80));
            }
            else {
                byteBuffer.position();
                src.rewind();
                byteBuffer.put(src);
            }
        }
        else {
            this.PackPayloadLE(byteBuffer);
        }
    }
    
    public abstract void PackPayload(final ByteBuffer p0);
    
    public abstract void UnpackPayload(final ByteBuffer p0);
    
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
    
    protected void packBoolean(final ByteBuffer byteBuffer, final boolean b) {
        boolean b2;
        if (b) {
            b2 = true;
        }
        else {
            b2 = false;
        }
        byteBuffer.put((byte)(b2 ? 1 : 0));
    }
    
    protected void packByte(final ByteBuffer byteBuffer, final byte b) {
        byteBuffer.put(b);
    }
    
    protected void packDouble(final ByteBuffer byteBuffer, final double n) {
        byteBuffer.putDouble(n);
    }
    
    protected void packFixed(final ByteBuffer byteBuffer, final byte[] src, final int n) {
        if (src.length == n) {
            byteBuffer.put(src);
        }
        else {
            for (int i = 0; i < n; ++i) {
                if (i < src.length) {
                    byteBuffer.put(src[i]);
                }
                else {
                    byteBuffer.put((byte)0);
                }
            }
        }
    }
    
    protected void packFloat(final ByteBuffer byteBuffer, final float n) {
        byteBuffer.putFloat(n);
    }
    
    protected void packIPAddress(final ByteBuffer byteBuffer, final Inet4Address inet4Address) {
        byteBuffer.put(inet4Address.getAddress());
    }
    
    protected void packInt(final ByteBuffer byteBuffer, final int n) {
        byteBuffer.putInt(n);
    }
    
    protected void packLLQuaternion(final ByteBuffer byteBuffer, final LLQuaternion llQuaternion) {
        byteBuffer.putFloat(llQuaternion.x);
        byteBuffer.putFloat(llQuaternion.y);
        byteBuffer.putFloat(llQuaternion.z);
    }
    
    protected void packLLVector3(final ByteBuffer byteBuffer, final LLVector3 llVector3) {
        byteBuffer.putFloat(llVector3.x);
        byteBuffer.putFloat(llVector3.y);
        byteBuffer.putFloat(llVector3.z);
    }
    
    protected void packLLVector3d(final ByteBuffer byteBuffer, final LLVector3d llVector3d) {
        byteBuffer.putDouble(llVector3d.x);
        byteBuffer.putDouble(llVector3d.y);
        byteBuffer.putDouble(llVector3d.z);
    }
    
    protected void packLLVector4(final ByteBuffer byteBuffer, final LLVector4 llVector4) {
        byteBuffer.putFloat(llVector4.x);
        byteBuffer.putFloat(llVector4.y);
        byteBuffer.putFloat(llVector4.z);
        byteBuffer.putFloat(llVector4.w);
    }
    
    protected void packLong(final ByteBuffer byteBuffer, final long n) {
        byteBuffer.putLong(n);
    }
    
    protected void packShort(final ByteBuffer byteBuffer, final short n) {
        byteBuffer.putShort(n);
    }
    
    protected void packUUID(final ByteBuffer byteBuffer, final UUID uuid) {
        final ByteOrder order = byteBuffer.order();
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        byteBuffer.order(order);
    }
    
    protected void packVariable(final ByteBuffer byteBuffer, final byte[] src, final int n) {
        if (n == 1) {
            byteBuffer.put((byte)src.length);
        }
        else {
            byteBuffer.put((byte)(src.length & 0xFF));
            byteBuffer.put((byte)(src.length >>> 8 & 0xFF));
        }
        byteBuffer.put(src);
    }
    
    public void setEventListener(final SLMessageEventListener listener) {
        this.listener = listener;
    }
    
    protected boolean unpackBoolean(final ByteBuffer byteBuffer) {
        boolean b = false;
        if (byteBuffer.get() != 0) {
            b = true;
        }
        return b;
    }
    
    protected byte unpackByte(final ByteBuffer byteBuffer) {
        return byteBuffer.get();
    }
    
    protected double unpackDouble(final ByteBuffer byteBuffer) {
        return byteBuffer.getDouble();
    }
    
    protected byte[] unpackFixed(final ByteBuffer byteBuffer, final int n) {
        final byte[] dst = new byte[n];
        byteBuffer.get(dst);
        return dst;
    }
    
    protected float unpackFloat(final ByteBuffer byteBuffer) {
        return byteBuffer.getFloat();
    }
    
    protected Inet4Address unpackIPAddress(final ByteBuffer byteBuffer) {
        final byte[] array = new byte[4];
        byteBuffer.get(array);
        try {
            return (Inet4Address)InetAddress.getByAddress(array);
        }
        catch (final UnknownHostException ex) {
            return null;
        }
    }
    
    protected int unpackInt(final ByteBuffer byteBuffer) {
        return byteBuffer.getInt();
    }
    
    protected LLQuaternion unpackLLQuaternion(final ByteBuffer byteBuffer) {
        final LLQuaternion llQuaternion = new LLQuaternion();
        llQuaternion.x = byteBuffer.getFloat();
        llQuaternion.y = byteBuffer.getFloat();
        llQuaternion.z = byteBuffer.getFloat();
        return llQuaternion;
    }
    
    protected LLVector3 unpackLLVector3(final ByteBuffer byteBuffer) {
        final LLVector3 llVector3 = new LLVector3();
        llVector3.x = byteBuffer.getFloat();
        llVector3.y = byteBuffer.getFloat();
        llVector3.z = byteBuffer.getFloat();
        return llVector3;
    }
    
    protected LLVector3d unpackLLVector3d(final ByteBuffer byteBuffer) {
        final LLVector3d llVector3d = new LLVector3d();
        llVector3d.x = byteBuffer.getDouble();
        llVector3d.y = byteBuffer.getDouble();
        llVector3d.z = byteBuffer.getDouble();
        return llVector3d;
    }
    
    protected LLVector4 unpackLLVector4(final ByteBuffer byteBuffer) {
        final LLVector4 llVector4 = new LLVector4();
        llVector4.x = byteBuffer.getFloat();
        llVector4.y = byteBuffer.getFloat();
        llVector4.z = byteBuffer.getFloat();
        llVector4.w = byteBuffer.getFloat();
        return llVector4;
    }
    
    protected long unpackLong(final ByteBuffer byteBuffer) {
        return byteBuffer.getLong();
    }
    
    protected short unpackShort(final ByteBuffer byteBuffer) {
        return byteBuffer.getShort();
    }
    
    protected UUID unpackUUID(final ByteBuffer byteBuffer) {
        final ByteOrder order = byteBuffer.order();
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        final long long1 = byteBuffer.getLong();
        final long long2 = byteBuffer.getLong();
        byteBuffer.order(order);
        return new UUID(long1, long2);
    }
    
    protected byte[] unpackVariable(final ByteBuffer byteBuffer, int n) {
        if (n == 1) {
            n = (byteBuffer.get() & 0xFF);
        }
        else {
            n = ((byteBuffer.get() & 0xFF) | (byteBuffer.get() & 0xFF) << 8);
        }
        final byte[] dst = new byte[n];
        byteBuffer.get(dst);
        return dst;
    }
    
    public void writeToParcel(final Parcel parcel, int calcPayloadSize) {
        calcPayloadSize = this.CalcPayloadSize();
        final byte[] array = new byte[calcPayloadSize];
        this.PackPayload(ByteBuffer.wrap(array).order(ByteOrder.nativeOrder()));
        parcel.writeInt(calcPayloadSize);
        parcel.writeByteArray(array);
    }
}
