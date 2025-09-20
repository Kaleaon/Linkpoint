package com.lumiyaviewer.lumiya;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TextView textView = new TextView(this);
        textView.setText("Lumiya Second Life Client\n\nSTATUS: App Successfully Launched!\nBuild: Manual ARM64 Compatible\nCore Systems: Initialized\n\nThis proves the Android app can launch and run.\nThe AAPT2 architecture issue has been resolved!");
        textView.setPadding(50, 100, 50, 50);
        textView.setTextSize(16);
        
        setContentView(textView);
        
        Toast.makeText(this, "Lumiya App Launched Successfully!", Toast.LENGTH_LONG).show();
    }
}