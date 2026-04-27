// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.rlv;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
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
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.rlv:
//            RLVRestrictions, RLVCommands, RLVCommand, RLVRestrictionType

public class RLVController extends SLModule
{

    private boolean RLVEnabled;
    private String RLVEnablingCommand;
    private boolean RLVEnablingOffered;
    private UUID RLVEnablingUUID;
    private RLVRestrictions restrictions;

    public RLVController(SLAgentCircuit slagentcircuit)
    {
        super(slagentcircuit);
        RLVEnabled = false;
        RLVEnablingOffered = false;
        RLVEnablingCommand = null;
        RLVEnablingUUID = null;
        restrictions = new RLVRestrictions();
        RLVEnabled = GlobalOptions.getInstance().getRLVEnabled();
    }

    private void handleRLVCommand(UUID uuid, String s)
    {
        Debug.Printf("RLV command: '%s'", new Object[] {
            s
        });
        String s2 = "";
        String s3 = "";
        int i = s.indexOf('=');
        String s1 = s;
        if (i >= 0)
        {
            s2 = s.substring(i + 1);
            s1 = s.substring(0, i);
        }
        i = s1.indexOf(':');
        s = s1;
        if (i >= 0)
        {
            s3 = s1.substring(i + 1);
            s = s1.substring(0, i);
        }
        handleRLVCommandParsed(uuid, s, s2, s3);
    }

    private void handleRLVCommandParsed(UUID uuid, String s, String s1, String s2)
    {
        Debug.Printf("RLV command: '%s' param '%s' option '%s'", new Object[] {
            s, s1, s2
        });
        s = RLVCommands.getCommand(s);
        if (s != null)
        {
            RLVCommand rlvcommand = s.getHandler();
            if (rlvcommand != null)
            {
                rlvcommand.Handle(this, uuid, s, s1, s2);
            }
        }
    }

    private void handleRLVCommands(UUID uuid, String s)
    {
        s = s.split(",");
        int i = 0;
        for (int j = s.length; i < j; i++)
        {
            handleRLVCommand(uuid, s[i]);
        }

    }

    private void offerRLVEnable(ChatFromSimulator chatfromsimulator)
    {
        agentCircuit.HandleChatEvent(agentCircuit.getLocalChatterID(), new SLEnableRLVOfferEvent(chatfromsimulator, agentCircuit.getAgentUUID()), true);
    }

    public void HandleGlobalOptionsChange()
    {
        boolean flag = GlobalOptions.getInstance().getRLVEnabled();
        if (flag && RLVEnabled ^ true && RLVEnablingOffered && RLVEnablingCommand != null)
        {
            RLVEnablingOffered = false;
            Debug.Printf("Enabling accepted, original command: '%s'", new Object[] {
                RLVEnablingCommand
            });
            handleRLVCommands(RLVEnablingUUID, RLVEnablingCommand);
        }
        RLVEnabled = flag;
    }

    public boolean autoAcceptTeleport(UUID uuid)
    {
        if (!RLVEnabled)
        {
            return false;
        }
        return restrictions.isAllowed(RLVRestrictionType.accepttp, uuid.toString(), null);
    }

    public boolean canDetachItem(int i, UUID uuid)
    {
        Object obj = null;
        if (!RLVEnabled)
        {
            return true;
        }
        String s = obj;
        if (i >= 0)
        {
            s = obj;
            if (i < 56)
            {
                SLAttachmentPoint slattachmentpoint = SLAttachmentPoint.attachmentPoints[i];
                s = obj;
                if (slattachmentpoint != null)
                {
                    s = slattachmentpoint.name;
                }
            }
        }
        return s == null || restrictions.isAllowed(RLVRestrictionType.detach, s, uuid);
    }

    public boolean canRecvChat(String s, UUID uuid)
    {
        if (!RLVEnabled)
        {
            return true;
        }
        return s.startsWith("/") || restrictions.isAllowed(RLVRestrictionType.recvchat, uuid.toString(), null);
    }

