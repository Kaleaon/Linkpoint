// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.rlv;

import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdRedirChat;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdSendChannel;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdGetStatus;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdGetAttach;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdAddOutfit;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdGetOutfit;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdRemoveOutfit;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdSit;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdUnsit;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdRezObjects;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdEditObjects;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdViewNotecard;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdShowInventory;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdAcceptTeleport;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdTeleportTo;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdTeleportLure;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdTeleportSit;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdTeleportLocation;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdTeleportLandmark;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdRecvIM;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdSendIM;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdRecvChat;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdSendChat;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdDetach;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdClear;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdVersion;

public enum RLVCommands
{
    accepttp((Class<? extends RLVCommand>)RLVCmdAcceptTeleport.class), 
    addoutfit((Class<? extends RLVCommand>)RLVCmdAddOutfit.class), 
    clear((Class<? extends RLVCommand>)RLVCmdClear.class), 
    detach((Class<? extends RLVCommand>)RLVCmdDetach.class), 
    edit((Class<? extends RLVCommand>)RLVCmdEditObjects.class), 
    getattach((Class<? extends RLVCommand>)RLVCmdGetAttach.class), 
    getoutfit((Class<? extends RLVCommand>)RLVCmdGetOutfit.class), 
    getstatus((Class<? extends RLVCommand>)RLVCmdGetStatus.class), 
    recvchat((Class<? extends RLVCommand>)RLVCmdRecvChat.class), 
    recvim((Class<? extends RLVCommand>)RLVCmdRecvIM.class), 
    redirchat((Class<? extends RLVCommand>)RLVCmdRedirChat.class), 
    remoutfit((Class<? extends RLVCommand>)RLVCmdRemoveOutfit.class), 
    rez((Class<? extends RLVCommand>)RLVCmdRezObjects.class), 
    sendchannel((Class<? extends RLVCommand>)RLVCmdSendChannel.class), 
    sendchat((Class<? extends RLVCommand>)RLVCmdSendChat.class), 
    sendim((Class<? extends RLVCommand>)RLVCmdSendIM.class), 
    showinv((Class<? extends RLVCommand>)RLVCmdShowInventory.class), 
    sit((Class<? extends RLVCommand>)RLVCmdSit.class), 
    sittp((Class<? extends RLVCommand>)RLVCmdTeleportSit.class), 
    tplm((Class<? extends RLVCommand>)RLVCmdTeleportLandmark.class), 
    tploc((Class<? extends RLVCommand>)RLVCmdTeleportLocation.class), 
    tplure((Class<? extends RLVCommand>)RLVCmdTeleportLure.class), 
    tpto((Class<? extends RLVCommand>)RLVCmdTeleportTo.class), 
    unsit((Class<? extends RLVCommand>)RLVCmdUnsit.class), 
    version((Class<? extends RLVCommand>)RLVCmdVersion.class), 
    versionnew((Class<? extends RLVCommand>)RLVCmdVersion.class), 
    versionnum((Class<? extends RLVCommand>)RLVCmdVersion.class), 
    viewnote((Class<? extends RLVCommand>)RLVCmdViewNotecard.class);
    
    private Class<? extends RLVCommand> handler;
    
    private RLVCommands(final Class<? extends RLVCommand> handler) {
        this.handler = handler;
    }
    
    public static RLVCommands getCommand(final String s) {
        try {
            return valueOf(s);
        }
        catch (final IllegalArgumentException ex) {
            return null;
        }
    }
    
    public RLVCommand getHandler() {
        try {
            return (RLVCommand)this.handler.newInstance();
        }
        catch (final IllegalAccessException ex) {
            return null;
        }
        catch (final InstantiationException ex2) {
            return null;
        }
        catch (final IllegalArgumentException ex3) {
            return null;
        }
    }
}
