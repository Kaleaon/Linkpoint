// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.rlv;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.Iterator;
import java.util.Set;
import com.lumiyaviewer.lumiya.slproto.messages.ChatFromViewer;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdVersion;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLEnableRLVOfferEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ChatFromSimulator;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;

public class RLVController extends SLModule
{
    private boolean RLVEnabled;
    private String RLVEnablingCommand;
    private boolean RLVEnablingOffered;
    private UUID RLVEnablingUUID;
    private RLVRestrictions restrictions;
    
    public RLVController(final SLAgentCircuit slAgentCircuit) {
        super(slAgentCircuit);
        this.RLVEnabled = false;
        this.RLVEnablingOffered = false;
        this.RLVEnablingCommand = null;
        this.RLVEnablingUUID = null;
        this.restrictions = new RLVRestrictions();
        this.RLVEnabled = GlobalOptions.getInstance().getRLVEnabled();
    }
    
    private void handleRLVCommand(final UUID uuid, String substring) {
        Debug.Printf("RLV command: '%s'", substring);
        String substring2 = "";
        String substring3 = "";
        final int index = substring.indexOf(61);
        String substring4 = substring;
        if (index >= 0) {
            substring2 = substring.substring(index + 1);
            substring4 = substring.substring(0, index);
        }
        final int index2 = substring4.indexOf(58);
        substring = substring4;
        if (index2 >= 0) {
            substring3 = substring4.substring(index2 + 1);
            substring = substring4.substring(0, index2);
        }
        this.handleRLVCommandParsed(uuid, substring, substring2, substring3);
    }
    
    private void handleRLVCommandParsed(final UUID uuid, final String s, final String s2, final String s3) {
        Debug.Printf("RLV command: '%s' param '%s' option '%s'", s, s2, s3);
        final RLVCommands command = RLVCommands.getCommand(s);
        if (command != null) {
            final RLVCommand handler = command.getHandler();
            if (handler != null) {
                handler.Handle(this, uuid, command, s2, s3);
            }
        }
    }
    
    private void handleRLVCommands(final UUID uuid, final String s) {
        final String[] split = s.split(",");
        for (int i = 0; i < split.length; ++i) {
            this.handleRLVCommand(uuid, split[i]);
        }
    }
    
    private void offerRLVEnable(final ChatFromSimulator chatFromSimulator) {
        this.agentCircuit.HandleChatEvent(this.agentCircuit.getLocalChatterID(), new SLEnableRLVOfferEvent(chatFromSimulator, this.agentCircuit.getAgentUUID()), true);
    }
    
    @Override
    public void HandleGlobalOptionsChange() {
        final boolean rlvEnabled = GlobalOptions.getInstance().getRLVEnabled();
        if (rlvEnabled && (this.RLVEnabled ^ true) && this.RLVEnablingOffered && this.RLVEnablingCommand != null) {
            this.RLVEnablingOffered = false;
            Debug.Printf("Enabling accepted, original command: '%s'", this.RLVEnablingCommand);
            this.handleRLVCommands(this.RLVEnablingUUID, this.RLVEnablingCommand);
        }
        this.RLVEnabled = rlvEnabled;
    }
    
    public boolean autoAcceptTeleport(final UUID uuid) {
        return this.RLVEnabled && this.restrictions.isAllowed(RLVRestrictionType.accepttp, uuid.toString(), null);
    }
    
    public boolean canDetachItem(final int n, final UUID uuid) {
        final String s = null;
        if (!this.RLVEnabled) {
            return true;
        }
        String name = s;
        if (n >= 0) {
            name = s;
            if (n < 56) {
                final SLAttachmentPoint slAttachmentPoint = SLAttachmentPoint.attachmentPoints[n];
                name = s;
                if (slAttachmentPoint != null) {
                    name = slAttachmentPoint.name;
                }
            }
        }
        return name == null || this.restrictions.isAllowed(RLVRestrictionType.detach, name, uuid);
    }
    
    public boolean canRecvChat(final String s, final UUID uuid) {
        return !this.RLVEnabled || s.startsWith("/") || this.restrictions.isAllowed(RLVRestrictionType.recvchat, uuid.toString(), null);
    }
    
