package com.lumiya.simple;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Create a simple text view programmatically (no resources needed)
        TextView textView = new TextView(this);
        textView.setText("Lumiya Second Life Client\n\n" +
                        "STATUS: App Successfully Launched!\n" +
                        "Build: Manual ARM64 Compatible\n" +
                        "Core Systems: Initialized\n\n" +
                        "This proves the Android app can launch and run.\n" +
                        "Next step: Implement full login functionality.");
        textView.setPadding(50, 100, 50, 50);
        textView.setTextSize(16);
        
        setContentView(textView);
        
        // Show a toast to prove functionality
        Toast.makeText(this, "Lumiya App Launched Successfully!", Toast.LENGTH_LONG).show();
    }
}