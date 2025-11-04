// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.Debug;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommands;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommand;

public class RLVCmdVersion implements RLVCommand
{
    private static final int RLV_VERSION_BUILD = 0;
    private static final int RLV_VERSION_MAJOR = 1;
    private static final int RLV_VERSION_MINOR = 10;
    private static final int RLV_VERSION_PATCH = 1;
    private static final int RLVa_VERSION_MAJOR = 1;
    private static final int RLVa_VERSION_MINOR = 10;
    private static final int RLVa_VERSION_PATCH = 1;
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues() {
        if (RLVCmdVersion.-com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues != null) {
            return RLVCmdVersion.-com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues = new int[RLVCommands.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.accepttp.ordinal()] = 4;
                try {
                    -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.addoutfit.ordinal()] = 5;
                    try {
                        -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.clear.ordinal()] = 6;
                        try {
                            -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.detach.ordinal()] = 7;
                            try {
                                -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.edit.ordinal()] = 8;
                                try {
                                    -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.getattach.ordinal()] = 9;
                                    try {
                                        -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.getoutfit.ordinal()] = 10;
                                        try {
                                            -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.getstatus.ordinal()] = 11;
                                            try {
                                                -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.recvchat.ordinal()] = 12;
                                                try {
                                                    -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.recvim.ordinal()] = 13;
                                                    try {
                                                        -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.redirchat.ordinal()] = 14;
                                                        try {
                                                            -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.remoutfit.ordinal()] = 15;
                                                            try {
                                                                -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.rez.ordinal()] = 16;
                                                                try {
                                                                    -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.sendchannel.ordinal()] = 17;
                                                                    try {
                                                                        -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.sendchat.ordinal()] = 18;
                                                                        try {
                                                                            -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.sendim.ordinal()] = 19;
                                                                            try {
                                                                                -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.showinv.ordinal()] = 20;
                                                                                try {
                                                                                    -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.sit.ordinal()] = 21;
                                                                                    try {
                                                                                        -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.sittp.ordinal()] = 22;
                                                                                        try {
                                                                                            -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.tplm.ordinal()] = 23;
                                                                                            try {
                                                                                                -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.tploc.ordinal()] = 24;
                                                                                                try {
                                                                                                    -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.tplure.ordinal()] = 25;
                                                                                                    try {
                                                                                                        -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.tpto.ordinal()] = 26;
                                                                                                        try {
                                                                                                            -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.unsit.ordinal()] = 27;
                                                                                                            try {
                                                                                                                -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.version.ordinal()] = 1;
                                                                                                                try {
                                                                                                                    -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.versionnew.ordinal()] = 2;
                                                                                                                    try {
                                                                                                                        -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.versionnum.ordinal()] = 3;
                                                                                                                        try {
                                                                                                                            -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues[RLVCommands.viewnote.ordinal()] = 28;
                                                                                                                            return -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues = -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues;
                                                                                                                        }
                                                                                                                        catch (final NoSuchFieldError noSuchFieldError) {}
                                                                                                                    }
                                                                                                                    catch (final NoSuchFieldError noSuchFieldError2) {}
                                                                                                                }
                                                                                                                catch (final NoSuchFieldError noSuchFieldError3) {}
                                                                                                            }
                                                                                                            catch (final NoSuchFieldError noSuchFieldError4) {}
                                                                                                        }
                                                                                                        catch (final NoSuchFieldError noSuchFieldError5) {}
                                                                                                    }
                                                                                                    catch (final NoSuchFieldError noSuchFieldError6) {}
                                                                                                }
                                                                                                catch (final NoSuchFieldError noSuchFieldError7) {}
                                                                                            }
                                                                                            catch (final NoSuchFieldError noSuchFieldError8) {}
                                                                                        }
                                                                                        catch (final NoSuchFieldError noSuchFieldError9) {}
                                                                                    }
                                                                                    catch (final NoSuchFieldError noSuchFieldError10) {}
                                                                                }
                                                                                catch (final NoSuchFieldError noSuchFieldError11) {}
                                                                            }
                                                                            catch (final NoSuchFieldError noSuchFieldError12) {}
                                                                        }
                                                                        catch (final NoSuchFieldError noSuchFieldError13) {}
                                                                    }
                                                                    catch (final NoSuchFieldError noSuchFieldError14) {}
                                                                }
                                                                catch (final NoSuchFieldError noSuchFieldError15) {}
                                                            }
                                                            catch (final NoSuchFieldError noSuchFieldError16) {}
                                                        }
                                                        catch (final NoSuchFieldError noSuchFieldError17) {}
                                                    }
                                                    catch (final NoSuchFieldError noSuchFieldError18) {}
                                                }
                                                catch (final NoSuchFieldError noSuchFieldError19) {}
                                            }
                                            catch (final NoSuchFieldError noSuchFieldError20) {}
                                        }
                                        catch (final NoSuchFieldError noSuchFieldError21) {}
                                    }
                                    catch (final NoSuchFieldError noSuchFieldError22) {}
                                }
                                catch (final NoSuchFieldError noSuchFieldError23) {}
                            }
                            catch (final NoSuchFieldError noSuchFieldError24) {}
                        }
                        catch (final NoSuchFieldError noSuchFieldError25) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError26) {}
                }
                catch (final NoSuchFieldError noSuchFieldError27) {}
            }
            catch (final NoSuchFieldError noSuchFieldError28) {
                continue;
            }
            break;
        }
    }
    
    public static String getManualVersionReply() {
        return String.format("RestrainedLove viewer v%d.%d.%d", 1, 10, 1);
    }
    
    @Override
    public void Handle(final RLVController rlvController, final UUID uuid, final RLVCommands rlvCommands, final String s, final String s2) {
        while (true) {
            int int1 = 0;
        Label_0133:
            while (true) {
                Label_0127: {
                    try {
                        int1 = Integer.parseInt(s);
                        switch (-getcom-lumiyaviewer-lumiya-slproto-modules-rlv-RLVCommandsSwitchesValues()[rlvCommands.ordinal()]) {
                            case 1:
                            case 2: {
                                if (rlvCommands == RLVCommands.versionnew) {
                                    final String s3 = "RestrainedLove";
                                    rlvController.sayOnChannel(int1, String.format("%s viewer v%d.%d.%d (RLVa %d.%d.%d)", s3, 1, 10, 1, 1, 10, 1));
                                    break;
                                }
                                break Label_0127;
                            }
                            case 3: {
                                break Label_0133;
                            }
                        }
                        return;
                    }
                    catch (final NumberFormatException ex) {
                        Debug.Warning(ex);
                        return;
                    }
                }
                final String s3 = "RestrainedLife";
                continue;
            }
            rlvController.sayOnChannel(int1, String.format("%d%02d%02d%02d", 1, 10, 1, 0));
        }
    }
}
