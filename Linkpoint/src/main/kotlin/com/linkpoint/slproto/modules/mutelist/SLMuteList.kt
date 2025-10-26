package com.linkpoint.slproto.modules.mutelist

import com.google.common.collect.ImmutableList
import com.linkpoint.Debug
import com.linkpoint.dao.MuteListCachedData
import com.linkpoint.dao.MuteListCachedDataDao
import com.linkpoint.react.AsyncRequestHandler
import com.linkpoint.react.RequestHandler
import com.linkpoint.react.ResultHandler
import com.linkpoint.react.SimpleRequestHandler
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.handler.SLMessageHandler
import com.linkpoint.slproto.messages.MuteListRequest
import com.linkpoint.slproto.messages.MuteListUpdate
import com.linkpoint.slproto.messages.RemoveMuteListEntry
import com.linkpoint.slproto.messages.UpdateMuteListEntry
import com.linkpoint.slproto.messages.UseCachedMuteList
import com.linkpoint.slproto.modules.SLModule
import com.linkpoint.slproto.modules.xfer.ELLPath
import com.linkpoint.slproto.modules.xfer.SLXfer
import com.linkpoint.slproto.users.manager.UserManager
import de.greenrobot.dao.query.LazyList
import java.util.Iterator
import java.util.UUID
import java.util.zip.CRC32
import javax.annotation.Nonnull

