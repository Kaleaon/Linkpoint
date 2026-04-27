// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.widget.TextView;

class ChatEventOverlay
{

    final TextView textView;
    final long timestamp;

    ChatEventOverlay(long l, TextView textview)
    {
        timestamp = l;
        textView = textview;
    }
}
