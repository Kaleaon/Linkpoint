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

class SLMuteList : SLModule() : SLXfer.SLXferCompletionListener {
    private Integer cachedCRC = null
    private val MuteListCachedDataDao muteListCachedDataDao
    private volatile MuteListData muteListData = MuteListData()
    private val RequestHandler<SubscriptionSingleKey> muteListRequestHandler = AsyncRequestHandler(this.agentCircuit, SimpleRequestHandler<SubscriptionSingleKey>() {
        public Unit onRequest(SubscriptionSingleKey subscriptionSingleKey) {
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

    private Unit RequestMuteList() {
        MuteListRequest muteListRequest = MuteListRequest()
        muteListRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
        muteListRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
        muteListRequest.MuteData_Field.MuteCRC = this.cachedCRC != null ? this.cachedCRC.intValue() : 0
        muteListRequest.isReliable = true
        SendMessage(muteListRequest)
        Debug.Printf("MuteList: Requested mute list (CRC %08x)", Integer.valueOf(muteListRequest.MuteData_Field.MuteCRC))
    }

    public Unit Block(MuteListEntry muteListEntry) {
        this.muteListData = this.muteListData.Block(muteListEntry)
        Debug.Printf("MuteList: adding entry %s '%s'", muteListEntry.uuid.toString(), muteListEntry.name)
        UpdateMuteListEntry updateMuteListEntry = UpdateMuteListEntry()
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

    public Unit HandleCircuitReady() {
        super.HandleCircuitReady()
        if (this.muteListCachedDataDao != null) {
            LazyList listLazy = this.muteListCachedDataDao.queryBuilder().listLazy()
            Iterator it = listLazy.iterator()
            if (it.hasNext()) {
                MuteListCachedData muteListCachedData = (MuteListCachedData) it.next()
                this.muteListData = MuteListData(muteListCachedData.getData())
                this.cachedCRC = Integer.valueOf(muteListCachedData.getCRC())
                this.userManager.muteListPool().requestUpdate(SubscriptionSingleKey.Value)
            }
            listLazy.close()
            RequestMuteList()
        }
    }

    public Unit HandleCloseCircuit() {
        if (this.userManager != null) {
            this.userManager.muteListPool().detachRequestHandler(this.muteListRequestHandler)
        }
        super.HandleCloseCircuit()
    }

    @SLMessageHandler
    public Unit HandleMuteListUpdate(MuteListUpdate muteListUpdate) {
        String stringFromVariableOEM = SLMessage.stringFromVariableOEM(muteListUpdate.MuteData_Field.Filename)
        Debug.Printf("MuteList: fileName = '%s'", stringFromVariableOEM)
        if (!stringFromVariableOEM.equals("")) {
            this.agentCircuit.getModules().xferManager.RequestXfer(stringFromVariableOEM, ELLPath.LL_PATH_CACHE, true, this, (Object) null)
        }
    }

    @SLMessageHandler
    public Unit HandleUseCachedMuteList(UseCachedMuteList useCachedMuteList) {
        Debug.Printf("MuteList: Using cached mute list.", Object[0])
    }

    public Unit Unblock(MuteListEntry muteListEntry) {
        this.muteListData = this.muteListData.Unblock(muteListEntry)
        Debug.Printf("MuteList: removing entry %s '%s'", muteListEntry.uuid.toString(), muteListEntry.name)
        RemoveMuteListEntry removeMuteListEntry = RemoveMuteListEntry()
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

    public Boolean isMuted(UUID uuid, MuteType muteType) {
        if (uuid != null) {
            return this.muteListData.isMuted(uuid, muteType)
        }
        return false
    }

    public Boolean isMutedByName(String str) {
        return this.muteListData.isMutedByName(str)
    }

    public Unit onXferComplete(Object obj, String str, Byte[] bArr) {
        if (bArr != null) {
            this.muteListData = MuteListData(bArr)
            if (this.muteListCachedDataDao != null) {
                CRC32 crc32 = CRC32()
                crc32.update(bArr)
                Long value = crc32.getValue()
                this.muteListCachedDataDao.deleteAll()
                this.muteListCachedDataDao.insert(MuteListCachedData((Long) null, (Int) value, bArr))
            }
            this.userManager.muteListPool().requestUpdate(SubscriptionSingleKey.Value)
        }
    }
}
