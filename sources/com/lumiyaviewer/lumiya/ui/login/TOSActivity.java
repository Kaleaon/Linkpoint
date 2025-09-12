// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.login;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import com.google.common.io.CharStreams;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.ui.common.ThemedActivity;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TOSActivity extends ThemedActivity
    implements android.view.View.OnClickListener
{

    public TOSActivity()
    {
    }

    public void onClick(View view)
    {
        switch (view.getId())
        {
        default:
            return;

        case 2131755670: 
            setResult(-1);
            finish();
            return;

        case 2131755671: 
            setResult(0);
            break;
        }
        finish();
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(0x7f0400ac);
        findViewById(0x7f100296).setOnClickListener(this);
        findViewById(0x7f100297).setOnClickListener(this);
        WebView webview = (WebView)findViewById(0x7f100295);
        int i;
        if (isLightTheme())
        {
            i = Color.parseColor("#FFFFFF");
        } else
        {
            i = Color.parseColor("#000000");
        }
        webview.setBackgroundColor(i);
        if (isLightTheme())
        {
            bundle = "\tbackground-color: #FFFFFF;\n\tcolor: #000000;\n";
        } else
        {
            bundle = "\tbackground-color: #000000;\n\tcolor: #FFFFFF;\n";
        }
        try
        {
            InputStream inputstream = getAssets().open("tos/index.html");
            String s = CharStreams.toString(new InputStreamReader(inputstream));
            inputstream.close();
            webview.loadData(s.replace("<!-- STYLES -->", bundle), "text/html", "UTF-8");
            return;
        }
        // Misplaced declaration of an exception variable
        catch (Bundle bundle)
        {
            Debug.Warning(bundle);
        }
    }
}
