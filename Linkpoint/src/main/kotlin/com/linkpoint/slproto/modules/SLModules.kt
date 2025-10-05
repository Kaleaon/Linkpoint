package com.linkpoint.slproto.modules

import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLGridConnection
import com.linkpoint.slproto.caps.SLCaps
import com.linkpoint.slproto.dispnames.SLDisplayNameFetcher
import com.linkpoint.slproto.inventory.SLInventory
import com.linkpoint.slproto.modules.finance.SLFinancialInfo
import com.linkpoint.slproto.modules.groups.SLGroupManager
import com.linkpoint.slproto.modules.mutelist.SLMuteList
import com.linkpoint.slproto.modules.rlv.RLVController
import com.linkpoint.slproto.modules.search.SLSearch
import com.linkpoint.slproto.modules.texfetcher.SLTextureFetcher
import com.linkpoint.slproto.modules.texuploader.SLTextureUploader
import com.linkpoint.slproto.modules.transfer.SLTransferManager
import com.linkpoint.slproto.modules.voice.SLVoice
import com.linkpoint.slproto.modules.xfer.SLXferManager
import java.util.ArrayList
import java.util.List
import javax.annotation.Nonnull

class SLModules {
    val SLAvatarAppearance avatarAppearance
    val SLAvatarControl avatarControl
    val SLDisplayNameFetcher displayNameFetcher
    val SLDrawDistance drawDistance
    val SLFinancialInfo financialInfo
    val SLSearch gridSearch
    val SLGroupManager groupManager
    val SLInventory inventory
    val SLMinimap minimap
    private val List<SLModule> modules = ArrayList()
    val SLMuteList muteList
    val RLVController rlvController
    val SLTaskInventories taskInventories
    val SLTextureFetcher textureFetcher
    val SLTextureUploader textureUploader
    val SLTransferManager transferManager
    val SLUserNameFetcher userNameFetcher
    val SLUserProfiles userProfiles
    val SLVoice voice
    val SLWorldMap worldMap
    val SLXferManager xferManager

    public SLModules(SLAgentCircuit sLAgentCircuit, SLCaps sLCaps, SLGridConnection sLGridConnection) {
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

    fun HandleCircuitReady() {
        for (SLModule HandleCircuitReady : this.modules) {
            HandleCircuitReady.HandleCircuitReady()
        }
    }

    fun HandleCloseCircuit() {
        for (SLModule HandleCloseCircuit : this.modules) {
            HandleCloseCircuit.HandleCloseCircuit()
        }
    }

    fun HandleGlobalOptionsChange() {
        for (SLModule HandleGlobalOptionsChange : this.modules) {
            HandleGlobalOptionsChange.HandleGlobalOptionsChange()
        }
    }
}
