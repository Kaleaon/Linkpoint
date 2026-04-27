// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.rlv;


public final class RLVRestrictionType extends Enum
{
    public static final class RLVRuleMatchType extends Enum
    {

        private static final RLVRuleMatchType $VALUES[];
        public static final RLVRuleMatchType TargetNoExceptions;
        public static final RLVRuleMatchType TargetSpecifiesAllowance;
        public static final RLVRuleMatchType TargetSpecifiesException;
        public static final RLVRuleMatchType TargetSpecifiesRestriction;

        public static RLVRuleMatchType valueOf(String s)
        {
            return (RLVRuleMatchType)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/modules/rlv/RLVRestrictionType$RLVRuleMatchType, s);
        }

        public static RLVRuleMatchType[] values()
        {
            return $VALUES;
        }

        static 
        {
            TargetSpecifiesException = new RLVRuleMatchType("TargetSpecifiesException", 0);
            TargetSpecifiesRestriction = new RLVRuleMatchType("TargetSpecifiesRestriction", 1);
            TargetNoExceptions = new RLVRuleMatchType("TargetNoExceptions", 2);
            TargetSpecifiesAllowance = new RLVRuleMatchType("TargetSpecifiesAllowance", 3);
            $VALUES = (new RLVRuleMatchType[] {
                TargetSpecifiesException, TargetSpecifiesRestriction, TargetNoExceptions, TargetSpecifiesAllowance
            });
        }

        private RLVRuleMatchType(String s, int i)
        {
            super(s, i);
        }
    }


    private static final RLVRestrictionType $VALUES[];
    public static final RLVRestrictionType accepttp;
    public static final RLVRestrictionType addoutfit;
    public static final RLVRestrictionType detach;
    public static final RLVRestrictionType edit;
    public static final RLVRestrictionType recvchat;
    public static final RLVRestrictionType recvim;
    public static final RLVRestrictionType redirchat;
    public static final RLVRestrictionType remoutfit;
    public static final RLVRestrictionType rez;
    public static final RLVRestrictionType sendchannel;
    public static final RLVRestrictionType sendchat;
    public static final RLVRestrictionType sendim;
    public static final RLVRestrictionType showinv;
    public static final RLVRestrictionType sit;
    public static final RLVRestrictionType sittp;
    public static final RLVRestrictionType tplm;
    public static final RLVRestrictionType tploc;
    public static final RLVRestrictionType tplure;
    public static final RLVRestrictionType unsit;
    public static final RLVRestrictionType viewnote;
    private RLVRuleMatchType ruleMatchType;

    private RLVRestrictionType(String s, int i, RLVRuleMatchType rlvrulematchtype)
    {
        super(s, i);
        ruleMatchType = rlvrulematchtype;
    }

    public static RLVRestrictionType valueOf(String s)
    {
        return (RLVRestrictionType)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/modules/rlv/RLVRestrictionType, s);
    }

    public static RLVRestrictionType[] values()
    {
        return $VALUES;
    }

    public RLVRuleMatchType getRuleMatchType()
    {
        return ruleMatchType;
    }

    static 
    {
        detach = new RLVRestrictionType("detach", 0, RLVRuleMatchType.TargetSpecifiesRestriction);
        sendchat = new RLVRestrictionType("sendchat", 1, RLVRuleMatchType.TargetNoExceptions);
        recvchat = new RLVRestrictionType("recvchat", 2, RLVRuleMatchType.TargetSpecifiesException);
        sendim = new RLVRestrictionType("sendim", 3, RLVRuleMatchType.TargetSpecifiesException);
        recvim = new RLVRestrictionType("recvim", 4, RLVRuleMatchType.TargetSpecifiesException);
        tplm = new RLVRestrictionType("tplm", 5, RLVRuleMatchType.TargetNoExceptions);
        tploc = new RLVRestrictionType("tploc", 6, RLVRuleMatchType.TargetNoExceptions);
        sittp = new RLVRestrictionType("sittp", 7, RLVRuleMatchType.TargetNoExceptions);
        tplure = new RLVRestrictionType("tplure", 8, RLVRuleMatchType.TargetSpecifiesException);
        accepttp = new RLVRestrictionType("accepttp", 9, RLVRuleMatchType.TargetSpecifiesAllowance);
        showinv = new RLVRestrictionType("showinv", 10, RLVRuleMatchType.TargetNoExceptions);
        viewnote = new RLVRestrictionType("viewnote", 11, RLVRuleMatchType.TargetNoExceptions);
        edit = new RLVRestrictionType("edit", 12, RLVRuleMatchType.TargetSpecifiesException);
        rez = new RLVRestrictionType("rez", 13, RLVRuleMatchType.TargetNoExceptions);
        unsit = new RLVRestrictionType("unsit", 14, RLVRuleMatchType.TargetNoExceptions);
        sit = new RLVRestrictionType("sit", 15, RLVRuleMatchType.TargetNoExceptions);
        remoutfit = new RLVRestrictionType("remoutfit", 16, RLVRuleMatchType.TargetSpecifiesRestriction);
        addoutfit = new RLVRestrictionType("addoutfit", 17, RLVRuleMatchType.TargetSpecifiesRestriction);
        redirchat = new RLVRestrictionType("redirchat", 18, RLVRuleMatchType.TargetSpecifiesRestriction);
        sendchannel = new RLVRestrictionType("sendchannel", 19, RLVRuleMatchType.TargetSpecifiesException);
        $VALUES = (new RLVRestrictionType[] {
            detach, sendchat, recvchat, sendim, recvim, tplm, tploc, sittp, tplure, accepttp, 
            showinv, viewnote, edit, rez, unsit, sit, remoutfit, addoutfit, redirchat, sendchannel
        });
    }
}
