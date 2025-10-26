package com.linkpoint.slproto.modules.rlv

import com.linkpoint.Debug
import com.linkpoint.GlobalOptions
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.assets.SLWearableType
import com.linkpoint.slproto.avatar.SLAttachmentPoint
import com.linkpoint.slproto.chat.SLEnableRLVOfferEvent
import com.linkpoint.slproto.messages.ChatFromSimulator
import com.linkpoint.slproto.messages.ChatFromViewer
import com.linkpoint.slproto.messages.ImprovedInstantMessage
import com.linkpoint.slproto.modules.SLModule
import com.linkpoint.slproto.modules.SLModules
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdVersion
import com.linkpoint.slproto.types.LLVector3
import java.util.Set
import java.util.UUID

class RLVController : SLModule() {
    private Boolean RLVEnabled
    private String RLVEnablingCommand
    private Boolean RLVEnablingOffered
    private UUID RLVEnablingUUID
    private RLVRestrictions restrictions

    public RLVController(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit)
        this.RLVEnabled = false
        this.RLVEnablingOffered = false
        this.RLVEnablingCommand = null
        this.RLVEnablingUUID = null
        this.restrictions = RLVRestrictions()
        this.RLVEnabled = GlobalOptions.getInstance().getRLVEnabled()
    }

     private fun handleRLVCommand(uuid: UUID, str: String) {
        Debug.Printf("RLV command: '%s'", str)
        val str2: String = ""
        val str3: String = ""
        val indexOf: Int = str.indexOf(61)
        if (indexOf >= 0) {
            str2 = str.substring(indexOf + 1)
            str = str.substring(0, indexOf)
        }
        val indexOf2: Int = str.indexOf(58)
        if (indexOf2 >= 0) {
            str3 = str.substring(indexOf2 + 1)
            str = str.substring(0, indexOf2)
        }
        handleRLVCommandParsed(uuid, str, str2, str3)
    }

     private fun handleRLVCommandParsed(uuid: UUID, str: String, str2: String, str3: String) {
        RLVCommand handler
        Debug.Printf("RLV command: '%s' param '%s' option '%s'", str, str2, str3)
        val command: RLVCommands = RLVCommands.getCommand(str)
        if (command != null && (handler = command.getHandler()) != null) {
            handler.Handle(this, uuid, command, str2, str3)
        }
    }

     private fun handleRLVCommands(uuid: UUID, str: String) {
        for (String handleRLVCommand : str.split(",")) {
            handleRLVCommand(uuid, handleRLVCommand)
        }
    }

     private fun offerRLVEnable(chatFromSimulator: ChatFromSimulator) {
        this.agentCircuit.HandleChatEvent(this.agentCircuit.getLocalChatterID(), SLEnableRLVOfferEvent(chatFromSimulator, this.agentCircuit.getAgentUUID()), true)
    }

    fun HandleGlobalOptionsChange() {
        val rLVEnabled: Boolean = GlobalOptions.getInstance().getRLVEnabled()
        if (rLVEnabled && (!this.RLVEnabled) && this.RLVEnablingOffered && this.RLVEnablingCommand != null) {
            this.RLVEnablingOffered = false
            Debug.Printf("Enabling accepted, original command: '%s'", this.RLVEnablingCommand)
            handleRLVCommands(this.RLVEnablingUUID, this.RLVEnablingCommand)
        }
        this.RLVEnabled = rLVEnabled
    }

     public fun autoAcceptTeleport(uuid: UUID): Boolean {
        return this.RLVEnabled && this.restrictions.isAllowed(RLVRestrictionType.accepttp, uuid.toString(), (UUID) null)
    }

     public fun canDetachItem(i: Int, uuid: UUID): Boolean {
        SLAttachmentPoint sLAttachmentPoint
        val str: String = null
        if (!this.RLVEnabled) {
            return true
        }
        if (i >= 0 && i < 56 && (sLAttachmentPoint = SLAttachmentPoint.attachmentPoints[i]) != null) {
            str = sLAttachmentPoint.name
        }
        return str == null || this.restrictions.isAllowed(RLVRestrictionType.detach, str, uuid)
    }

     public fun canRecvChat(str: String, uuid: UUID): Boolean {
        return !this.RLVEnabled || str.startsWith("/") || this.restrictions.isAllowed(RLVRestrictionType.recvchat, uuid.toString(), (UUID) null)
    }

     public fun canRecvIM(uuid: UUID): Boolean {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.recvim, uuid.toString(), (UUID) null)
    }

     public fun canSendIM(uuid: UUID): Boolean {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.sendim, uuid.toString(), (UUID) null)
    }

     public fun canShowInventory(): Boolean {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.showinv, "", (UUID) null)
    }

     public fun canSit(): Boolean {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.sit, "", (UUID) null)
    }

     public fun canStandUp(): Boolean {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.unsit, "", (UUID) null)
    }

     public fun canTakeItemOff(sLWearableType: SLWearableType): Boolean {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.remoutfit, sLWearableType.getName(), (UUID) null)
    }

     public fun canTeleportBySitting(): Boolean {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.sittp, "", (UUID) null)
    }

     public fun canTeleportToLandmark(): Boolean {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.tplm, "", (UUID) null)
    }

     public fun canTeleportToLocation(): Boolean {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.tploc, "", (UUID) null)
    }

     public fun canTeleportToLure(uuid: UUID): Boolean {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.tplure, uuid.toString(), (UUID) null)
    }

     public fun canViewNotecard(): Boolean {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.viewnote, "", (UUID) null)
    }

     public fun canWearItem(sLWearableType: SLWearableType): Boolean {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.addoutfit, sLWearableType.getName(), (UUID) null)
    }

     public fun getModules(): SLModules {
        return this.agentCircuit.getModules()
    }

     public fun getRestrictions(): RLVRestrictions {
        return this.restrictions
    }

     public fun onIncomingChat(chatFromSimulator: ChatFromSimulator): Boolean {
        if (chatFromSimulator.ChatData_Field.SourceType != 2 || chatFromSimulator.ChatData_Field.ChatType != 8) {
            return false
        }
        val stringFromVariableUTF: String = SLMessage.stringFromVariableUTF(chatFromSimulator.ChatData_Field.Message)
        if (!stringFromVariableUTF.startsWith("@")) {
            return false
        }
        val uuid: UUID = chatFromSimulator.ChatData_Field.SourceID
        if (this.RLVEnabled) {
            handleRLVCommands(uuid, stringFromVariableUTF.substring(1))
        } else if (!this.RLVEnablingOffered) {
            this.RLVEnablingOffered = true
            this.RLVEnablingUUID = uuid
            this.RLVEnablingCommand = stringFromVariableUTF.substring(1)
            offerRLVEnable(chatFromSimulator)
        }
        return true
    }

     public fun onIncomingIM(improvedInstantMessage: ImprovedInstantMessage): Boolean {
        if (!this.RLVEnabled) {
            return false
        }
        val i: Int = improvedInstantMessage.MessageBlock_Field.Dialog
        val stringFromVariableOEM: String = SLMessage.stringFromVariableOEM(improvedInstantMessage.MessageBlock_Field.FromAgentName)
        val stringFromVariableUTF: String = SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message)
        Debug.Printf("IM: type %d from '%s' text '%s'", Integer.valueOf(i), stringFromVariableOEM, stringFromVariableUTF)
        switch (i) {
            case 0:
                if (stringFromVariableUTF.equalsIgnoreCase("@version")) {
                    this.agentCircuit.SendInstantMessage(improvedInstantMessage.AgentData_Field.AgentID, RLVCmdVersion.getManualVersionReply())
                    return true
                }
                break
        }
        return false
    }

     public fun onSendLocalChat(i: Int, str: String): Boolean {
        if (!this.RLVEnabled) {
            return true
        }
        if (i == 0) {
            if (!str.startsWith("/")) {
                val targetsForRestriction: Set<String> = this.restrictions.getTargetsForRestriction(RLVRestrictionType.redirchat)
                if (targetsForRestriction != null) {
                    for (String parseInt : targetsForRestriction) {
                        try {
                            val parseInt2: Int = Integer.parseInt(parseInt)
                            val chatFromViewer: ChatFromViewer = ChatFromViewer()
                            chatFromViewer.AgentData_Field.AgentID = this.circuitInfo.agentID
                            chatFromViewer.AgentData_Field.SessionID = this.circuitInfo.sessionID
                            chatFromViewer.ChatData_Field.Channel = parseInt2
                            chatFromViewer.ChatData_Field.Type = 1
                            chatFromViewer.ChatData_Field.Message = SLMessage.stringToVariableUTF(str)
                            chatFromViewer.isReliable = true
                            SendMessage(chatFromViewer)
                        } catch (NumberFormatException e) {
                            Debug.Warning(e)
                        }
                    }
                }
                if (!this.restrictions.isAllowed(RLVRestrictionType.sendchat, "", (UUID) null)) {
                    return false
                }
            }
        } else if (!this.restrictions.isAllowed(RLVRestrictionType.sendchannel, Integer.toString(i), (UUID) null)) {
            return false
        }
        return true
    }

    fun sayOnChannel(i: Int, str: String) {
        Debug.Printf("RLV reply (%d): '%s'", Integer.valueOf(i), str)
        val chatFromViewer: ChatFromViewer = ChatFromViewer()
        chatFromViewer.AgentData_Field.AgentID = this.circuitInfo.agentID
        chatFromViewer.AgentData_Field.SessionID = this.circuitInfo.sessionID
        chatFromViewer.ChatData_Field.Channel = i
        chatFromViewer.ChatData_Field.Type = 1
        chatFromViewer.ChatData_Field.Message = SLMessage.stringToVariableUTF(str)
        chatFromViewer.isReliable = true
        SendMessage(chatFromViewer)
    }

    fun teleportToGlobalPos(uuid: UUID, lLVector3: LLVector3) {
        if (this.RLVEnabled && this.restrictions.isAllowed(RLVRestrictionType.tploc, "", (UUID) null, uuid)) {
            this.agentCircuit.TeleportToGlobalPosition(lLVector3)
        }
    }
}
