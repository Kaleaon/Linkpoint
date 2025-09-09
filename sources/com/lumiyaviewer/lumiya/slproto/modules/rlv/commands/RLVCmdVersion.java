package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommand;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommands;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import java.util.UUID;

public class RLVCmdVersion implements RLVCommand {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f128comlumiyaviewerlumiyaslprotomodulesrlvRLVCommandsSwitchesValues = null;
    private static final int RLV_VERSION_BUILD = 0;
    private static final int RLV_VERSION_MAJOR = 1;
    private static final int RLV_VERSION_MINOR = 10;
    private static final int RLV_VERSION_PATCH = 1;
    private static final int RLVa_VERSION_MAJOR = 1;
    private static final int RLVa_VERSION_MINOR = 10;
    private static final int RLVa_VERSION_PATCH = 1;

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues  reason: not valid java name */
    private static /* synthetic */ int[] m232getcomlumiyaviewerlumiyaslprotomodulesrlvRLVCommandsSwitchesValues() {
        if (f128comlumiyaviewerlumiyaslprotomodulesrlvRLVCommandsSwitchesValues != null) {
            return f128comlumiyaviewerlumiyaslprotomodulesrlvRLVCommandsSwitchesValues;
        }
        int[] iArr = new int[RLVCommands.values().length];
        try {
            iArr[RLVCommands.accepttp.ordinal()] = 4;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[RLVCommands.addoutfit.ordinal()] = 5;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[RLVCommands.clear.ordinal()] = 6;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[RLVCommands.detach.ordinal()] = 7;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[RLVCommands.edit.ordinal()] = 8;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[RLVCommands.getattach.ordinal()] = 9;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[RLVCommands.getoutfit.ordinal()] = 10;
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[RLVCommands.getstatus.ordinal()] = 11;
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[RLVCommands.recvchat.ordinal()] = 12;
        } catch (NoSuchFieldError e9) {
        }
        try {
            iArr[RLVCommands.recvim.ordinal()] = 13;
        } catch (NoSuchFieldError e10) {
        }
        try {
            iArr[RLVCommands.redirchat.ordinal()] = 14;
        } catch (NoSuchFieldError e11) {
        }
        try {
            iArr[RLVCommands.remoutfit.ordinal()] = 15;
        } catch (NoSuchFieldError e12) {
        }
        try {
            iArr[RLVCommands.rez.ordinal()] = 16;
        } catch (NoSuchFieldError e13) {
        }
        try {
            iArr[RLVCommands.sendchannel.ordinal()] = 17;
        } catch (NoSuchFieldError e14) {
        }
        try {
            iArr[RLVCommands.sendchat.ordinal()] = 18;
        } catch (NoSuchFieldError e15) {
        }
        try {
            iArr[RLVCommands.sendim.ordinal()] = 19;
        } catch (NoSuchFieldError e16) {
        }
        try {
            iArr[RLVCommands.showinv.ordinal()] = 20;
        } catch (NoSuchFieldError e17) {
        }
        try {
            iArr[RLVCommands.sit.ordinal()] = 21;
        } catch (NoSuchFieldError e18) {
        }
        try {
            iArr[RLVCommands.sittp.ordinal()] = 22;
        } catch (NoSuchFieldError e19) {
        }
        try {
            iArr[RLVCommands.tplm.ordinal()] = 23;
        } catch (NoSuchFieldError e20) {
        }
        try {
            iArr[RLVCommands.tploc.ordinal()] = 24;
        } catch (NoSuchFieldError e21) {
        }
        try {
            iArr[RLVCommands.tplure.ordinal()] = 25;
        } catch (NoSuchFieldError e22) {
        }
        try {
            iArr[RLVCommands.tpto.ordinal()] = 26;
        } catch (NoSuchFieldError e23) {
        }
        try {
            iArr[RLVCommands.unsit.ordinal()] = 27;
        } catch (NoSuchFieldError e24) {
        }
        try {
            iArr[RLVCommands.version.ordinal()] = 1;
        } catch (NoSuchFieldError e25) {
        }
        try {
            iArr[RLVCommands.versionnew.ordinal()] = 2;
        } catch (NoSuchFieldError e26) {
        }
        try {
            iArr[RLVCommands.versionnum.ordinal()] = 3;
        } catch (NoSuchFieldError e27) {
        }
        try {
            iArr[RLVCommands.viewnote.ordinal()] = 28;
        } catch (NoSuchFieldError e28) {
        }
        f128comlumiyaviewerlumiyaslprotomodulesrlvRLVCommandsSwitchesValues = iArr;
        return iArr;
    }

    public static String getManualVersionReply() {
        return String.format("RestrainedLove viewer v%d.%d.%d", new Object[]{1, 10, 1});
    }

    public void Handle(RLVController rLVController, UUID uuid, RLVCommands rLVCommands, String str, String str2) {
        try {
            int parseInt = Integer.parseInt(str);
            switch (m232getcomlumiyaviewerlumiyaslprotomodulesrlvRLVCommandsSwitchesValues()[rLVCommands.ordinal()]) {
                case 1:
                case 2:
                    Object[] objArr = new Object[7];
                    objArr[0] = rLVCommands == RLVCommands.versionnew ? "RestrainedLove" : "RestrainedLife";
                    objArr[1] = 1;
                    objArr[2] = 10;
                    objArr[3] = 1;
                    objArr[4] = 1;
                    objArr[5] = 10;
                    objArr[6] = 1;
                    rLVController.sayOnChannel(parseInt, String.format("%s viewer v%d.%d.%d (RLVa %d.%d.%d)", objArr));
                    return;
                case 3:
                    rLVController.sayOnChannel(parseInt, String.format("%d%02d%02d%02d", new Object[]{1, 10, 1, 0}));
                    return;
                default:
                    return;
            }
        } catch (NumberFormatException e) {
            Debug.Warning(e);
        }
        Debug.Warning(e);
    }
}
