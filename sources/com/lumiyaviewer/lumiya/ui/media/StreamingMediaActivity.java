// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.media;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.StreamingMediaService;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;

public class StreamingMediaActivity extends AppCompatActivity
    implements android.view.View.OnClickListener
{

    public StreamingMediaActivity()
    {
    }

    public void onClick(View view)
    {
        switch (view.getId())
        {
        default:
            return;

        case 2131755615: 
            view = new Intent(getIntent());
            break;
        }
        view.setAction("com.lumiyaviewer.lumiya.ACTION_STOP_MEDIA");
        view.setClass(this, com/lumiyaviewer/lumiya/StreamingMediaService);
        startService(view);
        finish();
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(0x7f0400a5);
        bundle = getIntent();
        if (bundle.hasExtra("parcelData"))
        {
            bundle = (ParcelData)bundle.getSerializableExtra("parcelData");
            if (bundle != null)
            {
                ((TextView)findViewById(0x7f10028b)).setText(bundle.getName());
            }
        }
        findViewById(0x7f10025f).setOnClickListener(this);
    }
}
