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
        val list: List<SLModule> = this.modules
        val sLUserNameFetcher: SLUserNameFetcher = SLUserNameFetcher(sLAgentCircuit, sLCaps)
        this.userNameFetcher = sLUserNameFetcher
        list.add(sLUserNameFetcher)
        val list2: List<SLModule> = this.modules
        val sLSearch: SLSearch = SLSearch(sLAgentCircuit)
        this.gridSearch = sLSearch
        list2.add(sLSearch)
        val list3: List<SLModule> = this.modules
        val sLMinimap: SLMinimap = SLMinimap(sLAgentCircuit)
        this.minimap = sLMinimap
        list3.add(sLMinimap)
        val list4: List<SLModule> = this.modules
        val sLAvatarControl: SLAvatarControl = SLAvatarControl(sLAgentCircuit)
        this.avatarControl = sLAvatarControl
        list4.add(sLAvatarControl)
        val list5: List<SLModule> = this.modules
        val sLDrawDistance: SLDrawDistance = SLDrawDistance(sLAgentCircuit)
        this.drawDistance = sLDrawDistance
        list5.add(sLDrawDistance)
        val list6: List<SLModule> = this.modules
        val sLInventory: SLInventory = SLInventory(sLAgentCircuit, sLCaps)
        this.inventory = sLInventory
        list6.add(sLInventory)
        val list7: List<SLModule> = this.modules
        val sLWorldMap: SLWorldMap = SLWorldMap(sLAgentCircuit)
        this.worldMap = sLWorldMap
        list7.add(sLWorldMap)
        val list8: List<SLModule> = this.modules
        val sLTransferManager: SLTransferManager = SLTransferManager(sLAgentCircuit)
        this.transferManager = sLTransferManager
        list8.add(sLTransferManager)
        val list9: List<SLModule> = this.modules
        val sLTextureFetcher: SLTextureFetcher = SLTextureFetcher(sLAgentCircuit, sLCaps, sLGridConnection.authReply.agentAppearanceService)
        this.textureFetcher = sLTextureFetcher
        list9.add(sLTextureFetcher)
        val list10: List<SLModule> = this.modules
        val sLTextureUploader: SLTextureUploader = SLTextureUploader(sLAgentCircuit, sLCaps)
        this.textureUploader = sLTextureUploader
        list10.add(sLTextureUploader)
        val list11: List<SLModule> = this.modules
        val sLAvatarAppearance: SLAvatarAppearance = SLAvatarAppearance(sLAgentCircuit, this.inventory, sLCaps)
        this.avatarAppearance = sLAvatarAppearance
        list11.add(sLAvatarAppearance)
        val list12: List<SLModule> = this.modules
        val rLVController: RLVController = RLVController(sLAgentCircuit)
        this.rlvController = rLVController
        list12.add(rLVController)
        val list13: List<SLModule> = this.modules
        val sLXferManager: SLXferManager = SLXferManager(sLAgentCircuit)
        this.xferManager = sLXferManager
        list13.add(sLXferManager)
        val list14: List<SLModule> = this.modules
        val sLTaskInventories: SLTaskInventories = SLTaskInventories(sLAgentCircuit)
        this.taskInventories = sLTaskInventories
        list14.add(sLTaskInventories)
        val list15: List<SLModule> = this.modules
        val sLMuteList: SLMuteList = SLMuteList(sLAgentCircuit)
        this.muteList = sLMuteList
        list15.add(sLMuteList)
        val list16: List<SLModule> = this.modules
        val sLFinancialInfo: SLFinancialInfo = SLFinancialInfo(sLAgentCircuit)
        this.financialInfo = sLFinancialInfo
        list16.add(sLFinancialInfo)
        val list17: List<SLModule> = this.modules
        val sLGroupManager: SLGroupManager = SLGroupManager(sLAgentCircuit)
        this.groupManager = sLGroupManager
        list17.add(sLGroupManager)
        val list18: List<SLModule> = this.modules
        val sLUserProfiles: SLUserProfiles = SLUserProfiles(sLAgentCircuit, sLCaps)
        this.userProfiles = sLUserProfiles
        list18.add(sLUserProfiles)
        val list19: List<SLModule> = this.modules
        val sLDisplayNameFetcher: SLDisplayNameFetcher = SLDisplayNameFetcher(sLAgentCircuit, sLCaps)
        this.displayNameFetcher = sLDisplayNameFetcher
        list19.add(sLDisplayNameFetcher)
        val list20: List<SLModule> = this.modules
        val sLVoice: SLVoice = SLVoice(sLAgentCircuit, sLCaps)
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
