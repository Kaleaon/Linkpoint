// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto;

import android.os.Parcel;
import android.os.Parcelable;
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

// Referenced classes of package com.lumiyaviewer.lumiya.slproto:
//            SLDefaultMessage, SLMessageEventListener

public abstract class SLMessage
    implements Parcelable
{

    public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public SLMessage createFromParcel(Parcel parcel)
        {
            byte abyte0[] = new byte[parcel.readInt()];
            parcel.readByteArray(abyte0);
            parcel = ByteBuffer.wrap(abyte0).order(ByteOrder.nativeOrder());
            SLMessage slmessage = SLMessageFactory.CreateByID(SLMessage.DecodeMessageIDGeneric(parcel));
            if (slmessage != null)
            {
                slmessage.UnpackPayload(parcel);
                return slmessage;
            } else
            {
                return null;
            }
        }

        public volatile Object createFromParcel(Parcel parcel)
        {
            return createFromParcel(parcel);
        }

        public SLMessage[] newArray(int i)
        {
            return null;
        }

        public volatile Object[] newArray(int i)
        {
            return newArray(i);
        }

    };
    private static final byte LL_ACK_FLAG = 16;
    private static final byte LL_RELIABLE_FLAG = 64;
    private static final byte LL_RESENT_FLAG = 32;
    private static final byte LL_ZERO_CODE_FLAG = -128;
    public static final int MAX_MESSAGE_SIZE = 0x10000;
    public static final int MAX_PAYLOAD_SIZE = 1018;
    public static final int MAX_TRANSMIT_SIZE = 1024;
    public boolean isReliable;
    public boolean isResent;
    private SLMessageEventListener listener;
    public int retries;
    public long sentTimeMillis;
    public int seqNum;
    public boolean zeroCoded;

    public SLMessage()
    {
        listener = null;
    }

    public static int DecodeMessageID(ByteBuffer bytebuffer)
    {
        byte byte0 = bytebuffer.get();
        if (byte0 != -1)
        {
            return byte0;
        }
        byte0 = bytebuffer.get();
        if (byte0 != -1)
        {
            return byte0 | 0xff00;
        } else
        {
            return bytebuffer.getShort() | 0xffff0000;
        }
    }

    public static int DecodeMessageIDGeneric(ByteBuffer bytebuffer)
    {
        byte byte0 = bytebuffer.get();
        if (byte0 != -1)
        {
            return byte0;
        }
        byte0 = bytebuffer.get();
        if (byte0 != -1)
        {
            return byte0 | 0xff00;
        } else
        {
            return bytebuffer.get() << 8 & 0xff00 | 0xffff0000 | bytebuffer.get() & 0xff;
        }
    }

    private void PackPayloadLE(ByteBuffer bytebuffer)
    {
        ByteOrder byteorder = bytebuffer.order();
        bytebuffer.order(ByteOrder.LITTLE_ENDIAN);
        PackPayload(bytebuffer);
        bytebuffer.order(byteorder);
    }

    public static SLMessage Unpack(ByteBuffer bytebuffer, ByteBuffer bytebuffer1, List list)
    {
        boolean flag1 = true;
        int k = bytebuffer.limit();
        byte byte1 = bytebuffer.get();
        int l = bytebuffer.getInt();
        byte byte0 = bytebuffer.get();
        if (byte0 != 0)
        {
            bytebuffer.position(byte0 + bytebuffer.position());
        }
        if ((byte1 & 0x10) != 0)
        {
            byte byte2 = bytebuffer.get(bytebuffer.limit() - 1);
            int j = bytebuffer.limit() - 1 - byte2 * 4;
            for (int i = 0; i < byte2; i++)
            {
                list.add(Integer.valueOf(bytebuffer.getInt(j)));
                j += 4;
            }

            bytebuffer.limit(j);
        }
        SLMessage slmessage;
        boolean flag;
        if ((byte1 & 0xffffff80) != 0)
        {
            bytebuffer1.clear();
            bytebuffer1.order(ByteOrder.BIG_ENDIAN);
            ZeroDecode(bytebuffer1, bytebuffer);
            bytebuffer1.flip();
        } else
        {
            bytebuffer1 = bytebuffer;
        }
        slmessage = SLMessageFactory.CreateByID(DecodeMessageID(bytebuffer1));
        list = slmessage;
        if (slmessage == null)
        {
            list = new SLDefaultMessage();
        }
        list.seqNum = l;
        if ((byte1 & 0x40) != 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        list.isReliable = flag;
        if ((byte1 & 0x20) != 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        list.isResent = flag;
        if ((byte1 & 0xffffff80) != 0)
        {
            flag = flag1;
        } else
        {
            flag = false;
        }
        list.zeroCoded = flag;
        try
        {
            list.UnpackPayloadLE(bytebuffer1);
        }
        // Misplaced declaration of an exception variable
        catch (ByteBuffer bytebuffer)
        {
            Debug.Log((new StringBuilder()).append("Message too short: ").append(list.getClass().getSimpleName()).toString());
            return list;
        }
        catch (Exception exception)
        {
            Debug.Log((new StringBuilder()).append("Failed to unpack (").append(list.getClass().getSimpleName()).append("), zeroCoded = ").append(((SLMessage) (list)).zeroCoded).toString());
            Debug.DumpBuffer("decodedPayload", bytebuffer1);
            Debug.DumpBuffer("origPacket w/o acks", bytebuffer);
            bytebuffer.limit(k);
            Debug.DumpBuffer("origPacket", bytebuffer);
            exception.printStackTrace();
            return null;
        }
        return list;
    }

    private void UnpackPayloadLE(ByteBuffer bytebuffer)
    {
        ByteOrder byteorder = bytebuffer.order();
        bytebuffer.order(ByteOrder.LITTLE_ENDIAN);
        UnpackPayload(bytebuffer);
        bytebuffer.order(byteorder);
    }

    private static void ZeroDecode(ByteBuffer bytebuffer, ByteBuffer bytebuffer1)
    {
        bytebuffer.position(DirectByteBuffer.zeroDecode(bytebuffer.array(), bytebuffer.arrayOffset() + bytebuffer.position(), bytebuffer.capacity() - bytebuffer.position(), bytebuffer1.array(), bytebuffer1.arrayOffset() + bytebuffer1.position(), bytebuffer1.remaining()) + bytebuffer.position());
    }

    private static void ZeroEncode(ByteBuffer bytebuffer, ByteBuffer bytebuffer1)
    {
        int i = 0;
        boolean flag = false;
        while (bytebuffer.hasRemaining()) 
        {
            byte byte0 = bytebuffer.get();
            if (byte0 != 0)
            {
                int j = i;
                if (i != 0)
                {
                    bytebuffer1.put((byte)i);
                    j = 0;
                    flag = false;
                }
                bytebuffer1.put(byte0);
                i = j;
            } else
            {
                boolean flag1 = flag;
                if (!flag)
                {
                    bytebuffer1.put(byte0);
                    flag1 = true;
                }
                i++;
                flag = flag1;
            }
        }
        if (i != 0)
        {
            bytebuffer1.put((byte)i);
        }
    }

    public static int flipBytes(int i)
    {
        return (byte)(i >>> 24) & 0xff | ((byte)(i >>> 16) << 8 & 0xff00 | ((byte)(i >>> 8) << 16 & 0xff0000 | (byte)(i >>> 0) << 24 & 0xff000000));
    }

    public static String stringFromVariableOEM(byte abyte0[])
    {
        Object obj;
        try
        {
            abyte0 = new String(abyte0, "ISO-8859-1");
        }
        // Misplaced declaration of an exception variable
        catch (byte abyte0[])
        {
            abyte0 = "";
        }
        obj = abyte0;
        if (abyte0.endsWith("\0"))
        {
            obj = abyte0.substring(0, abyte0.length() - 1);
        }
        return ((String) (obj));
    }

    public static String stringFromVariableUTF(byte abyte0[])
    {
        Object obj;
        try
        {
            abyte0 = new String(abyte0, "UTF-8");
        }
        // Misplaced declaration of an exception variable
        catch (byte abyte0[])
        {
            abyte0 = "";
        }
        obj = abyte0;
        if (abyte0.endsWith("\0"))
        {
            obj = abyte0.substring(0, abyte0.length() - 1);
        }
        return ((String) (obj));
    }

    public static byte[] stringToVariableOEM(String s)
    {
        try
        {
            s = (new StringBuilder()).append(s).append("\0").toString().getBytes("ISO-8859-1");
        }
        // Misplaced declaration of an exception variable
        catch (String s)
        {
            return (new byte[] {
                0
            });
        }
        return s;
    }

    public static byte[] stringToVariableUTF(String s)
    {
        try
        {
            s = (new StringBuilder()).append(s).append("\0").toString().getBytes("UTF-8");
        }
        // Misplaced declaration of an exception variable
        catch (String s)
        {
            return (new byte[] {
                0
            });
        }
        return s;
    }

    public int AppendPendingAcks(ByteBuffer bytebuffer, List list)
    {
        list = list.iterator();
        int i;
        for (i = 0; list.hasNext() && bytebuffer.position() <= 1019; i++)
        {
            bytebuffer.putInt(((Integer)list.next()).intValue());
        }

        if (i != 0)
        {
            bytebuffer.put(0, (byte)(bytebuffer.get(0) | 0x10));
            bytebuffer.put((byte)i);
        }
        return i;
    }

    public abstract int CalcPayloadSize();

    public abstract void Handle(SLMessageHandler slmessagehandler);

    public void Pack(ByteBuffer bytebuffer, ByteBuffer bytebuffer1)
    {
        bytebuffer.clear();
        bytebuffer.order(ByteOrder.BIG_ENDIAN);
        byte byte0;
        byte byte1;
        if (isReliable)
        {
            byte0 = (byte)64;
        } else
        {
            byte0 = 0;
        }
        byte1 = byte0;
        if (isResent)
        {
            byte1 = (byte)(byte0 | 0x20);
        }
        bytebuffer.put(byte1);
        bytebuffer.putInt(seqNum);
        bytebuffer.put((byte)0);
        if (zeroCoded)
        {
            bytebuffer1.clear();
            bytebuffer1.order(ByteOrder.BIG_ENDIAN);
            PackPayloadLE(bytebuffer1);
            bytebuffer1.flip();
            int i = bytebuffer1.limit();
            int j = bytebuffer.position();
            ZeroEncode(bytebuffer1, bytebuffer);
            if (bytebuffer.position() - j < i)
            {
                bytebuffer.put(0, (byte)(bytebuffer.get(0) | 0xffffff80));
                return;
            } else
            {
                bytebuffer.position(j);
                bytebuffer1.rewind();
                bytebuffer.put(bytebuffer1);
                return;
            }
        } else
        {
            PackPayloadLE(bytebuffer);
            return;
        }
    }

    public abstract void PackPayload(ByteBuffer bytebuffer);

    public abstract void UnpackPayload(ByteBuffer bytebuffer);

    public int describeContents()
    {
        return 0;
    }

    public void handleMessageAcknowledged()
    {
        if (listener != null)
        {
            listener.onMessageAcknowledged(this);
        }
    }

    public void handleMessageTimeout()
    {
        if (listener != null)
        {
            listener.onMessageTimeout(this);
        }
    }

    protected void packBoolean(ByteBuffer bytebuffer, boolean flag)
    {
        int i;
        if (flag)
        {
            i = 1;
        } else
        {
            i = 0;
        }
        bytebuffer.put((byte)i);
    }

    protected void packByte(ByteBuffer bytebuffer, byte byte0)
    {
        bytebuffer.put(byte0);
    }

    protected void packDouble(ByteBuffer bytebuffer, double d)
    {
        bytebuffer.putDouble(d);
    }

    protected void packFixed(ByteBuffer bytebuffer, byte abyte0[], int i)
    {
        if (abyte0.length == i)
        {
            bytebuffer.put(abyte0);
        } else
        {
            int j = 0;
            while (j < i) 
            {
                if (j < abyte0.length)
                {
                    bytebuffer.put(abyte0[j]);
                } else
                {
                    bytebuffer.put((byte)0);
                }
                j++;
            }
        }
    }

    protected void packFloat(ByteBuffer bytebuffer, float f)
    {
        bytebuffer.putFloat(f);
    }

    protected void packIPAddress(ByteBuffer bytebuffer, Inet4Address inet4address)
    {
        bytebuffer.put(inet4address.getAddress());
    }

    protected void packInt(ByteBuffer bytebuffer, int i)
    {
        bytebuffer.putInt(i);
    }

    protected void packLLQuaternion(ByteBuffer bytebuffer, LLQuaternion llquaternion)
    {
        bytebuffer.putFloat(llquaternion.x);
        bytebuffer.putFloat(llquaternion.y);
        bytebuffer.putFloat(llquaternion.z);
    }

    protected void packLLVector3(ByteBuffer bytebuffer, LLVector3 llvector3)
    {
        bytebuffer.putFloat(llvector3.x);
        bytebuffer.putFloat(llvector3.y);
        bytebuffer.putFloat(llvector3.z);
    }

    protected void packLLVector3d(ByteBuffer bytebuffer, LLVector3d llvector3d)
    {
        bytebuffer.putDouble(llvector3d.x);
        bytebuffer.putDouble(llvector3d.y);
        bytebuffer.putDouble(llvector3d.z);
    }

    protected void packLLVector4(ByteBuffer bytebuffer, LLVector4 llvector4)
    {
        bytebuffer.putFloat(llvector4.x);
        bytebuffer.putFloat(llvector4.y);
        bytebuffer.putFloat(llvector4.z);
        bytebuffer.putFloat(llvector4.w);
    }

    protected void packLong(ByteBuffer bytebuffer, long l)
    {
        bytebuffer.putLong(l);
    }

    protected void packShort(ByteBuffer bytebuffer, short word0)
    {
        bytebuffer.putShort(word0);
    }

    protected void packUUID(ByteBuffer bytebuffer, UUID uuid)
    {
        ByteOrder byteorder = bytebuffer.order();
        bytebuffer.order(ByteOrder.BIG_ENDIAN);
        bytebuffer.putLong(uuid.getMostSignificantBits());
        bytebuffer.putLong(uuid.getLeastSignificantBits());
        bytebuffer.order(byteorder);
    }

    protected void packVariable(ByteBuffer bytebuffer, byte abyte0[], int i)
    {
        if (i == 1)
        {
            bytebuffer.put((byte)abyte0.length);
        } else
        {
            bytebuffer.put((byte)(abyte0.length & 0xff));
            bytebuffer.put((byte)(abyte0.length >>> 8 & 0xff));
        }
        bytebuffer.put(abyte0);
    }

    public void setEventListener(SLMessageEventListener slmessageeventlistener)
    {
        listener = slmessageeventlistener;
    }

    protected boolean unpackBoolean(ByteBuffer bytebuffer)
    {
        boolean flag = false;
        if (bytebuffer.get() != 0)
        {
            flag = true;
        }
        return flag;
    }

    protected byte unpackByte(ByteBuffer bytebuffer)
    {
        return bytebuffer.get();
    }

    protected double unpackDouble(ByteBuffer bytebuffer)
    {
        return bytebuffer.getDouble();
    }

    protected byte[] unpackFixed(ByteBuffer bytebuffer, int i)
    {
        byte abyte0[] = new byte[i];
        bytebuffer.get(abyte0);
        return abyte0;
    }

    protected float unpackFloat(ByteBuffer bytebuffer)
    {
        return bytebuffer.getFloat();
    }

    protected Inet4Address unpackIPAddress(ByteBuffer bytebuffer)
    {
        byte abyte0[] = new byte[4];
        bytebuffer.get(abyte0);
        try
        {
            bytebuffer = (Inet4Address)Inet4Address.getByAddress(abyte0);
        }
        // Misplaced declaration of an exception variable
        catch (ByteBuffer bytebuffer)
        {
            return null;
        }
        return bytebuffer;
    }

    protected int unpackInt(ByteBuffer bytebuffer)
    {
        return bytebuffer.getInt();
    }

    protected LLQuaternion unpackLLQuaternion(ByteBuffer bytebuffer)
    {
        LLQuaternion llquaternion = new LLQuaternion();
        llquaternion.x = bytebuffer.getFloat();
        llquaternion.y = bytebuffer.getFloat();
        llquaternion.z = bytebuffer.getFloat();
        return llquaternion;
    }

    protected LLVector3 unpackLLVector3(ByteBuffer bytebuffer)
    {
        LLVector3 llvector3 = new LLVector3();
        llvector3.x = bytebuffer.getFloat();
        llvector3.y = bytebuffer.getFloat();
        llvector3.z = bytebuffer.getFloat();
        return llvector3;
    }

    protected LLVector3d unpackLLVector3d(ByteBuffer bytebuffer)
    {
        LLVector3d llvector3d = new LLVector3d();
        llvector3d.x = bytebuffer.getDouble();
        llvector3d.y = bytebuffer.getDouble();
        llvector3d.z = bytebuffer.getDouble();
        return llvector3d;
    }

    protected LLVector4 unpackLLVector4(ByteBuffer bytebuffer)
    {
        LLVector4 llvector4 = new LLVector4();
        llvector4.x = bytebuffer.getFloat();
        llvector4.y = bytebuffer.getFloat();
        llvector4.z = bytebuffer.getFloat();
        llvector4.w = bytebuffer.getFloat();
        return llvector4;
    }

    protected long unpackLong(ByteBuffer bytebuffer)
    {
        return bytebuffer.getLong();
    }

    protected short unpackShort(ByteBuffer bytebuffer)
    {
        return bytebuffer.getShort();
    }

    protected UUID unpackUUID(ByteBuffer bytebuffer)
    {
        ByteOrder byteorder = bytebuffer.order();
        bytebuffer.order(ByteOrder.BIG_ENDIAN);
        long l = bytebuffer.getLong();
        long l1 = bytebuffer.getLong();
        bytebuffer.order(byteorder);
        return new UUID(l, l1);
    }

    protected byte[] unpackVariable(ByteBuffer bytebuffer, int i)
    {
        byte abyte0[];
        if (i == 1)
        {
            i = bytebuffer.get() & 0xff;
        } else
        {
            i = bytebuffer.get() & 0xff | (bytebuffer.get() & 0xff) << 8;
        }
        abyte0 = new byte[i];
        bytebuffer.get(abyte0);
        return abyte0;
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        i = CalcPayloadSize();
        byte abyte0[] = new byte[i];
        PackPayload(ByteBuffer.wrap(abyte0).order(ByteOrder.nativeOrder()));
        parcel.writeInt(i);
        parcel.writeByteArray(abyte0);
    }

}
