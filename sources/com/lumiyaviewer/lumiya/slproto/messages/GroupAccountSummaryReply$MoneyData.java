// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            GroupAccountSummaryReply

public static class Q
{

    public int Balance;
    public int CurrentInterval;
    public int GroupTaxCurrent;
    public int GroupTaxEstimate;
    public int IntervalDays;
    public int LandTaxCurrent;
    public int LandTaxEstimate;
    public byte LastTaxDate[];
    public int LightTaxCurrent;
    public int LightTaxEstimate;
    public int NonExemptMembers;
    public int ObjectTaxCurrent;
    public int ObjectTaxEstimate;
    public int ParcelDirFeeCurrent;
    public int ParcelDirFeeEstimate;
    public UUID RequestID;
    public byte StartDate[];
    public byte TaxDate[];
    public int TotalCredits;
    public int TotalDebits;

    public Q()
    {
    }
}
