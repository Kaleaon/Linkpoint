// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.login;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.ui.common.ThemedActivity;

public class WhatsNewActivity extends ThemedActivity
{

    public WhatsNewActivity()
    {
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(0x7f0400bc);
        bundle = String.format(getString(0x7f09038a), new Object[] {
            LumiyaApp.getAppVersion()
        });
        ((TextView)findViewById(0x7f1002de)).setText(bundle);
        bundle = getResources().getStringArray(0x7f0f001c);
        StringBuilder stringbuilder = new StringBuilder();
        int j = bundle.length;
        for (int i = 0; i < j; i++)
        {
            String s = bundle[i];
            if (stringbuilder.length() != 0)
            {
                stringbuilder.append("\n\n");
            }
            stringbuilder.append("\u2022 ").append(s);
        }

        ((TextView)findViewById(0x7f1001c7)).setText(stringbuilder.toString());
    }
}
