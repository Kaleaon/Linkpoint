package com.lumiyaviewer.lumiya.slproto.modules.mutelist;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.MuteListCachedData;
import com.lumiyaviewer.lumiya.dao.MuteListCachedDataDao;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.messages.MuteListRequest;
import com.lumiyaviewer.lumiya.slproto.messages.MuteListUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.RemoveMuteListEntry;
import com.lumiyaviewer.lumiya.slproto.messages.UpdateMuteListEntry;
import com.lumiyaviewer.lumiya.slproto.messages.UseCachedMuteList;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;
import com.lumiyaviewer.lumiya.slproto.modules.xfer.ELLPath;
import com.lumiyaviewer.lumiya.slproto.modules.xfer.SLXfer;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import de.greenrobot.dao.query.LazyList;
import java.util.Iterator;
import java.util.UUID;
import java.util.zip.CRC32;
import javax.annotation.Nonnull;

public class SLMuteList extends SLModule implements SLXfer.SLXferCompletionListener {
    private Integer cachedCRC = null;
    private final MuteListCachedDataDao muteListCachedDataDao;
    private volatile MuteListData muteListData = new MuteListData();
    private final RequestHandler<SubscriptionSingleKey> muteListRequestHandler = new AsyncRequestHandler(this.agentCircuit, new SimpleRequestHandler<SubscriptionSingleKey>() {
        public void onRequest(@Nonnull SubscriptionSingleKey subscriptionSingleKey) {
            if (SLMuteList.this.muteListResultHandler != null) {
                SLMuteList.this.muteListResultHandler.onResultData(SubscriptionSingleKey.Value, SLMuteList.this.getMuteList());
            }
        }
    /* access modifiers changed from: private */
    public final ResultHandler<SubscriptionSingleKey, ImmutableList<MuteListEntry>> muteListResultHandler;
    private final UserManager userManager;

    public SLMuteList(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit);
        this.userManager = UserManager.getUserManager(sLAgentCircuit.getAgentUUID());
        if (this.userManager != null) {
            this.muteListCachedDataDao = this.userManager.getDaoSession().getMuteListCachedDataDao();
            this.muteListResultHandler = this.userManager.muteListPool().attachRequestHandler(this.muteListRequestHandler);
            return;
        }
        this.muteListCachedDataDao = null;
        this.muteListResultHandler = null;
    }

    private void RequestMuteList() {
        MuteListRequest muteListRequest = new MuteListRequest();
        muteListRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        muteListRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        muteListRequest.MuteData_Field.MuteCRC = this.cachedCRC != null ? this.cachedCRC.intValue() : 0;
        muteListRequest.isReliable = true;
        SendMessage(muteListRequest);
        Debug.Printf("MuteList: Requested mute list (CRC %08x)", Integer.valueOf(muteListRequest.MuteData_Field.MuteCRC));
    }

    public void Block(MuteListEntry muteListEntry) {
        this.muteListData = this.muteListData.Block(muteListEntry);
        Debug.Printf("MuteList: adding entry %s '%s'", muteListEntry.uuid.toString(), muteListEntry.name);
        UpdateMuteListEntry updateMuteListEntry = new UpdateMuteListEntry();
        updateMuteListEntry.AgentData_Field.AgentID = this.circuitInfo.agentID;
        updateMuteListEntry.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        updateMuteListEntry.MuteData_Field.MuteID = muteListEntry.uuid;
        updateMuteListEntry.MuteData_Field.MuteName = SLMessage.stringToVariableOEM(muteListEntry.name);
        updateMuteListEntry.MuteData_Field.MuteType = muteListEntry.type.ordinal();
        updateMuteListEntry.MuteData_Field.MuteFlags = muteListEntry.flags;
        updateMuteListEntry.isReliable = true;
        SendMessage(updateMuteListEntry);
        this.userManager.muteListPool().requestUpdate(SubscriptionSingleKey.Value);
    }

    public void HandleCircuitReady() {
        super.HandleCircuitReady();
        if (this.muteListCachedDataDao != null) {
            LazyList listLazy = this.muteListCachedDataDao.queryBuilder().listLazy();
            Iterator it = listLazy.iterator();
            if (it.hasNext()) {
                MuteListCachedData muteListCachedData = (MuteListCachedData) it.next();
                this.muteListData = new MuteListData(muteListCachedData.getData());
                this.cachedCRC = Integer.valueOf(muteListCachedData.getCRC());
                this.userManager.muteListPool().requestUpdate(SubscriptionSingleKey.Value);
            }
            listLazy.close();
            RequestMuteList();
        }
    }

    public void HandleCloseCircuit() {
        if (this.userManager != null) {
            this.userManager.muteListPool().detachRequestHandler(this.muteListRequestHandler);
        }
        super.HandleCloseCircuit();
    }

    @SLMessageHandler
    public void HandleMuteListUpdate(MuteListUpdate muteListUpdate) {
        String stringFromVariableOEM = SLMessage.stringFromVariableOEM(muteListUpdate.MuteData_Field.Filename);
        Debug.Printf("MuteList: fileName = '%s'", stringFromVariableOEM);
        if (!stringFromVariableOEM.equals("")) {
            this.agentCircuit.getModules().xferManager.RequestXfer(stringFromVariableOEM, ELLPath.LL_PATH_CACHE, true, this, (Object) null);
        }
    }

    @SLMessageHandler
    public void HandleUseCachedMuteList(UseCachedMuteList useCachedMuteList) {
        Debug.Printf("MuteList: Using cached mute list.", new Object[0]);
    }

    public void Unblock(MuteListEntry muteListEntry) {
        this.muteListData = this.muteListData.Unblock(muteListEntry);
        Debug.Printf("MuteList: removing entry %s '%s'", muteListEntry.uuid.toString(), muteListEntry.name);
        RemoveMuteListEntry removeMuteListEntry = new RemoveMuteListEntry();
        removeMuteListEntry.AgentData_Field.AgentID = this.circuitInfo.agentID;
        removeMuteListEntry.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        removeMuteListEntry.MuteData_Field.MuteID = muteListEntry.uuid;
        removeMuteListEntry.MuteData_Field.MuteName = SLMessage.stringToVariableOEM(muteListEntry.name);
        removeMuteListEntry.isReliable = true;
        SendMessage(removeMuteListEntry);
        this.userManager.muteListPool().requestUpdate(SubscriptionSingleKey.Value);
    }

    public ImmutableList<MuteListEntry> getMuteList() {
        return this.muteListData.getMuteList();
    }

    public boolean isMuted(UUID uuid, MuteType muteType) {
        if (uuid != null) {
            return this.muteListData.isMuted(uuid, muteType);
        }
        return false;
    }

    public boolean isMutedByName(String str) {
        return this.muteListData.isMutedByName(str);
    }

    public void onXferComplete(Object obj, String str, byte[] bArr) {
        if (bArr != null) {
            this.muteListData = new MuteListData(bArr);
            if (this.muteListCachedDataDao != null) {
                CRC32 crc32 = new CRC32();
                crc32.update(bArr);
                long value = crc32.getValue();
                this.muteListCachedDataDao.deleteAll();
                this.muteListCachedDataDao.insert(new MuteListCachedData((Long) null, (int) value, bArr));
            }
            this.userManager.muteListPool().requestUpdate(SubscriptionSingleKey.Value);
        }
    }
}
