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

/* loaded from: classes.dex */
public class SLMuteList extends SLModule implements SLXfer.SLXferCompletionListener {
    private Integer cachedCRC;
    private final MuteListCachedDataDao muteListCachedDataDao;
    private volatile MuteListData muteListData;
    private final RequestHandler<SubscriptionSingleKey> muteListRequestHandler;
    private final ResultHandler<SubscriptionSingleKey, ImmutableList<MuteListEntry>> muteListResultHandler;
    private final UserManager userManager;

    public SLMuteList(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit);
        this.muteListData = new MuteListData();
        this.cachedCRC = null;
        this.muteListRequestHandler = new AsyncRequestHandler(this.agentCircuit, new SimpleRequestHandler<SubscriptionSingleKey>() { // from class: com.lumiyaviewer.lumiya.slproto.modules.mutelist.SLMuteList.1
            @Override // com.lumiyaviewer.lumiya.react.RequestHandler
            public void onRequest(@Nonnull SubscriptionSingleKey subscriptionSingleKey) {
                if (SLMuteList.this.muteListResultHandler != null) {
                    SLMuteList.this.muteListResultHandler.onResultData(SubscriptionSingleKey.Value, SLMuteList.this.getMuteList());
                }
            }
        });
        this.userManager = UserManager.getUserManager(sLAgentCircuit.getAgentUUID());
        if (this.userManager != null) {
            this.muteListCachedDataDao = this.userManager.getDaoSession().getMuteListCachedDataDao();
            this.muteListResultHandler = this.userManager.muteListPool().attachRequestHandler(this.muteListRequestHandler);
        } else {
            this.muteListCachedDataDao = null;
            this.muteListResultHandler = null;
        }
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

    @Override // com.lumiyaviewer.lumiya.slproto.modules.SLModule
    public void HandleCircuitReady() {
        super.HandleCircuitReady();
        if (this.muteListCachedDataDao != null) {
            LazyList<MuteListCachedData> listLazy = this.muteListCachedDataDao.queryBuilder().listLazy();
            Iterator<MuteListCachedData> it = listLazy.iterator();
            if (it.hasNext()) {
                MuteListCachedData next = it.next();
                this.muteListData = new MuteListData(next.getData());
                this.cachedCRC = Integer.valueOf(next.getCRC());
                this.userManager.muteListPool().requestUpdate(SubscriptionSingleKey.Value);
            }
            listLazy.close();
            RequestMuteList();
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.modules.SLModule
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
        if (stringFromVariableOEM.equals("")) {
            return;
        }
        this.agentCircuit.getModules().xferManager.RequestXfer(stringFromVariableOEM, ELLPath.LL_PATH_CACHE, true, this, null);
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

    @Override // com.lumiyaviewer.lumiya.slproto.modules.xfer.SLXfer.SLXferCompletionListener
    public void onXferComplete(Object obj, String str, byte[] bArr) {
        if (bArr != null) {
            this.muteListData = new MuteListData(bArr);
            if (this.muteListCachedDataDao != null) {
                CRC32 crc32 = new CRC32();
                crc32.update(bArr);
                long value = crc32.getValue();
                this.muteListCachedDataDao.deleteAll();
                this.muteListCachedDataDao.insert(new MuteListCachedData(null, (int) value, bArr));
            }
            this.userManager.muteListPool().requestUpdate(SubscriptionSingleKey.Value);
        }
    }
}
