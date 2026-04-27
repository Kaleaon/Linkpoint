package com.linkpoint.ui.login

import android.os.Bundle
import android.widget.TextView
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.ui.common.ThemedActivity

class WhatsNewActivity : ThemedActivity() {
    override fun onCreate(bundle: Bundle) {
        super.onCreate(bundle)
        setContentView((Int) R.layout.whats_new)
        ((TextView) findViewById(R.id.whatsnewCaption)).setText(String.format(getString(R.string.whatsnew_caption), Array<Any>{LinkpointApp.getAppVersion()}))
        val stringArray: Array<String> = getResources().getStringArray(R.array.whatsnew_array)
        val sb: StringBuilder = StringBuilder()
        for (String str : stringArray) {
            if (sb.length() != 0) {
                sb.append("\n\n")
            }
            sb.append("• ").append(str)
        }
        ((TextView) findViewById(R.id.whatsnewText)).setText(sb.toString())
    }
}
