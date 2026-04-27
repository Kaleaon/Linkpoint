// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.xfer;

import java.util.Iterator;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.messages.RequestXfer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.ConfirmXferPacket;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.messages.SendXferPacket;
import java.util.LinkedList;
import java.util.List;

public class SLXfer
{
    private boolean deleteOnCompletion;
    private int expectedDataLen;
    private int expectedPacketNum;
    private String fileName;
    private ELLPath filePath;
    private boolean hasCompleted;
    private long id;
    private List<XferListenerInvocation> listeners;
    private byte[] receivedData;
    private int receivedDataLen;
    
    public SLXfer(final long id, final String fileName, final ELLPath filePath, final boolean deleteOnCompletion) {
        this.listeners = new LinkedList<XferListenerInvocation>();
        this.hasCompleted = false;
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
        this.deleteOnCompletion = deleteOnCompletion;
        this.receivedData = null;
        this.receivedDataLen = 0;
        this.expectedDataLen = 0;
        this.expectedPacketNum = 0;
    }
    
    public void HandleDataPacket(final SLXferManager slXferManager, final SendXferPacket sendXferPacket) {
        int n = 4;
        Debug.Printf("XferPacket: packetNum %d (0x%x), dataLen %d", sendXferPacket.XferID_Field.Packet, sendXferPacket.XferID_Field.Packet, sendXferPacket.DataPacket_Field.Data.length);
        final int n2 = Integer.MAX_VALUE & sendXferPacket.XferID_Field.Packet;
        final boolean b = (sendXferPacket.XferID_Field.Packet & Integer.MIN_VALUE) != 0x0;
        if (n2 == this.expectedPacketNum) {
            int length;
            if (n2 == 0) {
                if (sendXferPacket.DataPacket_Field.Data.length < 4) {
                    return;
                }
                this.expectedDataLen = ((sendXferPacket.DataPacket_Field.Data[0] & 0xFF) | (sendXferPacket.DataPacket_Field.Data[1] << 8 & 0xFF00) | (sendXferPacket.DataPacket_Field.Data[2] << 16 & 0xFF0000) | (sendXferPacket.DataPacket_Field.Data[3] << 24 & 0xFF000000));
                Debug.Printf("XferPacket: expected data len = %d (0x%x)", this.expectedDataLen, this.expectedDataLen);
                this.receivedData = new byte[this.expectedDataLen];
                length = sendXferPacket.DataPacket_Field.Data.length - 4;
            }
            else {
                length = sendXferPacket.DataPacket_Field.Data.length;
                n = 0;
            }
            if (this.receivedDataLen + length > this.expectedDataLen) {
                return;
            }
            System.arraycopy(sendXferPacket.DataPacket_Field.Data, n, this.receivedData, this.receivedDataLen, length);
            this.receivedDataLen += length;
            ++this.expectedPacketNum;
            if (b) {
                this.hasCompleted = true;
            }
        }
        if (n2 <= this.expectedPacketNum) {
            final ConfirmXferPacket confirmXferPacket = new ConfirmXferPacket();
            confirmXferPacket.XferID_Field.ID = this.id;
            confirmXferPacket.XferID_Field.Packet = sendXferPacket.XferID_Field.Packet;
            confirmXferPacket.isReliable = true;
            slXferManager.SendMessage(confirmXferPacket);
        }
    }
    
    public void StartTransfer(final SLXferManager slXferManager) {
        final RequestXfer requestXfer = new RequestXfer();
        requestXfer.XferID_Field.ID = this.id;
        requestXfer.XferID_Field.Filename = SLMessage.stringToVariableOEM(this.fileName);
        requestXfer.XferID_Field.FilePath = this.filePath.getCode();
        requestXfer.XferID_Field.DeleteOnCompletion = this.deleteOnCompletion;
        requestXfer.XferID_Field.UseBigPackets = false;
        requestXfer.XferID_Field.VFileID = new UUID(0L, 0L);
        requestXfer.XferID_Field.VFileType = -1;
        requestXfer.isReliable = true;
        slXferManager.SendMessage(requestXfer);
    }
    
    public void addListener(final SLXferCompletionListener slXferCompletionListener, final Object o) {
        this.listeners.add(new XferListenerInvocation(o, slXferCompletionListener));
    }
    
    public byte[] getData() {
        return this.receivedData;
    }
    
    public String getFilename() {
        return this.fileName;
    }
    
    public void invokeListeners() {
        final Iterator<Object> iterator = this.listeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().invokeListener(this.fileName, this.receivedData);
        }
    }
    
    public boolean isCompleted() {
        return this.hasCompleted;
    }
    
    public interface SLXferCompletionListener
    {
        void onXferComplete(final Object p0, final String p1, final byte[] p2);
    }
    
    private static class XferListenerInvocation
    {
        private SLXferCompletionListener listener;
        private Object tag;
        
        public XferListenerInvocation(final Object tag, final SLXferCompletionListener listener) {
            this.tag = tag;
            this.listener = listener;
        }
        
        public void invokeListener(final String s, final byte[] array) {
            this.listener.onXferComplete(this.tag, s, array);
        }
    }
}
