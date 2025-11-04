// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.mutelist;

import de.greenrobot.dao.AbstractDao;
import java.util.zip.CRC32;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.messages.RemoveMuteListEntry;
import com.lumiyaviewer.lumiya.slproto.messages.UseCachedMuteList;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.modules.xfer.ELLPath;
import com.lumiyaviewer.lumiya.slproto.messages.MuteListUpdate;
import java.util.Iterator;
import de.greenrobot.dao.query.LazyList;
import com.lumiyaviewer.lumiya.dao.MuteListCachedData;
import com.lumiyaviewer.lumiya.slproto.messages.UpdateMuteListEntry;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.MuteListRequest;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.dao.MuteListCachedDataDao;
import com.lumiyaviewer.lumiya.slproto.modules.xfer.SLXfer;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;

public class SLMuteList extends SLModule implements SLXferCompletionListener
{
    private Integer cachedCRC;
    private final MuteListCachedDataDao muteListCachedDataDao;
    private volatile MuteListData muteListData;
    private final RequestHandler<SubscriptionSingleKey> muteListRequestHandler;
    private final ResultHandler<SubscriptionSingleKey, ImmutableList<MuteListEntry>> muteListResultHandler;
    private final UserManager userManager;
    
    public SLMuteList(final SLAgentCircuit slAgentCircuit) {
        super(slAgentCircuit);
        this.muteListData = new MuteListData();
        this.cachedCRC = null;
        this.muteListRequestHandler = new AsyncRequestHandler<SubscriptionSingleKey>(this.agentCircuit, new SimpleRequestHandler<SubscriptionSingleKey>() {
            @Override
            public void onRequest(@Nonnull final SubscriptionSingleKey subscriptionSingleKey) {
                if (SLMuteList.this.muteListResultHandler != null) {
                    SLMuteList.this.muteListResultHandler.onResultData(SubscriptionSingleKey.Value, SLMuteList.this.getMuteList());
                }
            }
        });
        this.userManager = UserManager.getUserManager(slAgentCircuit.getAgentUUID());
        if (this.userManager != null) {
            this.muteListCachedDataDao = this.userManager.getDaoSession().getMuteListCachedDataDao();
            this.muteListResultHandler = this.userManager.muteListPool().attachRequestHandler(this.muteListRequestHandler);
        }
        else {
            this.muteListCachedDataDao = null;
            this.muteListResultHandler = null;
        }
    }
    
    private void RequestMuteList() {
        final MuteListRequest muteListRequest = new MuteListRequest();
        muteListRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        muteListRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        final MuteListRequest.MuteData muteData_Field = muteListRequest.MuteData_Field;
        int intValue;
        if (this.cachedCRC != null) {
            intValue = this.cachedCRC;
        }
        else {
            intValue = 0;
        }
        muteData_Field.MuteCRC = intValue;
        muteListRequest.isReliable = true;
        this.SendMessage(muteListRequest);
        Debug.Printf("MuteList: Requested mute list (CRC %08x)", muteListRequest.MuteData_Field.MuteCRC);
    }
    
    public void Block(final MuteListEntry muteListEntry) {
        this.muteListData = this.muteListData.Block(muteListEntry);
        Debug.Printf("MuteList: adding entry %s '%s'", muteListEntry.uuid.toString(), muteListEntry.name);
        final UpdateMuteListEntry updateMuteListEntry = new UpdateMuteListEntry();
        updateMuteListEntry.AgentData_Field.AgentID = this.circuitInfo.agentID;
        updateMuteListEntry.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        updateMuteListEntry.MuteData_Field.MuteID = muteListEntry.uuid;
        updateMuteListEntry.MuteData_Field.MuteName = SLMessage.stringToVariableOEM(muteListEntry.name);
        updateMuteListEntry.MuteData_Field.MuteType = muteListEntry.type.ordinal();
        updateMuteListEntry.MuteData_Field.MuteFlags = muteListEntry.flags;
        updateMuteListEntry.isReliable = true;
        this.SendMessage(updateMuteListEntry);
        this.userManager.muteListPool().requestUpdate(SubscriptionSingleKey.Value);
    }
    
