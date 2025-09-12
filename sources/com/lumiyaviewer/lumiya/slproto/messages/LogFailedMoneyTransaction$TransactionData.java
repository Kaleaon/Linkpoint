// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.net.Inet4Address;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            LogFailedMoneyTransaction

public static class 
{

    public int Amount;
    public UUID DestID;
    public int FailureType;
    public int Flags;
    public int GridX;
    public int GridY;
    public Inet4Address SimulatorIP;
    public UUID SourceID;
    public UUID TransactionID;
    public int TransactionTime;
    public int TransactionType;

    public ()
    {
    }
}
