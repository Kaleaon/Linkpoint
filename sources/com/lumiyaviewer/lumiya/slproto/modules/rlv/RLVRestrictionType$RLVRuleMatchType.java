// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.rlv;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.rlv:
//            RLVRestrictionType

public static final class  extends Enum
{

    private static final TargetSpecifiesAllowance $VALUES[];
    public static final TargetSpecifiesAllowance TargetNoExceptions;
    public static final TargetSpecifiesAllowance TargetSpecifiesAllowance;
    public static final TargetSpecifiesAllowance TargetSpecifiesException;
    public static final TargetSpecifiesAllowance TargetSpecifiesRestriction;

    public static  valueOf(String s)
    {
        return ()Enum.valueOf(com/lumiyaviewer/lumiya/slproto/modules/rlv/RLVRestrictionType$RLVRuleMatchType, s);
    }

    public static [] values()
    {
        return $VALUES;
    }

    static 
    {
        TargetSpecifiesException = new <init>("TargetSpecifiesException", 0);
        TargetSpecifiesRestriction = new <init>("TargetSpecifiesRestriction", 1);
        TargetNoExceptions = new <init>("TargetNoExceptions", 2);
        TargetSpecifiesAllowance = new <init>("TargetSpecifiesAllowance", 3);
        $VALUES = (new .VALUES[] {
            TargetSpecifiesException, TargetSpecifiesRestriction, TargetNoExceptions, TargetSpecifiesAllowance
        });
    }

    private (String s, int i)
    {
        super(s, i);
    }
}
