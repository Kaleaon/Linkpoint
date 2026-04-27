// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.voice;

import android.view.View;
import butterknife.internal.DebouncingOnClickListener;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.voice:
//            VoiceStatusView_ViewBinding, VoiceStatusView

class val.target extends DebouncingOnClickListener
{

    final VoiceStatusView_ViewBinding this$0;
    final VoiceStatusView val$target;

    public void doClick(View view)
    {
        val$target.onVoiceMicOffButton();
    }

    ()
    {
        this$0 = final_voicestatusview_viewbinding;
        val$target = VoiceStatusView.this;
        super();
    }
}
