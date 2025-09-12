package com.lumiyaviewer.lumiya.slproto.modules.xfer;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.ConfirmXferPacket;
import com.lumiyaviewer.lumiya.slproto.messages.RequestXfer;
import com.lumiyaviewer.lumiya.slproto.messages.SendXferPacket;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class SLXfer {
    private boolean deleteOnCompletion;
    private int expectedDataLen;
    private int expectedPacketNum;
    private String fileName;
    private ELLPath filePath;
    private boolean hasCompleted = false;
    private long id;
    private List<XferListenerInvocation> listeners = new LinkedList();
    private byte[] receivedData;
    private int receivedDataLen;

    public interface SLXferCompletionListener {
        void onXferComplete(Object obj, String str, byte[] bArr);
    }

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
        this.receivedData = null;
        this.receivedDataLen = 0;
        this.expectedDataLen = 0;
        this.expectedPacketNum = 0;
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
            } else if (sendXferPacket.DataPacket_Field.Data.length >= 4) {
                this.expectedDataLen = (sendXferPacket.DataPacket_Field.Data[0] & UnsignedBytes.MAX_VALUE) | ((sendXferPacket.DataPacket_Field.Data[1] << 8) & 65280) | ((sendXferPacket.DataPacket_Field.Data[2] << 16) & 16711680) | ((sendXferPacket.DataPacket_Field.Data[3] << Ascii.CAN) & -16777216);
                Debug.Printf("XferPacket: expected data len = %d (0x%x)", Integer.valueOf(this.expectedDataLen), Integer.valueOf(this.expectedDataLen));
                this.receivedData = new byte[this.expectedDataLen];
                length = sendXferPacket.DataPacket_Field.Data.length - 4;
            } else {
                return;
            }
            if (this.receivedDataLen + length <= this.expectedDataLen) {
                System.arraycopy(sendXferPacket.DataPacket_Field.Data, i, this.receivedData, this.receivedDataLen, length);
                this.receivedDataLen = length + this.receivedDataLen;
                this.expectedPacketNum++;
                if (z) {
                    this.hasCompleted = true;
                }
            } else {
                return;
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
        requestXfer.XferID_Field.VFileID = new UUID(0, 0);
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
        for (XferListenerInvocation invokeListener : this.listeners) {
            invokeListener.invokeListener(this.fileName, this.receivedData);
        }
    }

    public boolean isCompleted() {
        return this.hasCompleted;
    }
}
