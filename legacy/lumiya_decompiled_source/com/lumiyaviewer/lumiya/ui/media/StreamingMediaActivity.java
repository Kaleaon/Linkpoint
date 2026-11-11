package com.lumiyaviewer.lumiya.ui.media;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.StreamingMediaService;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.ui.chat.profiles.ParcelPropertiesFragment;
/* loaded from: classes.dex */
public class StreamingMediaActivity extends AppCompatActivity implements View.OnClickListener {
    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.parcel_media_stop_button /* 2131755615 */:
                Intent intent = new Intent(getIntent());
                intent.setAction("com.lumiyaviewer.lumiya.ACTION_STOP_MEDIA");
                intent.setClass(this, StreamingMediaService.class);
                startService(intent);
                finish();
                return;
            default:
                return;
        }
    }

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        ParcelData parcelData;
        super.onCreate(bundle);
        setContentView(R.layout.streaming_media);
        Intent intent = getIntent();
        if (intent.hasExtra(ParcelPropertiesFragment.PARCEL_DATA_KEY) && (parcelData = (ParcelData) intent.getSerializableExtra(ParcelPropertiesFragment.PARCEL_DATA_KEY)) != null) {
            ((TextView) findViewById(R.id.locationNameView)).setText(parcelData.getName());
        }
        findViewById(R.id.parcel_media_stop_button).setOnClickListener(this);
    }
}
