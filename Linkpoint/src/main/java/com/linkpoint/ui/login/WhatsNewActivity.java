package com.linkpoint.ui.login;

import android.os.Bundle;
import android.widget.TextView;
import com.linkpoint.LumiyaApp;
import com.linkpoint.R;
import com.linkpoint.ui.common.ThemedActivity;

public class WhatsNewActivity extends ThemedActivity {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.whats_new);
        ((TextView) findViewById(R.id.whatsnewCaption)).setText(String.format(getString(R.string.whatsnew_caption), new Object[]{LumiyaApp.getAppVersion()}));
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
