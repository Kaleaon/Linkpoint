// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.render;

import android.widget.TextView;

class ChatEventOverlay
{
    final TextView textView;
    final long timestamp;
    
    ChatEventOverlay(final long timestamp, final TextView textView) {
        this.timestamp = timestamp;
        this.textView = textView;
    }
}
