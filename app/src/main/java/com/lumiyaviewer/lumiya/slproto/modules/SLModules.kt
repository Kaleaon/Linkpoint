package com.lumiyaviewer.lumiya.slproto.modules

import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.SLGridConnection
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps
import com.lumiyaviewer.lumiya.slproto.dispnames.SLDisplayNameFetcher
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory
import com.lumiyaviewer.lumiya.slproto.modules.finance.SLFinancialInfo
import com.lumiyaviewer.lumiya.slproto.modules.groups.SLGroupManager
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.SLMuteList
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController
import com.lumiyaviewer.lumiya.slproto.modules.search.SLSearch
import com.lumiyaviewer.lumiya.slproto.modules.texfetcher.SLTextureFetcher
import com.lumiyaviewer.lumiya.slproto.modules.texuploader.SLTextureUploader
import com.lumiyaviewer.lumiya.slproto.modules.transfer.SLTransferManager
import com.lumiyaviewer.lumiya.slproto.modules.voice.SLVoice
import com.lumiyaviewer.lumiya.slproto.modules.xfer.SLXferManager
import java.util.ArrayList
import java.util.List
import javax.annotation.Nonnull

class SLModules {
    @Nonnull
    SLAvatarAppearance avatarAppearance
    @Nonnull
    SLAvatarControl avatarControl
    @Nonnull
    SLDisplayNameFetcher displayNameFetcher
    @Nonnull
    SLDrawDistance drawDistance
    @Nonnull
    SLFinancialInfo financialInfo
    @Nonnull
    SLSearch gridSearch
    @Nonnull
    SLGroupManager groupManager
    @Nonnull
    SLInventory inventory
    @Nonnull
    SLMinimap minimap
    private List<SLModule> modules = ArrayList()
    @Nonnull
    SLMuteList muteList
    @Nonnull
    RLVController rlvController
    @Nonnull
    SLTaskInventories taskInventories
    @Nonnull
    SLTextureFetcher textureFetcher
    @Nonnull
    SLTextureUploader textureUploader
    @Nonnull
    SLTransferManager transferManager
    @Nonnull
    SLUserNameFetcher userNameFetcher
    @Nonnull
    SLUserProfiles userProfiles
    @Nonnull
    SLVoice voice
    @Nonnull
    SLWorldMap worldMap
    @Nonnull
    SLXferManager xferManager

    SLModules(SLAgentCircuit sLAgentCircuit, SLCaps sLCaps, SLGridConnection sLGridConnection) {
        List<SLModule> list = this.modules
        SLUserNameFetcher sLUserNameFetcher = SLUserNameFetcher(sLAgentCircuit, sLCaps)
        this.userNameFetcher = sLUserNameFetcher
        list.add(sLUserNameFetcher)
        List<SLModule> list2 = this.modules
        SLSearch sLSearch = SLSearch(sLAgentCircuit)
        this.gridSearch = sLSearch
        list2.add(sLSearch)
        List<SLModule> list3 = this.modules
        SLMinimap sLMinimap = SLMinimap(sLAgentCircuit)
        this.minimap = sLMinimap
        list3.add(sLMinimap)
        List<SLModule> list4 = this.modules
        SLAvatarControl sLAvatarControl = SLAvatarControl(sLAgentCircuit)
        this.avatarControl = sLAvatarControl
        list4.add(sLAvatarControl)
        List<SLModule> list5 = this.modules
        SLDrawDistance sLDrawDistance = SLDrawDistance(sLAgentCircuit)
        this.drawDistance = sLDrawDistance
        list5.add(sLDrawDistance)
        List<SLModule> list6 = this.modules
        SLInventory sLInventory = SLInventory(sLAgentCircuit, sLCaps)
        this.inventory = sLInventory
        list6.add(sLInventory)
        List<SLModule> list7 = this.modules
        SLWorldMap sLWorldMap = SLWorldMap(sLAgentCircuit)
        this.worldMap = sLWorldMap
        list7.add(sLWorldMap)
        List<SLModule> list8 = this.modules
        SLTransferManager sLTransferManager = SLTransferManager(sLAgentCircuit)
        this.transferManager = sLTransferManager
        list8.add(sLTransferManager)
        List<SLModule> list9 = this.modules
        SLTextureFetcher sLTextureFetcher = SLTextureFetcher(sLAgentCircuit, sLCaps, sLGridConnection.authReply.agentAppearanceService)
        this.textureFetcher = sLTextureFetcher
        list9.add(sLTextureFetcher)
        List<SLModule> list10 = this.modules
        SLTextureUploader sLTextureUploader = SLTextureUploader(sLAgentCircuit, sLCaps)
        this.textureUploader = sLTextureUploader
        list10.add(sLTextureUploader)
        List<SLModule> list11 = this.modules
        SLAvatarAppearance sLAvatarAppearance = SLAvatarAppearance(sLAgentCircuit, this.inventory, sLCaps)
        this.avatarAppearance = sLAvatarAppearance
        list11.add(sLAvatarAppearance)
        List<SLModule> list12 = this.modules
        RLVController rLVController = RLVController(sLAgentCircuit)
        this.rlvController = rLVController
        list12.add(rLVController)
        List<SLModule> list13 = this.modules
        SLXferManager sLXferManager = SLXferManager(sLAgentCircuit)
        this.xferManager = sLXferManager
        list13.add(sLXferManager)
        List<SLModule> list14 = this.modules
        SLTaskInventories sLTaskInventories = SLTaskInventories(sLAgentCircuit)
        this.taskInventories = sLTaskInventories
        list14.add(sLTaskInventories)
        List<SLModule> list15 = this.modules
        SLMuteList sLMuteList = SLMuteList(sLAgentCircuit)
        this.muteList = sLMuteList
        list15.add(sLMuteList)
        List<SLModule> list16 = this.modules
        SLFinancialInfo sLFinancialInfo = SLFinancialInfo(sLAgentCircuit)
        this.financialInfo = sLFinancialInfo
        list16.add(sLFinancialInfo)
        List<SLModule> list17 = this.modules
        SLGroupManager sLGroupManager = SLGroupManager(sLAgentCircuit)
        this.groupManager = sLGroupManager
        list17.add(sLGroupManager)
        List<SLModule> list18 = this.modules
        SLUserProfiles sLUserProfiles = SLUserProfiles(sLAgentCircuit, sLCaps)
        this.userProfiles = sLUserProfiles
        list18.add(sLUserProfiles)
        List<SLModule> list19 = this.modules
        SLDisplayNameFetcher sLDisplayNameFetcher = SLDisplayNameFetcher(sLAgentCircuit, sLCaps)
        this.displayNameFetcher = sLDisplayNameFetcher
        list19.add(sLDisplayNameFetcher)
        List<SLModule> list20 = this.modules
        SLVoice sLVoice = SLVoice(sLAgentCircuit, sLCaps)
        this.voice = sLVoice
        list20.add(sLVoice)
    }

    Unit HandleCircuitReady() {
        for (SLModule HandleCircuitReady : this.modules) {
            HandleCircuitReady.HandleCircuitReady()
        }
    }

    Unit HandleCloseCircuit() {
        for (SLModule HandleCloseCircuit : this.modules) {
            HandleCloseCircuit.HandleCloseCircuit()
        }
    }

    Unit HandleGlobalOptionsChange() {
        for (SLModule HandleGlobalOptionsChange : this.modules) {
            HandleGlobalOptionsChange.HandleGlobalOptionsChange()
        }
    }
}
