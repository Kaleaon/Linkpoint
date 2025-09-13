package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.dispnames.SLDisplayNameFetcher;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.modules.finance.SLFinancialInfo;
import com.lumiyaviewer.lumiya.slproto.modules.groups.SLGroupManager;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.SLMuteList;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.search.SLSearch;
import com.lumiyaviewer.lumiya.slproto.modules.texfetcher.SLTextureFetcher;
import com.lumiyaviewer.lumiya.slproto.modules.texuploader.SLTextureUploader;
import com.lumiyaviewer.lumiya.slproto.modules.transfer.SLTransferManager;
import com.lumiyaviewer.lumiya.slproto.modules.voice.SLVoice;
import com.lumiyaviewer.lumiya.slproto.modules.xfer.SLXferManager;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class SLModules {
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
    private final List<SLModule> modules = new ArrayList();
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

    public SLModules(SLAgentCircuit sLAgentCircuit, SLCaps sLCaps, SLGridConnection sLGridConnection) {
        List<SLModule> list = this.modules;
        SLUserNameFetcher sLUserNameFetcher = new SLUserNameFetcher(sLAgentCircuit, sLCaps);
        this.userNameFetcher = sLUserNameFetcher;
        list.add(sLUserNameFetcher);
        List<SLModule> list2 = this.modules;
        SLSearch sLSearch = new SLSearch(sLAgentCircuit);
        this.gridSearch = sLSearch;
        list2.add(sLSearch);
        List<SLModule> list3 = this.modules;
        SLMinimap sLMinimap = new SLMinimap(sLAgentCircuit);
        this.minimap = sLMinimap;
        list3.add(sLMinimap);
        List<SLModule> list4 = this.modules;
        SLAvatarControl sLAvatarControl = new SLAvatarControl(sLAgentCircuit);
        this.avatarControl = sLAvatarControl;
        list4.add(sLAvatarControl);
        List<SLModule> list5 = this.modules;
        SLDrawDistance sLDrawDistance = new SLDrawDistance(sLAgentCircuit);
        this.drawDistance = sLDrawDistance;
        list5.add(sLDrawDistance);
        List<SLModule> list6 = this.modules;
        SLInventory sLInventory = new SLInventory(sLAgentCircuit, sLCaps);
        this.inventory = sLInventory;
        list6.add(sLInventory);
        List<SLModule> list7 = this.modules;
        SLWorldMap sLWorldMap = new SLWorldMap(sLAgentCircuit);
        this.worldMap = sLWorldMap;
        list7.add(sLWorldMap);
        List<SLModule> list8 = this.modules;
        SLTransferManager sLTransferManager = new SLTransferManager(sLAgentCircuit);
        this.transferManager = sLTransferManager;
        list8.add(sLTransferManager);
        List<SLModule> list9 = this.modules;
        SLTextureFetcher sLTextureFetcher = new SLTextureFetcher(sLAgentCircuit, sLCaps, sLGridConnection.authReply.agentAppearanceService);
        this.textureFetcher = sLTextureFetcher;
        list9.add(sLTextureFetcher);
        List<SLModule> list10 = this.modules;
        SLTextureUploader sLTextureUploader = new SLTextureUploader(sLAgentCircuit, sLCaps);
        this.textureUploader = sLTextureUploader;
        list10.add(sLTextureUploader);
        List<SLModule> list11 = this.modules;
        SLAvatarAppearance sLAvatarAppearance = new SLAvatarAppearance(sLAgentCircuit, this.inventory, sLCaps);
        this.avatarAppearance = sLAvatarAppearance;
        list11.add(sLAvatarAppearance);
        List<SLModule> list12 = this.modules;
        RLVController rLVController = new RLVController(sLAgentCircuit);
        this.rlvController = rLVController;
        list12.add(rLVController);
        List<SLModule> list13 = this.modules;
        SLXferManager sLXferManager = new SLXferManager(sLAgentCircuit);
        this.xferManager = sLXferManager;
        list13.add(sLXferManager);
        List<SLModule> list14 = this.modules;
        SLTaskInventories sLTaskInventories = new SLTaskInventories(sLAgentCircuit);
        this.taskInventories = sLTaskInventories;
        list14.add(sLTaskInventories);
        List<SLModule> list15 = this.modules;
        SLMuteList sLMuteList = new SLMuteList(sLAgentCircuit);
        this.muteList = sLMuteList;
        list15.add(sLMuteList);
        List<SLModule> list16 = this.modules;
        SLFinancialInfo sLFinancialInfo = new SLFinancialInfo(sLAgentCircuit);
        this.financialInfo = sLFinancialInfo;
        list16.add(sLFinancialInfo);
        List<SLModule> list17 = this.modules;
        SLGroupManager sLGroupManager = new SLGroupManager(sLAgentCircuit);
        this.groupManager = sLGroupManager;
        list17.add(sLGroupManager);
        List<SLModule> list18 = this.modules;
        SLUserProfiles sLUserProfiles = new SLUserProfiles(sLAgentCircuit, sLCaps);
        this.userProfiles = sLUserProfiles;
        list18.add(sLUserProfiles);
        List<SLModule> list19 = this.modules;
        SLDisplayNameFetcher sLDisplayNameFetcher = new SLDisplayNameFetcher(sLAgentCircuit, sLCaps);
        this.displayNameFetcher = sLDisplayNameFetcher;
        list19.add(sLDisplayNameFetcher);
        List<SLModule> list20 = this.modules;
        SLVoice sLVoice = new SLVoice(sLAgentCircuit, sLCaps);
        this.voice = sLVoice;
        list20.add(sLVoice);
    }

    public void HandleCircuitReady() {
        for (SLModule HandleCircuitReady : this.modules) {
            HandleCircuitReady.HandleCircuitReady();
        }
    }

    public void HandleCloseCircuit() {
        for (SLModule HandleCloseCircuit : this.modules) {
            HandleCloseCircuit.HandleCloseCircuit();
        }
    }

    public void HandleGlobalOptionsChange() {
        for (SLModule HandleGlobalOptionsChange : this.modules) {
            HandleGlobalOptionsChange.HandleGlobalOptionsChange();
        }
    }
}
