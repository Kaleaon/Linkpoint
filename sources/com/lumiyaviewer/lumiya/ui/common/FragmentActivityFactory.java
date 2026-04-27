// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public interface FragmentActivityFactory
{

    public abstract Intent createIntent(Context context, Bundle bundle);

    public abstract Class getFragmentClass();
}
