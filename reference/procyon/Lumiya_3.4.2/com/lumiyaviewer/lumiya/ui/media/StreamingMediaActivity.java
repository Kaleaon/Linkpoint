// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.media;

import android.widget.TextView;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import android.os.Bundle;
import android.content.Context;
import com.lumiyaviewer.lumiya.StreamingMediaService;
import android.content.Intent;
import android.view.View;
import android.view.View$OnClickListener;
import android.support.v7.app.AppCompatActivity;

public class StreamingMediaActivity extends AppCompatActivity implements View$OnClickListener
{
    public void onClick(final View view) {
        switch (view.getId()) {
            case 2131755615: {
                final Intent intent = new Intent(this.getIntent());
                intent.setAction("com.lumiyaviewer.lumiya.ACTION_STOP_MEDIA");
                intent.setClass((Context)this, (Class)StreamingMediaService.class);
                this.startService(intent);
                this.finish();
                break;
            }
        }
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(2130968741);
        final Intent intent = this.getIntent();
        if (intent.hasExtra("parcelData")) {
            final ParcelData parcelData = (ParcelData)intent.getSerializableExtra("parcelData");
            if (parcelData != null) {
                this.findViewById(2131755659).setText((CharSequence)parcelData.getName());
            }
        }
        this.findViewById(2131755615).setOnClickListener((View$OnClickListener)this);
    }
}
