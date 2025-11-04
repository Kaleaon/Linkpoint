package com.lumiyaviewer.lumiya.slproto.modules.xfer;

import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.ConfirmXferPacket;
import com.lumiyaviewer.lumiya.slproto.messages.RequestXfer;
import com.lumiyaviewer.lumiya.slproto.messages.SendXferPacket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/* loaded from: classes.dex */
public class SLXfer {
    private boolean deleteOnCompletion;
    private String fileName;
    private ELLPath filePath;
    private long id;
    private List<XferListenerInvocation> listeners = new LinkedList();
    private boolean hasCompleted = false;
    private byte[] receivedData = null;
    private int receivedDataLen = 0;
    private int expectedDataLen = 0;
    private int expectedPacketNum = 0;

    /* loaded from: classes.dex */
    public interface SLXferCompletionListener {
        void onXferComplete(Object obj, String str, byte[] bArr);
    }

    /* loaded from: classes.dex */
    private static class XferListenerInvocation {
        private SLXferCompletionListener listener;
        private Object tag;

        public XferListenerInvocation(Object obj, SLXferCompletionListener sLXferCompletionListener) {
            this.tag = obj;
            this.listener = sLXferCompletionListener;
        }

        public void invokeListener(String str, byte[] bArr) {
            this.listener.onXferComplete(this.tag, str, bArr);
        }
    }

    public SLXfer(long j, String str, ELLPath eLLPath, boolean z) {
        this.id = j;
        this.fileName = str;
        this.filePath = eLLPath;
        this.deleteOnCompletion = z;
    }

    public void HandleDataPacket(SLXferManager sLXferManager, SendXferPacket sendXferPacket) {
        int length;
        int i = 4;
        Debug.Printf("XferPacket: packetNum %d (0x%x), dataLen %d", Integer.valueOf(sendXferPacket.XferID_Field.Packet), Integer.valueOf(sendXferPacket.XferID_Field.Packet), Integer.valueOf(sendXferPacket.DataPacket_Field.Data.length));
        int i2 = Integer.MAX_VALUE & sendXferPacket.XferID_Field.Packet;
        boolean z = (sendXferPacket.XferID_Field.Packet & Integer.MIN_VALUE) != 0;
        if (i2 == this.expectedPacketNum) {
            if (i2 != 0) {
                i = 0;
                length = sendXferPacket.DataPacket_Field.Data.length;
            } else {
                if (sendXferPacket.DataPacket_Field.Data.length < 4) {
                    return;
                }
                this.expectedDataLen = (sendXferPacket.DataPacket_Field.Data[0] & UnsignedBytes.MAX_VALUE) | ((sendXferPacket.DataPacket_Field.Data[1] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | ((sendXferPacket.DataPacket_Field.Data[2] << 16) & 16711680) | ((sendXferPacket.DataPacket_Field.Data[3] << Ascii.CAN) & ViewCompat.MEASURED_STATE_MASK);
                Debug.Printf("XferPacket: expected data len = %d (0x%x)", Integer.valueOf(this.expectedDataLen), Integer.valueOf(this.expectedDataLen));
                this.receivedData = new byte[this.expectedDataLen];
                length = sendXferPacket.DataPacket_Field.Data.length - 4;
            }
            if (this.receivedDataLen + length > this.expectedDataLen) {
                return;
            }
            System.arraycopy(sendXferPacket.DataPacket_Field.Data, i, this.receivedData, this.receivedDataLen, length);
            this.receivedDataLen = length + this.receivedDataLen;
            this.expectedPacketNum++;
            if (z) {
                this.hasCompleted = true;
            }
        }
        if (i2 <= this.expectedPacketNum) {
            ConfirmXferPacket confirmXferPacket = new ConfirmXferPacket();
            confirmXferPacket.XferID_Field.ID = this.id;
            confirmXferPacket.XferID_Field.Packet = sendXferPacket.XferID_Field.Packet;
            confirmXferPacket.isReliable = true;
            sLXferManager.SendMessage(confirmXferPacket);
        }
    }

    public void StartTransfer(SLXferManager sLXferManager) {
        RequestXfer requestXfer = new RequestXfer();
        requestXfer.XferID_Field.ID = this.id;
        requestXfer.XferID_Field.Filename = SLMessage.stringToVariableOEM(this.fileName);
        requestXfer.XferID_Field.FilePath = this.filePath.getCode();
        requestXfer.XferID_Field.DeleteOnCompletion = this.deleteOnCompletion;
        requestXfer.XferID_Field.UseBigPackets = false;
        requestXfer.XferID_Field.VFileID = new UUID(0L, 0L);
        requestXfer.XferID_Field.VFileType = -1;
        requestXfer.isReliable = true;
        sLXferManager.SendMessage(requestXfer);
    }

    public void addListener(SLXferCompletionListener sLXferCompletionListener, Object obj) {
        this.listeners.add(new XferListenerInvocation(obj, sLXferCompletionListener));
    }

    public byte[] getData() {
        return this.receivedData;
    }

    public String getFilename() {
        return this.fileName;
    }

    public void invokeListeners() {
        Iterator<T> it = this.listeners.iterator();
        while (it.hasNext()) {
            ((XferListenerInvocation) it.next()).invokeListener(this.fileName, this.receivedData);
        }
    }

    public boolean isCompleted() {
        return this.hasCompleted;
    }
}
