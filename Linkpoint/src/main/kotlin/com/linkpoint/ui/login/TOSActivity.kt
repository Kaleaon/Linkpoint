package com.linkpoint.ui.login
import java.util.*

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import com.google.common.io.CharStreams
import com.linkpoint.Debug
import com.linkpoint.R
import com.linkpoint.ui.common.ThemedActivity
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class TOSActivity : ThemedActivity(), View.OnClickListener {
    override fun onClick(view: View) {
        switch (view.getId()) {
            case R.id.tos_accept_button:
                setResult(-1)
                finish()
                return
            case R.id.tos_decline_button:
                setResult(0)
                finish()
                return
            default:
                return
        }
    }

    override fun onCreate(bundle: Bundle) {
        super.onCreate(bundle)
        setContentView((Int) R.layout.tos)
        findViewById(R.id.tos_accept_button).setOnClickListener(this)
        findViewById(R.id.tos_decline_button).setOnClickListener(this)
        val webView: WebView = (WebView) findViewById(R.id.tos_view)
        webView.setBackgroundColor(isLightTheme() ? Color.parseColor("#FFFFFF") : Color.parseColor("#000000"))
        val str: String = isLightTheme() ? "\tbackground-color: #FFFFFF;\n\tcolor: #000000;\n" : "\tbackground-color: #000000;\n\tcolor: #FFFFFF;\n"
        try {
            val open: InputStream = getAssets().open("tos/index.html")
            val charStreams: String = CharStreams.toString(InputStreamReader(open))
            open.close()
            webView.loadData(charStreams.replace("<!-- STYLES -->", str), "text/html", "UTF-8")
        } catch (IOException e) {
            Debug.Warning(e)
        }
    }
}