    @Override
    public void HandleCircuitReady() {
        super.HandleCircuitReady();
        if (this.muteListCachedDataDao != null) {
            final LazyList<MuteListCachedData> listLazy = ((AbstractDao<MuteListCachedData, K>)this.muteListCachedDataDao).queryBuilder().listLazy();
            final Iterator<Object> iterator = listLazy.iterator();
            if (iterator.hasNext()) {
                final MuteListCachedData muteListCachedData = iterator.next();
                this.muteListData = new MuteListData(muteListCachedData.getData());
                this.cachedCRC = muteListCachedData.getCRC();
                this.userManager.muteListPool().requestUpdate(SubscriptionSingleKey.Value);
            }
            listLazy.close();
            this.RequestMuteList();
        }
    }
    
    @Override
    public void HandleCloseCircuit() {
        if (this.userManager != null) {
            this.userManager.muteListPool().detachRequestHandler(this.muteListRequestHandler);
        }
        super.HandleCloseCircuit();
    }
    
    @SLMessageHandler
    public void HandleMuteListUpdate(final MuteListUpdate muteListUpdate) {
        final String stringFromVariableOEM = SLMessage.stringFromVariableOEM(muteListUpdate.MuteData_Field.Filename);
        Debug.Printf("MuteList: fileName = '%s'", stringFromVariableOEM);
        if (!stringFromVariableOEM.equals("")) {
            this.agentCircuit.getModules().xferManager.RequestXfer(stringFromVariableOEM, ELLPath.LL_PATH_CACHE, true, this, null);
        }
    }
    
    @SLMessageHandler
    public void HandleUseCachedMuteList(final UseCachedMuteList list) {
        Debug.Printf("MuteList: Using cached mute list.", new Object[0]);
    }
    
    public void Unblock(final MuteListEntry muteListEntry) {
        this.muteListData = this.muteListData.Unblock(muteListEntry);
        Debug.Printf("MuteList: removing entry %s '%s'", muteListEntry.uuid.toString(), muteListEntry.name);
        final RemoveMuteListEntry removeMuteListEntry = new RemoveMuteListEntry();
        removeMuteListEntry.AgentData_Field.AgentID = this.circuitInfo.agentID;
        removeMuteListEntry.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        removeMuteListEntry.MuteData_Field.MuteID = muteListEntry.uuid;
        removeMuteListEntry.MuteData_Field.MuteName = SLMessage.stringToVariableOEM(muteListEntry.name);
        removeMuteListEntry.isReliable = true;
        this.SendMessage(removeMuteListEntry);
        this.userManager.muteListPool().requestUpdate(SubscriptionSingleKey.Value);
    }
    
    public ImmutableList<MuteListEntry> getMuteList() {
        return this.muteListData.getMuteList();
    }
    
    public boolean isMuted(final UUID uuid, final MuteType muteType) {
        return uuid != null && this.muteListData.isMuted(uuid, muteType);
    }
    
    public boolean isMutedByName(final String s) {
        return this.muteListData.isMutedByName(s);
    }
    
    @Override
    public void onXferComplete(final Object o, final String s, final byte[] b) {
        if (b != null) {
            this.muteListData = new MuteListData(b);
            if (this.muteListCachedDataDao != null) {
                final CRC32 crc32 = new CRC32();
                crc32.update(b);
                final long value = crc32.getValue();
                this.muteListCachedDataDao.deleteAll();
                ((AbstractDao<MuteListCachedData, K>)this.muteListCachedDataDao).insert(new MuteListCachedData(null, (int)value, b));
            }
            this.userManager.muteListPool().requestUpdate(SubscriptionSingleKey.Value);
        }
    }
}
