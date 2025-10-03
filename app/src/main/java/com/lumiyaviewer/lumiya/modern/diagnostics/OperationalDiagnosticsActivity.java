package com.lumiyaviewer.lumiya.modern.diagnostics;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Simple UI to run and display operational diagnostics.
 */
public class OperationalDiagnosticsActivity extends AppCompatActivity {
    private static final String TAG = "OperationalDiagUI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 16, 24, 24);

        Toolbar toolbar = new Toolbar(this);
        toolbar.setBackgroundColor(0xFF455A64);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setSubtitleTextColor(0xFFE0E0E0);
        toolbar.setTitle("Operational Diagnostics");
        LinearLayout.LayoutParams toolbarParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            (int) (56 * getResources().getDisplayMetrics().density));
        toolbar.setLayoutParams(toolbarParams);
        root.addView(toolbar);

        TextView header = new TextView(this);
        header.setText("Run checks to verify the app is fully operational on this device.");
        header.setTextSize(14);
        header.setPadding(16, 16, 16, 8);
        root.addView(header);

        TextView summary = new TextView(this);
        summary.setTextSize(16);
        summary.setTypeface(null, Typeface.BOLD);
        summary.setTextColor(0xFF1976D2);
        summary.setPadding(16, 8, 16, 16);
        root.addView(summary);

        TextView details = new TextView(this);
        details.setTextSize(13);
        details.setPadding(16, 8, 16, 16);
        details.setText("Press RUN to execute diagnostics.");
        root.addView(details);

        Button runButton = new Button(this);
        runButton.setText("RUN DIAGNOSTICS");
        runButton.setPadding(16, 12, 16, 12);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(16, 8, 16, 16);
        runButton.setLayoutParams(btnParams);
        root.addView(runButton);

        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OperationalReadinessChecker.Report report = OperationalReadinessChecker.runAllChecks(OperationalDiagnosticsActivity.this);
                String text = report.toMultilineString();
                details.setText(text);
                String sum = report.isOperational() ? "✅ Fully operational" : "❌ Issues detected";
                sum += "  (" + report.getPassedCount() + "/" + report.getTotalCount() + ")";
                summary.setText(sum);
                Log.i(TAG, "Diagnostics completed:\n" + text);
            }
        });

        scroll.addView(root);
        setContentView(scroll);
    }
}

