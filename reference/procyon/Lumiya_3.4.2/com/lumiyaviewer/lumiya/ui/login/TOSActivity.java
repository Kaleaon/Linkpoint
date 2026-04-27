// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.login;

import java.io.InputStream;
import java.io.IOException;
import com.lumiyaviewer.lumiya.Debug;
import com.google.common.io.CharStreams;
import java.io.InputStreamReader;
import android.graphics.Color;
import android.webkit.WebView;
import android.os.Bundle;
import android.view.View;
import android.view.View$OnClickListener;
import com.lumiyaviewer.lumiya.ui.common.ThemedActivity;

public class TOSActivity extends ThemedActivity implements View$OnClickListener
{
    public void onClick(final View view) {
        switch (view.getId()) {
            case 2131755670: {
                this.setResult(-1);
                this.finish();
                break;
            }
            case 2131755671: {
                this.setResult(0);
                this.finish();
                break;
            }
        }
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(2130968748);
        this.findViewById(2131755670).setOnClickListener((View$OnClickListener)this);
        this.findViewById(2131755671).setOnClickListener((View$OnClickListener)this);
        final WebView webView = this.findViewById(2131755669);
        Label_0121: {
            if (!this.isLightTheme()) {
                break Label_0121;
            }
            int backgroundColor = Color.parseColor("#FFFFFF");
        Label_0069_Outer:
            while (true) {
                webView.setBackgroundColor(backgroundColor);
                Label_0130: {
                    if (!this.isLightTheme()) {
                        break Label_0130;
                    }
                    String replacement = "\tbackground-color: #FFFFFF;\n\tcolor: #000000;\n";
                    try {
                        while (true) {
                            final InputStream open = this.getAssets().open("tos/index.html");
                            final String string = CharStreams.toString(new InputStreamReader(open));
                            open.close();
                            webView.loadData(string.replace("<!-- STYLES -->", replacement), "text/html", "UTF-8");
                            return;
                            backgroundColor = Color.parseColor("#000000");
                            continue Label_0069_Outer;
                            replacement = "\tbackground-color: #000000;\n\tcolor: #FFFFFF;\n";
                            continue;
                        }
                    }
                    catch (final IOException ex) {
                        Debug.Warning(ex);
                    }
                }
                break;
            }
        }
    }
}
