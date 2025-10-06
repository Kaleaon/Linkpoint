package com.linkpoint.ui.login

import android.os.Bundle
import android.widget.TextView
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.ui.common.ThemedActivity

class WhatsNewActivity : ThemedActivity() {
    fun onCreate(Bundle bundle) {
        super.onCreate(bundle)
        setContentView((Int) R.layout.whats_new)
        ((TextView) findViewById(R.id.whatsnewCaption)).setText(String.format(getString(R.string.whatsnew_caption), Object[]{LinkpointApp.getAppVersion()}))
        String[] stringArray = getResources().getStringArray(R.array.whatsnew_array)
        StringBuilder sb = StringBuilder()
        for (String str : stringArray) {
            if (sb.length() != 0) {
                sb.append("\n\n")
            }
            sb.append("• ").append(str)
        }
        ((TextView) findViewById(R.id.whatsnewText)).setText(sb.toString())
    }
}
