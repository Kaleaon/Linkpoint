package com.lumiyaviewer.lumiya.ui.login;

import android.os.Bundle;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.ui.common.ThemedActivity;

/* loaded from: classes.dex */
public class WhatsNewActivity extends ThemedActivity {
    @Override // com.lumiyaviewer.lumiya.ui.common.ThemedActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.whats_new);
        ((TextView) findViewById(R.id.whatsnewCaption)).setText(String.format(getString(R.string.whatsnew_caption), LumiyaApp.getAppVersion()));
        String[] stringArray = getResources().getStringArray(R.array.whatsnew_array);
        StringBuilder sb = new StringBuilder();
        for (String str : stringArray) {
            if (sb.length() != 0) {
                sb.append("\n\n");
            }
            sb.append("• ").append(str);
        }
        ((TextView) findViewById(R.id.whatsnewText)).setText(sb.toString());
    }
}
