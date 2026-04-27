// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommand;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommands;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import java.util.UUID;

public class RLVCmdVersion
    implements RLVCommand
{

    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_rlv_2D_RLVCommandsSwitchesValues[];
    private static final int RLV_VERSION_BUILD = 0;
    private static final int RLV_VERSION_MAJOR = 1;
    private static final int RLV_VERSION_MINOR = 10;
    private static final int RLV_VERSION_PATCH = 1;
    private static final int RLVa_VERSION_MAJOR = 1;
    private static final int RLVa_VERSION_MINOR = 10;
    private static final int RLVa_VERSION_PATCH = 1;

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_rlv_2D_RLVCommandsSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_rlv_2D_RLVCommandsSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_rlv_2D_RLVCommandsSwitchesValues;
        }
        int ai[] = new int[RLVCommands.values().length];
        try
        {
            ai[RLVCommands.accepttp.ordinal()] = 4;
        }
        catch (NoSuchFieldError nosuchfielderror27) { }
        try
        {
            ai[RLVCommands.addoutfit.ordinal()] = 5;
        }
        catch (NoSuchFieldError nosuchfielderror26) { }
        try
        {
            ai[RLVCommands.clear.ordinal()] = 6;
        }
        catch (NoSuchFieldError nosuchfielderror25) { }
        try
        {
            ai[RLVCommands.detach.ordinal()] = 7;
        }
        catch (NoSuchFieldError nosuchfielderror24) { }
        try
        {
            ai[RLVCommands.edit.ordinal()] = 8;
        }
        catch (NoSuchFieldError nosuchfielderror23) { }
        try
        {
            ai[RLVCommands.getattach.ordinal()] = 9;
        }
        catch (NoSuchFieldError nosuchfielderror22) { }
        try
        {
            ai[RLVCommands.getoutfit.ordinal()] = 10;
        }
        catch (NoSuchFieldError nosuchfielderror21) { }
        try
        {
            ai[RLVCommands.getstatus.ordinal()] = 11;
        }
        catch (NoSuchFieldError nosuchfielderror20) { }
        try
        {
            ai[RLVCommands.recvchat.ordinal()] = 12;
        }
        catch (NoSuchFieldError nosuchfielderror19) { }
        try
        {
            ai[RLVCommands.recvim.ordinal()] = 13;
        }
        catch (NoSuchFieldError nosuchfielderror18) { }
        try
        {
            ai[RLVCommands.redirchat.ordinal()] = 14;
        }
        catch (NoSuchFieldError nosuchfielderror17) { }
        try
        {
            ai[RLVCommands.remoutfit.ordinal()] = 15;
        }
        catch (NoSuchFieldError nosuchfielderror16) { }
        try
        {
            ai[RLVCommands.rez.ordinal()] = 16;
        }
        catch (NoSuchFieldError nosuchfielderror15) { }
        try
        {
            ai[RLVCommands.sendchannel.ordinal()] = 17;
        }
        catch (NoSuchFieldError nosuchfielderror14) { }
        try
        {
            ai[RLVCommands.sendchat.ordinal()] = 18;
        }
        catch (NoSuchFieldError nosuchfielderror13) { }
        try
        {
            ai[RLVCommands.sendim.ordinal()] = 19;
        }
        catch (NoSuchFieldError nosuchfielderror12) { }
        try
        {
            ai[RLVCommands.showinv.ordinal()] = 20;
        }
        catch (NoSuchFieldError nosuchfielderror11) { }
        try
        {
            ai[RLVCommands.sit.ordinal()] = 21;
        }
        catch (NoSuchFieldError nosuchfielderror10) { }
        try
        {
            ai[RLVCommands.sittp.ordinal()] = 22;
        }
        catch (NoSuchFieldError nosuchfielderror9) { }
        try
        {
            ai[RLVCommands.tplm.ordinal()] = 23;
        }
        catch (NoSuchFieldError nosuchfielderror8) { }
        try
        {
            ai[RLVCommands.tploc.ordinal()] = 24;
        }
        catch (NoSuchFieldError nosuchfielderror7) { }
        try
        {
            ai[RLVCommands.tplure.ordinal()] = 25;
        }
        catch (NoSuchFieldError nosuchfielderror6) { }
        try
        {
            ai[RLVCommands.tpto.ordinal()] = 26;
        }
        catch (NoSuchFieldError nosuchfielderror5) { }
        try
        {
            ai[RLVCommands.unsit.ordinal()] = 27;
        }
        catch (NoSuchFieldError nosuchfielderror4) { }
        try
        {
            ai[RLVCommands.version.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror3) { }
        try
        {
            ai[RLVCommands.versionnew.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[RLVCommands.versionnum.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[RLVCommands.viewnote.ordinal()] = 28;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_rlv_2D_RLVCommandsSwitchesValues = ai;
        return ai;
    }

    public RLVCmdVersion()
    {
    }

    public static String getManualVersionReply()
    {
        return String.format("RestrainedLove viewer v%d.%d.%d", new Object[] {
            Integer.valueOf(1), Integer.valueOf(10), Integer.valueOf(1)
        });
    }

    public void Handle(RLVController rlvcontroller, UUID uuid, RLVCommands rlvcommands, String s, String s1)
    {
        int i = Integer.parseInt(s);
        _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_rlv_2D_RLVCommandsSwitchesValues()[rlvcommands.ordinal()];
        JVM INSTR tableswitch 1 3: default 173
    //                   1 40
    //                   2 40
    //                   3 128;
           goto _L1 _L2 _L2 _L3
_L2:
        if (rlvcommands == RLVCommands.versionnew)
        {
            uuid = "RestrainedLove";
        } else
        {
            uuid = "RestrainedLife";
        }
        try
        {
            rlvcontroller.sayOnChannel(i, String.format("%s viewer v%d.%d.%d (RLVa %d.%d.%d)", new Object[] {
                uuid, Integer.valueOf(1), Integer.valueOf(10), Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(10), Integer.valueOf(1)
            }));
            return;
        }
        // Misplaced declaration of an exception variable
        catch (RLVController rlvcontroller)
        {
            Debug.Warning(rlvcontroller);
        }
        return;
_L3:
        rlvcontroller.sayOnChannel(i, String.format("%d%02d%02d%02d", new Object[] {
            Integer.valueOf(1), Integer.valueOf(10), Integer.valueOf(1), Integer.valueOf(0)
        }));
        return;
_L1:
    }
}