    public boolean canRecvIM(final UUID uuid) {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.recvim, uuid.toString(), null);
    }
    
    public boolean canSendIM(final UUID uuid) {
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
    
    public boolean canTakeItemOff(final SLWearableType slWearableType) {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.remoutfit, slWearableType.getName(), null);
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
    
    public boolean canTeleportToLure(final UUID uuid) {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.tplure, uuid.toString(), null);
    }
    
    public boolean canViewNotecard() {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.viewnote, "", null);
    }
    
    public boolean canWearItem(final SLWearableType slWearableType) {
        return !this.RLVEnabled || this.restrictions.isAllowed(RLVRestrictionType.addoutfit, slWearableType.getName(), null);
    }
    
    public SLModules getModules() {
        return this.agentCircuit.getModules();
    }
    
    public RLVRestrictions getRestrictions() {
        return this.restrictions;
    }
    
    public boolean onIncomingChat(final ChatFromSimulator chatFromSimulator) {
        if (chatFromSimulator.ChatData_Field.SourceType == 2 && chatFromSimulator.ChatData_Field.ChatType == 8) {
            final String stringFromVariableUTF = SLMessage.stringFromVariableUTF(chatFromSimulator.ChatData_Field.Message);
            if (stringFromVariableUTF.startsWith("@")) {
                final UUID sourceID = chatFromSimulator.ChatData_Field.SourceID;
                if (this.RLVEnabled) {
                    this.handleRLVCommands(sourceID, stringFromVariableUTF.substring(1));
                }
                else if (!this.RLVEnablingOffered) {
                    this.RLVEnablingOffered = true;
                    this.RLVEnablingUUID = sourceID;
                    this.RLVEnablingCommand = stringFromVariableUTF.substring(1);
                    this.offerRLVEnable(chatFromSimulator);
                }
                return true;
            }
        }
        return false;
    }
    
    public boolean onIncomingIM(final ImprovedInstantMessage improvedInstantMessage) {
        if (!this.RLVEnabled) {
            return false;
        }
        final int dialog = improvedInstantMessage.MessageBlock_Field.Dialog;
        final String stringFromVariableOEM = SLMessage.stringFromVariableOEM(improvedInstantMessage.MessageBlock_Field.FromAgentName);
        final String stringFromVariableUTF = SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message);
        Debug.Printf("IM: type %d from '%s' text '%s'", dialog, stringFromVariableOEM, stringFromVariableUTF);
        switch (dialog) {
            case 0: {
                if (stringFromVariableUTF.equalsIgnoreCase("@version")) {
                    this.agentCircuit.SendInstantMessage(improvedInstantMessage.AgentData_Field.AgentID, RLVCmdVersion.getManualVersionReply());
                    return true;
                }
                break;
            }
        }
        return false;
    }
    
    public boolean onSendLocalChat(int int1, final String s) {
        if (!this.RLVEnabled) {
            return true;
        }
        if (int1 == 0) {
            if (!s.startsWith("/")) {
                final Set<String> targetsForRestriction = this.restrictions.getTargetsForRestriction(RLVRestrictionType.redirchat);
                if (targetsForRestriction != null) {
                    for (final String s2 : targetsForRestriction) {
                        try {
                            int1 = Integer.parseInt(s2);
                            final ChatFromViewer chatFromViewer = new ChatFromViewer();
                            chatFromViewer.AgentData_Field.AgentID = this.circuitInfo.agentID;
                            chatFromViewer.AgentData_Field.SessionID = this.circuitInfo.sessionID;
                            chatFromViewer.ChatData_Field.Channel = int1;
                            chatFromViewer.ChatData_Field.Type = 1;
                            chatFromViewer.ChatData_Field.Message = SLMessage.stringToVariableUTF(s);
                            chatFromViewer.isReliable = true;
                            this.SendMessage(chatFromViewer);
                        }
                        catch (final NumberFormatException ex) {
                            Debug.Warning(ex);
                        }
                    }
                }
                if (!this.restrictions.isAllowed(RLVRestrictionType.sendchat, "", null)) {
                    return false;
                }
            }
        }
        else if (!this.restrictions.isAllowed(RLVRestrictionType.sendchannel, Integer.toString(int1), null)) {
            return false;
        }
        return true;
    }
    
    public void sayOnChannel(final int n, final String s) {
        Debug.Printf("RLV reply (%d): '%s'", n, s);
        final ChatFromViewer chatFromViewer = new ChatFromViewer();
        chatFromViewer.AgentData_Field.AgentID = this.circuitInfo.agentID;
        chatFromViewer.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        chatFromViewer.ChatData_Field.Channel = n;
        chatFromViewer.ChatData_Field.Type = 1;
        chatFromViewer.ChatData_Field.Message = SLMessage.stringToVariableUTF(s);
        chatFromViewer.isReliable = true;
        this.SendMessage(chatFromViewer);
    }
    
    public void teleportToGlobalPos(final UUID uuid, final LLVector3 llVector3) {
        if (!this.RLVEnabled) {
            return;
        }
        if (!this.restrictions.isAllowed(RLVRestrictionType.tploc, "", null, uuid)) {
            return;
        }
        this.agentCircuit.TeleportToGlobalPosition(llVector3);
    }
}
