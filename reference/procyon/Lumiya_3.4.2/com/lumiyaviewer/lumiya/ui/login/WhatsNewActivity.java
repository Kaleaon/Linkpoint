// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.login;

import com.lumiyaviewer.lumiya.LumiyaApp;
import android.widget.TextView;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.ui.common.ThemedActivity;

public class WhatsNewActivity extends ThemedActivity
{
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(2130968764);
        this.findViewById(2131755742).setText((CharSequence)String.format(this.getString(2131297162), LumiyaApp.getAppVersion()));
        final String[] stringArray = this.getResources().getStringArray(2131689500);
        final StringBuilder sb = new StringBuilder();
        for (final String str : stringArray) {
            if (sb.length() != 0) {
                sb.append("\n\n");
            }
            sb.append("\u2022 ").append(str);
        }
        this.findViewById(2131755463).setText((CharSequence)sb.toString());
    }
}
