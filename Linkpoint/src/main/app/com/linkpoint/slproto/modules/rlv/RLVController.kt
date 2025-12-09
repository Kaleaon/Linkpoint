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

class RLVController : SLModule {
    private Boolean RLVEnabled
    private String RLVEnablingCommand
    private Boolean RLVEnablingOffered
    private UUID RLVEnablingUUID
    private RLVRestrictions restrictions

    RLVController(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit)
        this.RLVEnabled = false
        this.RLVEnablingOffered = false
        this.RLVEnablingCommand = null
        this.RLVEnablingUUID = null
        this.restrictions = RLVRestrictions()
        this.RLVEnabled = GlobalOptions.getInstance().getRLVEnabled()
    }

    private Unit handleRLVCommand(UUID uuid, String str) {
        Debug.Printf("RLV command: '%s'", str)
        String str2 = ""
        String str3 = ""
        Int indexOf = str.indexOf(61)
        if (indexOf >= 0) {
            str2 = str.substring(indexOf + 1)
            str = str.substring(0, indexOf)
        }
        Int indexOf2 = str.indexOf(58)
        if (indexOf2 >= 0) {
            str3 = str.substring(indexOf2 + 1)
            str = str.substring(0, indexOf2)
        }
        handleRLVCommandParsed(uuid, str, str2, str3)
    }

    private Unit handleRLVCommandParsed(UUID uuid, String str, String str2, String str3) {
        RLVCommand handler
        Debug.Printf("RLV command: '%s' param '%s' option '%s'", str, str2, str3)
        RLVCommands command = RLVCommands.getCommand(str)
        if (command != null && (handler = command.getHandler()) != null) {
            handler.Handle(this, uuid, command, str2, str3)
        }
    }

    private Unit handleRLVCommands(UUID uuid, String str) {
        for (String handleRLVCommand : str.split(",")) {
            handleRLVCommand(uuid, handleRLVCommand)
        }
    }

    private Unit offerRLVEnable(ChatFromSimulator chatFromSimulator) {
        this.agentCircuit.HandleChatEvent(this.agentCircuit.getLocalChatterID(), SLEnableRLVOfferEvent(chatFromSimulator, this.agentCircuit.getAgentUUID()), true)
    }

    fun HandleGlobalOptionsChange(): Unit {
        Boolean rLVEnabled = GlobalOptions.getInstance().getRLVEnabled()
        if (rLVEnabled && (!this.RLVEnabled) && this.RLVEnablingOffered && this.RLVEnablingCommand != null) {
            this.RLVEnablingOffered = false
            Debug.Printf("Enabling accepted, original command: '%s'", this.RLVEnablingCommand)
            handleRLVCommands(this.RLVEnablingUUID, this.RLVEnablingCommand)
        }
        this.RLVEnabled = rLVEnabled
    }

    fun autoAcceptTeleport(UUID uuid): Boolean {
        return this.RLVEnabled && this.restrictions.isAllowed(RLVRestrictionType.accepttp, uuid.toString(), (UUID) null)
    }

    fun canDetachItem(Int i, UUID uuid): Boolean {
        SLAttachmentPoint sLAttachmentPoint
        String str = null
        if (!this.RLVEnabled) {
            return true
        }
        if (i >= 0 && i < 56 && (sLAttachmentPoint = SLAttachmentPoint.attachmentPoints[i]) != null) {
            str = sLAttachmentPoint.name
        }
        return str == null || this.restrictions.isAllowed(RLVRestrictionType.detach, str, uuid)
    }

    fun canRecvChat(String str, UUID uuid): Boolean {
        return !this.RLVEnabled || str.startsWith("/") || this.restrictions.isAllowed(RLVRestrictionType.recvchat, uuid.toString(), (UUID) null)
    }

    fun canRecvIM(UUID uuid): Boolean {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.recvim, uuid.toString(), (UUID) null)
    }

    fun canSendIM(UUID uuid): Boolean {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.sendim, uuid.toString(), (UUID) null)
    }

    fun canShowInventory(): Boolean {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.showinv, "", (UUID) null)
    }

    fun canSit(): Boolean {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.sit, "", (UUID) null)
    }

    fun canStandUp(): Boolean {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.unsit, "", (UUID) null)
    }

    fun canTakeItemOff(SLWearableType sLWearableType): Boolean {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.remoutfit, sLWearableType.getName(), (UUID) null)
    }

    fun canTeleportBySitting(): Boolean {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.sittp, "", (UUID) null)
    }

    fun canTeleportToLandmark(): Boolean {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.tplm, "", (UUID) null)
    }

    fun canTeleportToLocation(): Boolean {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.tploc, "", (UUID) null)
    }

    fun canTeleportToLure(UUID uuid): Boolean {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.tplure, uuid.toString(), (UUID) null)
    }

    fun canViewNotecard(): Boolean {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.viewnote, "", (UUID) null)
    }

    fun canWearItem(SLWearableType sLWearableType): Boolean {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.addoutfit, sLWearableType.getName(), (UUID) null)
    }

    fun getModules(): SLModules {
        return this.agentCircuit.getModules()
    }

    fun getRestrictions(): RLVRestrictions {
        return this.restrictions
    }

    fun onIncomingChat(ChatFromSimulator chatFromSimulator): Boolean {
        if (chatFromSimulator.ChatData_Field.SourceType != 2 || chatFromSimulator.ChatData_Field.ChatType != 8) {
            return false
        }
        String stringFromVariableUTF = SLMessage.stringFromVariableUTF(chatFromSimulator.ChatData_Field.Message)
        if (!stringFromVariableUTF.startsWith("@")) {
            return false
        }
        UUID uuid = chatFromSimulator.ChatData_Field.SourceID
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

    fun onIncomingIM(ImprovedInstantMessage improvedInstantMessage): Boolean {
        if (!this.RLVEnabled) {
            return false
        }
        Int i = improvedInstantMessage.MessageBlock_Field.Dialog
        String stringFromVariableOEM = SLMessage.stringFromVariableOEM(improvedInstantMessage.MessageBlock_Field.FromAgentName)
        String stringFromVariableUTF = SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message)
        Debug.Printf("IM: type %d from '%s' text '%s'", Int.valueOf(i), stringFromVariableOEM, stringFromVariableUTF)
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

    fun onSendLocalChat(Int i, String str): Boolean {
        if (!this.RLVEnabled) {
            return true
        }
        if (i == 0) {
            if (!str.startsWith("/")) {
                Set<String> targetsForRestriction = this.restrictions.getTargetsForRestriction(RLVRestrictionType.redirchat)
                if (targetsForRestriction != null) {
                    for (String parseInt : targetsForRestriction) {
                        try {
                            Int parseInt2 = Int.parseInt(parseInt)
                            ChatFromViewer chatFromViewer = ChatFromViewer()
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
        } else if (!this.restrictions.isAllowed(RLVRestrictionType.sendchannel, Int.toString(i), (UUID) null)) {
            return false
        }
        return true
    }

    fun sayOnChannel(Int i, String str): Unit {
        Debug.Printf("RLV reply (%d): '%s'", Int.valueOf(i), str)
        ChatFromViewer chatFromViewer = ChatFromViewer()
        chatFromViewer.AgentData_Field.AgentID = this.circuitInfo.agentID
        chatFromViewer.AgentData_Field.SessionID = this.circuitInfo.sessionID
        chatFromViewer.ChatData_Field.Channel = i
        chatFromViewer.ChatData_Field.Type = 1
        chatFromViewer.ChatData_Field.Message = SLMessage.stringToVariableUTF(str)
        chatFromViewer.isReliable = true
        SendMessage(chatFromViewer)
    }

    fun teleportToGlobalPos(UUID uuid, LLVector3 lLVector3): Unit {
        if (this.RLVEnabled && this.restrictions.isAllowed(RLVRestrictionType.tploc, "", (UUID) null, uuid)) {
            this.agentCircuit.TeleportToGlobalPosition(lLVector3)
        }
    }
}
