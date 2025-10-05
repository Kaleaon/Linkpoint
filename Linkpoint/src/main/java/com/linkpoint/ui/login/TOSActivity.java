package com.lumiyaviewer.lumiya.ui.login;
import java.util.*;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import com.google.common.io.CharStreams;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.ui.common.ThemedActivity;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TOSActivity extends ThemedActivity implements View.OnClickListener {
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tos_accept_button:
                setResult(-1);
                finish();
                return;
            case R.id.tos_decline_button:
                setResult(0);
                finish();
                return;
            default:
                return;
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.tos);
        findViewById(R.id.tos_accept_button).setOnClickListener(this);
        findViewById(R.id.tos_decline_button).setOnClickListener(this);
        WebView webView = (WebView) findViewById(R.id.tos_view);
        webView.setBackgroundColor(isLightTheme() ? Color.parseColor("#FFFFFF") : Color.parseColor("#000000"));
        String str = isLightTheme() ? "\tbackground-color: #FFFFFF;\n\tcolor: #000000;\n" : "\tbackground-color: #000000;\n\tcolor: #FFFFFF;\n";
        try {
            InputStream open = getAssets().open("tos/index.html");
            String charStreams = CharStreams.toString(new InputStreamReader(open));
            open.close();
            webView.loadData(charStreams.replace("<!-- STYLES -->", str), "text/html", "UTF-8");
        } catch (IOException e) {
            Debug.Warning(e);
        }
    }
}