    public boolean canRecvIM(UUID uuid)
    {
        if (!RLVEnabled)
        {
            return true;
        }
        return restrictions.isAllowed(RLVRestrictionType.recvim, uuid.toString(), null);
    }

    public boolean canSendIM(UUID uuid)
    {
        if (!RLVEnabled)
        {
            return true;
        }
        return restrictions.isAllowed(RLVRestrictionType.sendim, uuid.toString(), null);
    }

    public boolean canShowInventory()
    {
        if (!RLVEnabled)
        {
            return true;
        }
        return restrictions.isAllowed(RLVRestrictionType.showinv, "", null);
    }

    public boolean canSit()
    {
        if (!RLVEnabled)
        {
            return true;
        }
        return restrictions.isAllowed(RLVRestrictionType.sit, "", null);
    }

    public boolean canStandUp()
    {
        if (!RLVEnabled)
        {
            return true;
        }
        return restrictions.isAllowed(RLVRestrictionType.unsit, "", null);
    }

    public boolean canTakeItemOff(SLWearableType slwearabletype)
    {
        if (!RLVEnabled)
        {
            return true;
        }
        return restrictions.isAllowed(RLVRestrictionType.remoutfit, slwearabletype.getName(), null);
    }

    public boolean canTeleportBySitting()
    {
        if (!RLVEnabled)
        {
            return true;
        }
        return restrictions.isAllowed(RLVRestrictionType.sittp, "", null);
    }

    public boolean canTeleportToLandmark()
    {
        if (!RLVEnabled)
        {
            return true;
        }
        return restrictions.isAllowed(RLVRestrictionType.tplm, "", null);
    }

    public boolean canTeleportToLocation()
    {
        if (!RLVEnabled)
        {
            return true;
        }
        return restrictions.isAllowed(RLVRestrictionType.tploc, "", null);
    }

    public boolean canTeleportToLure(UUID uuid)
    {
        if (!RLVEnabled)
        {
            return true;
        }
        return restrictions.isAllowed(RLVRestrictionType.tplure, uuid.toString(), null);
    }

    public boolean canViewNotecard()
    {
        if (!RLVEnabled)
        {
            return true;
        }
        return restrictions.isAllowed(RLVRestrictionType.viewnote, "", null);
    }

    public boolean canWearItem(SLWearableType slwearabletype)
    {
        if (!RLVEnabled)
        {
            return true;
        }
        return restrictions.isAllowed(RLVRestrictionType.addoutfit, slwearabletype.getName(), null);
    }

    public SLModules getModules()
    {
        return agentCircuit.getModules();
    }

    public RLVRestrictions getRestrictions()
    {
        return restrictions;
    }

    public boolean onIncomingChat(ChatFromSimulator chatfromsimulator)
    {
        String s;
        UUID uuid;
        if (chatfromsimulator.ChatData_Field.SourceType != 2 || chatfromsimulator.ChatData_Field.ChatType != 8)
        {
            break MISSING_BLOCK_LABEL_104;
        }
        s = SLMessage.stringFromVariableUTF(chatfromsimulator.ChatData_Field.Message);
        if (!s.startsWith("@"))
        {
            break MISSING_BLOCK_LABEL_104;
        }
        uuid = chatfromsimulator.ChatData_Field.SourceID;
        if (!RLVEnabled) goto _L2; else goto _L1
_L1:
        handleRLVCommands(uuid, s.substring(1));
_L4:
        return true;
_L2:
        if (RLVEnablingOffered) goto _L4; else goto _L3
_L3:
        RLVEnablingOffered = true;
        RLVEnablingUUID = uuid;
        RLVEnablingCommand = s.substring(1);
        offerRLVEnable(chatfromsimulator);
        return true;
        return false;
    }

