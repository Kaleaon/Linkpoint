package com.lumiyaviewer.lumiya.slproto.modules.rlv;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.slproto.chat.SLEnableRLVOfferEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ChatFromSimulator;
import com.lumiyaviewer.lumiya.slproto.messages.ChatFromViewer;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdVersion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/* loaded from: classes.dex */
public class RLVController extends SLModule {
    private boolean RLVEnabled;
    private String RLVEnablingCommand;
    private boolean RLVEnablingOffered;
    private UUID RLVEnablingUUID;
    private RLVRestrictions restrictions;

    public RLVController(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit);
        this.RLVEnabled = false;
        this.RLVEnablingOffered = false;
        this.RLVEnablingCommand = null;
        this.RLVEnablingUUID = null;
        this.restrictions = new RLVRestrictions();
        this.RLVEnabled = GlobalOptions.getInstance().getRLVEnabled();
    }

    private void handleRLVCommand(UUID uuid, String str) {
        Debug.Printf("RLV command: '%s'", str);
        String str2 = "";
        String str3 = "";
        int indexOf = str.indexOf(61);
        if (indexOf >= 0) {
            str2 = str.substring(indexOf + 1);
            str = str.substring(0, indexOf);
        }
        int indexOf2 = str.indexOf(58);
        if (indexOf2 >= 0) {
            str3 = str.substring(indexOf2 + 1);
            str = str.substring(0, indexOf2);
        }
        handleRLVCommandParsed(uuid, str, str2, str3);
    }

    private void handleRLVCommandParsed(UUID uuid, String str, String str2, String str3) {
        RLVCommand handler;
        Debug.Printf("RLV command: '%s' param '%s' option '%s'", str, str2, str3);
        RLVCommands command = RLVCommands.getCommand(str);
        if (command == null || (handler = command.getHandler()) == null) {
            return;
        }
        handler.Handle(this, uuid, command, str2, str3);
    }

    private void handleRLVCommands(UUID uuid, String str) {
        for (String str2 : str.split(",")) {
            handleRLVCommand(uuid, str2);
        }
    }

    private void offerRLVEnable(ChatFromSimulator chatFromSimulator) {
        this.agentCircuit.HandleChatEvent(this.agentCircuit.getLocalChatterID(), new SLEnableRLVOfferEvent(chatFromSimulator, this.agentCircuit.getAgentUUID()), true);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.modules.SLModule
    public void HandleGlobalOptionsChange() {
        boolean rLVEnabled = GlobalOptions.getInstance().getRLVEnabled();
        if (rLVEnabled && (!this.RLVEnabled) && this.RLVEnablingOffered && this.RLVEnablingCommand != null) {
            this.RLVEnablingOffered = false;
            Debug.Printf("Enabling accepted, original command: '%s'", this.RLVEnablingCommand);
            handleRLVCommands(this.RLVEnablingUUID, this.RLVEnablingCommand);
        }
        this.RLVEnabled = rLVEnabled;
    }

    public boolean autoAcceptTeleport(UUID uuid) {
        return this.RLVEnabled && this.restrictions.isAllowed(RLVRestrictionType.accepttp, uuid.toString(), null);
    }

    public boolean canDetachItem(int i, UUID uuid) {
        SLAttachmentPoint sLAttachmentPoint;
        String str = null;
        if (!this.RLVEnabled) {
            return true;
        }
        if (i >= 0 && i < 56 && (sLAttachmentPoint = SLAttachmentPoint.attachmentPoints[i]) != null) {
            str = sLAttachmentPoint.name;
        }
        return str == null || this.restrictions.isAllowed(RLVRestrictionType.detach, str, uuid);
    }

    public boolean canRecvChat(String str, UUID uuid) {
        return !this.RLVEnabled || str.startsWith("/") || this.restrictions.isAllowed(RLVRestrictionType.recvchat, uuid.toString(), null);
    }

    public boolean canRecvIM(UUID uuid) {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.recvim, uuid.toString(), null);
    }

    public boolean canSendIM(UUID uuid) {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.sendim, uuid.toString(), null);
    }

    public boolean canShowInventory() {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.showinv, "", null);
    }

    public boolean canSit() {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.sit, "", null);
    }

    public boolean canStandUp() {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.unsit, "", null);
    }

    public boolean canTakeItemOff(SLWearableType sLWearableType) {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.remoutfit, sLWearableType.getName(), null);
    }

    public boolean canTeleportBySitting() {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.sittp, "", null);
    }

    public boolean canTeleportToLandmark() {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.tplm, "", null);
    }

    public boolean canTeleportToLocation() {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.tploc, "", null);
    }

    public boolean canTeleportToLure(UUID uuid) {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.tplure, uuid.toString(), null);
    }

    public boolean canViewNotecard() {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.viewnote, "", null);
    }

    public boolean canWearItem(SLWearableType sLWearableType) {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.addoutfit, sLWearableType.getName(), null);
    }

    public SLModules getModules() {
        return this.agentCircuit.getModules();
    }

    public RLVRestrictions getRestrictions() {
        return this.restrictions;
    }

    public boolean onIncomingChat(ChatFromSimulator chatFromSimulator) {
        if (chatFromSimulator.ChatData_Field.SourceType != 2 || chatFromSimulator.ChatData_Field.ChatType != 8) {
            return false;
        }
        String stringFromVariableUTF = SLMessage.stringFromVariableUTF(chatFromSimulator.ChatData_Field.Message);
        if (!stringFromVariableUTF.startsWith("@")) {
            return false;
        }
        UUID uuid = chatFromSimulator.ChatData_Field.SourceID;
        if (this.RLVEnabled) {
            handleRLVCommands(uuid, stringFromVariableUTF.substring(1));
        } else if (!this.RLVEnablingOffered) {
            this.RLVEnablingOffered = true;
            this.RLVEnablingUUID = uuid;
            this.RLVEnablingCommand = stringFromVariableUTF.substring(1);
            offerRLVEnable(chatFromSimulator);
        }
        return true;
    }

    public boolean onIncomingIM(ImprovedInstantMessage improvedInstantMessage) {
        if (!this.RLVEnabled) {
            return false;
        }
        int i = improvedInstantMessage.MessageBlock_Field.Dialog;
        String stringFromVariableOEM = SLMessage.stringFromVariableOEM(improvedInstantMessage.MessageBlock_Field.FromAgentName);
        String stringFromVariableUTF = SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message);
        Debug.Printf("IM: type %d from '%s' text '%s'", Integer.valueOf(i), stringFromVariableOEM, stringFromVariableUTF);
        switch (i) {
            case 0:
                if (stringFromVariableUTF.equalsIgnoreCase("@version")) {
                    this.agentCircuit.SendInstantMessage(improvedInstantMessage.AgentData_Field.AgentID, RLVCmdVersion.getManualVersionReply());
                    return true;
                }
            default:
                return false;
        }
    }

    public boolean onSendLocalChat(int i, String str) {
        if (!this.RLVEnabled) {
            return true;
        }
        if (i == 0) {
            if (!str.startsWith("/")) {
                Set<String> targetsForRestriction = this.restrictions.getTargetsForRestriction(RLVRestrictionType.redirchat);
                if (targetsForRestriction != null) {
                    Iterator<T> it = targetsForRestriction.iterator();
                    while (it.hasNext()) {
                        try {
                            int parseInt = Integer.parseInt((String) it.next());
                            ChatFromViewer chatFromViewer = new ChatFromViewer();
                            chatFromViewer.AgentData_Field.AgentID = this.circuitInfo.agentID;
                            chatFromViewer.AgentData_Field.SessionID = this.circuitInfo.sessionID;
                            chatFromViewer.ChatData_Field.Channel = parseInt;
                            chatFromViewer.ChatData_Field.Type = 1;
                            chatFromViewer.ChatData_Field.Message = SLMessage.stringToVariableUTF(str);
                            chatFromViewer.isReliable = true;
                            SendMessage(chatFromViewer);
                        } catch (NumberFormatException e) {
                            Debug.Warning(e);
                        }
                    }
                }
                if (!this.restrictions.isAllowed(RLVRestrictionType.sendchat, "", null)) {
                    return false;
                }
            }
        } else if (!this.restrictions.isAllowed(RLVRestrictionType.sendchannel, Integer.toString(i), null)) {
            return false;
        }
        return true;
    }

    public void sayOnChannel(int i, String str) {
        Debug.Printf("RLV reply (%d): '%s'", Integer.valueOf(i), str);
        ChatFromViewer chatFromViewer = new ChatFromViewer();
        chatFromViewer.AgentData_Field.AgentID = this.circuitInfo.agentID;
        chatFromViewer.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        chatFromViewer.ChatData_Field.Channel = i;
        chatFromViewer.ChatData_Field.Type = 1;
        chatFromViewer.ChatData_Field.Message = SLMessage.stringToVariableUTF(str);
        chatFromViewer.isReliable = true;
        SendMessage(chatFromViewer);
    }

    public void teleportToGlobalPos(UUID uuid, LLVector3 lLVector3) {
        if (this.RLVEnabled && this.restrictions.isAllowed(RLVRestrictionType.tploc, "", null, uuid)) {
            this.agentCircuit.TeleportToGlobalPosition(lLVector3);
        }
    }
}
