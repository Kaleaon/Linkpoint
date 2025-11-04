// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules;

import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.modules.xfer.SLXferManager;
import com.lumiyaviewer.lumiya.slproto.modules.voice.SLVoice;
import com.lumiyaviewer.lumiya.slproto.modules.transfer.SLTransferManager;
import com.lumiyaviewer.lumiya.slproto.modules.texuploader.SLTextureUploader;
import com.lumiyaviewer.lumiya.slproto.modules.texfetcher.SLTextureFetcher;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.SLMuteList;
import java.util.List;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.modules.groups.SLGroupManager;
import com.lumiyaviewer.lumiya.slproto.modules.search.SLSearch;
import com.lumiyaviewer.lumiya.slproto.modules.finance.SLFinancialInfo;
import com.lumiyaviewer.lumiya.slproto.dispnames.SLDisplayNameFetcher;
import javax.annotation.Nonnull;

public class SLModules
{
    @Nonnull
    public final SLAvatarAppearance avatarAppearance;
    @Nonnull
    public final SLAvatarControl avatarControl;
    @Nonnull
    public final SLDisplayNameFetcher displayNameFetcher;
    @Nonnull
    public final SLDrawDistance drawDistance;
    @Nonnull
    public final SLFinancialInfo financialInfo;
    @Nonnull
    public final SLSearch gridSearch;
    @Nonnull
    public final SLGroupManager groupManager;
    @Nonnull
    public final SLInventory inventory;
    @Nonnull
    public final SLMinimap minimap;
    private final List<SLModule> modules;
    @Nonnull
    public final SLMuteList muteList;
    @Nonnull
    public final RLVController rlvController;
    @Nonnull
    public final SLTaskInventories taskInventories;
    @Nonnull
    public final SLTextureFetcher textureFetcher;
    @Nonnull
    public final SLTextureUploader textureUploader;
    @Nonnull
    public final SLTransferManager transferManager;
    @Nonnull
    public final SLUserNameFetcher userNameFetcher;
    @Nonnull
    public final SLUserProfiles userProfiles;
    @Nonnull
    public final SLVoice voice;
    @Nonnull
    public final SLWorldMap worldMap;
    @Nonnull
    public final SLXferManager xferManager;
    
    public SLModules(final SLAgentCircuit slAgentCircuit, final SLCaps slCaps, final SLGridConnection slGridConnection) {
        (this.modules = new ArrayList<SLModule>()).add(this.userNameFetcher = new SLUserNameFetcher(slAgentCircuit, slCaps));
        this.modules.add(this.gridSearch = new SLSearch(slAgentCircuit));
        this.modules.add(this.minimap = new SLMinimap(slAgentCircuit));
        this.modules.add(this.avatarControl = new SLAvatarControl(slAgentCircuit));
        this.modules.add(this.drawDistance = new SLDrawDistance(slAgentCircuit));
        this.modules.add(this.inventory = new SLInventory(slAgentCircuit, slCaps));
        this.modules.add(this.worldMap = new SLWorldMap(slAgentCircuit));
        this.modules.add(this.transferManager = new SLTransferManager(slAgentCircuit));
        this.modules.add(this.textureFetcher = new SLTextureFetcher(slAgentCircuit, slCaps, slGridConnection.authReply.agentAppearanceService));
        this.modules.add(this.textureUploader = new SLTextureUploader(slAgentCircuit, slCaps));
        this.modules.add(this.avatarAppearance = new SLAvatarAppearance(slAgentCircuit, this.inventory, slCaps));
        this.modules.add(this.rlvController = new RLVController(slAgentCircuit));
        this.modules.add(this.xferManager = new SLXferManager(slAgentCircuit));
        this.modules.add(this.taskInventories = new SLTaskInventories(slAgentCircuit));
        this.modules.add(this.muteList = new SLMuteList(slAgentCircuit));
        this.modules.add(this.financialInfo = new SLFinancialInfo(slAgentCircuit));
        this.modules.add(this.groupManager = new SLGroupManager(slAgentCircuit));
        this.modules.add(this.userProfiles = new SLUserProfiles(slAgentCircuit, slCaps));
        this.modules.add(this.displayNameFetcher = new SLDisplayNameFetcher(slAgentCircuit, slCaps));
        this.modules.add(this.voice = new SLVoice(slAgentCircuit, slCaps));
    }
    
    public void HandleCircuitReady() {
        final Iterator<Object> iterator = this.modules.iterator();
        while (iterator.hasNext()) {
            iterator.next().HandleCircuitReady();
        }
    }
    
    public void HandleCloseCircuit() {
        final Iterator<Object> iterator = this.modules.iterator();
        while (iterator.hasNext()) {
            iterator.next().HandleCloseCircuit();
        }
    }
    
    public void HandleGlobalOptionsChange() {
        final Iterator<Object> iterator = this.modules.iterator();
        while (iterator.hasNext()) {
            iterator.next().HandleGlobalOptionsChange();
        }
    }
}
