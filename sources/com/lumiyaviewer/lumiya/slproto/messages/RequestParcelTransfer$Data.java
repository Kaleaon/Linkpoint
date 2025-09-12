// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            RequestParcelTransfer

public static class I
{

    public int ActualArea;
    public int Amount;
    public int BillableArea;
    public UUID DestID;
    public boolean Final;
    public int Flags;
    public UUID OwnerID;
    public UUID SourceID;
    public UUID TransactionID;
    public int TransactionTime;
    public int TransactionType;

    public I()
    {
    }
}
