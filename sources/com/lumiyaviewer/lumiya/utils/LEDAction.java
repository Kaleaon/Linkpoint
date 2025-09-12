// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils;


public final class LEDAction extends Enum
{

    private static final LEDAction $VALUES[];
    public static final LEDAction Always;
    public static final LEDAction Fast;
    public static final LEDAction None;
    public static final LEDAction Slow;
    private String preferenceValue;

    private LEDAction(String s, int i, String s1)
    {
        super(s, i);
        preferenceValue = s1;
    }

    public static LEDAction getByPreferenceString(String s)
    {
        for (int i = 0; i < values().length; i++)
        {
            if (values()[i].preferenceValue.equals(s))
            {
                return values()[i];
            }
        }

        return None;
    }

    public static LEDAction valueOf(String s)
    {
        return (LEDAction)Enum.valueOf(com/lumiyaviewer/lumiya/utils/LEDAction, s);
    }

    public static LEDAction[] values()
    {
        return $VALUES;
    }

    static 
    {
        None = new LEDAction("None", 0, "none");
        Slow = new LEDAction("Slow", 1, "slow");
        Fast = new LEDAction("Fast", 2, "fast");
        Always = new LEDAction("Always", 3, "always");
        $VALUES = (new LEDAction[] {
            None, Slow, Fast, Always
        });
    }
}
