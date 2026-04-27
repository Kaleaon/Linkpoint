package com.linkpoint.slproto.modules.xfer

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.Debug
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.messages.ConfirmXferPacket
import com.linkpoint.slproto.messages.RequestXfer
import com.linkpoint.slproto.messages.SendXferPacket
import java.util.LinkedList
import java.util.List
import java.util.UUID

class SLXfer {
    private Boolean deleteOnCompletion
    private Int expectedDataLen
    private Int expectedPacketNum
    private String fileName
    private ELLPath filePath
    private Boolean hasCompleted = false
    private Long id
    private List<XferListenerInvocation> listeners = LinkedList()
    private ByteArray receivedData
    private Int receivedDataLen

    interface SLXferCompletionListener {
        Unit onXferComplete(Object obj, String str, ByteArray bArr)
    }

    @JvmStatic
private class XferListenerInvocation {
        private SLXferCompletionListener listener
        private Object tag

        public XferListenerInvocation(Object obj, SLXferCompletionListener sLXferCompletionListener) {
            this.tag = obj
            this.listener = sLXferCompletionListener
        }

        fun invokeListener(String str, ByteArray bArr) {
            this.listener.onXferComplete(this.tag, str, bArr)
        }
    }

    public SLXfer(Long j, String str, ELLPath eLLPath, Boolean z) {
        this.id = j
        this.fileName = str
        this.filePath = eLLPath
        this.deleteOnCompletion = z
        this.receivedData = null
        this.receivedDataLen = 0
        this.expectedDataLen = 0
        this.expectedPacketNum = 0
    }

    fun HandleDataPacket(SLXferManager sLXferManager, SendXferPacket sendXferPacket) {
        Int length
        Int i = 4
        Debug.Printf("XferPacket: packetNum %d (0x%x), dataLen %d", Integer.valueOf(sendXferPacket.XferID_Field.Packet), Integer.valueOf(sendXferPacket.XferID_Field.Packet), Integer.valueOf(sendXferPacket.DataPacket_Field.Data.length))
        Int i2 = Integer.MAX_VALUE & sendXferPacket.XferID_Field.Packet
        Boolean z = (sendXferPacket.XferID_Field.Packet & Integer.MIN_VALUE) != 0
        if (i2 == this.expectedPacketNum) {
            if (i2 != 0) {
                i = 0
                length = sendXferPacket.DataPacket_Field.Data.length
            } else if (sendXferPacket.DataPacket_Field.Data.length >= 4) {
                this.expectedDataLen = (sendXferPacket.DataPacket_Field.Data[0] & UnsignedBytes.MAX_VALUE) | ((sendXferPacket.DataPacket_Field.Data[1] << 8) & 65280) | ((sendXferPacket.DataPacket_Field.Data[2] << 16) & 16711680) | ((sendXferPacket.DataPacket_Field.Data[3] << Ascii.CAN) & -16777216)
                Debug.Printf("XferPacket: expected data len = %d (0x%x)", Integer.valueOf(this.expectedDataLen), Integer.valueOf(this.expectedDataLen))
                this.receivedData = Byte[this.expectedDataLen]
                length = sendXferPacket.DataPacket_Field.Data.length - 4
            } else {
                return
            }
            if (this.receivedDataLen + length <= this.expectedDataLen) {
                System.arraycopy(sendXferPacket.DataPacket_Field.Data, i, this.receivedData, this.receivedDataLen, length)
                this.receivedDataLen = length + this.receivedDataLen
                this.expectedPacketNum++
                if (z) {
                    this.hasCompleted = true
                }
            } else {
                return
            }
        }
        if (i2 <= this.expectedPacketNum) {
            ConfirmXferPacket confirmXferPacket = ConfirmXferPacket()
            confirmXferPacket.XferID_Field.ID = this.id
            confirmXferPacket.XferID_Field.Packet = sendXferPacket.XferID_Field.Packet
            confirmXferPacket.isReliable = true
            sLXferManager.SendMessage(confirmXferPacket)
        }
    }

    fun StartTransfer(SLXferManager sLXferManager) {
        RequestXfer requestXfer = RequestXfer()
        requestXfer.XferID_Field.ID = this.id
        requestXfer.XferID_Field.Filename = SLMessage.stringToVariableOEM(this.fileName)
        requestXfer.XferID_Field.FilePath = this.filePath.getCode()
        requestXfer.XferID_Field.DeleteOnCompletion = this.deleteOnCompletion
        requestXfer.XferID_Field.UseBigPackets = false
        requestXfer.XferID_Field.VFileID = UUID(0, 0)
        requestXfer.XferID_Field.VFileType = -1
        requestXfer.isReliable = true
        sLXferManager.SendMessage(requestXfer)
    }

    fun addListener(SLXferCompletionListener sLXferCompletionListener, Object obj) {
        this.listeners.add(XferListenerInvocation(obj, sLXferCompletionListener))
    }

    public ByteArray getData() {
        return this.receivedData
    }

    public String getFilename() {
        return this.fileName
    }

    fun invokeListeners() {
        for (XferListenerInvocation invokeListener : this.listeners) {
            invokeListener.invokeListener(this.fileName, this.receivedData)
        }
    }

    public Boolean isCompleted() {
        return this.hasCompleted
    }
}
