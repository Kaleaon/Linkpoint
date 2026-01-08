package com.linkpoint.slproto.modules.rlv

import com.linkpoint.slproto.modules.rlv.commands.RLVCmdAcceptTeleport
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdAddOutfit
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdClear
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdDetach
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdEditObjects
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdGetAttach
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdGetOutfit
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdGetStatus
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdRecvChat
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdRecvIM
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdRedirChat
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdRemoveOutfit
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdRezObjects
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdSendChannel
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdSendChat
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdSendIM
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdShowInventory
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdSit
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdTeleportLandmark
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdTeleportLocation
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdTeleportLure
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdTeleportSit
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdTeleportTo
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdUnsit
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdVersion
import com.linkpoint.slproto.modules.rlv.commands.RLVCmdViewNotecard

enum RLVCommands {
    version(RLVCmdVersion.class),
    versionnew(RLVCmdVersion.class),
    versionnum(RLVCmdVersion.class),
    clear(RLVCmdClear.class),
    detach(RLVCmdDetach.class),
    sendchat(RLVCmdSendChat.class),
    recvchat(RLVCmdRecvChat.class),
    sendim(RLVCmdSendIM.class),
    recvim(RLVCmdRecvIM.class),
    tplm(RLVCmdTeleportLandmark.class),
    tploc(RLVCmdTeleportLocation.class),
    sittp(RLVCmdTeleportSit.class),
    tplure(RLVCmdTeleportLure.class),
    tpto(RLVCmdTeleportTo.class),
    accepttp(RLVCmdAcceptTeleport.class),
    showinv(RLVCmdShowInventory.class),
    viewnote(RLVCmdViewNotecard.class),
    edit(RLVCmdEditObjects.class),
    rez(RLVCmdRezObjects.class),
    unsit(RLVCmdUnsit.class),
    sit(RLVCmdSit.class),
    remoutfit(RLVCmdRemoveOutfit.class),
    getoutfit(RLVCmdGetOutfit.class),
    addoutfit(RLVCmdAddOutfit.class),
    getattach(RLVCmdGetAttach.class),
    getstatus(RLVCmdGetStatus.class),
    sendchannel(RLVCmdSendChannel.class),
    redirchat(RLVCmdRedirChat.class)
    
    private Class<? : RLVCommand> handler

    private RLVCommands(Class<? : RLVCommand> cls) {
        this.handler = cls
    }

    fun getCommand(String str): RLVCommands {
        try {
            return valueOf(str)
        } catch (IllegalArgumentException e) {
            return null
        }
    }

    fun getHandler(): RLVCommand {
        try {
            return (this as RLVCommand).handler.newInstance()
        } catch (IllegalArgumentException e) {
            return null
        } catch (InstantiationException e2) {
            return null
        } catch (IllegalAccessException e3) {
            return null
        }
    }
}
