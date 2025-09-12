// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import de.greenrobot.dao.Property;
import java.util.Date;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.dao:
//            MoneyTransactionDao

public static class 
{

    public static final Property AgentUUID = new Property(2, java/util/UUID, "agentUUID", false, "AGENT_UUID");
    public static final Property Id = new Property(0, java/lang/Long, "id", true, "_id");
    public static final Property NewBalance;
    public static final Property Timestamp = new Property(1, java/util/Date, "timestamp", false, "TIMESTAMP");
    public static final Property TransactionAmount;

    static 
    {
        TransactionAmount = new Property(3, Integer.TYPE, "transactionAmount", false, "TRANSACTION_AMOUNT");
        NewBalance = new Property(4, Integer.TYPE, "newBalance", false, "NEW_BALANCE");
    }

    public ()
    {
    }
}
