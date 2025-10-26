package com.lumiyaviewer.lumiya.stub;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.view.Gravity;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Minimal stub login activity to enable APK build.
 * 
 * This replaces the broken CleanLoginActivity.kt which has compilation errors from poor Java-to-Kotlin migration.
 * Once the 221,328+ Kotlin compilation errors are fixed, this can be replaced with the full implementation.
 */
public class StubCleanLoginActivity extends AppCompatActivity {
    private static final String TAG = "StubLoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.i(TAG, "Lumiya stub login activity started");
        
        // Create a simple UI programmatically (no layout XML needed)
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(Color.parseColor("#1E1E1E"));
        layout.setPadding(40, 40, 40, 40);
        
        TextView titleText = new TextView(this);
        titleText.setText("Lumiya Second Life Viewer");
        titleText.setTextSize(24);
        titleText.setTextColor(Color.WHITE);
        titleText.setGravity(Gravity.CENTER);
        titleText.setPadding(0, 0, 0, 40);
        layout.addView(titleText);
        
        TextView statusText = new TextView(this);
        statusText.setText("Build Status: MINIMAL STUB\n\n" +
                          "This is a minimal working APK build.\n\n" +
                          "The full Second Life viewer functionality\n" +
                          "requires fixing 221,328 Kotlin compilation errors\n" +
                          "from the incomplete Java-to-Kotlin migration.\n\n" +
                          "See docs/LINKPOINT_APK_BUILD_ANALYSIS.md for details.\n\n" +
                          "Priority fixes needed:\n" +
                          "1. AsyncRequestHandler.kt - mark as 'open class'\n" +
                          "2. ShaderProgram.kt - fix constructor syntax\n" +
                          "3. LLSDNode.kt - mark methods as 'open'\n" +
                          "4. SLMessage.kt - add missing methods\n" +
                          "5. ~500 message handler files - fix override methods");
        statusText.setTextSize(14);
        statusText.setTextColor(Color.parseColor("#CCCCCC"));
        statusText.setGravity(Gravity.CENTER);
        layout.addView(statusText);
        
        setContentView(layout);
        
        Log.i(TAG, "Stub UI displayed - APK build successful");
    }
}
