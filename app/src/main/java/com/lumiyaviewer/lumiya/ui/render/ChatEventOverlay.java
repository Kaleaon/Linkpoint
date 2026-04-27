package com.lumiyaviewer.lumiya.ui.render;

import android.widget.TextView;

class ChatEventOverlay {
    final TextView textView;
    final long timestamp;

    ChatEventOverlay(long j, TextView textView2) {
        this.timestamp = j;
        this.textView = textView2;
    }
}
