// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.rlv;

import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdAcceptTeleport;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdAddOutfit;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdClear;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdDetach;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdEditObjects;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdGetAttach;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdGetOutfit;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdGetStatus;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdRecvChat;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdRecvIM;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdRedirChat;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdRemoveOutfit;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdRezObjects;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdSendChannel;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdSendChat;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdSendIM;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdShowInventory;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdSit;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdTeleportLandmark;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdTeleportLocation;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdTeleportLure;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdTeleportSit;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdTeleportTo;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdUnsit;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdVersion;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdViewNotecard;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.rlv:
//            RLVCommand

public final class RLVCommands extends Enum
{

    private static final RLVCommands $VALUES[];
    public static final RLVCommands accepttp;
    public static final RLVCommands addoutfit;
    public static final RLVCommands clear;
    public static final RLVCommands detach;
    public static final RLVCommands edit;
    public static final RLVCommands getattach;
    public static final RLVCommands getoutfit;
    public static final RLVCommands getstatus;
    public static final RLVCommands recvchat;
    public static final RLVCommands recvim;
    public static final RLVCommands redirchat;
    public static final RLVCommands remoutfit;
    public static final RLVCommands rez;
    public static final RLVCommands sendchannel;
    public static final RLVCommands sendchat;
    public static final RLVCommands sendim;
    public static final RLVCommands showinv;
    public static final RLVCommands sit;
    public static final RLVCommands sittp;
    public static final RLVCommands tplm;
    public static final RLVCommands tploc;
    public static final RLVCommands tplure;
    public static final RLVCommands tpto;
    public static final RLVCommands unsit;
    public static final RLVCommands version;
    public static final RLVCommands versionnew;
    public static final RLVCommands versionnum;
    public static final RLVCommands viewnote;
    private Class handler;

    private RLVCommands(String s, int i, Class class1)
    {
        super(s, i);
        handler = class1;
    }

    public static RLVCommands getCommand(String s)
    {
        try
        {
            s = valueOf(s);
        }
        // Misplaced declaration of an exception variable
        catch (String s)
        {
            return null;
        }
        return s;
    }

    public static RLVCommands valueOf(String s)
    {
        return (RLVCommands)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/modules/rlv/RLVCommands, s);
    }

    public static RLVCommands[] values()
    {
        return $VALUES;
    }

    public RLVCommand getHandler()
    {
        RLVCommand rlvcommand;
        try
        {
            rlvcommand = (RLVCommand)handler.newInstance();
        }
        catch (IllegalArgumentException illegalargumentexception)
        {
            return null;
        }
        catch (InstantiationException instantiationexception)
        {
            return null;
        }
        catch (IllegalAccessException illegalaccessexception)
        {
            return null;
        }
        return rlvcommand;
    }

    static 
    {
        version = new RLVCommands("version", 0, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdVersion);
        versionnew = new RLVCommands("versionnew", 1, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdVersion);
        versionnum = new RLVCommands("versionnum", 2, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdVersion);
        clear = new RLVCommands("clear", 3, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdClear);
        detach = new RLVCommands("detach", 4, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdDetach);
        sendchat = new RLVCommands("sendchat", 5, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdSendChat);
        recvchat = new RLVCommands("recvchat", 6, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdRecvChat);
        sendim = new RLVCommands("sendim", 7, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdSendIM);
        recvim = new RLVCommands("recvim", 8, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdRecvIM);
        tplm = new RLVCommands("tplm", 9, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdTeleportLandmark);
        tploc = new RLVCommands("tploc", 10, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdTeleportLocation);
        sittp = new RLVCommands("sittp", 11, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdTeleportSit);
        tplure = new RLVCommands("tplure", 12, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdTeleportLure);
        tpto = new RLVCommands("tpto", 13, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdTeleportTo);
        accepttp = new RLVCommands("accepttp", 14, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdAcceptTeleport);
        showinv = new RLVCommands("showinv", 15, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdShowInventory);
        viewnote = new RLVCommands("viewnote", 16, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdViewNotecard);
        edit = new RLVCommands("edit", 17, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdEditObjects);
        rez = new RLVCommands("rez", 18, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdRezObjects);
        unsit = new RLVCommands("unsit", 19, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdUnsit);
        sit = new RLVCommands("sit", 20, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdSit);
        remoutfit = new RLVCommands("remoutfit", 21, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdRemoveOutfit);
        getoutfit = new RLVCommands("getoutfit", 22, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdGetOutfit);
        addoutfit = new RLVCommands("addoutfit", 23, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdAddOutfit);
        getattach = new RLVCommands("getattach", 24, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdGetAttach);
        getstatus = new RLVCommands("getstatus", 25, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdGetStatus);
        sendchannel = new RLVCommands("sendchannel", 26, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdSendChannel);
        redirchat = new RLVCommands("redirchat", 27, com/lumiyaviewer/lumiya/slproto/modules/rlv/commands/RLVCmdRedirChat);
        $VALUES = (new RLVCommands[] {
            version, versionnew, versionnum, clear, detach, sendchat, recvchat, sendim, recvim, tplm, 
            tploc, sittp, tplure, tpto, accepttp, showinv, viewnote, edit, rez, unsit, 
            sit, remoutfit, getoutfit, addoutfit, getattach, getstatus, sendchannel, redirchat
        });
    }
}
