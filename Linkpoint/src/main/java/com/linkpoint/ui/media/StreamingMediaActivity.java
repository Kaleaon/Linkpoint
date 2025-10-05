package com.linkpoint.ui.media;
import java.util.*;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.linkpoint.R;
import com.linkpoint.StreamingMediaService;
import com.linkpoint.slproto.users.ParcelData;
import com.linkpoint.ui.chat.profiles.ParcelPropertiesFragment;

public class StreamingMediaActivity extends AppCompatActivity implements View.OnClickListener {
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.parcel_media_stop_button:
                Intent intent = new Intent(getIntent());
                intent.setAction("com.linkpoint.ACTION_STOP_MEDIA");
                intent.setClass(this, StreamingMediaService.class);
                startService(intent);
                finish();
                return;
            default:
                return;
        }
    }

    public void onCreate(Bundle bundle) {
        ParcelData parcelData;
        super.onCreate(bundle);
        setContentView((int) R.layout.streaming_media);
        Intent intent = getIntent();
        if (intent.hasExtra(ParcelPropertiesFragment.PARCEL_DATA_KEY) && (parcelData = (ParcelData) intent.getSerializableExtra(ParcelPropertiesFragment.PARCEL_DATA_KEY)) != null) {
            ((TextView) findViewById(R.id.locationNameView)).setText(parcelData.getName());
        }
        findViewById(R.id.parcel_media_stop_button).setOnClickListener(this);
    }
}