class SLMuteList : SLModule(), SLXfer.SLXferCompletionListener {
    private Integer cachedCRC = null
    private val MuteListCachedDataDao muteListCachedDataDao
    private volatile MuteListData muteListData = MuteListData()
    private val RequestHandler<SubscriptionSingleKey> muteListRequestHandler = AsyncRequestHandler(this.agentCircuit, SimpleRequestHandler<SubscriptionSingleKey>() {
        fun onRequest(subscriptionSingleKey: SubscriptionSingleKey) {
            if (SLMuteList.this.muteListResultHandler != null) {
                SLMuteList.this.muteListResultHandler.onResultData(SubscriptionSingleKey.Value, SLMuteList.this.getMuteList())
            }
        }
    /* access modifiers changed from: private */
    val ResultHandler<SubscriptionSingleKey, ImmutableList<MuteListEntry>> muteListResultHandler
    private val UserManager userManager

    public SLMuteList(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit)
        this.userManager = UserManager.getUserManager(sLAgentCircuit.getAgentUUID())
        if (this.userManager != null) {
            this.muteListCachedDataDao = this.userManager.getDaoSession().getMuteListCachedDataDao()
            this.muteListResultHandler = this.userManager.muteListPool().attachRequestHandler(this.muteListRequestHandler)
            return
        }
        this.muteListCachedDataDao = null
        this.muteListResultHandler = null
    }

    private fun RequestMuteList() {
        val muteListRequest: MuteListRequest = MuteListRequest()
        muteListRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
        muteListRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
        muteListRequest.MuteData_Field.MuteCRC = this.cachedCRC != null ? this.cachedCRC.intValue() : 0
        muteListRequest.isReliable = true
        SendMessage(muteListRequest)
        Debug.Printf("MuteList: Requested mute list (CRC %08x)", Integer.valueOf(muteListRequest.MuteData_Field.MuteCRC))
    }

    fun Block(muteListEntry: MuteListEntry) {
        this.muteListData = this.muteListData.Block(muteListEntry)
        Debug.Printf("MuteList: adding entry %s '%s'", muteListEntry.uuid.toString(), muteListEntry.name)
        val updateMuteListEntry: UpdateMuteListEntry = UpdateMuteListEntry()
        updateMuteListEntry.AgentData_Field.AgentID = this.circuitInfo.agentID
        updateMuteListEntry.AgentData_Field.SessionID = this.circuitInfo.sessionID
        updateMuteListEntry.MuteData_Field.MuteID = muteListEntry.uuid
        updateMuteListEntry.MuteData_Field.MuteName = SLMessage.stringToVariableOEM(muteListEntry.name)
        updateMuteListEntry.MuteData_Field.MuteType = muteListEntry.type.ordinal()
        updateMuteListEntry.MuteData_Field.MuteFlags = muteListEntry.flags
        updateMuteListEntry.isReliable = true
        SendMessage(updateMuteListEntry)
        this.userManager.muteListPool().requestUpdate(SubscriptionSingleKey.Value)
    }

    fun HandleCircuitReady() {
        super.HandleCircuitReady()
        if (this.muteListCachedDataDao != null) {
            val listLazy: LazyList = this.muteListCachedDataDao.queryBuilder().listLazy()
            val it: Iterator = listLazy.iterator()
            if (it.hasNext()) {
                val muteListCachedData: MuteListCachedData = (MuteListCachedData) it.next()
                this.muteListData = MuteListData(muteListCachedData.getData())
                this.cachedCRC = Integer.valueOf(muteListCachedData.getCRC())
                this.userManager.muteListPool().requestUpdate(SubscriptionSingleKey.Value)
            }
            listLazy.close()
            RequestMuteList()
        }
    }

    fun HandleCloseCircuit() {
        if (this.userManager != null) {
            this.userManager.muteListPool().detachRequestHandler(this.muteListRequestHandler)
        }
        super.HandleCloseCircuit()
    }

    @SLMessageHandler
    fun HandleMuteListUpdate(muteListUpdate: MuteListUpdate) {
        val stringFromVariableOEM: String = SLMessage.stringFromVariableOEM(muteListUpdate.MuteData_Field.Filename)
        Debug.Printf("MuteList: fileName = '%s'", stringFromVariableOEM)
        if (!stringFromVariableOEM.equals("")) {
            this.agentCircuit.getModules().xferManager.RequestXfer(stringFromVariableOEM, ELLPath.LL_PATH_CACHE, true, this, (Object) null)
        }
    }

    @SLMessageHandler
    fun HandleUseCachedMuteList(useCachedMuteList: UseCachedMuteList) {
        Debug.Printf("MuteList: Using cached mute list.", Object[0])
    }

    fun Unblock(muteListEntry: MuteListEntry) {
        this.muteListData = this.muteListData.Unblock(muteListEntry)
        Debug.Printf("MuteList: removing entry %s '%s'", muteListEntry.uuid.toString(), muteListEntry.name)
        val removeMuteListEntry: RemoveMuteListEntry = RemoveMuteListEntry()
        removeMuteListEntry.AgentData_Field.AgentID = this.circuitInfo.agentID
        removeMuteListEntry.AgentData_Field.SessionID = this.circuitInfo.sessionID
        removeMuteListEntry.MuteData_Field.MuteID = muteListEntry.uuid
        removeMuteListEntry.MuteData_Field.MuteName = SLMessage.stringToVariableOEM(muteListEntry.name)
        removeMuteListEntry.isReliable = true
        SendMessage(removeMuteListEntry)
        this.userManager.muteListPool().requestUpdate(SubscriptionSingleKey.Value)
    }

    public ImmutableList<MuteListEntry> getMuteList() {
        return this.muteListData.getMuteList()
    }

     public fun isMuted(uuid: UUID, muteType: MuteType): Boolean {
        if (uuid != null) {
            return this.muteListData.isMuted(uuid, muteType)
        }
        return false
    }

     public fun isMutedByName(str: String): Boolean {
        return this.muteListData.isMutedByName(str)
    }

    fun onXferComplete(obj: Object, str: String, bArr: ByteArray) {
        if (bArr != null) {
            this.muteListData = MuteListData(bArr)
            if (this.muteListCachedDataDao != null) {
                val crc32: CRC32 = CRC32()
                crc32.update(bArr)
                val value: Long = crc32.getValue()
                this.muteListCachedDataDao.deleteAll()
                this.muteListCachedDataDao.insert(MuteListCachedData((Long) null, (Int) value, bArr))
            }
            this.userManager.muteListPool().requestUpdate(SubscriptionSingleKey.Value)
        }
    }
}
