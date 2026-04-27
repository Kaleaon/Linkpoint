package com.linkpoint.ui.render

import android.widget.TextView

class ChatEventOverlay {
    final TextView textView
    final Long timestamp

    ChatEventOverlay(Long j, TextView textView2) {
        this.timestamp = j
        this.textView = textView2
    }
}