    public boolean onIncomingIM(ImprovedInstantMessage improvedinstantmessage)
    {
        String s2;
        int i;
        if (!RLVEnabled)
        {
            return false;
        }
        i = improvedinstantmessage.MessageBlock_Field.Dialog;
        String s = SLMessage.stringFromVariableOEM(improvedinstantmessage.MessageBlock_Field.FromAgentName);
        s2 = SLMessage.stringFromVariableUTF(improvedinstantmessage.MessageBlock_Field.Message);
        Debug.Printf("IM: type %d from '%s' text '%s'", new Object[] {
            Integer.valueOf(i), s, s2
        });
        i;
        JVM INSTR tableswitch 0 0: default 88
    //                   0 90;
           goto _L1 _L2
_L1:
        return false;
_L2:
        if (s2.equalsIgnoreCase("@version"))
        {
            String s1 = RLVCmdVersion.getManualVersionReply();
            agentCircuit.SendInstantMessage(improvedinstantmessage.AgentData_Field.AgentID, s1);
            return true;
        }
        if (true) goto _L1; else goto _L3
_L3:
    }

    public boolean onSendLocalChat(int i, String s)
    {
        if (!RLVEnabled)
        {
            return true;
        }
        if (i == 0)
        {
            if (!s.startsWith("/"))
            {
                Object obj = restrictions.getTargetsForRestriction(RLVRestrictionType.redirchat);
                if (obj != null)
                {
                    for (obj = ((Iterable) (obj)).iterator(); ((Iterator) (obj)).hasNext();)
                    {
                        Object obj1 = (String)((Iterator) (obj)).next();
                        try
                        {
                            i = Integer.parseInt(((String) (obj1)));
                            obj1 = new ChatFromViewer();
                            ((ChatFromViewer) (obj1)).AgentData_Field.AgentID = circuitInfo.agentID;
                            ((ChatFromViewer) (obj1)).AgentData_Field.SessionID = circuitInfo.sessionID;
                            ((ChatFromViewer) (obj1)).ChatData_Field.Channel = i;
                            ((ChatFromViewer) (obj1)).ChatData_Field.Type = 1;
                            ((ChatFromViewer) (obj1)).ChatData_Field.Message = SLMessage.stringToVariableUTF(s);
                            obj1.isReliable = true;
                            SendMessage(((SLMessage) (obj1)));
                        }
                        catch (NumberFormatException numberformatexception)
                        {
                            Debug.Warning(numberformatexception);
                        }
                    }

                }
                if (!restrictions.isAllowed(RLVRestrictionType.sendchat, "", null))
                {
                    return false;
                }
            }
        } else
        if (!restrictions.isAllowed(RLVRestrictionType.sendchannel, Integer.toString(i), null))
        {
            return false;
        }
        return true;
    }

    public void sayOnChannel(int i, String s)
    {
        Debug.Printf("RLV reply (%d): '%s'", new Object[] {
            Integer.valueOf(i), s
        });
        ChatFromViewer chatfromviewer = new ChatFromViewer();
        chatfromviewer.AgentData_Field.AgentID = circuitInfo.agentID;
        chatfromviewer.AgentData_Field.SessionID = circuitInfo.sessionID;
        chatfromviewer.ChatData_Field.Channel = i;
        chatfromviewer.ChatData_Field.Type = 1;
        chatfromviewer.ChatData_Field.Message = SLMessage.stringToVariableUTF(s);
        chatfromviewer.isReliable = true;
        SendMessage(chatfromviewer);
    }

    public void teleportToGlobalPos(UUID uuid, LLVector3 llvector3)
    {
        if (!RLVEnabled)
        {
            return;
        }
        if (!restrictions.isAllowed(RLVRestrictionType.tploc, "", null, uuid))
        {
            return;
        } else
        {
            agentCircuit.TeleportToGlobalPosition(llvector3);
            return;
        }
    }
}
