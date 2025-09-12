// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.orm;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

// Referenced classes of package com.lumiyaviewer.lumiya.orm:
//            DBHandleCache

public static interface 
{

    public abstract SQLiteDatabase openOrCreateDatabase(String s)
        throws SQLiteException;
}
